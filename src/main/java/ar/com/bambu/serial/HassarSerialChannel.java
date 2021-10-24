package ar.com.bambu.serial;

import ar.com.bambu.communicator.EpsonCommunicator;
import com.fazecast.jSerialComm.SerialPort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpos.iso.ISOUtil;


import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class HassarSerialChannel {
    public static final byte STX = 0x02;
    public static final byte ETX = 0x03;
    public static final byte ESC = 0x1B;
    public static final byte DEL = 0x1C;
    public static final byte ACK = 0x06;
    public static final byte NACK = 0x15;
    public static final byte DC2 = 0x12;
    public static final byte DC4 = 0x14;
    public static final byte INTER = (byte) 0x80;
    private byte seq = (byte) 0x81;
    private static final Logger logger = LogManager.getLogger(EpsonSerialChannel.class);


    public String getComPort() {
        return comPort;
    }

    public void setComPort(String comPort) {
        this.comPort = comPort;
    }

    private String comPort ;
    private byte[] typeComando;

    public byte[] sendMsg(byte[] dataFrame, byte seq) throws Exception {
        //creo un nuevo byte[] con el start de package, seq le pongo lo que me mandaron y le chanto el end y el checksum
        byte[] outFrame = this.generateFrame(dataFrame, seq);
        logger.debug("Por enviar frame por puerto serie:  " + ISOUtil.byte2hex(outFrame));
        this.writeFrame(outFrame);
        Thread.sleep(100); //lo agregue pero no se si funcionara
        return this.readFrame();

    }

    public byte[] sendMsg(byte[] dataFrame) throws Exception {
        return this.sendMsg(dataFrame, this.getNextSeq());
    }

    /**
     * Que onda la sequencia de hassar?, voy a hacer lo mismo que con epson porque no me dice que este mal.
     * @return
     */
    private byte getNextSeq() {
        byte result = seq;
        if (seq == (byte) 0xff) {
            seq = (byte) 0x81;
        } else {
            seq++;
        }
        logger.debug("MANDAMOS SEQ " + result  );
        return result;
    }

    private byte[] generateFrame(byte[] dataFrame, byte seq) throws Exception {
        int checkSum = STX;
        checkSum += (seq & 0xff);
        checkSum += (ETX & 0xff);
        checkSum += (ESC & 0xff);
        for (int i = 0; i < dataFrame.length - 1; i++) {
            checkSum += (dataFrame[i] & 0xff);
        }
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        result.write(STX);
        result.write(seq);
        //hassar siempre pone un ESC antes del comando
        result.write(ESC);
        result.write(dataFrame, 0, dataFrame.length - 1);
        result.write(ETX);
        String sChecksum = ISOUtil.padleft(Integer.toHexString(checkSum), 4, '0');
        result.write(sChecksum.getBytes());
        return result.toByteArray();
    }

    private void writeFrame(byte[] data) throws Exception {
        int escritos = 0;
        String CommPortFiscal = Fiscal.getPortName();
        int baudrate = Fiscal.getBaudRate();
//        SerialPort comPort = SerialPort.getCommPort("COM31");
        SerialPort comPort = SerialPort.getCommPort(CommPortFiscal);
        comPort.setParity(SerialPort.NO_PARITY);
        comPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        comPort.setNumDataBits(8);
        comPort.setBaudRate(baudrate);
        //serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

        //comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 20000, 0);
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 20000, 20000);
            if (!comPort.openPort()) {
                throw new Exception("Cannot open serial port.");
            }
        try {
            escritos = comPort.writeBytes(data, data.length);

            if (escritos == 0 || escritos == -1) {
                comPort.closePort();
                throw new Exception("Write error. escritos " + escritos);
            }
        }catch (Exception e)
        {
            throw new Exception("Write error. escritos " + escritos);

        }
            comPort.closePort();
    }

    private byte[] readFrame()  {
        boolean desfasado = false;
        String CommPortFiscal =   Fiscal.getPortName();
       // SerialPort comPort = SerialPort.getCommPort("COM31");
        SerialPort comPort = SerialPort.getCommPort(CommPortFiscal);


        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 20000, 0);



        //a pesar que le puse el timeout arriba ahora el ciclo de abajo lo cierro a los 30 seg

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            if (!comPort.openPort()) {
                throw new Exception("Cannot open serial port.");
            }

            byte[] readBuffer = new byte[4];

            comPort.readBytes(readBuffer, 1);
            //este caso deberia ser un ack  stx, el ack ya se leyo antes aca viene un stx para arrancar
            logger.warn("COMANDO " + this.getTypeComando()[0] +  " BYTEiNICIAL  " + readBuffer[0] +  " sEQ " + (seq -1)  );


            if (readBuffer[0] != STX
                    && (readBuffer[0] != DC2 && readBuffer[0] != DC4 && readBuffer[0] != ACK && readBuffer[0] != NACK && readBuffer[0] != ESC)
                    && ( this.getTypeComando()[0] == 119 || this.getTypeComando()[0] == 161 || this.getTypeComando()[0] == -95)) {
                //119 es e comando 0x77 el 161 es el 0xA1 (espera)
                logger.warn("OJO NO VIENE EL ACK PRIMERO, VINO  " + readBuffer[0] +" pongo como desfasado" );
                //esto lo pongo porque deberia venir por ejemplo 06 02 C0 1B 77 1C 30 30 30 30
                //pero se desfasa y viene   C0 1B 77 1C 30 30 30 30    y se comio el ack y el 02.
                //mi opcion es lo rescontruyo si es 119 o a1 y se vera
                //veamos si es la secuencia
                if( readBuffer[0] == (seq - 1) ){
                    logger.warn("DESFASADO PERO VINO LA SEQUENCIA " + readBuffer[0]  );
                    logger.warn("SUPONEMOS QUE SE PERDIERON 1 BYTE ASI QUE SIGA "  );
              //      result.write( (seq - 1));
                    result.write( this.getTypeComando()[0]);
                //    result.write(STX);
                    desfasado = true;
                }
            }

            if (readBuffer[0] != STX && desfasado == false) {
                //nos desincronizamos, leer hasta encontrar un 0x02 que no le siga a un ESC
                while (readBuffer[0] != STX /*&& (System.currentTimeMillis()-startTime)< (tiempoTimeout+ tiempoExtra)*/ ) {
                    if (readBuffer[0] == ESC) {
                        //no puede terminar en ESC una trama
                        logger.info("ESC read, buscando STX " + ISOUtil.byte2hex(readBuffer));
                        comPort.readBytes(readBuffer, 1);
                    } else if (readBuffer[0] == ACK) {
                        logger.debug("ACK READ");
                    } else if (readBuffer[0] == NACK) {
                        logger.error("NACK leido de impresora, cortando lectura.");
                        throw new IOException("Nack leido de impresora.");
                    } else if(readBuffer[0] == DC2 || readBuffer[0] == DC4) {
                        logger.debug("Llego un DC2/DC4: "+readBuffer[0]+ " SEGUIMOS esperamos un STX");
                    //    tiempoExtra += 1000;  //aumentamos la espera 1 segundo porque la fscal esta ocupada, la respuesta son 400 ms
                    } else{
                        //ojo aca con lo que llega
                        logger.debug("Llego un: "+readBuffer[0]+ "esperamos un STX");

                    }

                    comPort.readBytes(readBuffer, 1);
                }
            }
            if(readBuffer[0] != STX && desfasado == false) {
                logger.error("No puedo leer el puerto salgo por timeout.");
                throw new IOException("No puedo leer el puerto salgo por timeout.");
            }
            //leido ya stx,
            comPort.readBytes(readBuffer, 1);
            //leido ya SEQ
            comPort.readBytes(readBuffer, 1);
            //hassar siempre manda un ESC luego del seq
            comPort.readBytes(readBuffer, 1);

            while (readBuffer[0] != ETX) {
                // logger.debug("in the loop: " + ISOUtil.hexString(result.toByteArray()));
                if (readBuffer[0] == ESC) {
                    //no puede terminar en ESC una trama
                    logger.debug("no puede terminar en ESC una trama");
                    result.write(ESC);
                    comPort.readBytes(readBuffer, 1);
                }
                result.write(readBuffer[0]);
                comPort.readBytes(readBuffer, 1);
            }
            //logger.debug("out of the loop: " + ISOUtil.hexString(result.toByteArray()));
            //leimos hasta ETX, leo 4 bytes mas de checksum que no hago nada con esto por ahora.
            comPort.readBytes(readBuffer, 4);
        } catch (Exception e) {
            logger.error("Error serial reading;", e);
        }
        try{
            comPort.writeBytes(new byte[]{0x06}, 1);

            //y pongo un delimitador porque deberia de tener segun jpos.
            result.write(DEL);
            logger.debug("final de leer serial: " + ISOUtil.hexString(result.toByteArray()));
        } catch (Exception e) {
            logger.error("Error serial reading/writing;", e);
        }

        comPort.closePort();
        return result.toByteArray();
    }
    public byte[] getTypeComando() {
        return typeComando;
    }

    public void setTypeComando(byte[]  typeComando) {
        this.typeComando = typeComando;
    }
}
