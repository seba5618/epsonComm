package ar.com.bambu.afip;

import ar.com.bambu.communicator.EpsonCommunicator;
import ar.com.bambu.communicator.reply.AuditoriaJornadasFiscales;
import ar.com.bambu.communicator.reply.ConfiguracionFechayHora;
import ar.com.bambu.communicator.reply.InformacionTransaccional;
import ar.com.bambu.communicator.reply.ReporteAfip;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AuditoriaAfipSegunFecha implements Function{

    private EpsonCommunicator communicator ;

    private static final Logger logger = LogManager.getLogger(AuditoriaAfipSegunFecha.class);
    private final static String FILE_NAME="EpsonAfip";

    public AuditoriaAfipSegunFecha(EpsonCommunicator communicator) {
        this.communicator = communicator;
    }

    /**
     * El reporte de auditoria de AFIP es una orquestacion de varios mensajes hacia la impresora.
     *
     * 1- Obtener la Informacion transaccional 9-15
     * 2- Obtengo jornadasDescargadasHasta del mensaje anterior, le sumo 1 (¿puedo usar lo que me viene de "desde" de los tres campos anteriores?)
     * 3- Mandar un 813 con la z del paso 2.
     * 4- El 813 no lo necesito completo, pero si los campos de fecha desde y fecha hasta
     * 5- Empezar a mandar un reporte afip con las fechas del paso anterior, se mandan 3 9 con distintos parametros.
     * 6- Los 3 reporte afip del paso anterior genero archivos con el nombre y el contenido que nos respondio la impresora.
     */
    @Override
    public void apply() {

        try {
            InformacionTransaccional informacionTransaccional = this.communicator.getInformacionTransaccional();
            int jornadasDescargadasHasta = informacionTransaccional.getJornadasDescargadasHasta();
            AuditoriaJornadasFiscales auditoriaDeJornadasFiscalesPorRangoDeCierreZ =
                    this.communicator.getAuditoriaDeJornadasFiscalesPorRangoDeCierreZ(jornadasDescargadasHasta + 1, jornadasDescargadasHasta+1, false);
            String fechaDesde = this.getFechaDesde(auditoriaDeJornadasFiscalesPorRangoDeCierreZ);
            this.getFechaHasta(auditoriaDeJornadasFiscalesPorRangoDeCierreZ);
        //aca no se que hacer si las fechas de los z son distintas, pero suponiendo son iguales obtener el rango de fechas segun esta fecha
            // 1 al 7 del mes, o 8 al 14 del mes, o 15 al 21 del mes o 22 a fin de mes.

            //los dias a presentar segun rango 1-7 desde el 8 al 12 presentar
            //los dias a presentar segun rango 8-14 desde el 15 al 19 presentar
            //los dias a presentar segun rango 15-21 desde el 22 al 26 presentar
            //los dias a presentar segun rango 22-fin mes desde el 8 al 29,30,31 presentar 1 al 5

            //que dia es la fiscal?? asi sabemos si tenemos que ejecutasr
            String fechaHora = this.communicator.getFechaHora().getFecha(); ////Ddmmyy (ej: “300102)
            int dia = Integer.parseInt(fechaHora.substring(0,2));

            logger.info("Nro dia de la fiscal " + dia  );


            String[] rangoFechaAfip = this.getRangoFechaAfip(fechaDesde);

            //todo
            //falta llamar 3 veces al metodo de reporte afip y guardar los archivos que nos devuelve.

            ReporteAfip reporteAfipPorRangoDeFechas1 = communicator.getReporteAfipPorRangoDeFechas(new byte[]{0x00, 0x04}, rangoFechaAfip[0], rangoFechaAfip[1]);

            reporteAfipPorRangoDeFechas1.saveFile();

            ReporteAfip reporteAfipPorRangoDeFechas2 = communicator.getReporteAfipPorRangoDeFechas(new byte[]{0x00, 0x00}, rangoFechaAfip[0], rangoFechaAfip[1]);
            reporteAfipPorRangoDeFechas2.saveFile();

            ReporteAfip reporteAfipPorRangoDeFechas3 = communicator.getReporteAfipPorRangoDeFechas(new byte[]{0x00, 0x02}, rangoFechaAfip[0], rangoFechaAfip[1]);
            reporteAfipPorRangoDeFechas3.saveFile();
// OutputStream os = new FileOutputStream(FILE_NAME + "_"+nroPuntoVta + "_"+rangoI +"_a_"+ rangoF+ ".zip");

            int nroPuntoVta = this.communicator.ConsultarNroPuntoVenta();

            logger.info("Nombre del Zip " + FILE_NAME + "_"+nroPuntoVta + "_" +rangoFechaAfip[0] +"_a_"+ rangoFechaAfip[1]+ ".zip" );

            List<String> srcFiles = Arrays.asList(reporteAfipPorRangoDeFechas1.getFileName(), reporteAfipPorRangoDeFechas2.getFileName(),reporteAfipPorRangoDeFechas3.getFileName());
            FileOutputStream fos = new FileOutputStream(FILE_NAME + "_"+nroPuntoVta  + "_"+ rangoFechaAfip[0] +"_a_"+ rangoFechaAfip[1]+ ".zip");
            ZipOutputStream zipOut = new ZipOutputStream(fos);

            for (String srcFile : srcFiles) {
                File fileToZip = new File(srcFile);
                FileInputStream fis = new FileInputStream(fileToZip);
                ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
                fis.close();
                if(fileToZip.exists() && !fileToZip.isDirectory()) {
                    logger.info("Borrando " + fileToZip.getName() );
                    fileToZip.delete();
                }


            }
            zipOut.close();
            fos.close();

        } catch (Exception e) {
            logger.error("rompio afip", e);
        }
    }

    //horrible, deberia leer el xml entero y pedir por xpath pero bue
    private String getFechaDesde(AuditoriaJornadasFiscales auditoriaDeJornadasFiscalesPorRangoDeCierreZ) {
        String xml = auditoriaDeJornadasFiscalesPorRangoDeCierreZ.getXmlData();
        int i = xml.lastIndexOf("<fechaZDesde>");
        String result = new String(xml.toCharArray(), i+13, 10);
        return result;
    }

    //horrible, deberia leer el xml entero y pedir por xpath pero bue
    private String getFechaHasta(AuditoriaJornadasFiscales auditoriaDeJornadasFiscalesPorRangoDeCierreZ) {
        String xml = auditoriaDeJornadasFiscalesPorRangoDeCierreZ.getXmlData();
        int i = xml.lastIndexOf("<fechaZHasta>");
        String result = new String(xml.toCharArray(), i+13, 10);
        return result;
    }

    protected String[] getRangoFechaAfip(String fecha) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat salida = new SimpleDateFormat("ddMMyy");
        logger.debug("fecha recibida en rango fecha afip: "+fecha);
        Date parse = simpleDateFormat.parse(fecha);
        String[] result = new String[2];
        Integer dia = Integer.parseInt(fecha.substring(8,10));
        Calendar start = Calendar.getInstance();
        start.setTime(parse);
        Calendar end = Calendar.getInstance();
        end.setTime(parse);
        if(dia <= 7){
            start.set(Calendar.DAY_OF_MONTH,1);
            end.set(Calendar.DAY_OF_MONTH,7);

        }else if ( dia <= 14){
            start.set(Calendar.DAY_OF_MONTH,8);
            end.set(Calendar.DAY_OF_MONTH,14);
        }else if(dia <= 21){
            start.set(Calendar.DAY_OF_MONTH,15);
            end.set(Calendar.DAY_OF_MONTH,21);
        }else {
            start.set(Calendar.DAY_OF_MONTH,22);
            end.set(Calendar.DAY_OF_MONTH,end.getActualMaximum(Calendar.DAY_OF_MONTH));
        }
        result[0]=salida.format(start.getTime());
        result[1]=salida.format(end.getTime());
        return result;

    }
}
