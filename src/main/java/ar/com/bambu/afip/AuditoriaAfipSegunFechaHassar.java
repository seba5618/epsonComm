package ar.com.bambu.afip;


import ar.com.bambu.communicator.HassarCommunicator;
import ar.com.bambu.communicator.reply.hassar.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;

public class AuditoriaAfipSegunFechaHassar  extends AuditoriaAfipSegunFecha implements Function{

    private HassarCommunicator communicator ;

    private static final Logger logger = LogManager.getLogger(AuditoriaAfipSegunFechaHassar.class);

    public AuditoriaAfipSegunFechaHassar(HassarCommunicator communicator) {
        this.communicator = communicator;
    }

    /**
     * El reporte de auditoria de AFIP de Hassar es una orquestacion de varios mensajes hacia la impresora.
     *
     * 1- Mandar un getConsultarCapacidadZetas para obtener la ultima z.
     * 2- Obtengo Rango de Fechas por Zeta de la z del punto 1.
     * 3- Empezar a mandar un getReporteElectronico con las fechas del paso anterior(que pasa si la fecha fin es en el futuro? quiero que este paso falle pero que me diga la ultima z bajada).
     * 4- Seguro que el paso 3 falla por saltos de Z bajadas, asi que mando un getConsultarUltimoError y este me dice la ultima z bajada.
     * 5- Repito el paso 2 con la z del punto 4 (+1) y el paso 3 con las fechas obtenidas.
     * 6- Pido un getConsultarDatosInicializacion para obtener numero de pos y con estos datos guardo el archivo zip obtenido en el punto 5.
     */
    @Override
    public void apply() throws Exception{
        Boolean continuarReporte = true;
        //obtengamos la fecha de la primera Z
        ObtenerRangoFechasPorZetas obtenerRangoFechasPorZetas = new ObtenerRangoFechasPorZetas();
        try {
            obtenerRangoFechasPorZetas = this.communicator.getObtenerRangoFechasPorZetas(1, 1);
        }catch ( Exception ex) {
            logger.error("Chau No conectamos al serial FIN DEL PROGRAMA  ");
            System.out.println("Chau No conectamos al serial FIN DEL PROGRAMA  ");
            System.exit(-1);
      }

        String fechaZFinal = obtenerRangoFechasPorZetas.getFechaZFinal();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");
        Date dateZInicial=simpleDateFormat.parse(fechaZFinal);
        logger.info("Z = 1 Fecha  " + dateZInicial);

        ConsultarCapacidadZetas consultarCapacidadZetas = this.communicator.getConsultarCapacidadZetas();
        ConsultarDatosInicializacion consultarDatosInicializacion = this.communicator.getConsultarDatosInicializacion();
        int ultimaZ = consultarCapacidadZetas.getUltimaZ();


        obtenerRangoFechasPorZetas = this.communicator.getObtenerRangoFechasPorZetas(ultimaZ, ultimaZ);
        fechaZFinal = obtenerRangoFechasPorZetas.getFechaZFinal();
        Date dateZFinal=simpleDateFormat.parse(fechaZFinal);

        Date[] rangoFechaAfip = this.getRangoFechaAfip(fechaZFinal,true);
        String[] rangoFechaAfipString = new String[]{simpleDateFormat.format(rangoFechaAfip[0]), simpleDateFormat.format(rangoFechaAfip[1])};

        if( rangoFechaAfip[1].after(new Date())) {
            logger.warn("Ojo fecha final en el futuro "+rangoFechaAfipString[1] +  " vs Hoy " +  simpleDateFormat.format(new Date()));
            continuarReporte = false;
        }
        while ( continuarReporte == true) {

            ReporteElectronico reporteElectronico = this.communicator.getObtenerReporteElectronico(rangoFechaAfipString[0], rangoFechaAfipString[1], "P");

            if (reporteElectronico.hayErrorFiscal()) {
                ConsultarUltimoError consultarUltimoError = this.communicator.getConsultarUltimoError();
                if (consultarUltimoError.isEmptyRange()) {
                    logger.error("Error no hay Z en el rango solicitado: " + consultarUltimoError);
                    //ojo puede no haber Z en ese rango pero faltan rangos aun
                    //consultarUltimoError.saveEmptyFile(pos, rango fecha)
                    if (this.communicator.ControlarFechaFile(rangoFechaAfipString[0],rangoFechaAfipString[1], consultarDatosInicializacion.getNroPos(),dateZInicial)) {
                        rangoFechaAfipString[0]=this.communicator.getfFechaI();
                        rangoFechaAfipString[1]=this.communicator.getfFfechaF();
                    }
                    DateTime ultimaFechaReporte = new DateTime(rangoFechaAfip[1]);
                    ultimaFechaReporte = ultimaFechaReporte.plusDays(1);
                    SimpleDateFormat formater = new SimpleDateFormat("yyMMdd");
                    logger.info("Recorro si faltan rangos {}", formater.format(ultimaFechaReporte.toDate()));
                    rangoFechaAfip = this.getRangoFechaAfip(formater.format(ultimaFechaReporte.toDate()),true);
                    rangoFechaAfipString = new String[]{simpleDateFormat.format(rangoFechaAfip[0]), simpleDateFormat.format(rangoFechaAfip[1])};
                    if( rangoFechaAfip[1].after(new Date())) {
                        logger.warn("Ojo otra vez fecha final en el futuro "+rangoFechaAfipString[1] +  " vs Hoy " +  simpleDateFormat.format(new Date()));
                        continuarReporte = false;
                    }

                } else if (consultarUltimoError.isReportGapError()) {
                    logger.warn("Gaps de z detectados, buscando la ultima z bajada: " + consultarUltimoError);
                    int ultimaZBajada = consultarUltimoError.getUltimaZBajada();
                    obtenerRangoFechasPorZetas = this.communicator.getObtenerRangoFechasPorZetas(ultimaZBajada + 1, ultimaZBajada + 1);
                    fechaZFinal = obtenerRangoFechasPorZetas.getFechaZFinal();
                    rangoFechaAfip = this.getRangoFechaAfip(fechaZFinal, true);
                    logger.info("Primera iteracion de reporte afip hassar para fechas {} y {}", rangoFechaAfip[0], rangoFechaAfip[1]);
                    rangoFechaAfipString = new String[]{simpleDateFormat.format(rangoFechaAfip[0]), simpleDateFormat.format(rangoFechaAfip[1])};
                    reporteElectronico = this.communicator.getObtenerReporteElectronico(rangoFechaAfipString[0], rangoFechaAfipString[1], "P");
                   ///que pasa si hay error fiscal? ejemplo es empty range..debo seguir igual hasta el final
                    if (!reporteElectronico.hayErrorFiscal()) {
                        reporteElectronico.saveFile(consultarDatosInicializacion.getNroPos(), rangoFechaAfipString[0], rangoFechaAfipString[1]);
                    }
                    while(rangoFechaAfip[1].before(new Date())){
               //     while (rangoFechaAfip[1].before(dateZFinal)) {

                        logger.info("Entro al while con {} ", rangoFechaAfip[1]);
                        DateTime ultimaFechaReporte = new DateTime(rangoFechaAfip[1]);
                        ultimaFechaReporte = ultimaFechaReporte.plusDays(1);
                        SimpleDateFormat formater = new SimpleDateFormat("yyMMdd");
                        logger.info("Voy a formatear la fecha {}", formater.format(ultimaFechaReporte.toDate()));
                        rangoFechaAfip = this.getRangoFechaAfip(formater.format(ultimaFechaReporte.toDate()),true);
                        logger.info("Nueva iteracion de reporte afip hassar para fechas {} y {}", rangoFechaAfip[0], rangoFechaAfip[1]);
                        rangoFechaAfipString = new String[]{simpleDateFormat.format(rangoFechaAfip[0]), simpleDateFormat.format(rangoFechaAfip[1])};
                        reporteElectronico = this.communicator.getObtenerReporteElectronico(rangoFechaAfipString[0], rangoFechaAfipString[1], "P");
                        if (!reporteElectronico.hayErrorFiscal()) {
                            reporteElectronico.saveFile(consultarDatosInicializacion.getNroPos(), rangoFechaAfipString[0], rangoFechaAfipString[1]);
                        }
                        //incrementar rango!!!
                        ultimaFechaReporte = new DateTime(rangoFechaAfip[1]);
                        ultimaFechaReporte = ultimaFechaReporte.plusDays(1);
                        formater = new SimpleDateFormat("yyMMdd");
                        logger.info("Recorro si faltan rangos {}", formater.format(ultimaFechaReporte.toDate()));
                        rangoFechaAfip = this.getRangoFechaAfip(formater.format(ultimaFechaReporte.toDate()),true);
                        rangoFechaAfipString = new String[]{simpleDateFormat.format(rangoFechaAfip[0]), simpleDateFormat.format(rangoFechaAfip[1])};

                    }
                    continuarReporte = false;
                }

            } else {
                logger.info("Reporte generado ok sin GAPs para Z: " + ultimaZ);
                reporteElectronico.saveFile(consultarDatosInicializacion.getNroPos(), rangoFechaAfipString[0], rangoFechaAfipString[1]);
                DateTime ultimaFechaReporte = new DateTime(rangoFechaAfip[1]);
                ultimaFechaReporte = ultimaFechaReporte.plusDays(1);
                SimpleDateFormat formater = new SimpleDateFormat("yyMMdd");
                logger.info("Recorro si faltan rangos {}", formater.format(ultimaFechaReporte.toDate()));
                rangoFechaAfip = this.getRangoFechaAfip(formater.format(ultimaFechaReporte.toDate()),true);
                rangoFechaAfipString = new String[]{simpleDateFormat.format(rangoFechaAfip[0]), simpleDateFormat.format(rangoFechaAfip[1])};
                if( rangoFechaAfip[1].after(new Date())) {
                    logger.warn("Ojo otra vez fecha final en el futuro "+rangoFechaAfipString[1] +  " vs Hoy " +  simpleDateFormat.format(new Date()));
                    continuarReporte = false;
                }
            }
        }//end while

    }
    @Override
    protected Date[] getRangoFechaAfip(String fecha, Boolean tipoConsulta) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");
//el nuevo cambio hace que me de el rango anterior  a la feccha de la Z
        logger.debug("fecha recibida en rango fecha afip: " + fecha);
        Date parse = simpleDateFormat.parse(fecha);
        Date[] result = new Date[2];
        Integer dia = Integer.parseInt(fecha.substring(4, 6));
        Calendar start = Calendar.getInstance();
        start.setTime(parse);
        Calendar end = Calendar.getInstance();
        end.setTime(parse);

