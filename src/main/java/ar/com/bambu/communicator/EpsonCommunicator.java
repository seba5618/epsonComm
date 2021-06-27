package ar.com.bambu.communicator;

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

    public ConfiguracionFechayHora getFechaHora() throws Exception{
        logger.info("Sending Obtener Fecha y Hora");
        EpsonFrameMsg reply = this.sendGenericMsg(new byte[]{0x05,0x02}, new byte[]{0x00,0x00});
        ConfiguracionFechayHora result = new ConfiguracionFechayHora(reply);
        logger.info("Fecha: "+result.getFecha());
        logger.info("Hora: "+result.getHora());
        return result;
    }

    public InformacionDelEquipo getInformacionEquipo() throws Exception{
        logger.info("Sending Obtener Informacion del equipo");
        EpsonFrameMsg reply = this.sendGenericMsg(new byte[]{0x02,0x0A}, new byte[]{0x00,0x00});
        InformacionDelEquipo result = new InformacionDelEquipo(reply);
        logger.info("Nombre de la version: "+result.getVersion());
        logger.info("Nombre del Mecanismo impresor: "+result.getNombreMecanismoImpresion());
        return result;
    }
	
	public InformacionTransaccional getInformacionTransaccional() throws Exception{
        logger.info("Sending Obtener Informacion transaccional equipo");
        EpsonFrameMsg reply = this.sendGenericMsg(new byte[]{0x09,0x15}, new byte[]{0x00,0x00});
        InformacionTransaccional result = new InformacionTransaccional(reply);
        logger.info("desde  "+result.getCintaTestigoDigitalDesde());
        logger.info("hasta  "+result.getCintaTestigoDigitalHasta());
		logger.info("desde  "+result.getDuplicadosADesde());
        logger.info("hasta  "+result.getDuplicadosAHasta());
        return result;
    }


    /**
     *  Inicia la descarga del informe de auditoria por rango de cierre z.
     * @param zInicial
     * @param zFinal
     * @param completa si true, descarga todo lo disponible y avisa que acabe con un 14,
     *                 si false solo hace una llamada y cancela con 16
     * @return
     * @throws Exception
     */
    public AuditoriaJornadasFiscales getAuditoriaDeJornadasFiscalesPorRangoDeCierreZ(int zInicial, int zFinal, boolean completa) throws Exception{
        logger.info("Sending Comenzar Auditoria de Jornadas Fiscales ");
        String start = String.valueOf(zInicial);
        String end = String.valueOf(zFinal);
        //podria ser parametro el segundo parametro y no en duro 0x00x0x02
        this.sendGenericMsg(new byte[]{0x08,0x13}, new byte[]{0x00,0x02}, start.getBytes(ISOUtil.CHARSET), end.getBytes(ISOUtil.CHARSET));
        EpsonFrameMsg reply = this.sendGenericMsg(new byte[]{0x08, 0x14}, new byte[]{0x00,0x00});
        AuditoriaJornadasFiscales result = new AuditoriaJornadasFiscales(reply);
        while (completa && result.isPartialData()){
            reply = this.sendGenericMsg(new byte[]{0x08, 0x14}, new byte[]{0x00,0x00});
            result.update(reply);
        }
        if(completa){
            this.sendGenericMsg(new byte[]{0x08,0x15}, new byte[]{0x00,0x00});
        }else{
            this.sendGenericMsg(new byte[]{0x08,0x16}, new byte[]{0x00,0x00});
        }
        logger.info("XML Data: "+result.getXmlData());
        return result;
    }

    /**
     *  Inicia la descarga del reporte para afip por rango de fechas.
     * @param fechaInicial
     * @param fechaFinal
     * @param extension
     * Bit 0
     * ‘0’ – No marcar descarga.
     * ‘1’ – Marcar descarga.
     * Bit 1-2 ‘00’ – Descarga cinta testigo digital. (CTD)
     * ‘01’ – Descarga duplicados documentos tipo “A”.
     * ‘10’ – Descarga resumen de totales.
     * ‘11’ – Reservado.
     *
     * @todo encapsular extension, tal vez algo como algunas enums en Reporte
     * @return
     * @throws Exception
     */
    public ReporteAfip getReporteAfipPorRangoDeFechas(byte[] extension, String fechaInicial, String fechaFinal) throws Exception{
        logger.info("Sending Obtener Reporte por Rango de Fechas ");
        logger.debug("Fechas recibidas: "+fechaInicial+" y "+fechaFinal);
        fechaInicial = ISOUtil.padleft(fechaInicial, 6, '0');
        fechaFinal = ISOUtil.padleft(fechaFinal, 6, '0');

        EpsonFrameMsg reply = this.sendGenericMsg(new byte[]{0x09,0x51}, extension, fechaInicial.getBytes(ISOUtil.CHARSET), fechaFinal.getBytes(ISOUtil.CHARSET));
        ReporteAfip result = new ReporteAfip(reply);
        reply = this.sendGenericMsg(new byte[]{0x09, 0x70}, new byte[]{0x00,0x00});
        result.update(reply);
        while (result.isPartialData()){
            reply = this.sendGenericMsg(new byte[]{0x09, 0x70}, new byte[]{0x00,0x00});
            result.update(reply);
        }
        this.sendGenericMsg(new byte[]{0x09, 0x71}, new byte[]{0x00,0x00});
        return result;
    }



    public void setChannel(EpsonSerialChannel channel) {
        this.channel = channel;
    }
}
