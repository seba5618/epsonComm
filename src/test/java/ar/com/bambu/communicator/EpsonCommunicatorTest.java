package ar.com.bambu.communicator;


import ar.com.bambu.communicator.reply.ObtenerConfiguracionFechayHora;
import ar.com.bambu.communicator.reply.ObtenerInformacionDelEquipo;
import ar.com.bambu.serial.EpsonSerialChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jpos.iso.ISOUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EpsonCommunicatorTest {

    private static final Logger logger = LogManager.getLogger(EpsonCommunicatorTest.class);

    private EpsonCommunicator toTest = new EpsonCommunicator();

    @Mock
    private EpsonSerialChannel channel;

    @Before
    public void initialize(){


        this.toTest.setChannel(this.channel);
    }

    @Test
    public void testGetFecha() throws  Exception{
        logger.info("Test: Fecha Y Hora");

        String trama = "00001C" +
                "C0001C" +
                "1C" +
                "00001C" +
                "1C" +
                "3239303532311C" +
                "3231353134381C";
        byte[] bytes = ISOUtil.hex2byte(trama);
        Mockito.when(channel.sendMsg(Mockito.any(byte[].class))).thenReturn(bytes);
        ObtenerConfiguracionFechayHora fechaHora = this.toTest.getFechaHora();
        Assert.assertEquals("Fecha deberia ser: "+290521+". Pero es: "+fechaHora.getFecha(), "290521", fechaHora.getFecha() );
        Assert.assertEquals("Hora deberia ser: "+215148+". Pero es: "+fechaHora.getHora(), "215148", fechaHora.getHora()) ;
    }
    @Test
    public void testObtenerInformacionDelEquipo() throws  Exception{
        logger.info("Test: Obtener Informacion del Equipo");

        String trama = "00001C" +
                "C0001C" +
                "1C" +
                "00001C" +
                "1C" +
                "43657265731C" +
                "35341C" +
                "311C" +
                "301C" +
                "311C" +
                "34301C" +
                "746D743830301C" +
                "383338383630381C" +
                "33343230393739341C" +
                "3133313037321C" +
                "4E1C" +
                "001C";
        byte[] bytes = ISOUtil.hex2byte(trama);
        Mockito.when(channel.sendMsg(Mockito.any(byte[].class))).thenReturn(bytes);
        ObtenerInformacionDelEquipo informacionEquipo = this.toTest.getInformacionEquipo();


    }





}