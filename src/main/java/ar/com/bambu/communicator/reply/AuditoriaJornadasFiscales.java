package ar.com.bambu.communicator.reply;

import ar.com.bambu.jpos.EpsonFrameMsg;

public class AuditoriaJornadasFiscales implements Reply{

    private String xmlData;
    private boolean partialData;
    public AuditoriaJornadasFiscales(EpsonFrameMsg msg) {
        this.xmlData = msg.getString(6);
        this.partialData = msg.getBoolean(7);
    }

    public String getXmlData() {
        return xmlData;
    }

    public void setXmlData(String xmlData) {
        this.xmlData = xmlData;
    }

    public void update(EpsonFrameMsg msg){
        this.xmlData += xmlData;
        this.partialData = msg.getBoolean(7);
    }

    public boolean isPartialData() {
        return partialData;
    }

    public void setPartialData(boolean partialData) {
        this.partialData = partialData;
    }
}
