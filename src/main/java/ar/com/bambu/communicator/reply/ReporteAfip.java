package ar.com.bambu.communicator.reply;


import ar.com.bambu.jpos.EpsonFrameMsg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class ReporteAfip implements Reply {
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

    public void saveFile() throws IOException{
        if(partialData){
            throw new IOException("File not ready yet to be saved");
        }
        File file = new File(fileName);
        file.delete();
        OutputStream os = new FileOutputStream(fileName);
        PrintWriter output = new PrintWriter(new OutputStreamWriter(os, "US-ASCII"));
        output.write(dataHex);
        output.close();
    }
}
