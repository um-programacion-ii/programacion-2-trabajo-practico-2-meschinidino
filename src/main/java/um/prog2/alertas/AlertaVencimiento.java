package um.prog2.alertas;

import um.prog2.notificaciones.Notificacion;
import um.prog2.notificaciones.ServicioEnvioNotificaciones;
import um.prog2.prestamos.Prestamo;
import um.prog2.prestamos.SistemaPrestamos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Clase que monitorea las fechas de devolución de préstamos y envía alertas
 * cuando están próximos a vencer o ya han vencido.
 */
public class AlertaVencimiento {
    private final SistemaPrestamos sistemaPrestamos;
    private final ServicioEnvioNotificaciones servicioNotificaciones;
    private final ScheduledExecutorService scheduler;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Constructor de la clase AlertaVencimiento.
     *
     * @param sistemaPrestamos Sistema de préstamos a monitorear
     * @param servicioNotificaciones Servicio para enviar notificaciones
     */
    public AlertaVencimiento(SistemaPrestamos sistemaPrestamos, ServicioEnvioNotificaciones servicioNotificaciones) {
        this.sistemaPrestamos = sistemaPrestamos;
        this.servicioNotificaciones = servicioNotificaciones;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    /**
     * Inicia el monitoreo de préstamos, verificando periódicamente si hay préstamos
     * próximos a vencer o vencidos.
     *
     * @param intervaloMinutos Intervalo en minutos entre cada verificación
     */
    public void iniciarMonitoreo(int intervaloMinutos) {
        scheduler.scheduleAtFixedRate(
            this::verificarPrestamos,
            0,
            intervaloMinutos,
            TimeUnit.MINUTES
        );
    }

    /**
     * Verifica todos los préstamos activos y envía alertas para aquellos
     * que están próximos a vencer o ya han vencido.
     */
    private void verificarPrestamos() {
        List<Prestamo> prestamosActivos = sistemaPrestamos.obtenerTodosPrestamosActivos();
        LocalDateTime ahora = LocalDateTime.now();

        for (Prestamo prestamo : prestamosActivos) {
            if (prestamo.isActivo()) {
                long diasHastaVencimiento = ChronoUnit.DAYS.between(ahora, prestamo.getFechaDevolucion());

                if (diasHastaVencimiento == 1) {
                    // Alerta 1 día antes del vencimiento
                    enviarAlertaProximoVencimiento(prestamo);
                } else if (diasHastaVencimiento == 0) {
                    // Alerta el día del vencimiento
                    enviarAlertaVencimientoHoy(prestamo);
                } else if (diasHastaVencimiento < 0) {
                    // Alerta de préstamo vencido
                    enviarAlertaVencido(prestamo);
                }
            }
        }
    }

    /**
     * Envía una alerta de próximo vencimiento (1 día antes).
     *
     * @param prestamo Préstamo próximo a vencer
     */
    private void enviarAlertaProximoVencimiento(Prestamo prestamo) {
        String mensaje = String.format(
            "⚠️ ALERTA: Su préstamo del recurso '%s' vence MAÑANA (%s). " +
            "Responda con 'RENOVAR %s' para extender el préstamo.",
            prestamo.getRecurso().getIdentificador(),
            prestamo.getFechaDevolucion().format(FORMATTER),
            prestamo.getId()
        );

        servicioNotificaciones.enviarNotificacionPrestamo(
            mensaje,
            prestamo.getUsuario(),
            Notificacion.TipoNotificacion.VENCIMIENTO,
            prestamo.getRecurso(),
            prestamo.getId()
        );
    }

    /**
     * Envía una alerta de vencimiento el mismo día.
     *
     * @param prestamo Préstamo que vence hoy
     */
    private void enviarAlertaVencimientoHoy(Prestamo prestamo) {
        String mensaje = String.format(
            "🔔 ALERTA: Su préstamo del recurso '%s' vence HOY (%s). " +
            "Responda con 'RENOVAR %s' para extender el préstamo o devuelva el recurso.",
            prestamo.getRecurso().getIdentificador(),
            prestamo.getFechaDevolucion().format(FORMATTER),
            prestamo.getId()
        );

        servicioNotificaciones.enviarNotificacionPrestamo(
            mensaje,
            prestamo.getUsuario(),
            Notificacion.TipoNotificacion.VENCIMIENTO,
            prestamo.getRecurso(),
            prestamo.getId()
        );
    }

    /**
     * Envía una alerta de préstamo vencido.
     *
     * @param prestamo Préstamo vencido
     */
    private void enviarAlertaVencido(Prestamo prestamo) {
        String mensaje = String.format(
            "🚨 ALERTA: Su préstamo del recurso '%s' está VENCIDO desde el %s. " +
            "Responda con 'RENOVAR %s' para extender el préstamo o devuelva el recurso inmediatamente.",
            prestamo.getRecurso().getIdentificador(),
            prestamo.getFechaDevolucion().format(FORMATTER),
            prestamo.getId()
        );

        servicioNotificaciones.enviarNotificacionPrestamo(
            mensaje,
            prestamo.getUsuario(),
            Notificacion.TipoNotificacion.VENCIMIENTO,
            prestamo.getRecurso(),
            prestamo.getId()
        );
    }

    /**
     * Procesa una respuesta a una alerta, permitiendo renovar un préstamo.
     *
     * @param respuesta Respuesta del usuario
     * @param usuario Usuario que responde
     * @return true si la respuesta fue procesada correctamente, false en caso contrario
     */
    public boolean procesarRespuestaAlerta(String respuesta, um.prog2.usuario.Usuario usuario) {
        if (respuesta != null && respuesta.startsWith("RENOVAR ")) {
            String idPrestamo = respuesta.substring(8).trim();
            // Días de renovación predeterminados (podría ser configurable)
            int diasExtension = 7;

            try {
                sistemaPrestamos.solicitarRenovacion(idPrestamo, usuario, diasExtension);
                return true;
            } catch (Exception e) {
                servicioNotificaciones.enviarNotificacionSistema(
                    "Error al renovar préstamo: " + e.getMessage(),
                    usuario,
                    Notificacion.TipoNotificacion.ERROR,
                    "Sistema de Alertas"
                );
                return false;
            }
        }
        return false;
    }

    /**
     * Detiene el monitoreo de préstamos.
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
