package ar.com.bambu.communicator;

import ar.com.bambu.communicator.reply.hassar.ConsultarCapacidadZetas;
import ar.com.bambu.communicator.reply.hassar.ObtenerRangoFechasPorZetas;
import ar.com.bambu.communicator.reply.hassar.ReporteElectronico;
import ar.com.bambu.jpos.EpsonFrameMsg;
import ar.com.bambu.jpos.EpsonPackager;

import ar.com.bambu.jpos.HassarFrameMsg;
import ar.com.bambu.serial.HassarSerialChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpos.iso.ISOUtil;

public class HassarCommunicator {
    private static final Logger logger = LogManager.getLogger(HassarCommunicator.class);
    HassarSerialChannel channel = new HassarSerialChannel();
    public int NroPtoVta =0;
    public int statusFiscal =0;
    public int statusIMpresora =0;

    //estado fiscales impresora
    public static int ErrorMemoriaFiscal  = 1; // Error memoria fiscal.
    public static int ErrorMemoriaTrabajo  = 2; // Error memoria de trabajo.
    public static int ErrorMemoriaAuditoria = 3 ;//Error memoria de auditoría, o cinta testigo digital (CTD).
    public static int ErrorGeneral = 4;// Error general.
    public static int ErrorParametro =5; //Error en parámetro.
    public static int  ErrorEstado =6;// Error en estado actual.
    public static int ErrorAritmetico =7 ;//Error aritmético.
    public static int MemoriaFiscalLlena =8 ;//Memoria fiscal llena.
    public static int MemoriaFiscalCasiLlena =9 ;//Memoria fiscal casi llena.
    public static int MemoriaFiscalInicializada =10 ;// Memoria fiscal inicializada.
    public static int DocumentoFiscalAbierto = 13 ;//Hay un documento fiscal (DF) abierto.
    public static int DocumentoAbierto = 14 ;//Hay un documento abierto.
    public static int ErrorEjecucion =16 ;// Error de ejecución.

    // CMD_DATA_NOT_FOUND cuando tiro una z que aun no se hizo Dato no encontrado
    //POS_REPORT_GAP  cuando pido un rango de una Z posterior a el rango que debo bajar Jornadas fiscal no consecutiva a la última bajada


    public HassarFrameMsg sendGenericMsg(byte[] type, byte[]... params) throws Exception {


        HassarFrameMsg m = new HassarFrameMsg();
        m.setPackager(new EpsonPackager());
        int index = 1;
        m.set(index++, type);
        for (byte[] param : params) {
            m.set(index++, param);
        }
        logger.debug("Sending Msg Hassar: "+ ISOUtil.hexString(m.pack()));
        byte[] reply = channel.sendMsg(m.pack());
        logger.debug("Got Msg: "+ ISOUtil.hexString(reply));
        HassarFrameMsg replyMsg = new HassarFrameMsg();
        replyMsg.setPackager(new EpsonPackager());
        replyMsg.unpack(reply);
        statusFiscal = replyMsg.getInteger(3);
        statusIMpresora  = replyMsg.getInteger(2);

        logger.debug(" estado fiscal " + statusFiscal);

        return replyMsg;
    }

    protected void setChannel(HassarSerialChannel channel) {
        this.channel = channel;
    }

    public Boolean HayErrorFiscal(){
        int m = statusFiscal & 1; // m: 00000000000000000000000010000000
        m += statusFiscal & 2;
        m += statusFiscal & 3;
        m += statusFiscal & 4;
        m += statusFiscal & 6;
        m += statusFiscal & 7;
        m += statusFiscal & 16;

        if( m> 0)
            return true;
        else
            return false;
    }

    public int ConsultarNroPuntoVenta() {
        logger.info("Sending ConsultarNroPuntoVenta()");
        try {
            HassarFrameMsg reply = this.sendGenericMsg(new byte[]{0x73});
            NroPtoVta = reply.getInteger(7);

            logger.debug(NroPtoVta + " estado fiscal " + statusFiscal);

            HayErrorFiscal();



            return NroPtoVta;
        } catch(Exception excepcion)
        {
            System.out.println( excepcion.getMessage());
        }
        return  0;
    }

    public void ConsultarUltimoError() {
        logger.info("Sending ConsultarUltimoError()");
        String idUltimoError;
        String descripcion;
        try {
            HassarFrameMsg reply = this.sendGenericMsg(new byte[]{0x2C});
            idUltimoError= reply.getString(4);
            descripcion= reply.getString(5);

            logger.debug(idUltimoError + " descripcion error " + descripcion);




        } catch(Exception excepcion)
        {
            System.out.println( excepcion.getMessage());
        }

    }

    public ConsultarCapacidadZetas getConsultarCapacidadZetas() throws Exception{
        logger.info("Sending getConsultarCapacidadZetas (Hassar)");
        HassarFrameMsg reply = this.sendGenericMsg(new byte[]{0x37});
        ConsultarCapacidadZetas result = new ConsultarCapacidadZetas(reply);
        logger.debug(result.toString());
        return result;
    }

    public ObtenerRangoFechasPorZetas getObtenerRangoFechasPorZetas(int zInicial, int zFinal) throws Exception{
        logger.info("Sending getObtenerRangoFechasPorZetas (Hassar)");
        String start = String.valueOf(zInicial);
        String end = String.valueOf(zFinal);
        HassarFrameMsg reply = this.sendGenericMsg(new byte[]{(byte)0xBA}, start.getBytes(ISOUtil.CHARSET), end.getBytes(ISOUtil.CHARSET));
        ObtenerRangoFechasPorZetas result = new ObtenerRangoFechasPorZetas(reply);
        if(HayErrorFiscal()) {
            //pidamos el ultimo error de prueba porque aca mucho no sirve salvo para co
            ConsultarUltimoError();
        }
        logger.debug(result.toString());
        return result;
    }

    public ReporteElectronico getObtenerReporteElectronico(int fechaInicial, int fechaFinal, String tipoReporte) throws Exception{
        logger.info("Sending getObtenerReporteElectronico (Hassar)");
        logger.debug("fechaInicial: "+fechaInicial);
        logger.debug("fechaFinal: "+fechaFinal);
        logger.debug("tipReporte: "+tipoReporte);
        String start = String.valueOf(fechaInicial);
        String end = String.valueOf(fechaFinal);
        HassarFrameMsg reply = this.sendGenericMsg(new byte[]{0x76}, start.getBytes(ISOUtil.CHARSET), end.getBytes(ISOUtil.CHARSET), tipoReporte.getBytes(ISOUtil.CHARSET));

        HayErrorFiscal();
        ReporteElectronico result = new ReporteElectronico(reply);

        while (result.isPartialData()){
            logger.info("Respuesta parcial de Hassar, llamando comando obtener siguiente bloque");
            reply = this.sendGenericMsg(new byte[]{0x77}, start.getBytes(ISOUtil.CHARSET), end.getBytes(ISOUtil.CHARSET), tipoReporte.getBytes(ISOUtil.CHARSET));
            result.update(reply);
        }
        return result;
    }

}
