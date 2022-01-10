package ar.com.bambu.serial;

import ar.com.bambu.communicator.EpsonCommunicator;
import com.fazecast.jSerialComm.SerialPort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpos.iso.ISOUtil;


import java.io.ByteArrayOutputStream;
import java.io.IOException;



public class EpsonSerialChannel {
    public static final byte STX = 0x02;
    public static final byte ETX = 0x03;
    public static final byte ESC = 0x1B;
    public static final byte DEL = 0x1C;
    public static final byte ACK = 0x06;
    public static final byte NACK = 0x15;
    public static final byte INTER = (byte) 0x80;
    private byte seq = (byte) 0x81;
    private static final Logger logger = LogManager.getLogger(EpsonSerialChannel.class);
    private String portserial;
    private boolean puertoYaAbierto =  false;
    private SerialPort comPort;

    public void setPortserial(String portserial) {
        this.portserial = portserial;
    }




    public byte[] sendMsg(byte[] dataFrame, byte seq) throws Exception {
        //creo un nuevo byte[] con el start de package, seq le pongo lo que me mandaron y le chanto el end y el checksum
        byte[] outFrame = this.generateFrame(dataFrame, seq);
        logger.debug("Por enviar frame por puerto serie:  " + ISOUtil.byte2hex(outFrame));
        this.writeFrame(outFrame);
        return this.readFrame();
    }

    public byte[] sendMsg(byte[] dataFrame) throws Exception {
        return this.sendMsg(dataFrame, this.getNextSeq());
    }

    private byte getNextSeq() {
        byte result = seq;
        if (seq == (byte) 0xff) {
            seq = (byte) 0x81;
        } else {
            seq++;
        }
        return result;
    }

    private byte[] generateFrame(byte[] dataFrame, byte seq) throws Exception {
        int checkSum = STX;
        checkSum += (seq & 0xff);
        checkSum += (ETX & 0xff);
        for (int i = 0; i < dataFrame.length - 1; i++) {
            checkSum += (dataFrame[i] & 0xff);
        }
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        result.write(STX);
        result.write(seq);
        result.write(dataFrame, 0, dataFrame.length - 1);
        result.write(ETX);
        String sChecksum = ISOUtil.padleft(Integer.toHexString(checkSum), 4, '0');
        result.write(sChecksum.getBytes());
        return result.toByteArray();
    }

    private void writeFrame(byte[] data)  throws Exception  {
        int escritos = 0;
        if( puertoYaAbierto == false) {
            String CommPortFiscal = Fiscal.getPortName();
            int baudrate = Fiscal.getBaudRate();
            //        SerialPort comPort = SerialPort.getCommPort("COM31");
            comPort = SerialPort.getCommPort(CommPortFiscal);
            comPort.setParity(SerialPort.NO_PARITY);
            comPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
            comPort.setNumDataBits(8);
            comPort.setBaudRate(baudrate);
            //serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            //comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 20000, 0);
            comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 20000, 20000);
        }
        if (!comPort.openPort()) {
            throw new Exception("Cannot open serial port.");
        }
        try {
            puertoYaAbierto =true;
            escritos = comPort.writeBytes(data, data.length);

            if (escritos == 0 || escritos == -1) {
                comPort.closePort();
                puertoYaAbierto =false;
                throw new Exception("Write error. escritos " + escritos);
            }
        }catch (Exception e)
        {
            throw new Exception("Write error. escritos " + escritos);

        }
        //  comPort.closePort();
    }

    private byte[] readFrame() throws IOException {

        if( puertoYaAbierto == false) {
            String CommPortFiscal = Fiscal.getPortName();
            // SerialPort comPort = SerialPort.getCommPort("COM31");
            SerialPort comPort = SerialPort.getCommPort(CommPortFiscal);


            comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 20000, 0);
        }

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {

            byte[] readBuffer = new byte[4];

            comPort.readBytes(readBuffer, 1);


            if (readBuffer[0] != STX) {
                //nos desincronizamos, leer hasta encontrar un 0x02 que no le siga a un ESC
                while (readBuffer[0] != STX) {
                    if (readBuffer[0] == ESC) {
                        //no puede terminar en ESC una trama
                        logger.info("ESC read, buscando STX " + ISOUtil.byte2hex(readBuffer));
                        comPort.readBytes(readBuffer, 1);
                    } else if (readBuffer[0] == ACK) {
                        logger.debug("ACK READ");
                    } else if (readBuffer[0] == NACK) {
                        logger.error("NACK leido de impresora, cortando lectura.");
                        throw new IOException("Nack leido de impresora.");
                    }
                    comPort.readBytes(readBuffer, 1);
                }
            }
            //leido ya stx,
            comPort.readBytes(readBuffer, 1);
            while (readBuffer[0] == INTER) {
                logger.info("Leido paquete intermedio, lo leemos y volvemos a leer un paquete entero");
                this.readPaqueteIntermedio(comPort, readBuffer);
            }
            //leido ya SEQ
            comPort.readBytes(readBuffer, 1);
            while (readBuffer[0] != ETX) {
               // logger.debug("in the loop: " + ISOUtil.hexString(result.toByteArray()));
                if (readBuffer[0] == ESC) {
                    //no puede terminar en ESC una trama
                    result.write(ESC);
                    comPort.readBytes(readBuffer, 1);
                }
                result.write(readBuffer[0]);
                comPort.readBytes(readBuffer, 1);
            }
            logger.debug("out of the loop: " + ISOUtil.hexString(result.toByteArray()));
            //leimos hasta ETX, leo 4 bytes mas de checksum que no hago nada con esto por ahora.
            comPort.readBytes(readBuffer, 4);
            comPort.writeBytes(new byte[]{0x06}, 1);

            //y pongo un delimitador porque deberia de tener segun jpos.
            result.write(DEL);
            logger.debug("final de leer serial: " + ISOUtil.hexString(result.toByteArray()));

        } catch (Exception e) {
            logger.error("Error serial reading;", e);
        }
        comPort.closePort();

        return result.toByteArray();
    }

    private void readPaqueteIntermedio(SerialPort comPort, byte[] buffer) throws IOException {
        comPort.readBytes(buffer, 1);
        if (buffer[0] != ETX) {
            logger.error("Paquete intermedio no termina con ETX, abortando...");
            throw new IOException("Paquete intermedio no termina con ETX, abortando...");
        }

        comPort.readBytes(buffer, 4);
        comPort.readBytes(buffer, 1);
        if (buffer[0] != STX) {
            logger.error("Paquete intermedio termino y no recibimos STX, abortando...");
            throw new IOException("Paquete intermedio termino y no recibimos STX, abortando...");
        }
        comPort.readBytes(buffer, 1);
    }


}
