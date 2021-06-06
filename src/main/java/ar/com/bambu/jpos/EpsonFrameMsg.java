package ar.com.bambu.jpos;

import ar.com.bambu.communicator.EpsonCommunicator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;

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
                    s = new String((byte[]) obj, ISOUtil.CHARSET);
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
