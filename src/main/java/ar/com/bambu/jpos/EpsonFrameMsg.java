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
}
