package ar.com.bambu;

import ar.com.bambu.afip.AuditoriaAfipSegunFecha;
import ar.com.bambu.communicator.EpsonCommunicator;
import ar.com.bambu.communicator.reply.*;
//ObtenerConfiguracionFechayHora;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileReader;
import java.util.Properties;


/**
 * Hello world!
 */
public class App {



    private static final Logger logger = LogManager.getLogger(App.class);


    public static void main(String[] args)  throws Exception{

        String fichero = System.getProperty("user.dir") + "\\application.properties";
        Properties p = new Properties();
        p.load(new FileReader(fichero));

        AuditoriaAfipSegunFecha auditoriaAfipSegunFecha = new AuditoriaAfipSegunFecha(new EpsonCommunicator(p));
        auditoriaAfipSegunFecha.apply();
    }
}
