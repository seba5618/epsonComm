package ar.com.bambu.afip;

public class AuditoriaAfipSegunFechaHassar implements Function{

    /**
     * El reporte de auditoria de AFIP de Hassar es una orquestacion de varios mensajes hacia la impresora.
     *
     * 1- Mandar un getConsultarCapacidadZetas para obtener la ultima z.
     * 2- Obtengo Rango de Fechas por Zeta de la z del punto 1.
     * 3- Empezar a mandar un getReporteElectronico con las fechas del paso anterior(que pasa si la fecha fin es en el futuro? quiero que este paso falle pero que me diga la ultima z bajada).
     * 4- Seguro que el paso 3 falla por saltos de Z bajadas, asi que mando un getConsultarUltimoError y este me dice la ultima z bajada.
     * 5- Repito el paso 2 con la z del punto 4 (+1) y el paso 3 con las fechas obtenidas.
     * 6- Pido un getConsultarDatosInicializacion para obtener numero de pos y con estos datos guardo el archivo zip obtenido en el punto 5.
     */
    @Override
    public void apply() {

    }
}
