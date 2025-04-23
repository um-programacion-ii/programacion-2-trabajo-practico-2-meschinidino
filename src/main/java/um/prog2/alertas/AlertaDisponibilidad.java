package um.prog2.alertas;

import um.prog2.Enums.EstadoRecurso;
import um.prog2.interfaces.RecursoDigital;
import um.prog2.notificaciones.Notificacion;
import um.prog2.notificaciones.ServicioEnvioNotificaciones;
import um.prog2.prestamos.SistemaPrestamos;
import um.prog2.recursoDigital.GestorRecursos;
import um.prog2.reservas.Reserva;
import um.prog2.reservas.SistemaReservas;
import um.prog2.usuario.Usuario;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Clase que monitorea la disponibilidad de recursos reservados y envía alertas
 * cuando estos recursos están disponibles para préstamo.
 */
public class AlertaDisponibilidad {
    private final SistemaReservas sistemaReservas;
    private final SistemaPrestamos sistemaPrestamos;
    private final GestorRecursos gestorRecursos;
    private final ServicioEnvioNotificaciones servicioNotificaciones;
    private final ScheduledExecutorService scheduler;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Mapa para rastrear recursos que han sido notificados como disponibles
    private final Map<String, List<Usuario>> recursosNotificados = new HashMap<>();

