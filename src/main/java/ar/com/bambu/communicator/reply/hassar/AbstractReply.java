package ar.com.bambu.communicator.reply.hassar;

import ar.com.bambu.communicator.reply.Reply;
import ar.com.bambu.jpos.HassarFrameMsg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractReply implements Reply {
    private int tipoMensaje;
    private long estadoImpresora;
    private int estadoFiscal;

    private static final Logger logger = LogManager.getLogger(AbstractReply.class);


    //estado fiscales impresora
    private static final int ERROR_MEMORIA_FISCAL = 1; // Error memoria fiscal.
    private static final int ERROR_MEMORIA_TRABAJO = 2; // Error memoria de trabajo.
    private static final int ERROR_MEMORIA_AUDITORIA = 3;//Error memoria de auditoría, o cinta testigo digital (CTD).
    private static final int ERROR_GENERAL = 4;// Error general.
    private static final int ERROR_PARAMETRO = 5; //Error en parámetro.
    private static final int ERROR_ESTADO = 6;// Error en estado actual.
    private static final int ERROR_ARITMETICO = 7;//Error aritmético.
    private static final int MEMORIA_FISCAL_LLENA = 8;//Memoria fiscal llena.
    private static final int MEMORIA_FISCAL_CASI_LLENA = 9;//Memoria fiscal casi llena.
    private static final int MEMORIA_FISCAL_INICIALIZADA = 10;// Memoria fiscal inicializada.
    private static final int DOCUMENTO_FISCAL_ABIERTO = 13;//Hay un documento fiscal (DF) abierto.
    private static final int DOCUMENTO_ABIERTO = 14;//Hay un documento abierto.
    private static final int ERROR_EJECUCION = 16;// Error de ejecución.


    public AbstractReply(HassarFrameMsg msg) {
        this.tipoMensaje = msg.getByte(1);
        this.estadoImpresora = msg.getLongHex(2);
        this.estadoFiscal = msg.getInteger(3);
        logger.debug(" estado fiscal " + estadoFiscal);
        logger.debug(" estado impresora " + Long.toHexString(estadoImpresora));
        int numberComando = tipoMensaje & 0xff;
        logger.debug(" comando " + numberComando);
      }

    public AbstractReply() {

    }
    public int getTipoMensaje() {
        int auxTipo = tipoMensaje & 0xff;
        return auxTipo;
    }

    public void setTipoMensaje(int tipoMensaje) {
        this.tipoMensaje = tipoMensaje;
    }

    public long getEstadoImpresora() {
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

    public boolean hayErrorFiscal() {
        int m = estadoFiscal & ERROR_MEMORIA_FISCAL; // m: 00000000000000000000000010000000
        m += estadoFiscal & ERROR_MEMORIA_TRABAJO;
        m += estadoFiscal & ERROR_MEMORIA_AUDITORIA;
        m += estadoFiscal & ERROR_GENERAL;
        m += estadoFiscal & ERROR_ESTADO;
        m += estadoFiscal & ERROR_ARITMETICO;
        m += estadoFiscal & ERROR_EJECUCION;

        if (m > 0)
            return true;
        else
            return false;
    }
}
