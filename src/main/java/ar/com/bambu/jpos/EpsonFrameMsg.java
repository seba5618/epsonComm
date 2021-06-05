package ar.com.bambu.jpos;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;

public class EpsonFrameMsg extends ISOMsg {

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
                return null;
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
            e.printStackTrace();
        }
        return ((byte[])value)[0];
    }
}
