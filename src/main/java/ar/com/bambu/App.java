package ar.com.bambu;

import ar.com.bambu.jpos.EpsonPackager;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;

/**
 * Hello world!
 */
public class App {

    private static byte STX = 0x02;
    private static byte ETX = 0x03;

    public static void main(String[] args)  throws Exception{
        System.out.println("Hello World!");

        ISOMsg m = new ISOMsg();
        m.setPackager(new EpsonPackager());

        m.set(1,new byte[]{1});
        m.set(2,new byte[]{2});
        m.set(3,new byte[]{3});

        byte[] pack = m.pack();
        System.out.println(ISOUtil.byte2hex(pack));
        ISOMsg reply = new ISOMsg();


        reply.setPackager(new EpsonPackager());
        reply.unpack(pack);


        System.out.println(ISOUtil.byte2hex(reply.pack()));





    }
}
