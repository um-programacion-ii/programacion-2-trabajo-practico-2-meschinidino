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
    
    /**
     * Constructor para crear una notificación.
     * 
     * @param mensaje Mensaje de la notificación
     * @param destinatario Usuario destinatario de la notificación
     * @param tipo Tipo de notificación
     */
    public Notificacion(String mensaje, Usuario destinatario, TipoNotificacion tipo) {
        this.mensaje = mensaje;
        this.destinatario = destinatario;
        this.fechaCreacion = LocalDateTime.now();
        this.tipo = tipo;
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
     * Formatea la notificación para su visualización.
     * 
     * @return Notificación formateada
     */
    @Override
    public String toString() {
        return "[" + fechaCreacion + "] " + tipo + ": " + mensaje;
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
}