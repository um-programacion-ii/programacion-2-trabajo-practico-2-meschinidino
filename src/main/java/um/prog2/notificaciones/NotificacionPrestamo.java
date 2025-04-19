package um.prog2.notificaciones;

import um.prog2.interfaces.RecursoDigital;
import um.prog2.usuario.Usuario;

/**
 * Notificación específica para eventos relacionados con préstamos.
 */
public class NotificacionPrestamo extends Notificacion {
    private final RecursoDigital recurso;
    private final String idPrestamo;

    /**
     * Constructor para crear una notificación de préstamo.
     * 
     * @param mensaje Mensaje de la notificación
     * @param destinatario Usuario destinatario de la notificación
     * @param tipo Tipo de notificación
     * @param recurso Recurso relacionado con el préstamo
     * @param idPrestamo Identificador del préstamo
     */
    public NotificacionPrestamo(String mensaje, Usuario destinatario, TipoNotificacion tipo, 
                               RecursoDigital recurso, String idPrestamo) {
        super(mensaje, destinatario, tipo);
        this.recurso = recurso;
        this.idPrestamo = idPrestamo;
    }

    /**
     * Obtiene el recurso relacionado con el préstamo.
     * 
     * @return Recurso relacionado
     */
    public RecursoDigital getRecurso() {
        return recurso;
    }

    /**
     * Obtiene el identificador del préstamo.
     * 
     * @return Identificador del préstamo
     */
    public String getIdPrestamo() {
        return idPrestamo;
    }

    /**
     * Formatea la notificación para su visualización.
     * 
     * @return Notificación formateada
     */
    @Override
    public String toString() {
        return super.toString() + " [Recurso: " + recurso.getIdentificador() + ", ID Préstamo: " + idPrestamo + "]";
    }
}
