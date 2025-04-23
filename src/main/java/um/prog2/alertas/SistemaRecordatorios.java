package um.prog2.alertas;

import um.prog2.notificaciones.Notificacion;
import um.prog2.notificaciones.ServicioEnvioNotificaciones;
import um.prog2.usuario.Usuario;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Sistema de recordatorios periódicos que permite enviar notificaciones
 * programadas con diferentes niveles de urgencia.
 */
public class SistemaRecordatorios {
    private final ServicioEnvioNotificaciones servicioNotificaciones;
    private final ScheduledExecutorService scheduler;
    private final Map<String, ScheduledFuture<?>> recordatoriosActivos;
    private final List<Recordatorio> historialRecordatorios;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Constructor del sistema de recordatorios.
     *
     * @param servicioNotificaciones Servicio para enviar notificaciones
     */
    public SistemaRecordatorios(ServicioEnvioNotificaciones servicioNotificaciones) {
        this.servicioNotificaciones = servicioNotificaciones;
        this.scheduler = Executors.newScheduledThreadPool(2);
        this.recordatoriosActivos = new HashMap<>();
        this.historialRecordatorios = new ArrayList<>();
    }

    /**
     * Programa un recordatorio periódico.
     *
     * @param titulo Título del recordatorio
     * @param mensaje Mensaje del recordatorio
     * @param usuario Usuario destinatario
     * @param intervaloMinutos Intervalo en minutos entre cada recordatorio
     * @param nivelUrgencia Nivel de urgencia del recordatorio
     * @return ID del recordatorio programado
     */
    public String programarRecordatorio(String titulo, String mensaje, Usuario usuario, 
                                      int intervaloMinutos, Notificacion.NivelUrgencia nivelUrgencia) {
        // Generar ID único para el recordatorio
        String idRecordatorio = generarIdRecordatorio();
        
        // Crear objeto recordatorio para el historial
        Recordatorio recordatorio = new Recordatorio(
            idRecordatorio, titulo, mensaje, usuario, intervaloMinutos, nivelUrgencia
        );
        historialRecordatorios.add(recordatorio);
        
        // Programar la tarea periódica
        ScheduledFuture<?> tarea = scheduler.scheduleAtFixedRate(
            () -> enviarRecordatorio(recordatorio),
            0,
            intervaloMinutos,
            TimeUnit.MINUTES
        );
        
        // Guardar la referencia a la tarea para poder cancelarla después
        recordatoriosActivos.put(idRecordatorio, tarea);
        
        return idRecordatorio;
    }
    
    /**
     * Envía un recordatorio al usuario.
     *
     * @param recordatorio Recordatorio a enviar
     */
    private void enviarRecordatorio(Recordatorio recordatorio) {
        String mensajeCompleto = String.format(
            "%s: %s [Recordatorio programado cada %d minutos]",
            recordatorio.getTitulo(),
            recordatorio.getMensaje(),
            recordatorio.getIntervaloMinutos()
        );
        
        servicioNotificaciones.enviarNotificacionSistema(
            mensajeCompleto,
            recordatorio.getUsuario(),
            Notificacion.TipoNotificacion.SISTEMA,
            "Sistema de Recordatorios",
            recordatorio.getNivelUrgencia()
        );
        
        // Actualizar la última ejecución
        recordatorio.setUltimaEjecucion(LocalDateTime.now());
    }
    
    /**
     * Cancela un recordatorio programado.
     *
     * @param idRecordatorio ID del recordatorio a cancelar
     * @return true si se canceló correctamente, false si no existe
     */
    public boolean cancelarRecordatorio(String idRecordatorio) {
        ScheduledFuture<?> tarea = recordatoriosActivos.get(idRecordatorio);
        if (tarea != null) {
            tarea.cancel(false);
            recordatoriosActivos.remove(idRecordatorio);
            
            // Actualizar estado en el historial
            for (Recordatorio r : historialRecordatorios) {
                if (r.getId().equals(idRecordatorio)) {
                    r.setActivo(false);
                    break;
                }
            }
            
            return true;
        }
        return false;
    }
    
    /**
     * Obtiene el historial de recordatorios.
     *
     * @return Lista con el historial de recordatorios
     */
    public List<Recordatorio> getHistorialRecordatorios() {
        return new ArrayList<>(historialRecordatorios);
    }
    
    /**
     * Obtiene los recordatorios activos.
     *
     * @return Lista con los recordatorios activos
     */
    public List<Recordatorio> getRecordatoriosActivos() {
        List<Recordatorio> activos = new ArrayList<>();
        for (Recordatorio r : historialRecordatorios) {
            if (r.isActivo()) {
                activos.add(r);
            }
        }
        return activos;
    }
    
    /**
     * Genera un ID único para un recordatorio.
     *
     * @return ID único
     */
    private String generarIdRecordatorio() {
        return "REC-" + System.currentTimeMillis();
    }
    
    /**
     * Cierra el sistema de recordatorios.
     */
    public void cerrar() {
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
    
    /**
     * Clase interna que representa un recordatorio.
     */
    public static class Recordatorio {
        private final String id;
        private final String titulo;
        private final String mensaje;
        private final Usuario usuario;
        private final int intervaloMinutos;
        private final Notificacion.NivelUrgencia nivelUrgencia;
        private LocalDateTime fechaCreacion;
        private LocalDateTime ultimaEjecucion;
        private boolean activo;
        
        /**
         * Constructor de un recordatorio.
         *
         * @param id ID del recordatorio
         * @param titulo Título del recordatorio
         * @param mensaje Mensaje del recordatorio
         * @param usuario Usuario destinatario
         * @param intervaloMinutos Intervalo en minutos
         * @param nivelUrgencia Nivel de urgencia
         */
        public Recordatorio(String id, String titulo, String mensaje, Usuario usuario, 
                           int intervaloMinutos, Notificacion.NivelUrgencia nivelUrgencia) {
            this.id = id;
            this.titulo = titulo;
            this.mensaje = mensaje;
            this.usuario = usuario;
            this.intervaloMinutos = intervaloMinutos;
            this.nivelUrgencia = nivelUrgencia;
            this.fechaCreacion = LocalDateTime.now();
            this.activo = true;
        }
        
        // Getters
        public String getId() { return id; }
        public String getTitulo() { return titulo; }
        public String getMensaje() { return mensaje; }
        public Usuario getUsuario() { return usuario; }
        public int getIntervaloMinutos() { return intervaloMinutos; }
        public Notificacion.NivelUrgencia getNivelUrgencia() { return nivelUrgencia; }
        public LocalDateTime getFechaCreacion() { return fechaCreacion; }
        public LocalDateTime getUltimaEjecucion() { return ultimaEjecucion; }
        public boolean isActivo() { return activo; }
        
        // Setters
        public void setUltimaEjecucion(LocalDateTime ultimaEjecucion) { this.ultimaEjecucion = ultimaEjecucion; }
        public void setActivo(boolean activo) { this.activo = activo; }
        
        @Override
        public String toString() {
            return String.format("[%s] %s: %s (Cada %d min) - %s - %s",
                id, titulo, mensaje, intervaloMinutos, 
                nivelUrgencia, activo ? "ACTIVO" : "INACTIVO");
        }
    }
}