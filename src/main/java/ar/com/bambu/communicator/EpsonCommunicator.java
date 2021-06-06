package ar.com.bambu.communicator;

import ar.com.bambu.App;
import ar.com.bambu.communicator.reply.*;
import ar.com.bambu.jpos.EpsonFrameMsg;
import ar.com.bambu.jpos.EpsonPackager;
import ar.com.bambu.serial.EpsonSerialChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpos.iso.ISOUtil;


public class EpsonCommunicator {
    private static final Logger logger = LogManager.getLogger(EpsonCommunicator.class);
    EpsonSerialChannel channel = new EpsonSerialChannel();

    public EpsonFrameMsg sendGenericMsg(byte[] type, byte[]... params) throws Exception {


        EpsonFrameMsg m = new EpsonFrameMsg();
        m.setPackager(new EpsonPackager());
        int index = 1;
        m.set(index++, type);
        for (byte[] param : params) {
            m.set(index++, param);
        }
        logger.debug("Sending Msg: "+ ISOUtil.hexString(m.pack()));
        byte[] reply = channel.sendMsg(m.pack());
        logger.debug("Got Msg: "+ ISOUtil.hexString(reply));
        EpsonFrameMsg replyMsg = new EpsonFrameMsg();
        replyMsg.setPackager(new EpsonPackager());
        replyMsg.unpack(reply);
        return replyMsg;
    }

    public ObtenerConfiguracionFechayHora getFechaHora() throws Exception{
        logger.info("Sending Obtener Fecha y Hora");
        EpsonFrameMsg reply = this.sendGenericMsg(new byte[]{0x05,0x02}, new byte[]{0x00,0x00});
        ObtenerConfiguracionFechayHora result = new ObtenerConfiguracionFechayHora(reply);
        logger.info("Fecha: "+result.getFecha());
        logger.info("Hora: "+result.getHora());
        return result;
    }

    public ObtenerInformacionDelEquipo getInformacionEquipo() throws Exception{
        logger.info("Sending Obtener Informacion del equipo");
        EpsonFrameMsg reply = this.sendGenericMsg(new byte[]{0x02,0x0A}, new byte[]{0x00,0x00});
        ObtenerInformacionDelEquipo result = new ObtenerInformacionDelEquipo(reply);
        logger.info("Nombre de la version: "+result.getVersion());
        logger.info("Nombre del Mecanismo impresor: "+result.getNombreMecanismoImpresion());
        return result;
    }
	
	public ObtenerInformacionTransaccional getInformacionTransaccional() throws Exception{
        logger.info("Sending Obtener Informacion transaccional equipo");
        EpsonFrameMsg reply = this.sendGenericMsg(new byte[]{0x09,0x15}, new byte[]{0x00,0x00});
        ObtenerInformacionTransaccional result = new ObtenerInformacionTransaccional(reply);
        logger.info("desde  "+result.getCintaTestigoDigitalDesde());
        logger.info("hasta  "+result.getCintaTestigoDigitalHasta());
		logger.info("desde  "+result.getDuplicadosADesde());
        logger.info("hasta  "+result.getDuplicadosAHasta());
        return result;
    }

    public void setChannel(EpsonSerialChannel channel) {
        this.channel = channel;
    }
}