    /**
     * Constructor de la clase AlertaDisponibilidad.
     *
     * @param sistemaReservas Sistema de reservas a monitorear
     * @param sistemaPrestamos Sistema de préstamos para realizar préstamos inmediatos
     * @param gestorRecursos Gestor de recursos para obtener información de recursos
     * @param servicioNotificaciones Servicio para enviar notificaciones
     */
    public AlertaDisponibilidad(SistemaReservas sistemaReservas, 
                               SistemaPrestamos sistemaPrestamos,
                               GestorRecursos gestorRecursos,
                               ServicioEnvioNotificaciones servicioNotificaciones) {
        this.sistemaReservas = sistemaReservas;
        this.sistemaPrestamos = sistemaPrestamos;
        this.gestorRecursos = gestorRecursos;
        this.servicioNotificaciones = servicioNotificaciones;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    /**
     * Inicia el monitoreo de recursos, verificando periódicamente si hay recursos
     * reservados que ahora están disponibles.
     *
     * @param intervaloMinutos Intervalo en minutos entre cada verificación
     */
    public void iniciarMonitoreo(int intervaloMinutos) {
        scheduler.scheduleAtFixedRate(
            this::verificarRecursosDisponibles,
            0,
            intervaloMinutos,
            TimeUnit.MINUTES
        );
    }

    /**
     * Verifica todos los recursos y envía alertas para aquellos
     * que estaban reservados y ahora están disponibles.
     */
    private void verificarRecursosDisponibles() {
        List<RecursoDigital> recursosDisponibles = gestorRecursos.obtenerRecursosDisponiblesParaPrestamo();
        List<Reserva> reservasActivas = sistemaReservas.obtenerTodasLasReservasActivas();

        // Mapa para agrupar usuarios que han reservado el mismo recurso
        Map<String, List<Usuario>> usuariosPorRecurso = new HashMap<>();

        // Agrupar usuarios por recurso reservado
        for (Reserva reserva : reservasActivas) {
            String idRecurso = reserva.getRecurso().getIdentificador();
            usuariosPorRecurso.computeIfAbsent(idRecurso, k -> new ArrayList<>())
                             .add(reserva.getUsuario());
        }

        // Verificar recursos disponibles que tienen reservas
        for (RecursoDigital recurso : recursosDisponibles) {
            String idRecurso = recurso.getIdentificador();
            List<Usuario> usuariosInteresados = usuariosPorRecurso.get(idRecurso);

            if (usuariosInteresados != null && !usuariosInteresados.isEmpty()) {
                // Verificar si ya notificamos a estos usuarios sobre este recurso
                List<Usuario> usuariosYaNotificados = recursosNotificados.getOrDefault(idRecurso, new ArrayList<>());

                // Notificar solo a usuarios que no han sido notificados aún
                for (Usuario usuario : usuariosInteresados) {
                    if (!usuariosYaNotificados.contains(usuario)) {
                        enviarAlertaDisponibilidad(recurso, usuario);
                        usuariosYaNotificados.add(usuario);
                    }
                }

                // Actualizar la lista de usuarios notificados
                recursosNotificados.put(idRecurso, usuariosYaNotificados);
            }
        }
    }

    /**
     * Envía una alerta de disponibilidad de recurso.
     *
     * @param recurso Recurso disponible
     * @param usuario Usuario que reservó el recurso
     */
    private void enviarAlertaDisponibilidad(RecursoDigital recurso, Usuario usuario) {
        String mensaje = String.format(
            "✅ DISPONIBLE: El recurso '%s' que reservaste está ahora DISPONIBLE. " +
            "Responde con 'PRESTAR %s' para tomarlo en préstamo inmediatamente.",
            recurso.getIdentificador(),
            recurso.getIdentificador()
        );

        servicioNotificaciones.enviarNotificacionSistema(
            mensaje,
            usuario,
            Notificacion.TipoNotificacion.SISTEMA,
            "Sistema de Alertas"
        );
    }

    /**
     * Notifica a un usuario específico sobre la disponibilidad de un recurso.
     * 
     * @param recurso Recurso disponible
     * @param usuario Usuario a notificar
     */
    public void notificarDisponibilidad(RecursoDigital recurso, Usuario usuario) {
        if (recurso.getEstado() == EstadoRecurso.DISPONIBLE) {
            enviarAlertaDisponibilidad(recurso, usuario);

            // Registrar que este usuario ha sido notificado sobre este recurso
            recursosNotificados.computeIfAbsent(recurso.getIdentificador(), k -> new ArrayList<>())
                              .add(usuario);
        }
    }

    /**
     * Obtiene una lista de recursos que tienen reservas activas o préstamos activos.
     * 
     * @return Lista de recursos con reservas o préstamos
     */
    public List<RecursoDigital> obtenerRecursosDisponiblesConReservas() {
        List<Reserva> reservasActivas = sistemaReservas.obtenerTodasLasReservasActivas();
        List<RecursoDigital> resultado = new ArrayList<>();

        // Crear un conjunto de recursos con reservas
        Map<String, RecursoDigital> recursosConReservasOPrestamos = new HashMap<>();
        for (Reserva reserva : reservasActivas) {
            recursosConReservasOPrestamos.put(reserva.getRecurso().getIdentificador(), reserva.getRecurso());
        }

        // Obtener préstamos activos
        List<um.prog2.prestamos.Prestamo> prestamosActivos = sistemaPrestamos.obtenerTodosPrestamosActivos();
        for (um.prog2.prestamos.Prestamo prestamo : prestamosActivos) {
            recursosConReservasOPrestamos.put(prestamo.getRecurso().getIdentificador(), prestamo.getRecurso());
        }

        // Agregar todos los recursos con reservas o préstamos al resultado
        resultado.addAll(recursosConReservasOPrestamos.values());

        return resultado;
    }

    /**
     * Procesa una respuesta a una alerta, permitiendo tomar en préstamo un recurso.
     *
     * @param respuesta Respuesta del usuario
     * @param usuario Usuario que responde
     * @return true si la respuesta fue procesada correctamente, false en caso contrario
     */
    public boolean procesarRespuestaAlerta(String respuesta, Usuario usuario) {
        if (respuesta != null && respuesta.startsWith("PRESTAR ")) {
            String idRecurso = respuesta.substring(8).trim();
            // Días de préstamo predeterminados (podría ser configurable)
            int diasPrestamo = 7;

            try {
                // Buscar el recurso entre los disponibles
                RecursoDigital recurso = null;
                List<RecursoDigital> recursosDisponibles = gestorRecursos.obtenerRecursosDisponiblesParaPrestamo();
                for (RecursoDigital r : recursosDisponibles) {
                    if (r.getIdentificador().equals(idRecurso)) {
                        recurso = r;
                        break;
                    }
                }

                if (recurso != null) {
                    // Realizar el préstamo
                    sistemaPrestamos.solicitarPrestamo(usuario, recurso, diasPrestamo);

                    // Eliminar de la lista de notificados
                    List<Usuario> usuariosNotificados = recursosNotificados.get(idRecurso);
                    if (usuariosNotificados != null) {
                        usuariosNotificados.remove(usuario);
                        if (usuariosNotificados.isEmpty()) {
                            recursosNotificados.remove(idRecurso);
                        }
                    }

                    return true;
                } else {
                    servicioNotificaciones.enviarNotificacionSistema(
                        "El recurso solicitado no está disponible actualmente.",
                        usuario,
                        Notificacion.TipoNotificacion.ERROR,
                        "Sistema de Alertas"
                    );
                }
            } catch (Exception e) {
                servicioNotificaciones.enviarNotificacionSistema(
                    "Error al realizar préstamo: " + e.getMessage(),
                    usuario,
                    Notificacion.TipoNotificacion.ERROR,
                    "Sistema de Alertas"
                );
            }
        }
        return false;
    }

    /**
     * Detiene el monitoreo de recursos.
     */
    public void detenerMonitoreo() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
