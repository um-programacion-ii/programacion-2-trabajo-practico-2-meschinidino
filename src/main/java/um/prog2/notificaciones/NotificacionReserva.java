package um.prog2.notificaciones;

import um.prog2.interfaces.RecursoDigital;
import um.prog2.usuario.Usuario;

/**
 * Notificación específica para eventos relacionados con reservas.
 */
public class NotificacionReserva extends Notificacion {
    private final RecursoDigital recurso;
    private final String idReserva;
    private final int prioridad;

    /**
     * Constructor para crear una notificación de reserva.
     * 
     * @param mensaje Mensaje de la notificación
     * @param destinatario Usuario destinatario de la notificación
     * @param tipo Tipo de notificación
     * @param recurso Recurso relacionado con la reserva
     * @param idReserva Identificador de la reserva
     * @param prioridad Prioridad de la reserva
     */
    public NotificacionReserva(String mensaje, Usuario destinatario, TipoNotificacion tipo, 
                              RecursoDigital recurso, String idReserva, int prioridad) {
        this(mensaje, destinatario, tipo, recurso, idReserva, prioridad, NivelUrgencia.INFO);
    }

    /**
     * Constructor para crear una notificación de reserva con nivel de urgencia.
     * 
     * @param mensaje Mensaje de la notificación
     * @param destinatario Usuario destinatario de la notificación
     * @param tipo Tipo de notificación
     * @param recurso Recurso relacionado con la reserva
     * @param idReserva Identificador de la reserva
     * @param prioridad Prioridad de la reserva
     * @param nivelUrgencia Nivel de urgencia de la notificación
     */
    public NotificacionReserva(String mensaje, Usuario destinatario, TipoNotificacion tipo, 
                              RecursoDigital recurso, String idReserva, int prioridad, NivelUrgencia nivelUrgencia) {
        super(mensaje, destinatario, tipo, nivelUrgencia);
        this.recurso = recurso;
        this.idReserva = idReserva;
        this.prioridad = prioridad;
    }

    /**
     * Obtiene el recurso relacionado con la reserva.
     * 
     * @return Recurso relacionado
     */
    public RecursoDigital getRecurso() {
        return recurso;
    }

    /**
     * Obtiene el identificador de la reserva.
     * 
     * @return Identificador de la reserva
     */
    public String getIdReserva() {
        return idReserva;
    }

    /**
     * Obtiene la prioridad de la reserva.
     * 
     * @return Prioridad de la reserva
     */
    public int getPrioridad() {
        return prioridad;
    }

    /**
     * Formatea la notificación para su visualización.
     * 
     * @return Notificación formateada
     */
    @Override
    public String toString() {
        return super.toString() + " [Recurso: " + recurso.getIdentificador() + 
               ", ID Reserva: " + idReserva + ", Prioridad: " + prioridad + "]";
    }
}
