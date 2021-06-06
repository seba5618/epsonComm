package ar.com.bambu;

import ar.com.bambu.communicator.EpsonCommunicator;
import ar.com.bambu.communicator.reply.*;
//ObtenerConfiguracionFechayHora;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Hello world!
 */
public class App {
    private static final Logger logger = LogManager.getLogger(App.class);


    public static void main(String[] args)  throws Exception{
        EpsonCommunicator epsonCommunicator = new EpsonCommunicator();

		;

        ConfiguracionFechayHora fechaHora = epsonCommunicator.getFechaHora();
		
        logger.info("Aca va la fecha by Seba " + fechaHora.getFecha() + " hora " + fechaHora.getHora());
		
		InformacionDelEquipo InformacionEquipo = epsonCommunicator.getInformacionEquipo();
		logger.info("Aca va la fecha No va seba " + InformacionEquipo.getVersion() + " mecanismo " + InformacionEquipo.getNombreMecanismoImpresion()); 
	
		logger.info("Aca va la fecha by Seba " + fechaHora.getFecha() + " hora " + fechaHora.getHora());
		
		epsonCommunicator.getInformacionTransaccional();
		
    }
}
