package ar.com.bambu.communicator.reply.hassar;

import ar.com.bambu.afip.AuditoriaAfipSegunFechaHassar;
import ar.com.bambu.jpos.HassarFrameMsg;
import ar.com.bambu.utils.Ascii85;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class ReporteElectronico extends AbstractReply {

    private String data;
    private boolean partialData;


    private boolean fiscalEnEspera = false;

    private final static String FILE_NAME = "hassarAfip";
    private static final Logger logger = LogManager.getLogger(AuditoriaAfipSegunFechaHassar.class);

    public ReporteElectronico(HassarFrameMsg msg) {
        super(msg);
        fiscalEnEspera = false;
        if( this.getTipoMensaje() != 161 ) {
            this.partialData = msg.getBoolean(4);
            this.data = msg.getString(5);
            logger.debug("Inicio lectura de reporte en 76 " );
        }

    }

    public void SetDataMsj(HassarFrameMsg msg) {

        this.partialData = msg.getBoolean(4);
        this.data = msg.getString(5);
        logger.info("encabezado 76 es " + this.data );
        logger.info("partial data 76 es " + this.partialData  );

    }

    public boolean getFiscalEnEspera() {
        return fiscalEnEspera;
    }

    public void setFiscalEnEspera(boolean fiscalEnEspera) {
        this.fiscalEnEspera = fiscalEnEspera;
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

    public void update(HassarFrameMsg msg, boolean inicioReporte) throws Exception {
        //String dataAux=null;
        ConsultarEstadoImpresora(msg);

        if( inicioReporte ==false) {
            this.partialData = msg.getBoolean(4);
            if (fiscalEnEspera == false) {
                if (this.data == null) {
                    logger.info("Inicio acuular data");
                    this.data = msg.getString(5);
                } else {
                    logger.info("continuo acumular data");
                    this.data += msg.getString(5);
                }
            }
        }

    }

    public void ConsultarEstadoImpresora(HassarFrameMsg msg) throws Exception {
        Byte tipoMensaje =msg.getByte(1);
        Long estadoImpresora =msg.getLongHex(2);

        Integer estadoFiscal =msg.getInteger(3);
       // fiscalEnEspera = false;
        this.partialData = msg.getBoolean(4);

        int numberComando = tipoMensaje & 0xff;  // w = 119 el A1=161= ¡ bytes to unsigned byte in an integer.
        if(numberComando == 161) {
            logger.warn("Comando de espera de la fiscal ");
        }

        if (numberComando == 161) {
            fiscalEnEspera = true;
            logger.warn("CRespuesta procesada; 2 campos ");

        } else {
            logger.warn("CRespuesta procesada; 4 campos ");
            if( this.getFiscalEnEspera()) {
                logger.warn("Fiscal Salio de espera ");
                fiscalEnEspera = false;
            }
        }
        logger.debug(" tipo Mensaje.. "+  Integer.toHexString(tipoMensaje ));
        logger.debug(" tipo Mensaje Unsigned.. "+  numberComando);
        logger.debug(" estado fiscal.. "+estadoFiscal);
        logger.debug(" estado impresora " + Long.toHexString(estadoImpresora));

        this.setEstadoFiscal(estadoFiscal) ;
        this.setEstadoImpresora( estadoImpresora.intValue());
        //comandos soportados
        if( numberComando != 119  && numberComando != 161 && numberComando != 118) {
            logger.warn("COMANDO EXTRANIO OJO SE DESINCRONIZO. SALIR "  );
            throw new Exception("Comando desincronizado salir ");
        }
    }

    public void saveFile(int nroPuntoVta, String rangoI, String rangoF) throws IOException {
        if (partialData) {
            logger.warn ("File not ready yet to be saved" );
            throw new IOException("File not ready yet to be saved");
        }
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
                logger.warn ("OJO: YA  existe ESTE ARCHIVO DE REPORTE " +fileBackup2 );
                throw new Exception("YA  existe ESTE ARCHIVO DE REPORTE");
            }

            logger.info ("VOY a grabar " + this.data.length() +  " bytes" );
            String asci85 = this.data.replace("<~", "");
            asci85 = asci85.replace("~>", "");
            logger.info ("Asci85 " + asci85.length() +  " bytes" );

     /*       OutputStream os2 = new FileOutputStream("ouput_" + rangoI + "_a_" + rangoF + ".txt");
            os2.write(this.data.getBytes());
            os2.close();*/
            OutputStream os = new FileOutputStream(FILE_NAME + "_" + nroPuntoVta + "_" + rangoI + "_a_" + rangoF + ".zip");
            os.write(Ascii85.decode(asci85));
            os.close();
            logger.info ("SE Genero el siguiente archivo para la afip " +fileBackup2 );

          //  File f = new File("ouput_" + rangoI + "_a_" + rangoF + ".txt");
            //f.delete();


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

    public void deleteContent()  {
       this.setData("");
       this.setFiscalEnEspera(false);
       this.setPartialData(false);
       this.setEstadoFiscal(0);
       this.setEstadoImpresora(0);
       this.setTipoMensaje(0);
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
