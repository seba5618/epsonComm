package ar.com.bambu.serial;

import org.jpos.iso.ISOUtil;

import java.io.ByteArrayOutputStream;


public class EpsonSerialChannel {
    private static byte STX = 0x02;
    private static byte ETX = 0x03;
    private static byte ESC = 0x1B;
    private byte seq = (byte) 0x81;


    public byte[] sendMsg(byte [] dataFrame, byte seq) throws Exception {
        //creo un nuevo byte[] con el start de package, seq le pongo lo que me mandaron y le chanto el end y el checksum

        byte[] outFrame = this.generateFrame(dataFrame, seq);
        System.out.println("todo:  "+ISOUtil.byte2hex(outFrame));
        //luego leo la respuesta hasta que recibo un end of package que no fue escapado antes.

        return null;
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




}
