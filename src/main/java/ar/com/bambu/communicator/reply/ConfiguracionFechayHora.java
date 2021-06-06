package ar.com.bambu.communicator.reply;

import ar.com.bambu.jpos.EpsonFrameMsg;

public class ConfiguracionFechayHora implements Reply{
    private String fecha;
    private String hora;

    public ConfiguracionFechayHora(EpsonFrameMsg msg) {
        this.fecha = msg.getString(6);
        this.hora = msg.getString(7);
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}
