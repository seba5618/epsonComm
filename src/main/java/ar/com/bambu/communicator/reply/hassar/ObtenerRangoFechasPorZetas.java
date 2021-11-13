package ar.com.bambu.communicator.reply.hassar;

import ar.com.bambu.communicator.HassarCommunicator;
import ar.com.bambu.jpos.EpsonFrameMsg;
import ar.com.bambu.jpos.HassarFrameMsg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ObtenerRangoFechasPorZetas extends AbstractReply {
    private String fechaZInicial;
    private String fechaZFinal;
    private static final Logger logger = LogManager.getLogger(HassarCommunicator.class);
    private boolean fiscalEnEspera = false;

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

    public void ConsultarEstadoImpresoraFecha(HassarFrameMsg msg) throws Exception {
        Byte tipoMensaje =msg.getByte(1);
        Long estadoImpresora =msg.getLongHex(2);

        Integer estadoFiscal =msg.getInteger(3);
        // fiscalEnEspera = false;
         int numberComando = tipoMensaje & 0xff;  // w = 119 el A1=161= ¡ bytes to unsigned byte in an integer.
        if(numberComando == 161) {
            logger.warn("Comando de espera de la fiscal ");
        }

        if (numberComando == 161) {
            fiscalEnEspera = true;
            logger.warn("CRespuesta procesada; 2 campos ");

        } else {
            logger.warn("CRespuesta procesada; 4 campos ");
            if( this.getFiscalEnEspera()) {
                logger.warn("Fiscal Salio de espera ");
                fiscalEnEspera = false;
            }
        }
        logger.debug(" tipo Mensaje.. "+  Integer.toHexString(tipoMensaje ));
        logger.debug(" tipo Mensaje Unsigned.. "+  numberComando);
        logger.debug(" estado fiscal.. "+estadoFiscal);
        logger.debug(" estado impresora " + Long.toHexString(estadoImpresora));

        this.setEstadoFiscal(estadoFiscal) ;
        this.setEstadoImpresora( estadoImpresora.intValue());

    }

    public boolean getFiscalEnEspera() {
        return fiscalEnEspera;
    }

    public void SetDataMsj(HassarFrameMsg msg) {

        this.fechaZInicial = msg.getString(4);
        this.fechaZFinal = msg.getString(5);
        logger.info("Volvio respuesta Fecha por Z " + this.fechaZInicial + " - " +  this.fechaZFinal );
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
