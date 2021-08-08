package ar.com.bambu.communicator.reply.hassar;

import ar.com.bambu.jpos.HassarFrameMsg;
import ar.com.bambu.utils.Ascii85;

import java.io.*;

public class ReporteElectronico {

    private String data;
    private boolean partialData;

    private final static String FILE_NAME="hassarAfip";

    public ReporteElectronico(HassarFrameMsg msg) {
        this.partialData = msg.getBoolean(4);
        this.data = msg.getString(5);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isPartialData() {
        return partialData;
    }

    public void setPartialData(boolean partialData) {
        this.partialData = partialData;
    }

    public void update(HassarFrameMsg msg) {
        this.partialData = msg.getBoolean(4);
        if (this.data == null) {
            this.data = msg.getString(5);
        } else {
            this.data += msg.getString(5);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ReporteElectronico{");
        sb.append(", partialData=").append(partialData);
        sb.append('}');
        return sb.toString();
    }

    public void saveFile( int nroPuntoVta, String rangoI, String rangoF) throws IOException {
        if(partialData){
            throw new IOException("File not ready yet to be saved");
        }
        File file = new File(FILE_NAME + "_"+nroPuntoVta + "_"+rangoI +"_a_"+ rangoF);
        file.delete();
        File debug = new File("debug.txt");
        debug.delete();

        String asci85 = this.data.replace("<~","");
        asci85 = asci85.replace("~>","");
        OutputStream os = new FileOutputStream(FILE_NAME + "_"+nroPuntoVta + "_"+rangoI +"_a_"+ rangoF+ ".zip");
        os.write(Ascii85.decode(asci85));
        os.close();
    }
}
