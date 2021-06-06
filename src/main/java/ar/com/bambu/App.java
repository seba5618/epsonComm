package ar.com.bambu;

import ar.com.bambu.communicator.EpsonCommunicator;
import ar.com.bambu.communicator.reply.*;
//ObtenerConfiguracionFechayHora;
import ar.com.bambu.jpos.EpsonFrameMsg;
import ar.com.bambu.jpos.EpsonPackager;
import ar.com.bambu.serial.EpsonSerialChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;

/**
 * Hello world!
 */
public class App {
    private static final Logger logger = LogManager.getLogger(App.class);


    public static void main(String[] args)  throws Exception{
        EpsonCommunicator epsonCommunicator = new EpsonCommunicator();

		;

        ObtenerConfiguracionFechayHora fechaHora = epsonCommunicator.getFechaHora();
		
        logger.info("Aca va la fecha by Seba " + fechaHora.getFecha() + " hora " + fechaHora.getHora());
		
		ObtenerInformacionDelEquipo InformacionEquipo = epsonCommunicator.getInformacionEquipo();
		logger.info("Aca va la fecha No va seba " + InformacionEquipo.getVersion() + " mecanismo " + InformacionEquipo.getNombreMecanismoImpresion()); 
	
		logger.info("Aca va la fecha by Seba " + fechaHora.getFecha() + " hora " + fechaHora.getHora());
		
		epsonCommunicator.getInformacionTransaccional();
		
    }
}
