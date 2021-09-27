package ar.com.bambu;

import ar.com.bambu.afip.AuditoriaAfipSegunFechaHassar;
import ar.com.bambu.communicator.HassarCommunicator;
import ar.com.bambu.serial.Fiscal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Hello world!
 */
public class App {



    private static final Logger logger = LogManager.getLogger(App.class);


    public static void main(String[] args)  throws Exception{

        logger.debug("VERSION APP EXTRACCION 1.8");

        Fiscal fiscalPrinter = new Fiscal();
        fiscalPrinter.PuertoSerial();

        AuditoriaAfipSegunFechaHassar auditoriaAfipSegunFechaHassar = new AuditoriaAfipSegunFechaHassar(new HassarCommunicator());
        auditoriaAfipSegunFechaHassar.apply();

    }
}
