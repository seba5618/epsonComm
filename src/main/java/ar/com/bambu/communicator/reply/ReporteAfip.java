package ar.com.bambu.communicator.reply;


import ar.com.bambu.jpos.EpsonFrameMsg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ReporteAfip {
    private static final Logger logger = LogManager.getLogger(ReporteAfip.class);
    private String fileName;
    private String dataHex;
    private boolean partialData;

    public ReporteAfip(EpsonFrameMsg msg) {
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

    public String getDataHex() {
        return dataHex;
    }

    public void setDataHex(String dataHex) {
        this.dataHex = dataHex;
    }

    public void update(EpsonFrameMsg msg) {
        if (this.dataHex == null) {
            this.dataHex = msg.getString(6);
        } else {
            this.dataHex += msg.getString(6);
        }
        this.partialData = msg.getBoolean(7);
    }
}
