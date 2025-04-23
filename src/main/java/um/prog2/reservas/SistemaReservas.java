package um.prog2.reservas;

import um.prog2.Enums.EstadoRecurso;
import um.prog2.excepciones.RecursoNoDisponibleException;
import um.prog2.interfaces.RecursoDigital;
import um.prog2.interfaces.ServicioNotificaciones;
import um.prog2.prestamos.SistemaPrestamos;
import um.prog2.usuario.Usuario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Sistema que gestiona las reservas de recursos por usuarios.
 * Utiliza una cola de prioridad para procesar reservas según su prioridad.
 */
public class SistemaReservas {
    private final BlockingQueue<SolicitudReserva> colaSolicitudes;
    private final ExecutorService procesadorReservas;
    private final Map<String, Reserva> reservasActivas;
    private final List<Reserva> historialReservas;
    private final ServicioNotificaciones servicioNotificaciones;
    private final SistemaPrestamos sistemaPrestamos;

    /**
     * Constructor del sistema de reservas.
     * 
     * @param servicioNotificaciones Servicio para enviar notificaciones a los usuarios
     * @param sistemaPrestamos Sistema de préstamos para convertir reservas en préstamos
     */
    public SistemaReservas(ServicioNotificaciones servicioNotificaciones, SistemaPrestamos sistemaPrestamos) {
        // Usamos PriorityBlockingQueue para manejar prioridades
        this.colaSolicitudes = new PriorityBlockingQueue<>(11, Comparator.comparing(SolicitudReserva::getPrioridad).reversed());
        this.procesadorReservas = Executors.newSingleThreadExecutor();
        this.reservasActivas = new ConcurrentHashMap<>();
        this.historialReservas = Collections.synchronizedList(new ArrayList<>());
        this.servicioNotificaciones = servicioNotificaciones;
        this.sistemaPrestamos = sistemaPrestamos;

        // Iniciar el procesador de solicitudes
        iniciarProcesador();
    }

