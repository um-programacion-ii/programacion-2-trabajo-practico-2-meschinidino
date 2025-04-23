package um.prog2.interfaces;

import um.prog2.usuario.Usuario;

public interface ServicioNotificaciones {
    void enviarNotificacion(String mensaje, Usuario usuario);
}
