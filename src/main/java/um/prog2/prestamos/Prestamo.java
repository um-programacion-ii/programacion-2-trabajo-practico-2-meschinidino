package um.prog2.prestamos;

import um.prog2.Enums.EstadoRecurso;
import um.prog2.interfaces.RecursoDigital;
import um.prog2.usuario.Usuario;

import java.time.LocalDateTime;

/**
 * Clase que representa un préstamo de un recurso a un usuario.
 */
public class Prestamo {
    private final String id;
    private final Usuario usuario;
    private final RecursoDigital recurso;
    private LocalDateTime fechaPrestamo;
    private LocalDateTime fechaDevolucion;
    private boolean activo;

    /**
     * Constructor para crear un nuevo préstamo.
     * 
     * @param id Identificador único del préstamo
     * @param usuario Usuario que realiza el préstamo
     * @param recurso Recurso que se presta
     * @param diasPrestamo Duración del préstamo en días
     */
    public Prestamo(String id, Usuario usuario, RecursoDigital recurso, int diasPrestamo) {
        this.id = id;
        this.usuario = usuario;
        this.recurso = recurso;
        this.fechaPrestamo = LocalDateTime.now();
        this.fechaDevolucion = fechaPrestamo.plusDays(diasPrestamo);
        this.activo = true;
        
        // Actualizar el estado del recurso a PRESTADO
        recurso.actualizarEstado(EstadoRecurso.PRESTADO);
    }

    /**
     * Devuelve el recurso prestado, cambiando su estado a DISPONIBLE.
     * 
     * @return true si la devolución fue exitosa, false en caso contrario
     */
    public boolean devolver() {
        if (!activo) {
            return false;
        }
        
        recurso.actualizarEstado(EstadoRecurso.DISPONIBLE);
        activo = false;
        return true;
    }

    /**
     * Extiende la fecha de devolución del préstamo.
     * 
     * @param diasExtension Días adicionales para el préstamo
     * @return true si la renovación fue exitosa, false en caso contrario
     */
    public boolean renovar(int diasExtension) {
        if (!activo) {
            return false;
        }
        
        fechaDevolucion = fechaDevolucion.plusDays(diasExtension);
        return true;
    }

    /**
     * Verifica si el préstamo está vencido.
     * 
     * @return true si la fecha actual es posterior a la fecha de devolución, false en caso contrario
     */
    public boolean estaVencido() {
        return LocalDateTime.now().isAfter(fechaDevolucion) && activo;
    }

    // Getters
    public String getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public RecursoDigital getRecurso() {
        return recurso;
    }

    public LocalDateTime getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    public boolean isActivo() {
        return activo;
    }

    @Override
    public String toString() {
        return "Prestamo{" +
                "id='" + id + '\'' +
                ", usuario=" + usuario.getNombre() + " " + usuario.getApellido() +
                ", recurso=" + recurso.getIdentificador() +
                ", fechaPrestamo=" + fechaPrestamo +
                ", fechaDevolucion=" + fechaDevolucion +
                ", activo=" + activo +
                '}';
    }
}