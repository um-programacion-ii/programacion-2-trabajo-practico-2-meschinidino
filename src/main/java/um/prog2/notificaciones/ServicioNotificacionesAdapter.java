package um.prog2.notificaciones;

import um.prog2.interfaces.ServicioNotificaciones;
import um.prog2.usuario.Usuario;

/**
 * Adaptador que implementa la interfaz ServicioNotificaciones y utiliza
 * el nuevo sistema de notificaciones basado en ExecutorService.
 */
public class ServicioNotificacionesAdapter implements ServicioNotificaciones {
    private final ServicioEnvioNotificaciones servicioEnvio;
    private final String origen;
    
    /**
     * Constructor del adaptador.
     * 
     * @param servicioEnvio Servicio de envío de notificaciones
     * @param origen Origen de las notificaciones enviadas por este adaptador
     */
    public ServicioNotificacionesAdapter(ServicioEnvioNotificaciones servicioEnvio, String origen) {
        this.servicioEnvio = servicioEnvio;
        this.origen = origen;
    }
    
    /**
     * Envía una notificación utilizando el servicio de envío.
     * 
     * @param mensaje Mensaje de la notificación
     * @param usuario Usuario destinatario
     */
    @Override
    public void enviarNotificacion(String mensaje, Usuario usuario) {
        servicioEnvio.enviarNotificacionSistema(
                mensaje, 
                usuario, 
                Notificacion.TipoNotificacion.SISTEMA, 
                origen
        );
    }
    
    /**
     * Obtiene el servicio de envío de notificaciones.
     * 
     * @return Servicio de envío
     */
    public ServicioEnvioNotificaciones getServicioEnvio() {
        return servicioEnvio;
    }
}