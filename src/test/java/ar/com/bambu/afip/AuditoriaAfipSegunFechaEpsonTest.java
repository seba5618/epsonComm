package ar.com.bambu.afip;

import ar.com.bambu.communicator.EpsonCommunicator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class AuditoriaAfipSegunFechaEpsonTest {

    AuditoriaAfipSegunFechaEpson toTest ;

    @Mock
    private EpsonCommunicator communicator;


    @Before
    public void initialize(){
        this.toTest = new AuditoriaAfipSegunFechaEpson(communicator);

    }

    @Test
    public void calculoRangoFechaAfip() throws ParseException {
        Date[] rangoFechaAfip = toTest.getRangoFechaAfip("2019-06-28");
    }


}