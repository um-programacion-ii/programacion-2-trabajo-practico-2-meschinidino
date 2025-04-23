package um.prog2.alertas;

import um.prog2.notificaciones.Notificacion;
import um.prog2.notificaciones.ServicioEnvioNotificaciones;
import um.prog2.usuario.Usuario;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase que gestiona el historial de alertas y proporciona métodos para
 * visualizarlo en la consola con diferentes filtros.
 */
public class HistorialAlertas {
    private final ServicioEnvioNotificaciones servicioNotificaciones;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Constructor del historial de alertas.
     *
     * @param servicioNotificaciones Servicio de notificaciones que contiene el historial
     */
    public HistorialAlertas(ServicioEnvioNotificaciones servicioNotificaciones) {
        this.servicioNotificaciones = servicioNotificaciones;
    }

    /**
     * Muestra el historial completo de alertas en la consola.
     */
    public void mostrarHistorialCompleto() {
        List<Notificacion> historial = servicioNotificaciones.getHistorialNotificaciones();
        mostrarHistorial(historial, "HISTORIAL COMPLETO DE ALERTAS");
    }

    /**
     * Muestra el historial de alertas filtrado por nivel de urgencia.
     *
     * @param nivelUrgencia Nivel de urgencia para filtrar
     */
    public void mostrarHistorialPorNivelUrgencia(Notificacion.NivelUrgencia nivelUrgencia) {
        List<Notificacion> historial = servicioNotificaciones.getHistorialNotificaciones();
        List<Notificacion> filtrado = historial.stream()
                .filter(n -> n.getNivelUrgencia() == nivelUrgencia)
                .collect(Collectors.toList());
        
        mostrarHistorial(filtrado, "HISTORIAL DE ALERTAS - NIVEL: " + nivelUrgencia);
    }

    /**
     * Muestra el historial de alertas filtrado por tipo de notificación.
     *
     * @param tipo Tipo de notificación para filtrar
     */
    public void mostrarHistorialPorTipo(Notificacion.TipoNotificacion tipo) {
        List<Notificacion> historial = servicioNotificaciones.getHistorialNotificaciones();
        List<Notificacion> filtrado = historial.stream()
                .filter(n -> n.getTipo() == tipo)
                .collect(Collectors.toList());
        
        mostrarHistorial(filtrado, "HISTORIAL DE ALERTAS - TIPO: " + tipo);
    }

    /**
     * Muestra el historial de alertas filtrado por usuario.
     *
     * @param usuario Usuario para filtrar
     */
    public void mostrarHistorialPorUsuario(Usuario usuario) {
        List<Notificacion> historial = servicioNotificaciones.getHistorialNotificaciones();
        List<Notificacion> filtrado = historial.stream()
                .filter(n -> n.getDestinatario().equals(usuario))
                .collect(Collectors.toList());
        
        mostrarHistorial(filtrado, "HISTORIAL DE ALERTAS - USUARIO: " + usuario.getNombre());
    }

    /**
     * Muestra el historial de alertas filtrado por fecha.
     *
     * @param fechaInicio Fecha de inicio para filtrar
     * @param fechaFin Fecha de fin para filtrar
     */
    public void mostrarHistorialPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<Notificacion> historial = servicioNotificaciones.getHistorialNotificaciones();
        List<Notificacion> filtrado = historial.stream()
                .filter(n -> !n.getFechaCreacion().isBefore(fechaInicio) && !n.getFechaCreacion().isAfter(fechaFin))
                .collect(Collectors.toList());
        
        mostrarHistorial(filtrado, "HISTORIAL DE ALERTAS - PERIODO: " + 
                fechaInicio.format(FORMATTER) + " a " + fechaFin.format(FORMATTER));
    }

    /**
     * Muestra las alertas más recientes.
     *
     * @param cantidad Cantidad de alertas a mostrar
     */
    public void mostrarAlertasRecientes(int cantidad) {
        List<Notificacion> historial = servicioNotificaciones.getHistorialNotificaciones();
        List<Notificacion> recientes = historial.stream()
                .sorted(Comparator.comparing(Notificacion::getFechaCreacion).reversed())
                .limit(cantidad)
                .collect(Collectors.toList());
        
        mostrarHistorial(recientes, "ALERTAS RECIENTES (" + cantidad + ")");
    }

    /**
     * Método auxiliar para mostrar un historial de alertas en la consola.
     *
     * @param historial Lista de notificaciones a mostrar
     * @param titulo Título para la sección
     */
    private void mostrarHistorial(List<Notificacion> historial, String titulo) {
        System.out.println("\n===== " + titulo + " =====");
        
        if (historial.isEmpty()) {
            System.out.println("No hay alertas para mostrar.");
            return;
        }
        
        // Ordenar por fecha (más recientes primero)
        historial.sort(Comparator.comparing(Notificacion::getFechaCreacion).reversed());
        
        for (int i = 0; i < historial.size(); i++) {
            System.out.println((i + 1) + ". " + historial.get(i));
        }
        
        System.out.println("Total: " + historial.size() + " alertas");
        System.out.println("=======================================\n");
    }

    /**
     * Obtiene el historial completo de alertas.
     *
     * @return Lista con el historial completo
     */
    public List<Notificacion> obtenerHistorialCompleto() {
        return new ArrayList<>(servicioNotificaciones.getHistorialNotificaciones());
    }
}