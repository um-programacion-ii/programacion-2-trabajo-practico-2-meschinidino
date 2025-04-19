package um.prog2.interfaces;

import um.prog2.usuario.Usuario;

import java.time.LocalDateTime;

/**
 * Interfaz que define los métodos que debe implementar un recurso prestable.
 */
public interface Prestable {
    /**
     * Verifica si el recurso está disponible para préstamo.
     * 
     * @return true si el recurso está disponible, false en caso contrario
     */
    boolean estaDisponible();

    /**
     * Obtiene la fecha de devolución del recurso.
     * 
     * @return Fecha de devolución del recurso
     */
    LocalDateTime getFechaDevolucion();

    /**
     * Presta el recurso a un usuario.
     * 
     * @param usuario Usuario al que se presta el recurso
     */
    void prestar(Usuario usuario);

    /**
     * Devuelve el recurso, cambiando su estado a disponible.
     * 
     * @return true si la devolución fue exitosa, false en caso contrario
     */
    boolean devolver();
}
