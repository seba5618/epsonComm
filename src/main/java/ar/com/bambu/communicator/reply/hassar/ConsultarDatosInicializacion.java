package ar.com.bambu.communicator.reply.hassar;

import ar.com.bambu.jpos.HassarFrameMsg;

public class ConsultarDatosInicializacion extends AbstractReply{
    private String cuit;
    private String razonSocial;
    private String registro;
    private int nroPos;
    private String inicioActividades;
    private String iibb;
    private String iva;
    private String fechaInicioActividades;

    public ConsultarDatosInicializacion(HassarFrameMsg msg) {
        super(msg);
        this.cuit=msg.getString(4);
        this.razonSocial=msg.getString(5);
        this.registro=msg.getString(6);
        this.nroPos=msg.getInteger(7);
        this.inicioActividades=msg.getString(8);
        this.iibb=msg.getString(9);
        this.iva=msg.getString(10);
        this.fechaInicioActividades=msg.getString(11);
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getRegistro() {
        return registro;
    }

    public void setRegistro(String registro) {
        this.registro = registro;
    }

    public int getNroPos() {
        return nroPos;
    }

    public void setNroPos(int nroPos) {
        this.nroPos = nroPos;
    }

    public String getInicioActividades() {
        return inicioActividades;
    }

    public void setInicioActividades(String inicioActividades) {
        this.inicioActividades = inicioActividades;
    }

    public String getIibb() {
        return iibb;
    }

    public void setIibb(String iibb) {
        this.iibb = iibb;
    }

    public String getIva() {
        return iva;
    }

    public void setIva(String iva) {
        this.iva = iva;
    }

    public String getFechaInicioActividades() {
        return fechaInicioActividades;
    }

    public void setFechaInicioActividades(String fechaInicioActividades) {
        this.fechaInicioActividades = fechaInicioActividades;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ConsultarDatosInicializacion{");
        sb.append("cuit='").append(cuit).append('\'');
        sb.append(", razonSocial='").append(razonSocial).append('\'');
        sb.append(", registro='").append(registro).append('\'');
        sb.append(", nroPos='").append(nroPos).append('\'');
        sb.append(", inicioActividades='").append(inicioActividades).append('\'');
        sb.append(", iibb='").append(iibb).append('\'');
        sb.append(", iva='").append(iva).append('\'');
        sb.append(", fechaInicioActividades='").append(fechaInicioActividades).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
