package ar.com.bambu.communicator.reply;


import ar.com.bambu.jpos.EpsonFrameMsg;
import org.junit.Test;

import java.io.File;

public class ReporteAfipTest {

    private ReporteAfip toTest = new ReporteAfip(new EpsonFrameMsg());

    @Test
    public void testSaveFile() throws Exception{
        File toDelete = new File("delete");
        toDelete.delete();
        toTest.setDataHex("04050890");
        toTest.setFileName("delete");
        toTest.saveFile();
    }

}