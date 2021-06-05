package ar.com.bambu.jpos;

import ar.com.bambu.serial.EpsonSerialChannel;
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
            new IF_TCHARB(2048,"tipo mensaje"),
            new IF_TCHARB(2048,"tipo mensaje"),
            new IF_TCHARB(2048,"tipo mensaje"),
            new IF_TCHARB(2048,"tipo mensaje"),
            new IF_TCHARB(2048,"tipo mensaje"),
            new IF_TCHARB(2048,"tipo mensaje"),
            new IF_TCHARB(2048,"tipo mensaje"),
            new IF_TCHARB(2048,"tipo mensaje"),
            new IF_TCHARB(2048,"tipo mensaje"),
            new IF_TCHARB(2048,"tipo mensaje"),
            new IF_TCHARB(2048,"tipo mensaje"),
            new IF_TCHARB(2048,"tipo mensaje"),
            new IF_TCHARB(2048,"tipo mensaje"),
            new IF_TCHARB(2048,"tipo mensaje"),
            new IF_TCHARB(2048,"tipo mensaje"),
            new IF_TCHARB(2048,"tipo mensaje"),
            new IF_TCHARB(2048,"tipo mensaje"),
            new IF_TCHARB(2048,"tipo mensaje"),
            new IF_TCHARB(2048,"tipo mensaje"),
            new IF_TCHARB(2048,"tipo mensaje"),
            new IF_TCHARB(2048,"tipo mensaje"),
    };

    public EpsonPackager() {
        super();
        for(int i = 1; i<fld.length; i++){
            ((IF_FSTBINARY)fld[i]).setToken(Integer.toHexString(EpsonSerialChannel.DEL));
        }
        setFieldPackager(fld);
    }

    @Override
    protected boolean emitBitMap() {
        return false;
    }
}
