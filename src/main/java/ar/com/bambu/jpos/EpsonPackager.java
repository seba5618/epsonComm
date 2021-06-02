package ar.com.bambu.jpos;

import org.jpos.iso.*;
import org.jpos.tlv.packager.IF_FSTBINARY;

public class EpsonPackager extends ISOBasePackager {

    private static final boolean pad = false;

    protected ISOFieldPackager fld[] = {
            null,
            new IF_TCHARB(2048,"tipo mensaje"),
            new IF_TCHARB(2048,"tipo mensaje"),
            new IF_TCHARB(2048,"tipo mensaje"),
            new IF_TCHARB(2048,"tipo mensaje"),

            //aca tendria que venir un componente que me devuelva
    };


    //el 1b es caracter de escape, cada vez que en el medio haya un 02 o un 1c se manda un 1b. Tengo que hacer mi propio epson binary que el pack tenga en cuanta eso y el unpack tenga en cuenta eso.

    public EpsonPackager() {
        super();
        for(int i = 1; i<fld.length; i++){
            ((IF_FSTBINARY)fld[i]).setToken("1C");
        }
        setFieldPackager(fld);
    }

    @Override
    protected boolean emitBitMap() {
        return false;
    }
}
