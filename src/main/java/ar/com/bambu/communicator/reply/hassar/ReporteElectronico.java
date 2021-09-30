package ar.com.bambu.communicator.reply.hassar;

import ar.com.bambu.jpos.HassarFrameMsg;
import ar.com.bambu.utils.Ascii85;

import java.io.*;
import java.util.Properties;

public class ReporteElectronico extends AbstractReply {

    private String data;
    private boolean partialData;

    private final static String FILE_NAME = "hassarAfip";

    public ReporteElectronico(HassarFrameMsg msg) {
        super(msg);
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


    public void saveFile(int nroPuntoVta, String rangoI, String rangoF) throws IOException {
        if (partialData) {
            throw new IOException("File not ready yet to be saved");
        }
        File file = new File(FILE_NAME + "_" + nroPuntoVta + "_" + rangoI + "_a_" + rangoF);
        /*file.delete();
        File debug = new File("debug.txt");
        debug.delete();*/

        //veamo si tenemos una copia del archivo ya creada asi no la grabamos de nuevo
        String sCarpAct = System.getProperty("user.dir");
        File carpeta = new File(sCarpAct);

        String fileBackup = carpeta.toString() +  "/backupAfip/" + FILE_NAME + "_" + nroPuntoVta + "_" + rangoI + "_a_" + rangoF + ".zip";
        File archivo= new File(fileBackup);

        String fileBackup2 = carpeta.toString() +  "/" + FILE_NAME + "_" + nroPuntoVta + "_" + rangoI + "_a_" + rangoF + ".zip";
        File archivo2= new File(fileBackup2);
        try {
            if (archivo.exists() || archivo2.exists()) {
                System.out.println("OJO: YA  existe ESTE ARCHIVO DE REPORTE");
                throw new Exception("YA  existe ESTE ARCHIVO DE REPORTE");
            }

            String asci85 = this.data.replace("<~", "");
            asci85 = asci85.replace("~>", "");
            OutputStream os = new FileOutputStream(FILE_NAME + "_" + nroPuntoVta + "_" + rangoI + "_a_" + rangoF + ".zip");
            os.write(Ascii85.decode(asci85));
            os.close();
            //guardemos el rango en el archivio
        }catch(Exception e){
               System.out.println(e);
        }

    }

    public void saveFileAfip(int nroPuntoVta, String rangoI, String rangoF) throws IOException {
            // Get the file
            File f = new File("fileAfip.properties");

            // Check if the specified file
            // Exists or not
            if (!f.exists()) {
                f.createNewFile();
            }

        try {
            Properties prop = new Properties();
            // Leer el archivo de propiedades db.properties
            /// Guarde las propiedades en el archivo new.properties
            FileOutputStream oFile = new FileOutputStream(f, false);// verdadero significa abierto adicional
            prop.setProperty("PtoVta", String.valueOf(nroPuntoVta));
            prop.setProperty("RANGOI", String.valueOf(rangoI));
            prop.setProperty("RANGOF", String.valueOf(rangoF));
            prop.store(oFile, "");
            oFile.close();

        }catch(Exception e){
            System.out.println(e);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ReporteElectronico{");
        sb.append("data='").append(data).append('\'');
        sb.append(", partialData=").append(partialData);
        sb.append('}');
        return sb.toString();
    }
}
