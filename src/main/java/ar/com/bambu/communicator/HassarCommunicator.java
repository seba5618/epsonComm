package ar.com.bambu.communicator;

import ar.com.bambu.communicator.reply.hassar.*;

import ar.com.bambu.jpos.EpsonPackager;

import ar.com.bambu.jpos.HassarFrameMsg;
import ar.com.bambu.serial.HassarSerialChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpos.iso.ISOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class HassarCommunicator {
    private static final Logger logger = LogManager.getLogger(HassarCommunicator.class);
    HassarSerialChannel channel = new HassarSerialChannel();

    public String getfFechaI() {
        return fFechaI;
    }

    public void setfFechaI(String fFechaI) {
        this.fFechaI = fFechaI;
    }

    public String getfFfechaF() {
        return fFfechaF;
    }

    public void setfFfechaF(String fFfechaF) {
        this.fFfechaF = fFfechaF;
    }

    String fFechaI;
    String fFfechaF;


    // CMD_DATA_NOT_FOUND cuando tiro una z que aun no se hizo Dato no encontrado
    //POS_REPORT_GAP  cuando pido un rango de una Z posterior a el rango que debo bajar Jornadas fiscal no consecutiva a la ﷿ltima bajada


    public HassarFrameMsg sendGenericMsg(byte[] type, byte[]... params) throws Exception {
        HassarFrameMsg m = new HassarFrameMsg();
        m.setPackager(new EpsonPackager());
        int index = 1;
        m.set(index++, type);
        for (byte[] param : params) {
            m.set(index++, param);
        }
        logger.debug("Sending Msg Hassar: " + ISOUtil.hexString(m.pack()));
        byte[] reply = channel.sendMsg(m.pack());
        logger.debug("Got Msg: " + ISOUtil.hexString(reply));
        HassarFrameMsg replyMsg = new HassarFrameMsg();
        replyMsg.setPackager(new EpsonPackager());
        replyMsg.unpack(reply);
        return replyMsg;
    }

    protected void setChannel(HassarSerialChannel channel) {
        this.channel = channel;
    }


    public ConsultarDatosInicializacion getConsultarDatosInicializacion() throws Exception {
        logger.info("Sending getConsultarDatosInicializacion(Hassar)");
        HassarFrameMsg reply = this.sendGenericMsg(new byte[]{0x73});
        ConsultarDatosInicializacion response = new ConsultarDatosInicializacion(reply);
        return response;
    }


    public ConsultarUltimoError getConsultarUltimoError() throws Exception {
        logger.info("Sending ConsultarUltimoError(Hassar)");
        HassarFrameMsg reply = this.sendGenericMsg(new byte[]{0x2C});
        ConsultarUltimoError consultarUltimoError = new ConsultarUltimoError(reply);
        logger.debug(consultarUltimoError.toString());
        return consultarUltimoError;
    }

    public ConsultarCapacidadZetas getConsultarCapacidadZetas() throws Exception {
        logger.info("Sending getConsultarCapacidadZetas (Hassar)");
        HassarFrameMsg reply = this.sendGenericMsg(new byte[]{0x37});
        ConsultarCapacidadZetas result = new ConsultarCapacidadZetas(reply);
        logger.debug(result.toString());
        return result;
    }

    public ObtenerRangoFechasPorZetas getObtenerRangoFechasPorZetas(int zInicial, int zFinal) throws Exception {
        logger.info("Sending getObtenerRangoFechasPorZetas (Hassar) Inicial {}  Final {}" ,zInicial ,zFinal );
        String start = String.valueOf(zInicial);
        String end = String.valueOf(zFinal);
        HassarFrameMsg reply = this.sendGenericMsg(new byte[]{(byte) 0xBA}, start.getBytes(ISOUtil.CHARSET), end.getBytes(ISOUtil.CHARSET));
        ObtenerRangoFechasPorZetas result = new ObtenerRangoFechasPorZetas(reply);
        if (result.hayErrorFiscal()) {
            logger.info("Hubo Error Fiscal veamos cual fue");
            //pidamos el ultimo error de prueba porque aca mucho no sirve salvo para co
            getConsultarUltimoError();
        }
        logger.debug(result.toString());
        return result;
    }

    public ReporteElectronico getObtenerReporteElectronico(String fechaInicial, String fechaFinal, String tipoReporte) throws Exception {
        logger.info("Sending getObtenerReporteElectronico (Hassar)");
        logger.debug("fechaInicial: " + fechaInicial);
        logger.debug("fechaFinal: " + fechaFinal);
        logger.debug("tipReporte: " + tipoReporte);

        HassarFrameMsg reply = this.sendGenericMsg(new byte[]{0x76}, fechaInicial.getBytes(ISOUtil.CHARSET), fechaFinal.getBytes(ISOUtil.CHARSET), tipoReporte.getBytes(ISOUtil.CHARSET));


        ReporteElectronico result = new ReporteElectronico(reply);

        while (result.isPartialData()) {
            logger.info("Respuesta parcial de Hassar, llamando comando obtener siguiente bloque");
            reply = this.sendGenericMsg(new byte[]{0x77}, fechaInicial.getBytes(ISOUtil.CHARSET), fechaFinal.getBytes(ISOUtil.CHARSET), tipoReporte.getBytes(ISOUtil.CHARSET));
            result.update(reply);
        }
        return result;
    }

    public Boolean ControlarFechaFile(String fechaInicial, String fechaFinal, int nroPuntoVta, Date fechaPrimeraZ) {
        //aca sacamos el rango del archivo para no tenere que recurrir a toda la logica de empty_range o gap
        File f = new File("fileAfip.properties");

        // Check if the specified file
        // Exists or not
        if (!f.exists()) {
            return false;
        }

        Properties prop = new Properties();
        try {
            // Leer el archivo de propiedades db.properties
            prop.load(new FileInputStream(new File(f.getName())));

            String ptoventaprop = prop.getProperty("PtoVta");
            if (Integer.parseInt(ptoventaprop) != nroPuntoVta) {
                //es un archivo de otro punto de venta
                return false;
            }
            //controlemos fchas locas
            SimpleDateFormat formatter1 = new SimpleDateFormat("yyMMdd");


            fFechaI = prop.getProperty("RANGOI");
            fFfechaF = prop.getProperty("RANGOF");
            Date dateP = formatter1.parse(formatter1.format(fechaPrimeraZ));

            Date dateI = formatter1.parse(fFechaI);
            if (dateI.before(dateP)) {
                //el rango es anterior a la primera Z no puede ser borremos el archivo para qe no joda
                f.delete();
                fFechaI = "";
                fFfechaF = "";
                return false;
            }
            return true;

        } catch (Exception e) {
            logger.error("Error con fecha file " + e.getMessage());
            System.out.println(e);
        }
    return  false;
    }

}




