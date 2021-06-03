package ar.com.bambu;

import ar.com.bambu.jpos.EpsonFrameMsg;
import ar.com.bambu.jpos.EpsonPackager;
import ar.com.bambu.serial.EpsonSerialChannel;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;

/**
 * Hello world!
 */
public class App {



    public static void main(String[] args)  throws Exception{
        System.out.println("Hello World!");
        EpsonSerialChannel channel = new EpsonSerialChannel();

        EpsonFrameMsg m = new EpsonFrameMsg();
        m.setPackager(new EpsonPackager());

        m.set(1,new byte[]{0x05,0x02});
        m.set(2,new byte[]{0x00,0x00});


        byte[] pack = m.pack();
        System.out.println(ISOUtil.byte2hex(pack));





        byte[] reply = channel.sendMsg(pack);

        EpsonFrameMsg replyMsg = new EpsonFrameMsg();


        replyMsg.setPackager(new EpsonPackager());
        replyMsg.unpack(reply);

        System.out.println("respuesta: "+ ISOUtil.byte2hex(replyMsg.pack()));


    }
}
