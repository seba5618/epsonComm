package ar.com.bambu.afip;

import ar.com.bambu.communicator.EpsonCommunicator;
import ar.com.bambu.serial.EpsonSerialChannel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AuditoriaAfipSegunFechaTest {

    AuditoriaAfipSegunFecha toTest ;

    @Mock
    private EpsonCommunicator communicator;


    @Before
    public void initialize(){
        this.toTest = new AuditoriaAfipSegunFecha(communicator);

    }

    @Test
    public void calculoRangoFechaAfip() throws ParseException {
        String[] rangoFechaAfip = toTest.getRangoFechaAfip("2019-06-28");

    }


}