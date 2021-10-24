package ar.com.bambu.serial;

import ar.com.bambu.App;
import ar.com.bambu.utils.Parser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Hashtable;
import javax.xml.parsers.*;

public class Fiscal {

    private static final Logger logger = LogManager.getLogger(App.class);
    public static String MarcaImpresora ;
    public static String LogicalName ;
    public static String TipoSO;
    public static Boolean IsLinux = false;
    public static final int SINGLE_INSTANCE_NETWORK_SOCKET = 43439;
    public static final String SINGLE_INSTANCE_SHARED_KEY = "$$JavaServerHI$$\n";
    private static Parser parse;

    public static String getPortName() {
        return portName;
    }

    public static void setPortName(String portName) {
        Fiscal.portName = portName;
    }

    public static String portName;

    public static int getBaudRate() {
        return baudRate;
    }



    public void PuertoSerial() {
        if (VeamosLaImpresora("T900") == 1) {
            MarcaImpresora = "EPSON";
            LogicalName = "T900";
        } else {
            MarcaImpresora = "HASAR";
            if (VeamosLaImpresora("Hasar-PT250") == 1) {
                LogicalName = "Hasar-PT250";
            } else if (VeamosLaImpresora("Hasar-PT1000") == 1) {
                LogicalName = "Hasar-PT1000";
            }
        }
        VeamosElPuerto2(LogicalName);
    }
    private static int VeamosLaImpresora(String logicalName) {
        System.out.println("Abriendo peripheral.XML");
        openXML("peripheral.xml", logicalName);
        try {
            String Enable = parse.getData(1);
            if (Enable.compareTo("YES") == 0) {
                Enable = parse.getData(0);
                System.out.println("eModelo activo es : " + Enable);
                logger.debug("eModelo activo es : " + Enable);
                return 1;

            }
        } catch ( ArrayIndexOutOfBoundsException e) {
            System.out.println(logicalName + " no esta activa ");
        }
        return 0;
    }

    private static void VeamosElPuerto2(String logicalName) {
        //System.out.println("Abriendo XML");
        openXML("jpos.xml", logicalName);

/*        printerName = parse.getData(0);
        OutputTimeout = Integer.valueOf(parse.getData(1));*/
        portName = parse.getData(2);
/*        stopBits = Integer.valueOf(parse.getData(5));
        dataBits = Integer.valueOf(parse.getData(7));
        parity = Integer.valueOf(parse.getData(9));
        flowControl = parse.getData(10);*/
        baudRate = Integer.valueOf(parse.getData(11));

/*        firmwareVersion = parse.getData(12);
        decimales = Integer.valueOf(parse.getData(13));*/
        System.out.println("El puerto com es : " +   portName );
        logger.debug("El puerto com es : " +   portName );
        System.out.println("El Baudio es : " +   baudRate );
        logger.debug("El baudio  es : " +   baudRate );
    }
    public static void setBaudRate(int baudRate) {
        Fiscal.baudRate = baudRate;
    }

    public static int baudRate;

    public static Hashtable openXML(String xml_File, String logicalName) {
        Hashtable result = new Hashtable();
        parse = new Parser(xml_File, logicalName);
        return result;
    }

}


