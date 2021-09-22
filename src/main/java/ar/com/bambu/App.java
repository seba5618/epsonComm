package ar.com.bambu;

import ar.com.bambu.afip.AuditoriaAfipSegunFechaHassar;
import ar.com.bambu.communicator.HassarCommunicator;
//ObtenerConfiguracionFechayHora;
import ar.com.bambu.communicator.reply.hassar.ConsultarCapacidadZetas;
import ar.com.bambu.communicator.reply.hassar.ConsultarDatosInicializacion;
import ar.com.bambu.communicator.reply.hassar.ObtenerRangoFechasPorZetas;
import ar.com.bambu.communicator.reply.hassar.ReporteElectronico;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Hello world!
 */
public class App {



    private static final Logger logger = LogManager.getLogger(App.class);


    public static void main(String[] args)  throws Exception{

        AuditoriaAfipSegunFechaHassar auditoriaAfipSegunFechaHassar = new AuditoriaAfipSegunFechaHassar(new HassarCommunicator());
        auditoriaAfipSegunFechaHassar.apply();

    }
}
