package ar.com.bambu.afip;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AuditoriaAfipSegunFecha {

    private static final Logger logger = LogManager.getLogger(AuditoriaAfipSegunFecha.class);

    protected Date[] getRangoFechaAfip(String fecha, Boolean tipoConsulta) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        logger.debug("fecha recibida en rango fecha afip: " + fecha);
        Date parse = simpleDateFormat.parse(fecha);
        Date[] result = new Date[2];
        Integer dia = Integer.parseInt(fecha.substring(8, 10));
        Calendar start = Calendar.getInstance();
        start.setTime(parse);
        Calendar end = Calendar.getInstance();
        end.setTime(parse);
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
        result[0] = start.getTime();
        result[1] = end.getTime();
        return result;

    }

}
