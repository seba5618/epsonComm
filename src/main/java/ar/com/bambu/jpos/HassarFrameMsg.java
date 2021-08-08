package ar.com.bambu.jpos;

public class HassarFrameMsg extends EpsonFrameMsg{

    public boolean getBoolean(int fldno){
        return this.getInteger(fldno) == 1;
    }

}
