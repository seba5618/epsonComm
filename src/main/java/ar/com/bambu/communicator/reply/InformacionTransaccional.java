package ar.com.bambu.communicator.reply;

import ar.com.bambu.communicator.reply.epson.AbstractReply;
import ar.com.bambu.jpos.EpsonFrameMsg;

public class InformacionTransaccional extends AbstractReply implements Reply  {
    private int cintaTestigoDigitalDesde;
	private int cintaTestigoDigitalHasta;
	private int duplicadosADesde;
	private int duplicadosAHasta;
	private int resumenTotalesDesde;
	private int resumenTotalesHasta;
	private int jornadasDescargadasDesde;
	private int jornadasDescargadasHasta;
	private int jornadasBorradasDesde;
	private int jornadasBorradasHasta;
	
	
    public InformacionTransaccional(EpsonFrameMsg msg) {
    	super(msg);
		cintaTestigoDigitalDesde = msg.getInteger(6);
		cintaTestigoDigitalHasta = msg.getInteger(7);
		duplicadosADesde = msg.getInteger(8);
		duplicadosAHasta = msg.getInteger(9);
		resumenTotalesDesde = msg.getInteger(10);
		resumenTotalesHasta = msg.getInteger(11);
		jornadasDescargadasDesde = msg.getInteger(12);
		jornadasDescargadasHasta = msg.getInteger(13);
		jornadasBorradasDesde = msg.getInteger(14);
		jornadasBorradasHasta = msg.getInteger(15);

    }

	public int getCintaTestigoDigitalDesde() {
		return cintaTestigoDigitalDesde;
	}

	public void setCintaTestigoDigitalDesde(int cintaTestigoDigitalDesde) {
		this.cintaTestigoDigitalDesde = cintaTestigoDigitalDesde;
	}

	public int getCintaTestigoDigitalHasta() {
		return cintaTestigoDigitalHasta;
	}

	public void setCintaTestigoDigitalHasta(int cintaTestigoDigitalHasta) {
		this.cintaTestigoDigitalHasta = cintaTestigoDigitalHasta;
	}

	public int getDuplicadosADesde() {
		return duplicadosADesde;
	}

	public void setDuplicadosADesde(int duplicadosADesde) {
		this.duplicadosADesde = duplicadosADesde;
	}

	public int getDuplicadosAHasta() {
		return duplicadosAHasta;
	}

	public void setDuplicadosAHasta(int duplicadosAHasta) {
		this.duplicadosAHasta = duplicadosAHasta;
	}

	public int getResumenTotalesDesde() {
		return resumenTotalesDesde;
	}

	public void setResumenTotalesDesde(int resumenTotalesDesde) {
		this.resumenTotalesDesde = resumenTotalesDesde;
	}

	public int getResumenTotalesHasta() {
		return resumenTotalesHasta;
	}

	public void setResumenTotalesHasta(int resumenTotalesHasta) {
		this.resumenTotalesHasta = resumenTotalesHasta;
	}

	public int getJornadasDescargadasDesde() {
		return jornadasDescargadasDesde;
	}

	public void setJornadasDescargadasDesde(int jornadasDescargadasDesde) {
		this.jornadasDescargadasDesde = jornadasDescargadasDesde;
	}

	public int getJornadasDescargadasHasta() {
		return jornadasDescargadasHasta;
	}

	public void setJornadasDescargadasHasta(int jornadasDescargadasHasta) {
		this.jornadasDescargadasHasta = jornadasDescargadasHasta;
	}

	public int getJornadasBorradasDesde() {
		return jornadasBorradasDesde;
	}

	public void setJornadasBorradasDesde(int jornadasBorradasDesde) {
		this.jornadasBorradasDesde = jornadasBorradasDesde;
	}

	public int getJornadasBorradasHasta() {
		return jornadasBorradasHasta;
	}

	public void setJornadasBorradasHasta(int jornadasBorradasHasta) {
		this.jornadasBorradasHasta = jornadasBorradasHasta;
	}
}
