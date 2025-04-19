package um.prog2.prestamos;

import um.prog2.Enums.EstadoRecurso;
import um.prog2.excepciones.RecursoNoDisponibleException;
import um.prog2.interfaces.Prestable;
import um.prog2.interfaces.RecursoDigital;
import um.prog2.interfaces.ServicioNotificaciones;
import um.prog2.usuario.Usuario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Sistema que gestiona los préstamos de recursos a usuarios.
 * Utiliza una cola de solicitudes y un servicio de ejecución para procesar préstamos.
 */
public class SistemaPrestamos {
    private final BlockingQueue<SolicitudPrestamo> colaSolicitudes;
    private final ExecutorService procesadorPrestamos;
    private final Map<String, Prestamo> prestamosActivos;
    private final List<Prestamo> historialPrestamos;
    private final ServicioNotificaciones servicioNotificaciones;

    /**
     * Constructor del sistema de préstamos.
     * 
     * @param servicioNotificaciones Servicio para enviar notificaciones a los usuarios
     */
    public SistemaPrestamos(ServicioNotificaciones servicioNotificaciones) {
        this.colaSolicitudes = new LinkedBlockingQueue<>();
        this.procesadorPrestamos = Executors.newSingleThreadExecutor();
        this.prestamosActivos = new ConcurrentHashMap<>();
        this.historialPrestamos = Collections.synchronizedList(new ArrayList<>());
        this.servicioNotificaciones = servicioNotificaciones;

        // Iniciar el procesador de solicitudes
        iniciarProcesador();
    }