    /**
     * Inicia el procesador de solicitudes de reserva.
     */
    private void iniciarProcesador() {
        procesadorReservas.submit(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    SolicitudReserva solicitud = colaSolicitudes.take();
                    procesarSolicitud(solicitud);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    /**
     * Procesa una solicitud de reserva.
     * 
     * @param solicitud La solicitud a procesar
     */
    private void procesarSolicitud(SolicitudReserva solicitud) {
        try {
            System.out.println("[CONCURRENCIA] Procesando solicitud de tipo " + solicitud.getTipo() + 
                    " para usuario " + solicitud.getUsuario().getNombre() + 
                    " en thread " + Thread.currentThread().getName());

            switch (solicitud.getTipo()) {
                case RESERVAR:
                    realizarReserva(solicitud.getUsuario(), solicitud.getRecurso(), solicitud.getPrioridad());
                    break;
                case CANCELAR:
                    cancelarReserva(solicitud.getIdReserva());
                    break;
                case CONVERTIR_A_PRESTAMO:
                    convertirAPrestamo(solicitud.getIdReserva(), solicitud.getDiasPrestamo());
                    break;
            }
        } catch (RecursoNoDisponibleException e) {
            System.out.println("[CONCURRENCIA] Error al procesar solicitud: " + e.getMessage());
            // Notificar al usuario sobre el error
            servicioNotificaciones.enviarNotificacion(
                    "Error en solicitud de reserva: " + e.getMessage(),
                    solicitud.getUsuario()
            );
        }
    }

    /**
     * Solicita una reserva de un recurso para un usuario.
     * 
     * @param usuario Usuario que solicita la reserva
     * @param recurso Recurso a reservar
     * @param prioridad Prioridad de la reserva
     */
    public void solicitarReserva(Usuario usuario, RecursoDigital recurso, int prioridad) {
        SolicitudReserva solicitud = new SolicitudReserva(
                TipoSolicitud.RESERVAR,
                usuario,
                recurso,
                null,
                0,
                prioridad
        );
        colaSolicitudes.add(solicitud);
    }

    /**
     * Solicita la cancelación de una reserva.
     * 
     * @param idReserva Identificador de la reserva a cancelar
     * @param usuario Usuario que realiza la cancelación
     */
    public void solicitarCancelacion(String idReserva, Usuario usuario) {
        SolicitudReserva solicitud = new SolicitudReserva(
                TipoSolicitud.CANCELAR,
                usuario,
                null,
                idReserva,
                0,
                0
        );
        colaSolicitudes.add(solicitud);
    }

    /**
     * Solicita la conversión de una reserva a préstamo.
     * 
     * @param idReserva Identificador de la reserva a convertir
     * @param usuario Usuario que solicita la conversión
     * @param diasPrestamo Duración del préstamo en días
     */
    public void solicitarConversionAPrestamo(String idReserva, Usuario usuario, int diasPrestamo) {
        SolicitudReserva solicitud = new SolicitudReserva(
                TipoSolicitud.CONVERTIR_A_PRESTAMO,
                usuario,
                null,
                idReserva,
                diasPrestamo,
                0
        );
        colaSolicitudes.add(solicitud);
    }

    /**
     * Realiza una reserva de un recurso para un usuario.
     * 
     * @param usuario Usuario que realiza la reserva
     * @param recurso Recurso a reservar
     * @param prioridad Prioridad de la reserva
     * @throws RecursoNoDisponibleException Si el recurso no está disponible para reserva
     */
    private synchronized void realizarReserva(Usuario usuario, RecursoDigital recurso, int prioridad) throws RecursoNoDisponibleException {
        System.out.println("[CONCURRENCIA] Iniciando reserva para usuario " + usuario.getNombre() + 
                " del recurso " + recurso.getIdentificador() + " con prioridad " + prioridad + 
                " en thread " + Thread.currentThread().getName());

        // Solo se pueden reservar recursos que estén prestados
        if (recurso.getEstado() != EstadoRecurso.PRESTADO) {
            System.out.println("[CONCURRENCIA] Recurso no disponible para reserva: " + recurso.getIdentificador());
            throw new RecursoNoDisponibleException("El recurso no está en estado PRESTADO y no puede ser reservado");
        }

        String idReserva = generarIdReserva();
        Reserva reserva = new Reserva(idReserva, usuario, recurso, prioridad);
        reservasActivas.put(idReserva, reserva);

        // Actualizar el estado del recurso a RESERVADO
        recurso.actualizarEstado(EstadoRecurso.RESERVADO);

        System.out.println("[CONCURRENCIA] Reserva realizada con éxito: ID=" + idReserva);

        // Notificar al usuario
        servicioNotificaciones.enviarNotificacion(
                "Reserva realizada: " + recurso.getIdentificador() + 
                ". Prioridad: " + prioridad,
                usuario
        );
    }

    /**
     * Cancela una reserva.
     * 
     * @param idReserva Identificador de la reserva a cancelar
     * @throws RecursoNoDisponibleException Si la reserva no existe o ya fue cancelada
     */
    private synchronized void cancelarReserva(String idReserva) throws RecursoNoDisponibleException {
        System.out.println("[CONCURRENCIA] Iniciando cancelación de reserva ID=" + idReserva + 
                " en thread " + Thread.currentThread().getName());

        Reserva reserva = reservasActivas.get(idReserva);
        if (reserva == null) {
            System.out.println("[CONCURRENCIA] Reserva no encontrada: " + idReserva);
            throw new RecursoNoDisponibleException("La reserva no existe o ya fue cancelada");
        }

        boolean cancelada = reserva.cancelar();
        if (cancelada) {
            reservasActivas.remove(idReserva);
            historialReservas.add(reserva);

            System.out.println("[CONCURRENCIA] Verificando si hay más reservas para el recurso: " + 
                    reserva.getRecurso().getIdentificador());

            // Si no hay más reservas para este recurso, cambiar su estado a PRESTADO
            boolean hayMasReservas = false;
            for (Reserva r : reservasActivas.values()) {
                if (r.getRecurso().getIdentificador().equals(reserva.getRecurso().getIdentificador())) {
                    hayMasReservas = true;
                    break;
                }
            }

            if (!hayMasReservas) {
                System.out.println("[CONCURRENCIA] No hay más reservas, cambiando estado a PRESTADO");
                reserva.getRecurso().actualizarEstado(EstadoRecurso.PRESTADO);
            }

            System.out.println("[CONCURRENCIA] Cancelación realizada con éxito: ID=" + idReserva);

            // Notificar al usuario
            servicioNotificaciones.enviarNotificacion(
                    "Reserva cancelada: " + reserva.getRecurso().getIdentificador(),
                    reserva.getUsuario()
            );
        } else {
            System.out.println("[CONCURRENCIA] La reserva ya fue cancelada: " + idReserva);
            throw new RecursoNoDisponibleException("La reserva ya fue cancelada");
        }
    }

    /**
     * Convierte una reserva en un préstamo.
     * 
     * @param idReserva Identificador de la reserva a convertir
     * @param diasPrestamo Duración del préstamo en días
     * @throws RecursoNoDisponibleException Si la reserva no existe o ya fue convertida
     */
    private synchronized void convertirAPrestamo(String idReserva, int diasPrestamo) throws RecursoNoDisponibleException {
        System.out.println("[CONCURRENCIA] Iniciando conversión de reserva a préstamo ID=" + idReserva + 
                " por " + diasPrestamo + " días en thread " + Thread.currentThread().getName());

        Reserva reserva = reservasActivas.get(idReserva);
        if (reserva == null) {
            System.out.println("[CONCURRENCIA] Reserva no encontrada: " + idReserva);
            throw new RecursoNoDisponibleException("La reserva no existe o ya fue convertida");
        }

        boolean convertida = reserva.convertirEnPrestamo();
        if (convertida) {
            reservasActivas.remove(idReserva);
            historialReservas.add(reserva);

            System.out.println("[CONCURRENCIA] Solicitando préstamo para el recurso: " + 
                    reserva.getRecurso().getIdentificador());

            // Crear un préstamo para el recurso
            sistemaPrestamos.solicitarPrestamo(reserva.getUsuario(), reserva.getRecurso(), diasPrestamo);

            System.out.println("[CONCURRENCIA] Conversión realizada con éxito: ID=" + idReserva);

            // Notificar al usuario
            servicioNotificaciones.enviarNotificacion(
                    "Reserva convertida a préstamo: " + reserva.getRecurso().getIdentificador(),
                    reserva.getUsuario()
            );
        } else {
            System.out.println("[CONCURRENCIA] La reserva ya fue convertida o cancelada: " + idReserva);
            throw new RecursoNoDisponibleException("La reserva ya fue convertida o cancelada");
        }
    }

    /**
     * Genera un identificador único para una reserva.
     * 
     * @return Identificador único
     */
    private String generarIdReserva() {
        return "R-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Obtiene las reservas activas de un usuario.
     * 
     * @param usuario Usuario del que se quieren obtener las reservas
     * @return Lista de reservas activas del usuario
     */
    public synchronized List<Reserva> obtenerReservasActivas(Usuario usuario) {
        System.out.println("[CONCURRENCIA] Obteniendo reservas activas para usuario " + usuario.getNombre() + 
                " en thread " + Thread.currentThread().getName());

        List<Reserva> reservasUsuario = new ArrayList<>();
        for (Reserva reserva : reservasActivas.values()) {
            if (reserva.getUsuario().getID() == usuario.getID()) {
                reservasUsuario.add(reserva);
            }
        }

        System.out.println("[CONCURRENCIA] Reservas activas obtenidas. Total: " + reservasUsuario.size());
        return reservasUsuario;
    }

    /**
     * Obtiene el historial de reservas de un usuario.
     * 
     * @param usuario Usuario del que se quiere obtener el historial
     * @return Lista con el historial de reservas del usuario
     */
    public synchronized List<Reserva> obtenerHistorialReservas(Usuario usuario) {
        System.out.println("[CONCURRENCIA] Obteniendo historial de reservas para usuario " + usuario.getNombre() + 
                " en thread " + Thread.currentThread().getName());

        List<Reserva> historialUsuario = new ArrayList<>();
        synchronized (historialReservas) {
            for (Reserva reserva : historialReservas) {
                if (reserva.getUsuario().getID() == usuario.getID()) {
                    historialUsuario.add(reserva);
                }
            }
        }

        System.out.println("[CONCURRENCIA] Historial obtenido. Total reservas: " + historialUsuario.size());
        return historialUsuario;
    }

    /**
     * Obtiene todas las reservas activas.
     * 
     * @return Lista de todas las reservas activas
     */
    public synchronized List<Reserva> obtenerTodasLasReservasActivas() {
        System.out.println("[CONCURRENCIA] Obteniendo todas las reservas activas en thread " + 
                Thread.currentThread().getName());

        List<Reserva> todasLasReservas = new ArrayList<>(reservasActivas.values());

        System.out.println("[CONCURRENCIA] Total reservas activas: " + todasLasReservas.size());
        return todasLasReservas;
    }

    /**
     * Cierra el sistema de reservas.
     */
    public void cerrar() {
        procesadorReservas.shutdown();
    }

    /**
     * Enumeración que define los tipos de solicitudes de reserva.
     */
    private enum TipoSolicitud {
        RESERVAR,
        CANCELAR,
        CONVERTIR_A_PRESTAMO
    }

    /**
     * Clase interna que representa una solicitud de reserva.
     */
    private static class SolicitudReserva {
        private final TipoSolicitud tipo;
        private final Usuario usuario;
        private final RecursoDigital recurso;
        private final String idReserva;
        private final int diasPrestamo;
        private final int prioridad;

        public SolicitudReserva(TipoSolicitud tipo, Usuario usuario, RecursoDigital recurso, 
                               String idReserva, int diasPrestamo, int prioridad) {
            this.tipo = tipo;
            this.usuario = usuario;
            this.recurso = recurso;
            this.idReserva = idReserva;
            this.diasPrestamo = diasPrestamo;
            this.prioridad = prioridad;
        }

        public TipoSolicitud getTipo() {
            return tipo;
        }

        public Usuario getUsuario() {
            return usuario;
        }

        public RecursoDigital getRecurso() {
            return recurso;
        }

        public String getIdReserva() {
            return idReserva;
        }

        public int getDiasPrestamo() {
            return diasPrestamo;
        }

        public int getPrioridad() {
            return prioridad;
        }
    }
}
