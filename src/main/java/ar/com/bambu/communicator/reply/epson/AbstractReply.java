package ar.com.bambu.communicator.reply.epson;

import ar.com.bambu.communicator.reply.Reply;
import ar.com.bambu.jpos.EpsonFrameMsg;
import ar.com.bambu.jpos.HassarFrameMsg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;

public abstract class AbstractReply implements Reply {
    private int tipoMensaje;
    private String estadoImpresora;
    private String estadoFiscal;

    private static final Logger logger = LogManager.getLogger(AbstractReply.class);


    //estado fiscales impresora
    private static final int ERROR_MEMORIA_FISCAL = 1; // Error memoria fiscal.
    private static final int ERROR_MEMORIA_TRABAJO = 2; // Error memoria de trabajo.
    private static final int ERROR_MEMORIA_AUDITORIA = 4;//Error memoria de auditoría, o cinta testigo digital (CTD).
    //private static final int ERROR_GENERAL = 4;// Error general.
    //private static final int ERROR_PARAMETRO = 5; //Error en parámetro.
    //private static final int ERROR_ESTADO = 6;// Error en estado actual.
    //private static final int ERROR_ARITMETICO = 7;//Error aritmético.
    private static final int COMANDO_INVALIDO = 8;//COMANDO_INVALIDO
    private static final int MEMORIA_FISCAL_CASI_LLENA = 128;//Memoria fiscal casi llena.
    private static final int MEMORIA_FISCAL_INICIALIZADA = 10;// Memoria fiscal inicializada.
    private static final int DOCUMENTO_FISCAL_ABIERTO = 13;//Hay un documento fiscal (DF) abierto.
    private static final int DOCUMENTO_ABIERTO = 14;//Hay un documento abierto.
    private static final int ERROR_EJECUCION = 16;// Error de ejecución.
    private static final int ERROR_EJECUCION2 = 32;// Error de ejecución.


    public AbstractReply(EpsonFrameMsg msg) {
    /*    this.tipoMensaje = msg.getByte(1);
        int numberComando = tipoMensaje & 0xff;
        logger.debug(" comando " + numberComando);*/
        this.estadoImpresora = msg.getLongHex(1);
        logger.debug(" estado impresora " + estadoImpresora);
        System.out.println("Eestado impresora : " +   estadoImpresora );
         this.estadoFiscal =  msg.getLongHex(2);
        logger.debug(" estado fiscal " + estadoFiscal);
        System.out.println("Eestado fiscal : " +   estadoFiscal );
        hayErrorFiscal();


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

    public String getEstadoImpresora() {
        return estadoImpresora;
    }

    public void setEstadoImpresora(String estadoImpresora) {
        this.estadoImpresora = estadoImpresora;
    }

    public String getEstadoFiscal() {
        return estadoFiscal;
    }

    public void setEstadoFiscal(String estadoFiscal) {
        this.estadoFiscal = estadoFiscal;
    }

    public boolean hayErrorFiscal() {
       // System.out.println(Long.parseLong(estadoFiscal, 16));
        int estadofiscal2 = (int) Long.parseLong(estadoFiscal, 16);
        int m = estadofiscal2 & ERROR_MEMORIA_FISCAL; // m: 00000000000000000000000010000000
        m += estadofiscal2 & ERROR_MEMORIA_TRABAJO;
        m += estadofiscal2 & ERROR_MEMORIA_AUDITORIA;
        m += estadofiscal2 & COMANDO_INVALIDO;
      /*  m += estadofiscal2 & ERROR_ESTADO;
        m += estadofiscal2 & ERROR_ARITMETICO;*/
        m += estadofiscal2 & ERROR_EJECUCION;
        m += estadofiscal2 & ERROR_EJECUCION2;
        if (m > 0)
            return true;
        else
            return false;
    }
    public static String convertStringToHex(String str) {

        StringBuffer hex = new StringBuffer();

        // loop chars one by one
        for (char temp : str.toCharArray()) {

            // convert char to int, for char `a` decimal 97
            int decimal = (int) temp;

            // convert int to hex, for decimal 97 hex 61
            hex.append(Integer.toHexString(decimal));
        }

        return hex.toString();

    }
}

