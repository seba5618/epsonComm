package ar.com.bambu.communicator.reply;

import ar.com.bambu.jpos.EpsonFrameMsg;

public class InformacionTransaccional implements Reply{
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

   public int getCintaTestigoDigitalHasta() {
        return cintaTestigoDigitalHasta;
    }
	
	public int getDuplicadosADesde() {
        return duplicadosADesde;
    }

   public int getDuplicadosAHasta() {
        return duplicadosAHasta;
    }

    
}
