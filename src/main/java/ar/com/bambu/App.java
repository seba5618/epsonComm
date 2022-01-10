package ar.com.bambu;

import ar.com.bambu.afip.AuditoriaAfipSegunFecha;
import ar.com.bambu.communicator.EpsonCommunicator;
import ar.com.bambu.communicator.reply.*;
//ObtenerConfiguracionFechayHora;
import ar.com.bambu.serial.Fiscal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;


/**
 * Hello world!
 */
public class App {

    public static final int SINGLE_INSTANCE_NETWORK_SOCKET = 43439;
    public static final String SINGLE_INSTANCE_SHARED_KEY = "$$JavaServerHI$$\n";

    private static final Logger logger = LogManager.getLogger(App.class);


    public static void main(String[] args)  throws Exception{

        logger.debug("VERSION APP EXTRACCION EPSON 5.00");
        String fichero = System.getProperty("user.dir") + "\\application.properties";
        Properties p = new Properties();
        p.load(new FileReader(fichero));

        System.out.println("InstanceMan: El puerto esta ocupado. Notificando a la instancia...");
        Thread.sleep(10000);
        JFrame frame = new JFrame();
        frame.add(new JLabel("EXTRACCION DE DATOS FISCALES IMPRESORA PARA AFIP...AGUARDE"));
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);
        frame.setLocation(300, 300);
        frame.setSize(450, 200);
        frame.getContentPane().setBackground(Color.GREEN);

        System.out.println("Comenzando el proceso de extraccion - Mato Jserver");
        KillJavaserver();
        Thread.sleep(2000);

        Fiscal fiscalPrinter = new Fiscal();
        fiscalPrinter.PuertoSerial();

        AuditoriaAfipSegunFecha auditoriaAfipSegunFecha = new AuditoriaAfipSegunFecha(new EpsonCommunicator(p));
        auditoriaAfipSegunFecha.apply();
        frame.dispose();
        System.out.println("Fin aplicacion " );
        System.exit(0);
    }
    private static void KillJavaserver()  throws Exception {

        try {
            Socket clientSocket = new Socket(InetAddress.getLocalHost(), SINGLE_INSTANCE_NETWORK_SOCKET);
            OutputStream out = clientSocket.getOutputStream();
            out.write(SINGLE_INSTANCE_SHARED_KEY.getBytes());
            out.close();
            clientSocket.close();
            //return false;
        } catch (UnknownHostException e1) {
            //INVELLog.NewLog(true, false,"InstanceMan:"+e.getMessage() );
            System.out.println("Excepcion desconocida " + e1.getMessage());
            logger.debug("Excepcion desconocida " + e1.getMessage());
            //   return returnValueOnError;
        } catch (IOException e1) {
            System.out.println("Excepcion IOExcepcion " + e1.getMessage());
            logger.debug("Excepcion IOExcepcion " + e1.getMessage());

        } finally {

        }

    }
}
