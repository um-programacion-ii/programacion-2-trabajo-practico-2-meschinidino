package um.prog2.recursoDigital;

import um.prog2.usuario.Usuario;

import java.time.LocalDateTime;

public interface Prestable {
    boolean estaDisponible();
    LocalDateTime getFechaDevolucion();
    void prestar(Usuario usuario);
}