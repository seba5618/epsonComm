package ar.com.bambu.jpos;

import ar.com.bambu.communicator.EpsonCommunicator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;


public class EpsonFrameMsg extends ISOMsg {

    private static final Logger logger = LogManager.getLogger(EpsonFrameMsg.class);

    @Override
    public String getString(int fldno) {
        String s = null;
        if (this.hasField(fldno)) {
            try {
                Object obj = this.getValue(fldno);
                if (obj instanceof String) {
                    s = (String)obj;
                } else if (obj instanceof byte[]) {
                    s = new String((byte[]) obj, Charset.forName("US-ASCII"));
                }
            } catch (ISOException var4) {
                logger.error(var4);
            }
        }
        return s;
    }

    public int getInteger(int fldno){
        return Integer.parseInt(this.getString(fldno));
    }

    public long getLong(int fldno){
        return Long.parseLong(this.getString(fldno));
    }

    public boolean getBoolean(int fldno){
        return "S".equalsIgnoreCase(this.getString(fldno));
    }

    public byte getByte(int fldno) {
        Object value = null;
        try {
            value = this.getValue(fldno);
        } catch (ISOException e) {
            logger.error(e);
        }
        return ((byte[])value)[0];
    }
    public String getLongHex(int fldno){
            String s = null;
            if (this.hasField(fldno)) {
                try {
                    int[] unsigned = new int[2]; //TRUCHADO deberia ser el leng
                    Object obj = this.getValue(fldno);
                    if (obj instanceof String) {
                        s = (String)obj;
                    } else if (obj instanceof byte[]) {
                        if(fldno ==1 || fldno ==2) {

                            for (int i = 0; i <  ((byte[]) obj).length; i++) {
                                unsigned[i] = ((byte[]) obj)[i] & 0xFF;
                            }
                        }

                        StringBuilder builder = new StringBuilder(unsigned.length * 2);
                        for (int b : unsigned) {
                            builder.append(byteToUnsignedHex(b));
                        }
                        s=  builder.toString();

                        //s = new String((byte[]) obj, Charset.forName("US-ASCII"));
                      //  s = Arrays.toString(unsigned).replaceAll("\\[|\\]|,|\\s", "");
                        //       s = new String((unsigned[]) obj, Charset.forName("US-ASCII"));
                    }
                } catch (ISOException var4) {
                    logger.error(var4);
                }
            }
            return s;
        }

    private String  byteToUnsignedHex(int i) {
        String hex = Integer.toHexString( i);
        while(hex.length() < 2){
            hex = "0" + hex;
        }
        return hex;
    }

    public byte[] getBytes(int fldno){
        byte[] result = null;
        try {
            result =  (byte[]) this.getValue(fldno);
        } catch (Exception e) {
          logger.error(e);
        }
        return result;
    }
}
