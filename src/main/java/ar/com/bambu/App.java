package ar.com.bambu;

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

        m.set(0,"1");
        m.set(1,"2");

        byte[] pack = m.pack();

        ISOMsg reply = new ISOMsg();
        reply.getString(3)
        reply.setPackager(new EpsonPackager());
        reply.unpack(pack);



        System.out.println(ISOUtil.byte2hex(pack));

    }
}
