package ar.com.bambu.communicator.reply.hassar;

import ar.com.bambu.jpos.EpsonFrameMsg;

public class ConsultarCapacidadZetas {
    private int cantidadDeZetasRemanentes;
    private int ultimaZ;
    private int ultimaZBajada;
    private int ultimaZetaBorrable;

    public ConsultarCapacidadZetas(EpsonFrameMsg msg) {
        this.cantidadDeZetasRemanentes = msg.getInteger(4);
        this.ultimaZ = msg.getInteger(5);
        this.ultimaZBajada = msg.getInteger(6);
        this.ultimaZetaBorrable = msg.getInteger(7);
    }

    public int getCantidadDeZetasRemanentes() {
        return cantidadDeZetasRemanentes;
    }

    public void setCantidadDeZetasRemanentes(int cantidadDeZetasRemanentes) {
        this.cantidadDeZetasRemanentes = cantidadDeZetasRemanentes;
    }

    public int getUltimaZ() {
        return ultimaZ;
    }

    public void setUltimaZ(int ultimaZ) {
        this.ultimaZ = ultimaZ;
    }

    public int getUltimaZBajada() {
        return ultimaZBajada;
    }

    public void setUltimaZBajada(int ultimaZBajada) {
        this.ultimaZBajada = ultimaZBajada;
    }

    public int getUltimaZetaBorrable() {
        return ultimaZetaBorrable;
    }

    public void setUltimaZetaBorrable(int ultimaZetaBorrable) {
        this.ultimaZetaBorrable = ultimaZetaBorrable;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ConsultarCapacidadZetas{");
        sb.append("cantidadDeZetasRemanentes=").append(cantidadDeZetasRemanentes);
        sb.append(", ultimaZ=").append(ultimaZ);
        sb.append(", ultimaZBajada=").append(ultimaZBajada);
        sb.append(", ultimaZetaBorrable=").append(ultimaZetaBorrable);
        sb.append('}');
        return sb.toString();
    }


}
