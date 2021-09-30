package ar.com.bambu.communicator.reply.hassar;

import ar.com.bambu.jpos.HassarFrameMsg;

public class ConsultarUltimoError extends AbstractReply {

    private static final String POS_REPORT_GAP = "POS_REPORT_GAP";
    private static final String AUDIT_EMPTY_FISCAL_DAY_RANGE = "AUDIT_EMPTY_FISCAL_DAY_RANGE";

    private String ultimoError;
    private int parametroError;
    private String descripcion;
    private String contexto;
    private String nombreParametro;

    public ConsultarUltimoError(HassarFrameMsg msg) {
        super(msg);
        this.ultimoError = msg.getString(4);
        this.parametroError = msg.getInteger(5);

        this.descripcion = msg.getString(6);
        this.contexto = msg.getString(7);
        this.nombreParametro = msg.getString(8);
    }

    public boolean isReportGapError() {
        return POS_REPORT_GAP.equals(this.ultimoError);
    }

    public boolean isEmptyRange() {
        return AUDIT_EMPTY_FISCAL_DAY_RANGE.equals(this.ultimoError);
    }

    public int getUltimaZBajada() {
        int result = -1;
        if (this.getContexto() == null || !this.isReportGapError()) {
            return result;
        }
        int indexBegin = this.getContexto().indexOf(':');
        int indexEnd = this.getContexto().indexOf('(');

        if (indexBegin > 0 && indexEnd > 0) {
            String z = this.getContexto().substring(indexBegin+1, indexEnd).trim();
            result = Integer.parseInt(z);
        }
        if( result == -1) {
            if (this.getContexto().toUpperCase().contains("PRIMERA BAJADA")) {
                //no encontro la z, asi que supongo que debe ser la 1era z
                result =0;
            }

        }
        return result;
    }

    public String getUltimoError() {
        return ultimoError;
    }

    public void setUltimoError(String ultimoError) {
        this.ultimoError = ultimoError;
    }

    public int getParametroError() {
        return parametroError;
    }

    public void setParametroError(int parametroError) {
        this.parametroError = parametroError;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getContexto() {
        return contexto;
    }

    public void setContexto(String contexto) {
        this.contexto = contexto;
    }

    public String getNombreParametro() {
        return nombreParametro;
    }

    public void setNombreParametro(String nombreParametro) {
        this.nombreParametro = nombreParametro;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ConsultarUltimoError{");
        sb.append("ultimoError='").append(ultimoError).append('\'');
        sb.append(", parametroError=").append(parametroError);
        sb.append(", descripcion='").append(descripcion).append('\'');
        sb.append(", contexto='").append(contexto).append('\'');
        sb.append(", nombreParametro='").append(nombreParametro).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
