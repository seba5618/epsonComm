package ar.com.bambu;

import ar.com.bambu.afip.AuditoriaAfipSegunFechaHassar;
import ar.com.bambu.communicator.HassarCommunicator;
import ar.com.bambu.serial.Fiscal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.net.URL;


/**
 * Hello world!
 */
public class App {



    private static final Logger logger = LogManager.getLogger(App.class);


    public static void main(String[] args)  throws Exception{

        logger.debug("VERSION APP EXTRACCION 2.2");
        JFrame frame = new JFrame();
        frame.add(new JLabel("EXTRACCION DE DATOS FISCALES IMPRESORA PARA AFIP...AGUARDE"));
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);
        frame.setLocation(400,300);
        frame.setSize(450,200);
        frame.getContentPane().setBackground(Color.YELLOW);


        Fiscal fiscalPrinter = new Fiscal();
        fiscalPrinter.PuertoSerial();

        AuditoriaAfipSegunFechaHassar auditoriaAfipSegunFechaHassar = new AuditoriaAfipSegunFechaHassar(new HassarCommunicator());
        auditoriaAfipSegunFechaHassar.apply();
        frame.dispose();
    }
}