    /**
     * Inicia el procesador de solicitudes de préstamo.
     */
    private void iniciarProcesador() {
        procesadorPrestamos.submit(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    SolicitudPrestamo solicitud = colaSolicitudes.take();
                    procesarSolicitud(solicitud);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    /**
     * Procesa una solicitud de préstamo.
     * 
     * @param solicitud La solicitud a procesar
     */
    private void procesarSolicitud(SolicitudPrestamo solicitud) {
        try {
            System.out.println("[CONCURRENCIA] Procesando solicitud de tipo " + solicitud.getTipo() + 
                    " para usuario " + solicitud.getUsuario().getNombre() + 
                    " en thread " + Thread.currentThread().getName());

            switch (solicitud.getTipo()) {
                case PRESTAR:
                    realizarPrestamo(solicitud.getUsuario(), solicitud.getRecurso(), solicitud.getDiasPrestamo());
                    break;
                case DEVOLVER:
                    devolverRecurso(solicitud.getIdPrestamo());
                    break;
                case RENOVAR:
                    renovarPrestamo(solicitud.getIdPrestamo(), solicitud.getDiasPrestamo());
                    break;
            }
        } catch (RecursoNoDisponibleException e) {
            System.out.println("[CONCURRENCIA] Error al procesar solicitud: " + e.getMessage());
            // Notificar al usuario sobre el error
            servicioNotificaciones.enviarNotificacion(
                    "Error en solicitud de préstamo: " + e.getMessage(),
                    solicitud.getUsuario()
            );
        }
    }

    /**
     * Solicita un préstamo de un recurso para un usuario.
     * 
     * @param usuario Usuario que solicita el préstamo
     * @param recurso Recurso a prestar
     * @param diasPrestamo Duración del préstamo en días
     * @throws RecursoNoDisponibleException Si el recurso no está disponible
     */
    public void solicitarPrestamo(Usuario usuario, RecursoDigital recurso, int diasPrestamo) {
        SolicitudPrestamo solicitud = new SolicitudPrestamo(
                TipoSolicitud.PRESTAR,
                usuario,
                recurso,
                null,
                diasPrestamo
        );
        colaSolicitudes.add(solicitud);
    }

    /**
     * Solicita la devolución de un recurso.
     * 
     * @param idPrestamo Identificador del préstamo a devolver
     * @param usuario Usuario que realiza la devolución
     */
    public void solicitarDevolucion(String idPrestamo, Usuario usuario) {
        SolicitudPrestamo solicitud = new SolicitudPrestamo(
                TipoSolicitud.DEVOLVER,
                usuario,
                null,
                idPrestamo,
                0
        );
        colaSolicitudes.add(solicitud);
    }

    /**
     * Solicita la renovación de un préstamo.
     * 
     * @param idPrestamo Identificador del préstamo a renovar
     * @param usuario Usuario que solicita la renovación
     * @param diasExtension Días adicionales para el préstamo
     */
    public void solicitarRenovacion(String idPrestamo, Usuario usuario, int diasExtension) {
        SolicitudPrestamo solicitud = new SolicitudPrestamo(
                TipoSolicitud.RENOVAR,
                usuario,
                null,
                idPrestamo,
                diasExtension
        );
        colaSolicitudes.add(solicitud);
    }

    /**
     * Realiza un préstamo de un recurso a un usuario.
     * 
     * @param usuario Usuario que realiza el préstamo
     * @param recurso Recurso a prestar
     * @param diasPrestamo Duración del préstamo en días
     * @throws RecursoNoDisponibleException Si el recurso no está disponible
     */
    private synchronized void realizarPrestamo(Usuario usuario, RecursoDigital recurso, int diasPrestamo) throws RecursoNoDisponibleException {
        System.out.println("[CONCURRENCIA] Iniciando préstamo para usuario " + usuario.getNombre() + 
                " del recurso " + recurso.getIdentificador() + " en thread " + Thread.currentThread().getName());

        if (recurso.getEstado() != EstadoRecurso.DISPONIBLE) {
            System.out.println("[CONCURRENCIA] Recurso no disponible: " + recurso.getIdentificador());
            throw new RecursoNoDisponibleException("El recurso no está disponible para préstamo");
        }

        String idPrestamo = generarIdPrestamo();
        Prestamo prestamo = new Prestamo(idPrestamo, usuario, recurso, diasPrestamo);
        prestamosActivos.put(idPrestamo, prestamo);

        System.out.println("[CONCURRENCIA] Préstamo realizado con éxito: ID=" + idPrestamo);

        // Notificar al usuario
        servicioNotificaciones.enviarNotificacion(
                "Préstamo realizado: " + recurso.getIdentificador() + 
                ". Fecha de devolución: " + prestamo.getFechaDevolucion(),
                usuario
        );
    }

    /**
     * Devuelve un recurso prestado.
     * 
     * @param idPrestamo Identificador del préstamo a devolver
     * @throws RecursoNoDisponibleException Si el préstamo no existe o ya fue devuelto
     */
    private synchronized void devolverRecurso(String idPrestamo) throws RecursoNoDisponibleException {
        System.out.println("[CONCURRENCIA] Iniciando devolución de préstamo ID=" + idPrestamo + 
                " en thread " + Thread.currentThread().getName());

        Prestamo prestamo = prestamosActivos.get(idPrestamo);
        if (prestamo == null) {
            System.out.println("[CONCURRENCIA] Préstamo no encontrado: " + idPrestamo);
            throw new RecursoNoDisponibleException("El préstamo no existe o ya fue devuelto");
        }

        boolean devuelto = prestamo.devolver();
        if (devuelto) {
            prestamosActivos.remove(idPrestamo);
            historialPrestamos.add(prestamo);

            System.out.println("[CONCURRENCIA] Devolución realizada con éxito: ID=" + idPrestamo);

            // Notificar al usuario
            servicioNotificaciones.enviarNotificacion(
                    "Devolución realizada: " + prestamo.getRecurso().getIdentificador(),
                    prestamo.getUsuario()
            );
        } else {
            System.out.println("[CONCURRENCIA] El préstamo ya fue devuelto: " + idPrestamo);
            throw new RecursoNoDisponibleException("El préstamo ya fue devuelto");
        }
    }

    /**
     * Renueva un préstamo existente.
     * 
     * @param idPrestamo Identificador del préstamo a renovar
     * @param diasExtension Días adicionales para el préstamo
     * @throws RecursoNoDisponibleException Si el préstamo no existe o ya fue devuelto
     */
    private synchronized void renovarPrestamo(String idPrestamo, int diasExtension) throws RecursoNoDisponibleException {
        System.out.println("[CONCURRENCIA] Iniciando renovación de préstamo ID=" + idPrestamo + 
                " por " + diasExtension + " días en thread " + Thread.currentThread().getName());

        Prestamo prestamo = prestamosActivos.get(idPrestamo);
        if (prestamo == null) {
            System.out.println("[CONCURRENCIA] Préstamo no encontrado: " + idPrestamo);
            throw new RecursoNoDisponibleException("El préstamo no existe o ya fue devuelto");
        }

        boolean renovado = prestamo.renovar(diasExtension);
        if (renovado) {
            System.out.println("[CONCURRENCIA] Renovación realizada con éxito: ID=" + idPrestamo);

            // Notificar al usuario
            servicioNotificaciones.enviarNotificacion(
                    "Renovación realizada: " + prestamo.getRecurso().getIdentificador() + 
                    ". Nueva fecha de devolución: " + prestamo.getFechaDevolucion(),
                    prestamo.getUsuario()
            );
        } else {
            System.out.println("[CONCURRENCIA] El préstamo no puede ser renovado: " + idPrestamo);
            throw new RecursoNoDisponibleException("El préstamo no puede ser renovado");
        }
    }

    /**
     * Genera un identificador único para un préstamo.
     * 
     * @return Identificador único
     */
    private String generarIdPrestamo() {
        return "P-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Obtiene los préstamos activos de un usuario.
     * 
     * @param usuario Usuario del que se quieren obtener los préstamos
     * @return Lista de préstamos activos del usuario
     */
    public synchronized List<Prestamo> obtenerPrestamosActivos(Usuario usuario) {
        System.out.println("[CONCURRENCIA] Obteniendo préstamos activos para usuario " + usuario.getNombre() + 
                " en thread " + Thread.currentThread().getName());

        List<Prestamo> prestamosUsuario = new ArrayList<>();
        for (Prestamo prestamo : prestamosActivos.values()) {
            if (prestamo.getUsuario().getID() == usuario.getID()) {
                prestamosUsuario.add(prestamo);
            }
        }

        System.out.println("[CONCURRENCIA] Préstamos activos obtenidos. Total: " + prestamosUsuario.size());
        return prestamosUsuario;
    }

    /**
     * Obtiene el historial de préstamos de un usuario.
     * 
     * @param usuario Usuario del que se quiere obtener el historial
     * @return Lista con el historial de préstamos del usuario
     */
    public synchronized List<Prestamo> obtenerHistorialPrestamos(Usuario usuario) {
        System.out.println("[CONCURRENCIA] Obteniendo historial de préstamos para usuario " + usuario.getNombre() + 
                " en thread " + Thread.currentThread().getName());

        List<Prestamo> historialUsuario = new ArrayList<>();
        synchronized (historialPrestamos) {
            for (Prestamo prestamo : historialPrestamos) {
                if (prestamo.getUsuario().getID() == usuario.getID()) {
                    historialUsuario.add(prestamo);
                }
            }
        }

        System.out.println("[CONCURRENCIA] Historial obtenido. Total préstamos: " + historialUsuario.size());
        return historialUsuario;
    }

    /**
     * Verifica si hay préstamos vencidos y notifica a los usuarios.
     */
    public synchronized void verificarPrestamosVencidos() {
        System.out.println("[CONCURRENCIA] Verificando préstamos vencidos en thread " + Thread.currentThread().getName());
        int contadorVencidos = 0;

        for (Prestamo prestamo : prestamosActivos.values()) {
            if (prestamo.estaVencido()) {
                contadorVencidos++;
                System.out.println("[CONCURRENCIA] Préstamo vencido encontrado: ID=" + prestamo.getId());

                servicioNotificaciones.enviarNotificacion(
                        "Préstamo vencido: " + prestamo.getRecurso().getIdentificador() + 
                        ". Fecha de devolución: " + prestamo.getFechaDevolucion(),
                        prestamo.getUsuario()
                );
            }
        }

        System.out.println("[CONCURRENCIA] Verificación completada. Préstamos vencidos: " + contadorVencidos);
    }

    /**
     * Cierra el sistema de préstamos.
     */
    public void cerrar() {
        procesadorPrestamos.shutdown();
    }

    /**
     * Enumeración que define los tipos de solicitudes de préstamo.
     */
    private enum TipoSolicitud {
        PRESTAR,
        DEVOLVER,
        RENOVAR
    }

    /**
     * Clase interna que representa una solicitud de préstamo.
     */
    private static class SolicitudPrestamo {
        private final TipoSolicitud tipo;
        private final Usuario usuario;
        private final RecursoDigital recurso;
        private final String idPrestamo;
        private final int diasPrestamo;

        public SolicitudPrestamo(TipoSolicitud tipo, Usuario usuario, RecursoDigital recurso, 
                                String idPrestamo, int diasPrestamo) {
            this.tipo = tipo;
            this.usuario = usuario;
            this.recurso = recurso;
            this.idPrestamo = idPrestamo;
            this.diasPrestamo = diasPrestamo;
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

        public String getIdPrestamo() {
            return idPrestamo;
        }

        public int getDiasPrestamo() {
            return diasPrestamo;
        }
    }
}
