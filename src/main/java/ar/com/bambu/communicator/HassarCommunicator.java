package ar.com.bambu.communicator;

import ar.com.bambu.communicator.reply.hassar.*;

import ar.com.bambu.jpos.EpsonPackager;

import ar.com.bambu.jpos.HassarFrameMsg;
import ar.com.bambu.serial.HassarSerialChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpos.iso.ISOUtil;

public class HassarCommunicator {
    private static final Logger logger = LogManager.getLogger(HassarCommunicator.class);
    HassarSerialChannel channel = new HassarSerialChannel();




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


    public ConsultarDatosInicializacion getConsultarDatosInicializacion() throws Exception{
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
        logger.info("Sending getObtenerRangoFechasPorZetas (Hassar)");
        String start = String.valueOf(zInicial);
        String end = String.valueOf(zFinal);
        HassarFrameMsg reply = this.sendGenericMsg(new byte[]{(byte) 0xBA}, start.getBytes(ISOUtil.CHARSET), end.getBytes(ISOUtil.CHARSET));
        ObtenerRangoFechasPorZetas result = new ObtenerRangoFechasPorZetas(reply);
        if (result.hayErrorFiscal()) {
            //pidamos el ultimo error de prueba porque aca mucho no sirve salvo para co
            getConsultarUltimoError();
        }
        logger.debug(result.toString());
        return result;
    }

    public ReporteElectronico getObtenerReporteElectronico(int fechaInicial, int fechaFinal, String tipoReporte) throws Exception {
        logger.info("Sending getObtenerReporteElectronico (Hassar)");
        logger.debug("fechaInicial: " + fechaInicial);
        logger.debug("fechaFinal: " + fechaFinal);
        logger.debug("tipReporte: " + tipoReporte);
        String start = String.valueOf(fechaInicial);
        String end = String.valueOf(fechaFinal);
        HassarFrameMsg reply = this.sendGenericMsg(new byte[]{0x76}, start.getBytes(ISOUtil.CHARSET), end.getBytes(ISOUtil.CHARSET), tipoReporte.getBytes(ISOUtil.CHARSET));


        ReporteElectronico result = new ReporteElectronico(reply);

        while (result.isPartialData()) {
            logger.info("Respuesta parcial de Hassar, llamando comando obtener siguiente bloque");
            reply = this.sendGenericMsg(new byte[]{0x77}, start.getBytes(ISOUtil.CHARSET), end.getBytes(ISOUtil.CHARSET), tipoReporte.getBytes(ISOUtil.CHARSET));
            result.update(reply);
        }
        return result;
    }

}
