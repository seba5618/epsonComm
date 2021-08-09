package ar.com.bambu.communicator.reply.hassar;

import ar.com.bambu.jpos.EpsonFrameMsg;
import ar.com.bambu.jpos.HassarFrameMsg;

public class ObtenerRangoFechasPorZetas extends AbstractReply {
    private int fechaZInicial;
    private int fechaZFinal;

    public ObtenerRangoFechasPorZetas(HassarFrameMsg msg)  {
        super(msg);
        this.fechaZInicial = msg.getInteger(4);
        this.fechaZFinal = msg.getInteger(5);
    }

    public int getFechaZInicial() {
        return fechaZInicial;
    }

    public void setFechaZInicial(int fechaZInicial) {
        this.fechaZInicial = fechaZInicial;
    }

    public int getFechaZFinal() {
        return fechaZFinal;
    }

    public void setFechaZFinal(int fechaZFinal) {
        this.fechaZFinal = fechaZFinal;
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
