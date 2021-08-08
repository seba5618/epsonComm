package ar.com.bambu;

import ar.com.bambu.afip.AuditoriaAfipSegunFecha;
import ar.com.bambu.communicator.EpsonCommunicator;
import ar.com.bambu.communicator.HassarCommunicator;
import ar.com.bambu.communicator.reply.*;
//ObtenerConfiguracionFechayHora;
import ar.com.bambu.communicator.reply.hassar.ConsultarCapacidadZetas;
import ar.com.bambu.communicator.reply.hassar.ObtenerRangoFechasPorZetas;
import ar.com.bambu.communicator.reply.hassar.ReporteElectronico;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * Hello world!
 */
public class App {



    private static final Logger logger = LogManager.getLogger(App.class);


    public static void main(String[] args)  throws Exception{

        HassarCommunicator hassarCommunicator = new HassarCommunicator();

        logger.info(hassarCommunicator.ConsultarNroPuntoVenta());
       ConsultarCapacidadZetas consultarCapacidadZetas = hassarCommunicator.getConsultarCapacidadZetas();
        logger.info(consultarCapacidadZetas.toString());
        ObtenerRangoFechasPorZetas obtenerRangoFechasPorZetas = hassarCommunicator.getObtenerRangoFechasPorZetas(17,18);
        logger.info(obtenerRangoFechasPorZetas.toString());
       ReporteElectronico reporteElectronico = hassarCommunicator.getObtenerReporteElectronico(210801, 210807, "P");

        reporteElectronico.saveFile(hassarCommunicator.NroPtoVta, "210801", "210807");

    }
}
