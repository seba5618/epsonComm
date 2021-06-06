package ar.com.bambu.communicator.reply;


import ar.com.bambu.jpos.EpsonFrameMsg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Reporte {
    private static final Logger logger = LogManager.getLogger(Reporte.class);
    private String fileName;
    private byte[] data;
    private boolean partialData;
    public Reporte(EpsonFrameMsg msg) {
        this.fileName = msg.getString(6);
    }

    public boolean isPartialData() {
        return partialData;
    }

    public void setPartialData(boolean partialData) {
        this.partialData = partialData;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void update(EpsonFrameMsg msg){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            bytes.write(this.data);
            bytes.write(msg.getBytes(6));
            this.data = bytes.toByteArray();
        } catch (IOException e) {
            logger.error(e);
        }
        this.partialData = msg.getBoolean(7);
    }
}
