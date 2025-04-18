package um.prog2.notificaciones;
import um.prog2.usuario.Usuario;

public class ServicioNotificacionesEmail implements ServicioNotificaciones {
    
    @Override
    public void enviarNotificacion(String mensaje, Usuario usuario) {
        // Lógica para enviar una notificación por correo electrónico
        System.out.println("Enviando notificación por correo electrónico a " + usuario.getEmail() + ": " + mensaje);
    }
}
