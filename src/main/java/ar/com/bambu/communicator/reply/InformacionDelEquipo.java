package ar.com.bambu.communicator.reply;

import ar.com.bambu.jpos.EpsonFrameMsg;

public class InformacionDelEquipo implements Reply {

    private String version;
    private int idPais;
    private int majorVersion;
    private int minorVersion;
    private int build;
    private int idMecanismoImpresion;
    private String nombreMecanismoImpresion;
    private long capacidadMemoriaFiscal;
    private long capacidadTransacciones;
    private long capacidadMemoriaTrabajo;
    private boolean jumperServicioConectado;
    private byte estadoDipsSwitches;


    public InformacionDelEquipo(EpsonFrameMsg msg) {
        this.version=msg.getString(6);
        this.idPais=msg.getInteger(7);
        this.majorVersion=msg.getInteger(8);
        this.minorVersion=msg.getInteger(9);
        this.build=msg.getInteger(10);
        this.idMecanismoImpresion=msg.getInteger(11);
        this.nombreMecanismoImpresion=msg.getString(12);
        this.capacidadMemoriaFiscal=msg.getLong(13);
        this.capacidadTransacciones=msg.getLong(14);
        this.capacidadMemoriaTrabajo=msg.getLong(15);
        this.jumperServicioConectado=msg.getBoolean(16);
        this.estadoDipsSwitches=msg.getByte(17);

    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getIdPais() {
        return idPais;
    }

    public void setIdPais(int idPais) {
        this.idPais = idPais;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    public int getBuild() {
        return build;
    }

    public void setBuild(int build) {
        this.build = build;
    }

    public int getIdMecanismoImpresion() {
        return idMecanismoImpresion;
    }

    public void setIdMecanismoImpresion(int idMecanismoImpresion) {
        this.idMecanismoImpresion = idMecanismoImpresion;
    }

    public String getNombreMecanismoImpresion() {
        return nombreMecanismoImpresion;
    }

    public void setNombreMecanismoImpresion(String nombreMecanismoImpresion) {
        this.nombreMecanismoImpresion = nombreMecanismoImpresion;
    }

    public long getCapacidadMemoriaFiscal() {
        return capacidadMemoriaFiscal;
    }

    public void setCapacidadMemoriaFiscal(long capacidadMemoriaFiscal) {
        this.capacidadMemoriaFiscal = capacidadMemoriaFiscal;
    }

    public long getCapacidadTransacciones() {
        return capacidadTransacciones;
    }

    public void setCapacidadTransacciones(long capacidadTransacciones) {
        this.capacidadTransacciones = capacidadTransacciones;
    }

    public long getCapacidadMemoriaTrabajo() {
        return capacidadMemoriaTrabajo;
    }

    public void setCapacidadMemoriaTrabajo(long capacidadMemoriaTrabajo) {
        this.capacidadMemoriaTrabajo = capacidadMemoriaTrabajo;
    }

    public boolean isJumperServicioConectado() {
        return jumperServicioConectado;
    }

    public void setJumperServicioConectado(boolean jumperServicioConectado) {
        this.jumperServicioConectado = jumperServicioConectado;
    }

    public byte getEstadoDipsSwitches() {
        return estadoDipsSwitches;
    }

    public void setEstadoDipsSwitches(byte estadoDipsSwitches) {
        this.estadoDipsSwitches = estadoDipsSwitches;
    }
}
