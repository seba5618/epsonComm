package ar.com.bambu.communicator.reply.hassar;

import ar.com.bambu.jpos.EpsonFrameMsg;
import ar.com.bambu.jpos.HassarFrameMsg;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ObtenerRangoFechasPorZetas extends AbstractReply {
    private String fechaZInicial;
    private String fechaZFinal;

    public ObtenerRangoFechasPorZetas(HassarFrameMsg msg)  {
        super(msg);
        this.fechaZInicial = msg.getString(4);
        this.fechaZFinal = msg.getString(5);
    }
    public ObtenerRangoFechasPorZetas()  {
        super();
    }

    public String getFechaZInicial() {
        return fechaZInicial;
    }

    public void setFechaZInicial(String fechaZInicial) {
        this.fechaZInicial = fechaZInicial;
    }

    public String getFechaZFinal() {
        return fechaZFinal;
    }

    public void setFechaZFinal(String fechaZFinal) {
        this.fechaZFinal = fechaZFinal;
    }

    public boolean areBothDatesInThePast() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");

        DateTime end = new DateTime(simpleDateFormat.parse(this.fechaZFinal));

        if(end.hourOfDay().withMaximumValue().minuteOfHour().withMaximumValue().secondOfMinute().withMaximumValue().millisOfSecond().withMaximumValue().isAfterNow()){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ObtenerRangoFechasPorZetas{");
        sb.append("fechaZInicial=").append(fechaZInicial);
        sb.append(", fechaZFinal=").append(fechaZFinal);
        sb.append('}');
        return sb.toString();
    }
}
