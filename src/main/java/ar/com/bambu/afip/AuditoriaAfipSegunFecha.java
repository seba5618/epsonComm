package ar.com.bambu.afip;

import ar.com.bambu.communicator.EpsonCommunicator;

public class AuditoriaAfipSegunFecha implements Function{

    private EpsonCommunicator communicator ;

    public AuditoriaAfipSegunFecha(EpsonCommunicator communicator) {
        this.communicator = communicator;
    }

    /**
     * El reporte de auditoria de AFIP es una orquestacion de varios mensajes hacia la impresora.
     *
     * 1- Obtener la Informacion transaccional 9-15
     * 2- Obtengo jornadasDescargadasHasta del mensaje anterior, le sumo 1 (¿puedo usar lo que me viene de "desde" de los tres campos anteriores?)
     * 3- Mandar un 813 con la z del paso 2.
     * 4- El 813 no lo necesito completo, pero si los campos de fecha desde y fecha hasta
     * 5- Empezar a mandar un reporte afip con las fechas del paso anterior, se mandan 3 9 con distintos parametros.
     * 6- Los 3 reporte afip del paso anterior genero archivos con el nombre y el contenido que nos respondio la impresora.
     */
    @Override
    public void apply() {

    }
}
