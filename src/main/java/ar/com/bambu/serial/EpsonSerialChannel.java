package ar.com.bambu.serial;

import com.fazecast.jSerialComm.SerialPort;
import org.jpos.iso.ISOUtil;


import java.io.ByteArrayOutputStream;


public class EpsonSerialChannel {
    private static byte STX = 0x02;
    private static byte ETX = 0x03;
    private static byte ESC = 0x1B;
    private static byte DEL = 0x1C;
    private byte seq = (byte) 0x81;


    public byte[] sendMsg(byte [] dataFrame, byte seq) throws Exception {
        //creo un nuevo byte[] con el start de package, seq le pongo lo que me mandaron y le chanto el end y el checksum
        byte[] outFrame = this.generateFrame(dataFrame, seq);
        System.out.println("todo:  "+ISOUtil.byte2hex(outFrame));
        this.writeFrame(outFrame);
        return this.readFrame();
    }

    public byte[] sendMsg(byte [] dataFrame) throws Exception {
        return this.sendMsg(dataFrame, this.getNextSeq());
    }

    private byte getNextSeq() {
        byte result = seq;
        if(seq==(byte)0xff){
            seq=(byte)0x81;
        }else{
            seq++;
        }
        return result;
    }

    private byte[] generateFrame(byte [] dataFrame, byte seq) throws Exception{
        int checkSum = STX;
        checkSum+= (seq & 0xff);
        checkSum+= (ETX & 0xff);
        for(int i = 0; i<dataFrame.length-1;i++){
            checkSum+=(dataFrame[i] & 0xff);
        }
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        result.write(STX);
        result.write(seq);
        result.write(dataFrame,0,dataFrame.length-1);
        result.write(ETX);
        String sChecksum = ISOUtil.padleft(Integer.toHexString(checkSum), 4, '0');
        result.write(sChecksum.getBytes());
        return result.toByteArray();
    }

    private void writeFrame(byte[] data){
        SerialPort comPort = SerialPort.getCommPort("/dev/pts/3");
        comPort.openPort();
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 20000, 0);
        comPort.writeBytes(data, data.length);
        comPort.closePort();
    }

    private byte[] readFrame() {
        SerialPort comPort = SerialPort.getCommPort("/dev/pts/4");


        comPort.openPort();
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 20000, 0);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {

            byte[] readBuffer = new byte[4];

            int numRead = comPort.readBytes(readBuffer, 1);
            if (readBuffer[0] != STX) {
                //nos desincronizamos, leer hasta encontrar un 0x02 que no le siga a un ESC
                while (readBuffer[0] != ETX) {
                    System.out.println("Desincronizados, descartanto paquetes: ");
                    if (readBuffer[0] == ESC) {
                        //no puede terminar en ESC una trama
                        comPort.readBytes(readBuffer, 1);
                    }
                    comPort.readBytes(readBuffer, 1);
                }
            }
            //leido ya stx,
            comPort.readBytes(readBuffer, 1);
            //leido ya SEQ
            comPort.readBytes(readBuffer, 1);
            while (readBuffer[0] != ETX) {
                System.out.println("in the loop: "+ISOUtil.hexString(result.toByteArray()));
                if (readBuffer[0] == ESC) {
                    //no puede terminar en ESC una trama
                    result.write(ESC);
                    comPort.readBytes(readBuffer, 1);
                    result.write(readBuffer[0]);
                } else {
                    result.write(readBuffer[0]);
                }
                comPort.readBytes(readBuffer, 1);
            }
            System.out.println("out of the loop: "+ISOUtil.hexString(result.toByteArray()));
            //leimos hasta ETX, leo 4 bytes mas de checksum que no hago nada con esto por ahora.
            comPort.readBytes(readBuffer, 4);

            //y pongo un delimitador porque deberia de tener segun jpos.
            result.write(DEL);
            System.out.println("final de leer serial: "+ISOUtil.hexString(result.toByteArray()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        comPort.closePort();

        return result.toByteArray();
    }




}
