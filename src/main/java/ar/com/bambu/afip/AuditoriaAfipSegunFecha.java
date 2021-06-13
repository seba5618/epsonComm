package ar.com.bambu.afip;

import ar.com.bambu.communicator.EpsonCommunicator;
import ar.com.bambu.communicator.reply.AuditoriaJornadasFiscales;
import ar.com.bambu.communicator.reply.InformacionTransaccional;
import ar.com.bambu.communicator.reply.ReporteAfip;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AuditoriaAfipSegunFecha implements Function{

    private EpsonCommunicator communicator ;

    private static final Logger logger = LogManager.getLogger(AuditoriaAfipSegunFecha.class);

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
            String[] rangoFechaAfip = this.getRangoFechaAfip(fechaDesde);

            //todo
            //falta llamar 3 veces al metodo de reporte afip y guardar los archivos que nos devuelve.

            ReporteAfip reporteAfipPorRangoDeFechas = communicator.getReporteAfipPorRangoDeFechas(new byte[]{0x00, 0x04}, rangoFechaAfip[0], rangoFechaAfip[1]);
            reporteAfipPorRangoDeFechas.saveFile();

            reporteAfipPorRangoDeFechas = communicator.getReporteAfipPorRangoDeFechas(new byte[]{0x00, 0x00}, rangoFechaAfip[0], rangoFechaAfip[1]);
            reporteAfipPorRangoDeFechas.saveFile();

            reporteAfipPorRangoDeFechas = communicator.getReporteAfipPorRangoDeFechas(new byte[]{0x00, 0x02}, rangoFechaAfip[0], rangoFechaAfip[1]);
            reporteAfipPorRangoDeFechas.saveFile();


        } catch (Exception e) {
            logger.error("rompio afip", e);
        }
    }

    //horrible, deberia leer el xml entero y pedir por xpath pero bue
    private String getFechaDesde(AuditoriaJornadasFiscales auditoriaDeJornadasFiscalesPorRangoDeCierreZ) {
        String xml = auditoriaDeJornadasFiscalesPorRangoDeCierreZ.getXmlData();
        int i = xml.lastIndexOf("<fechaZDesde>");
        String result = new String(xml.toCharArray(), i, 10);
        return result;
    }

    //horrible, deberia leer el xml entero y pedir por xpath pero bue
    private String getFechaHasta(AuditoriaJornadasFiscales auditoriaDeJornadasFiscalesPorRangoDeCierreZ) {
        String xml = auditoriaDeJornadasFiscalesPorRangoDeCierreZ.getXmlData();
        int i = xml.lastIndexOf("<fechaZHasta>");
        String result = new String(xml.toCharArray(), i, 10);
        return result;
    }

    private String[] getRangoFechaAfip(String fecha) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("DDMMYYYY");
        Date parse = simpleDateFormat.parse(fecha);
        String[] result = new String[2];
        String sufijo = fecha.substring(2);
        Integer dia = Integer.parseInt(fecha.substring(0,2));

        if(dia <= 7){
            result[0]="01"+sufijo;
            result[1]="07"+sufijo;
        }else if ( dia <= 14){
            result[0]="08"+sufijo;
            result[1]="14"+sufijo;
        }else if(dia <= 21){
            result[0]="15"+sufijo;
            result[1]="21"+sufijo;
        }else {
            Calendar instance = Calendar.getInstance();
            instance.setTime(parse);
            result[0]="22"+sufijo;
            int day = instance.getActualMaximum(Calendar.DAY_OF_MONTH);
            result[1]=day+sufijo;
        }
        return result;

    }
}
