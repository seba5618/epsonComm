package ar.com.bambu.communicator.reply.hassar;

import ar.com.bambu.communicator.reply.Reply;
import ar.com.bambu.jpos.HassarFrameMsg;

public abstract  class AbstractReply implements Reply {
    private int tipoMensaje;
    private int estadoImpresora;
    private int estadoFiscal;



    public AbstractReply(HassarFrameMsg msg) {
        this.tipoMensaje = msg.getByte(1);
        this.estadoImpresora = msg.getInteger(2);
        this.estadoFiscal = msg.getInteger(3);
    }

    public int getTipoMensaje() {
        return tipoMensaje;
    }

    public void setTipoMensaje(int tipoMensaje) {
        this.tipoMensaje = tipoMensaje;
    }

    public int getEstadoImpresora() {
        return estadoImpresora;
    }

    public void setEstadoImpresora(int estadoImpresora) {
        this.estadoImpresora = estadoImpresora;
    }

    public int getEstadoFiscal() {
        return estadoFiscal;
    }

    public void setEstadoFiscal(int estadoFiscal) {
        this.estadoFiscal = estadoFiscal;
    }
}
