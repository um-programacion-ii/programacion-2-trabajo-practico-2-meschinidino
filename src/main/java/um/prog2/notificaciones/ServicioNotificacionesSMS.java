package um.prog2.notificaciones;
import um.prog2.interfaces.ServicioNotificaciones;
import um.prog2.usuario.Usuario;

public class ServicioNotificacionesSMS implements ServicioNotificaciones {
    @Override
    public void enviarNotificacion(String mensaje, Usuario usuario) {
        // Lógica para enviar una notificación por SMS
        System.out.println("Enviando notificación por SMS a " + usuario.getTelefono() + ": " + mensaje);
    }
}
