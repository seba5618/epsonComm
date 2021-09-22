package ar.com.bambu.afip;


import ar.com.bambu.communicator.HassarCommunicator;
import ar.com.bambu.communicator.reply.hassar.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;

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
        ConsultarCapacidadZetas consultarCapacidadZetas = this.communicator.getConsultarCapacidadZetas();
        ConsultarDatosInicializacion consultarDatosInicializacion = this.communicator.getConsultarDatosInicializacion();
        int ultimaZ = consultarCapacidadZetas.getUltimaZ();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");

        ObtenerRangoFechasPorZetas obtenerRangoFechasPorZetas = this.communicator.getObtenerRangoFechasPorZetas(ultimaZ, ultimaZ);
        String fechaZFinal = obtenerRangoFechasPorZetas.getFechaZFinal();
        Date[] rangoFechaAfip = this.getRangoFechaAfip(fechaZFinal);
        String[] rangoFechaAfipString = new String[]{simpleDateFormat.format(rangoFechaAfip[0]), simpleDateFormat.format(rangoFechaAfip[1])};

        ReporteElectronico reporteElectronico = this.communicator.getObtenerReporteElectronico(rangoFechaAfipString[0], rangoFechaAfipString[1], "P");

        if(reporteElectronico.hayErrorFiscal()){
            ConsultarUltimoError consultarUltimoError = this.communicator.getConsultarUltimoError();
            if(consultarUltimoError.isEmptyRange()){
                logger.error("Error no hay Z en el rango solicitado: "+consultarUltimoError);
                //consultarUltimoError.saveEmptyFile(pos, rango fecha)
            }else if (consultarUltimoError.isReportGapError()){
                logger.warn("Gaps de z detectados, buscando la ultima z bajada: "+consultarUltimoError);
                int ultimaZBajada = consultarUltimoError.getUltimaZBajada();
                obtenerRangoFechasPorZetas = this.communicator.getObtenerRangoFechasPorZetas(ultimaZBajada+1, ultimaZBajada+1);
                fechaZFinal = obtenerRangoFechasPorZetas.getFechaZFinal();
                rangoFechaAfip = this.getRangoFechaAfip(fechaZFinal);
                rangoFechaAfipString = new String[]{simpleDateFormat.format(rangoFechaAfip[0]), simpleDateFormat.format(rangoFechaAfip[1])};
                reporteElectronico = this.communicator.getObtenerReporteElectronico(rangoFechaAfipString[0], rangoFechaAfipString[1], "P");
            }
        }else{
            logger.warn("Revisar, se logro descargar reporte de la Z actual: "+ultimaZ);
        }
        reporteElectronico.saveFile(consultarDatosInicializacion.getNroPos(), rangoFechaAfipString[0],rangoFechaAfipString[1] );
    }

    protected String[] getRangoFechaAfipAnterior(String fecha) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");

        DateTime fechaDateTime = new DateTime( simpleDateFormat.parse(fecha));
        logger.debug("fecha recibida en rango fecha afip: " + fecha);

        String[] result = new String[2];
        Integer dia = fechaDateTime.getDayOfMonth();
        DateTime start = new DateTime(fechaDateTime);
        DateTime end = new DateTime(fechaDateTime);

        if (dia <= 7) {
            start.minusDays(8);
            end.minusDays(8);
            start.withDayOfMonth(22);
            end.dayOfMonth().withMaximumValue();


        } else if (dia <= 14) {
            start.withDayOfMonth(1);
            end.withDayOfMonth(7);

        } else if (dia <= 21) {
            start.withDayOfMonth(8);
            end.withDayOfMonth(14);

        } else {
            start.withDayOfMonth(15);
            end.withDayOfMonth(21);
        }
        result[0] = simpleDateFormat.format(start.toDate());
        result[1] = simpleDateFormat.format(end.toDate());
        return result;

    }

}
