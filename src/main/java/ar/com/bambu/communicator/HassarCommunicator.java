package ar.com.bambu.communicator;

import ar.com.bambu.communicator.reply.ConfiguracionFechayHora;
import ar.com.bambu.jpos.EpsonFrameMsg;
import ar.com.bambu.jpos.EpsonPackager;

import ar.com.bambu.serial.HassarSerialChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpos.iso.ISOUtil;

public class HassarCommunicator {
    private static final Logger logger = LogManager.getLogger(HassarCommunicator.class);
    HassarSerialChannel channel = new HassarSerialChannel();

    public EpsonFrameMsg sendGenericMsg(byte[] type, byte[]... params) throws Exception {


        EpsonFrameMsg m = new EpsonFrameMsg();
        m.setPackager(new EpsonPackager());
        int index = 1;
        m.set(index++, type);
        for (byte[] param : params) {
            m.set(index++, param);
        }
        logger.debug("Sending Msg Hassar: "+ ISOUtil.hexString(m.pack()));
        byte[] reply = channel.sendMsg(m.pack());
        logger.debug("Got Msg: "+ ISOUtil.hexString(reply));
        EpsonFrameMsg replyMsg = new EpsonFrameMsg();
        replyMsg.setPackager(new EpsonPackager());
        replyMsg.unpack(reply);
        return replyMsg;
    }

    public ConsultarCapacidadZetas getConsultarCapacidadZetas() throws Exception{
        logger.info("Sending getConsultarCapacidadZetas (Hassar)");
        EpsonFrameMsg reply = this.sendGenericMsg(new byte[]{0x05,0x02}, new byte[]{0x00,0x00});
        ConfiguracionFechayHora result = new ConfiguracionFechayHora(reply);
        logger.info("Fecha: "+result.getFecha());
        logger.info("Hora: "+result.getHora());
        return result;
    }
}
