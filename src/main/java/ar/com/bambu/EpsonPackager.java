package ar.com.bambu;

import org.jpos.iso.*;

public class EpsonPackager extends ISOBasePackager {

    private static final boolean pad = false;

    protected ISOFieldPackager fld[] = {
            new IF_TCHAR(2048,"tipo mensaje", "|"),
            new IF_TCHAR(2048,"tipo mensaje", "|"),
            new IF_TCHAR(2048,"tipo mensaje", "|"),
            new IF_TCHAR(2048,"tipo mensaje", "|"),
            new IF_TCHAR(2048,"tipo mensaje", "|"),
            new IF_TCHAR(2048,"tipo mensaje", "|"),
            new IF_TCHAR(2048,"tipo mensaje", "|"),
            new IF_TCHAR(2048,"tipo mensaje", "|"),
            new IF_TCHAR(2048,"tipo mensaje", "|"),

            //aca tendria que venir un componente que me devuelva
    };

    public EpsonPackager() {
        super();
        setFieldPackager(fld);
    }

    @Override
    protected boolean emitBitMap() {
        return false;
    }
}