        if( tipoConsulta == false) {
            if (dia <= 7) {
                start.add(Calendar.MONTH, -1);
                end.add(Calendar.MONTH, -1);
                start.set(Calendar.DAY_OF_MONTH, 22);
                end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));

            } else if (dia <= 14) {
                start.set(Calendar.DAY_OF_MONTH, 1);
                end.set(Calendar.DAY_OF_MONTH, 7);

            } else if (dia <= 21) {
                start.set(Calendar.DAY_OF_MONTH, 8);
                end.set(Calendar.DAY_OF_MONTH, 14);

            } else {
                start.set(Calendar.DAY_OF_MONTH, 15);
                end.set(Calendar.DAY_OF_MONTH, 21);
            }
        } else {

            if (dia <= 7) {
                start.set(Calendar.DAY_OF_MONTH, 1);
                end.set(Calendar.DAY_OF_MONTH, 7);
            } else if (dia <= 14) {
                start.set(Calendar.DAY_OF_MONTH, 8);
                end.set(Calendar.DAY_OF_MONTH, 14);
            } else if (dia <= 21) {
                start.set(Calendar.DAY_OF_MONTH, 15);
                end.set(Calendar.DAY_OF_MONTH, 21);
            } else {
                start.set(Calendar.DAY_OF_MONTH, 22);
                end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));
            }
        }

        result[0] = start.getTime();
        result[1] = end.getTime();
        return result;

    }

}
