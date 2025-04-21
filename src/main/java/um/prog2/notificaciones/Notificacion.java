package um.prog2.notificaciones;

import um.prog2.usuario.Usuario;
import java.time.LocalDateTime;

/**
 * Clase base para representar una notificación.
 */
public class Notificacion {
    private final String mensaje;
    private final Usuario destinatario;
    private final LocalDateTime fechaCreacion;
    private final TipoNotificacion tipo;
    private final NivelUrgencia nivelUrgencia;

    /**
     * Constructor para crear una notificación.
     * 
     * @param mensaje Mensaje de la notificación
     * @param destinatario Usuario destinatario de la notificación
     * @param tipo Tipo de notificación
     */
    public Notificacion(String mensaje, Usuario destinatario, TipoNotificacion tipo) {
        this(mensaje, destinatario, tipo, NivelUrgencia.INFO);
    }

    /**
     * Constructor para crear una notificación con nivel de urgencia.
     * 
     * @param mensaje Mensaje de la notificación
     * @param destinatario Usuario destinatario de la notificación
     * @param tipo Tipo de notificación
     * @param nivelUrgencia Nivel de urgencia de la notificación
     */
    public Notificacion(String mensaje, Usuario destinatario, TipoNotificacion tipo, NivelUrgencia nivelUrgencia) {
        this.mensaje = mensaje;
        this.destinatario = destinatario;
        this.fechaCreacion = LocalDateTime.now();
        this.tipo = tipo;
        this.nivelUrgencia = nivelUrgencia;
    }

    /**
     * Obtiene el mensaje de la notificación.
     * 
     * @return Mensaje de la notificación
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Obtiene el usuario destinatario de la notificación.
     * 
     * @return Usuario destinatario
     */
    public Usuario getDestinatario() {
        return destinatario;
    }

    /**
     * Obtiene la fecha de creación de la notificación.
     * 
     * @return Fecha de creación
     */
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * Obtiene el tipo de notificación.
     * 
     * @return Tipo de notificación
     */
    public TipoNotificacion getTipo() {
        return tipo;
    }

    /**
     * Obtiene el nivel de urgencia de la notificación.
     * 
     * @return Nivel de urgencia
     */
    public NivelUrgencia getNivelUrgencia() {
        return nivelUrgencia;
    }

    /**
     * Formatea la notificación para su visualización.
     * 
     * @return Notificación formateada
     */
    @Override
    public String toString() {
        return "[" + fechaCreacion + "] " + nivelUrgencia.getIcono() + " " + tipo + ": " + mensaje;
    }

    /**
     * Enumeración que define los tipos de notificaciones.
     */
    public enum TipoNotificacion {
        PRESTAMO("Préstamo"),
        DEVOLUCION("Devolución"),
        RENOVACION("Renovación"),
        RESERVA("Reserva"),
        CANCELACION("Cancelación"),
        VENCIMIENTO("Vencimiento"),
        SISTEMA("Sistema"),
        ERROR("Error");

        private final String nombre;

        TipoNotificacion(String nombre) {
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }

    /**
     * Enumeración que define los niveles de urgencia de las notificaciones.
     */
    public enum NivelUrgencia {
        INFO("Información", "ℹ️"),
        WARNING("Advertencia", "⚠️"),
        ERROR("Error", "🚨");

        private final String nombre;
        private final String icono;

        NivelUrgencia(String nombre, String icono) {
            this.nombre = nombre;
            this.icono = icono;
        }

        public String getNombre() {
            return nombre;
        }

        public String getIcono() {
            return icono;
        }

        @Override
        public String toString() {
            return icono + " " + nombre;
        }
    }
}
