package um.prog2.notificaciones;

import um.prog2.usuario.Usuario;

/**
 * Notificación específica para eventos relacionados con el sistema.
 */
public class NotificacionSistema extends Notificacion {
    private final String origen;

    /**
     * Constructor para crear una notificación del sistema.
     * 
     * @param mensaje Mensaje de la notificación
     * @param destinatario Usuario destinatario de la notificación
     * @param tipo Tipo de notificación
     * @param origen Origen de la notificación en el sistema
     */
    public NotificacionSistema(String mensaje, Usuario destinatario, TipoNotificacion tipo, String origen) {
        this(mensaje, destinatario, tipo, origen, NivelUrgencia.INFO);
    }

    /**
     * Constructor para crear una notificación del sistema con nivel de urgencia.
     * 
     * @param mensaje Mensaje de la notificación
     * @param destinatario Usuario destinatario de la notificación
     * @param tipo Tipo de notificación
     * @param origen Origen de la notificación en el sistema
     * @param nivelUrgencia Nivel de urgencia de la notificación
     */
    public NotificacionSistema(String mensaje, Usuario destinatario, TipoNotificacion tipo, String origen, NivelUrgencia nivelUrgencia) {
        super(mensaje, destinatario, tipo, nivelUrgencia);
        this.origen = origen;
    }

    /**
     * Obtiene el origen de la notificación en el sistema.
     * 
     * @return Origen de la notificación
     */
    public String getOrigen() {
        return origen;
    }

    /**
     * Formatea la notificación para su visualización.
     * 
     * @return Notificación formateada
     */
    @Override
    public String toString() {
        return super.toString() + " [Origen: " + origen + "]";
    }
}
