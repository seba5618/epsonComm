package ar.com.bambu.jpos;

import org.jpos.iso.*;
import org.jpos.tlv.packager.IF_FSTBINARY;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import static ar.com.bambu.serial.EpsonSerialChannel.*;

public class IF_TCHARB extends IF_FSTBINARY {

    private byte terminator = -1;
    private String token;

    public IF_TCHARB(int len, String description) {
        super(len, description);
    }

    @Override
    public void setToken(String token) {
        super.setToken(token);
        if (token != null && token.length() == 2) {
            this.token = token;
            this.terminator = (byte)Integer.parseInt(token, 16);
        } else {
            throw new IllegalArgumentException("IF_FSTBINARY needs a HEX token of 2 characters.");
        }
    }

    @Override
    public byte[] pack(ISOComponent c) throws ISOException {
        byte[] s = c.getBytes();
        int len;
        if (s == null) {
            return new byte[]{};
        } else if ((len = s.length) > this.getLength()) {
            throw new ISOException("Invalid length " + len + " packing IF_FSTBINARY field " + c.getKey() + " max length=" + this.getLength());
        } else {

            ByteArrayOutputStream result = new ByteArrayOutputStream();

            for (int i = 0; i < s.length; i++) {
                if (s[i] == STX || s[i] == ESC || s[i] == ETX) {
                    result.write(ESC);
                }
                result.write(s[i]);
            }
            result.write(this.terminator);
            return result.toByteArray();
        }
    }

    @Override
    public ISOComponent createComponent(int fieldNumber) {
        return new ISOBinaryField(fieldNumber);
    }

    @Override
    public int unpack(ISOComponent c, byte[] b, int offset) throws ISOException {
        if (!(c instanceof ISOBinaryField)) {
            throw new ISOException(c.getClass().getName() + " is not an ISOField");
        } else if(offset < b.length){
            int length = -1;

            for(int i = 0; i < this.getMaxPackedLength(); ++i) {
                byte dataByte = b[offset + i];
                if (dataByte == this.terminator) {
                    length = i;
                    break;
                }
            }

            if (length >= 0) {
                byte[] value = new byte[length];
                System.arraycopy(b, offset, value, 0, length);
                ByteArrayOutputStream result = new ByteArrayOutputStream();

                for (int i = 0; i < value.length; i++) {
                    if (value[i] == ESC && i<value.length-1) {
                        result.write(value[++i]);
                    }else{
                        result.write(value[i]);
                    }

                }
                c.setValue(result.toByteArray());
                return length + 1;
            } else {
                throw new ISOException("Terminating Backslash does not exist");
            }
        } else {
            return 0;
        }
    }

    private byte[] byteBufferToBytes(ByteBuffer buffer) {
        int dataLength = buffer.position();
        byte[] bytes = new byte[dataLength];
        buffer.position(0);
        buffer.get(bytes);
        buffer.position(dataLength);
        return bytes;
    }

    @Override
    public void unpack(ISOComponent c, InputStream in) throws IOException, ISOException {
        if (!(c instanceof ISOBinaryField)) {
            throw new ISOException(c.getClass().getName() + " is not an ISOField");
        } else {
            boolean endFound = false;
            if (in.markSupported()) {
                in.mark(this.getMaxPackedLength());
            }

            ByteBuffer buf = ByteBuffer.allocate(this.getMaxPackedLength());

            for(int i = 0; i < this.getMaxPackedLength() && in.available() > 0; ++i) {
                byte dataByte = (byte)in.read();
                if (dataByte == this.terminator) {
                    endFound = true;
                    break;
                }
                buf.put(dataByte);
            }

            if (endFound) {
                byte[] data = this.byteBufferToBytes(buf);
                c.setValue(data);
            } else {
                if (in.markSupported()) {
                    in.reset();
                }
                throw new ISOException("Terminating Backslash does not exist");
            }
        }
    }
}
