package ar.com.bambu;

import ar.com.bambu.afip.AuditoriaAfipSegunFechaHassar;
import ar.com.bambu.communicator.HassarCommunicator;
import ar.com.bambu.communicator.reply.hassar.ReporteElectronico;
import ar.com.bambu.serial.Fiscal;
import ar.com.bambu.utils.Ascii85;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;


/**
 * Hello world!
 */
public class App {



    private static final Logger logger = LogManager.getLogger(App.class);


    public static void main(String[] args)  throws Exception{

        logger.debug("VERSION APP EXTRACCION 3.62-beta");
     /*   JFrame frame = new JFrame();
        frame.add(new JLabel("EXTRACCION DE DATOS FISCALES IMPRESORA PARA AFIP...AGUARDE"));
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);
        frame.setLocation(400,300);
        frame.setSize(450,200);
        frame.getContentPane().setBackground(Color.YELLOW);
*/
/*        saveFileTest();
        System.exit(0);
*/
        Fiscal fiscalPrinter = new Fiscal();
        fiscalPrinter.PuertoSerial();
/*
        logger.warn ("PRIMER RANGO ");
        HassarCommunicator communicator = new HassarCommunicator();
        ReporteElectronico reporteElectronico = communicator.getObtenerReporteElectronico("210908", "210914", "P");
        reporteElectronico.saveFile(13,"210908","210914");
        reporteElectronico.deleteContent();
        logger.warn ("SEGUNDO RANGO ");

        reporteElectronico = communicator.getObtenerReporteElectronico("210915", "210921", "P");
        reporteElectronico.saveFile(13,"210915","210921");
        reporteElectronico.deleteContent();

        logger.warn ("TERCER  RANGO ");
        reporteElectronico = communicator.getObtenerReporteElectronico("210922", "210930", "P");
        reporteElectronico.saveFile(13,"210922","210930");
        reporteElectronico.deleteContent();

        logger.warn ("4TO RANGO RANGO ");
        reporteElectronico = communicator.getObtenerReporteElectronico("211001", "211007", "P");
        reporteElectronico.saveFile(13,"211001","211007");
        reporteElectronico.deleteContent();
        System.exit(0);
*/


        AuditoriaAfipSegunFechaHassar auditoriaAfipSegunFechaHassar = new AuditoriaAfipSegunFechaHassar(new HassarCommunicator());
        auditoriaAfipSegunFechaHassar.apply();
  //      frame.dispose();
    }

    public static void saveFileTest() throws IOException {
        String sCarpAct = System.getProperty("user.dir");
        File carpeta = new File(sCarpAct+ "/Ascii85.txt");


        try {
            BufferedReader reader = new BufferedReader(new FileReader(carpeta.getPath()));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            String ls = System.getProperty("line.separator");
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
// delete the last new line separator
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            reader.close();

            String content = stringBuilder.toString();

            logger.info ("VOY a grabar " + content.length() +  " bytes" );
            String asci85 = content.replace("<~", "");
            asci85 = asci85.replace("~>", "");
            logger.info ("Asci85 " + asci85.length() +  " bytes" );

            OutputStream os = new FileOutputStream("ouput_test.zip");
            os.write(Ascii85.decode(asci85));
            os.close();

            //  File f = new File("ouput_" + rangoI + "_a_" + rangoF + ".txt");
            //f.delete();


        }catch(Exception e){
            System.out.println(e);
        }

    }
}
