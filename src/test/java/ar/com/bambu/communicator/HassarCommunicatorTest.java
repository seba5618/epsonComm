package ar.com.bambu.communicator;


import ar.com.bambu.communicator.reply.ConfiguracionFechayHora;
import ar.com.bambu.communicator.reply.hassar.ConsultarCapacidadZetas;
import ar.com.bambu.communicator.reply.hassar.ConsultarUltimoError;
import ar.com.bambu.jpos.EpsonFrameMsg;
import ar.com.bambu.serial.HassarSerialChannel;
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
public class HassarCommunicatorTest {

    private static final Logger logger = LogManager.getLogger(HassarCommunicatorTest.class);
    private HassarCommunicator toTest = new HassarCommunicator();

    @Mock
    private HassarSerialChannel channel;

    @Before
    public void initialize() {
        this.toTest.setChannel(this.channel);
    }


    @Test
    public void testConsultarCapacidadZetas() throws Exception {
        logger.info("Test: ConsultarCapacidadZetas");

        String trama = "371C" +
                "303030301C" +
                "303230301C" +
                "333633331C" +
                "31371C";

        byte[] bytes = ISOUtil.hex2byte(trama);
        Mockito.when(channel.sendMsg(Mockito.any(byte[].class))).thenReturn(bytes);
        ConsultarCapacidadZetas consultarCapacidadZetas = this.toTest.getConsultarCapacidadZetas();
        Assert.assertEquals("Cantidad Z restantes deberias ser: " + 3633 + ". Pero es: " +
                consultarCapacidadZetas.getCantidadDeZetasRemanentes(), 3633, consultarCapacidadZetas.getCantidadDeZetasRemanentes());
        Assert.assertEquals("Ultimo Z emitido debería ser: " + 17 + ". Pero es: " + consultarCapacidadZetas.getUltimaZ(), 17, consultarCapacidadZetas.getUltimaZ());
    }

    @Test
    public void testConsultarUltimoError() throws Exception {
        logger.info("Test: testConsultarUltimoError");

        String trama = "2C1C" +
                "303030301C" +
                "303230301C" +
                "504F535F5245504F52545F4741501C" +
                "301C" +
                "4A6F726E616461732066697363616C206E6F20636F6E73656375746976612061206C6120A36C74696D612062616A6164611C" +
                "556C74696D61206A6F726E6164612062616A6164613A203134202830322F30372F32303231293B206A6F726E6164617320717565207365207175696572652062616A61723A2031382D3138202830372F30382F323032312D30372F30382F32303231291C";

        byte[] bytes = ISOUtil.hex2byte(trama);
        Mockito.when(channel.sendMsg(Mockito.any(byte[].class))).thenReturn(bytes);
        ConsultarUltimoError consultarUltimoError = this.toTest.getConsultarUltimoError();
        logger.info(consultarUltimoError.toString());
    }

}