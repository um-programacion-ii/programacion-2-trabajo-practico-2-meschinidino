package um.prog2.reservas;

import um.prog2.Enums.EstadoRecurso;
import um.prog2.interfaces.RecursoDigital;
import um.prog2.usuario.Usuario;

import java.time.LocalDateTime;

/**
 * Clase que representa una reserva de un recurso por un usuario.
 */
public class Reserva {
    private final String id;
    private final Usuario usuario;
    private final RecursoDigital recurso;
    private final LocalDateTime fechaReserva;
    private final int prioridad;
    private boolean activa;

    /**
     * Constructor para crear una nueva reserva.
     * 
     * @param id Identificador único de la reserva
     * @param usuario Usuario que realiza la reserva
     * @param recurso Recurso que se reserva
     * @param prioridad Prioridad de la reserva (mayor número = mayor prioridad)
     */
    public Reserva(String id, Usuario usuario, RecursoDigital recurso, int prioridad) {
        this.id = id;
        this.usuario = usuario;
        this.recurso = recurso;
        this.fechaReserva = LocalDateTime.now();
        this.prioridad = prioridad;
        this.activa = true;
    }

    /**
     * Cancela la reserva.
     * 
     * @return true si la cancelación fue exitosa, false en caso contrario
     */
    public boolean cancelar() {
        if (!activa) {
            return false;
        }
        
        activa = false;
        return true;
    }

    /**
     * Convierte la reserva en un préstamo.
     * 
     * @return true si la conversión fue exitosa, false en caso contrario
     */
    public boolean convertirEnPrestamo() {
        if (!activa) {
            return false;
        }
        
        recurso.actualizarEstado(EstadoRecurso.PRESTADO);
        activa = false;
        return true;
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

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public boolean isActiva() {
        return activa;
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "id='" + id + '\'' +
                ", usuario=" + usuario.getNombre() + " " + usuario.getApellido() +
                ", recurso=" + recurso.getIdentificador() +
                ", fechaReserva=" + fechaReserva +
                ", prioridad=" + prioridad +
                ", activa=" + activa +
                '}';
    }
}