package um.prog2.notificaciones;

import um.prog2.interfaces.ServicioNotificaciones;
import um.prog2.usuario.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Servicio para el envío asincrónico de notificaciones utilizando ExecutorService.
 * Implementa la interfaz ServicioNotificaciones para permitir un sistema unificado.
 * Soporta configuración de preferencias de notificación y diferentes niveles de urgencia.
 */
public class ServicioEnvioNotificaciones implements ServicioNotificaciones {
    private final BlockingQueue<Notificacion> colaNotificaciones;
    private final ExecutorService procesadorNotificaciones;
    private final List<ServicioNotificaciones> serviciosNotificacion;
    private final List<Notificacion> historialNotificaciones;
    private boolean mostrarEnConsola;
    private ConfiguracionNotificaciones configuracionNotificaciones;

    /**
     * Constructor del servicio de envío de notificaciones.
     */
    public ServicioEnvioNotificaciones() {
        this.colaNotificaciones = new LinkedBlockingQueue<>();
        this.procesadorNotificaciones = Executors.newSingleThreadExecutor();
        this.serviciosNotificacion = new ArrayList<>();
        this.historialNotificaciones = new ArrayList<>();
        this.mostrarEnConsola = true;
        this.configuracionNotificaciones = new ConfiguracionNotificaciones();

        // Iniciar el procesador de notificaciones
        iniciarProcesador();
    }

    /**
     * Constructor del servicio de envío de notificaciones con configuración personalizada.
     * 
     * @param configuracionNotificaciones Configuración de preferencias de notificación
     */
    public ServicioEnvioNotificaciones(ConfiguracionNotificaciones configuracionNotificaciones) {
        this.colaNotificaciones = new LinkedBlockingQueue<>();
        this.procesadorNotificaciones = Executors.newSingleThreadExecutor();
        this.serviciosNotificacion = new ArrayList<>();
        this.historialNotificaciones = new ArrayList<>();
        this.mostrarEnConsola = true;
        this.configuracionNotificaciones = configuracionNotificaciones;

        // Iniciar el procesador de notificaciones
        iniciarProcesador();
    }

    /**
     * Inicia el procesador de notificaciones.
     */
    private void iniciarProcesador() {
        procesadorNotificaciones.submit(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Notificacion notificacion = colaNotificaciones.take();
                    procesarNotificacion(notificacion);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    /**
     * Procesa una notificación enviándola a través de todos los servicios registrados,
     * respetando las preferencias de notificación del usuario.
     * 
     * @param notificacion La notificación a procesar
     */
    private void procesarNotificacion(Notificacion notificacion) {
        // Guardar en el historial (siempre se guarda, independientemente de las preferencias)
        historialNotificaciones.add(notificacion);

        Usuario usuario = notificacion.getDestinatario();

        // Mostrar en consola si está habilitado y el usuario tiene habilitado este canal
        if (mostrarEnConsola && 
            configuracionNotificaciones.debeEnviarNotificacion(notificacion, 
                ConfiguracionNotificaciones.CanalNotificacion.CONSOLA)) {
            System.out.println("NOTIFICACIÓN: " + notificacion);
        }

        // Enviar a través de los servicios registrados según las preferencias del usuario
        for (ServicioNotificaciones servicio : serviciosNotificacion) {
            // Determinar el canal según el tipo de servicio
            ConfiguracionNotificaciones.CanalNotificacion canal = determinarCanalServicio(servicio);

            // Verificar si el usuario tiene habilitado este canal para este tipo y nivel de notificación
            if (configuracionNotificaciones.debeEnviarNotificacion(notificacion, canal)) {
                servicio.enviarNotificacion(notificacion.getMensaje(), usuario);
            }
        }
    }

    /**
     * Determina el canal de notificación según el tipo de servicio.
     * 
     * @param servicio Servicio de notificaciones
     * @return Canal de notificación correspondiente
     */
    private ConfiguracionNotificaciones.CanalNotificacion determinarCanalServicio(ServicioNotificaciones servicio) {
        if (servicio instanceof ServicioNotificacionesEmail) {
            return ConfiguracionNotificaciones.CanalNotificacion.EMAIL;
        } else if (servicio instanceof ServicioNotificacionesSMS) {
            return ConfiguracionNotificaciones.CanalNotificacion.SMS;
        } else {
            return ConfiguracionNotificaciones.CanalNotificacion.CONSOLA;
        }
    }

    /**
     * Registra un servicio de notificaciones.
     * 
     * @param servicio Servicio a registrar
     */
    public void registrarServicio(ServicioNotificaciones servicio) {
        serviciosNotificacion.add(servicio);
    }

    /**
     * Elimina un servicio de notificaciones.
     * 
     * @param servicio Servicio a eliminar
     */
    public void eliminarServicio(ServicioNotificaciones servicio) {
        serviciosNotificacion.remove(servicio);
    }

    /**
     * Envía una notificación.
     * 
     * @param notificacion Notificación a enviar
     */
    public void enviarNotificacion(Notificacion notificacion) {
        colaNotificaciones.add(notificacion);
    }

    /**
     * Envía una notificación de préstamo.
     * 
     * @param mensaje Mensaje de la notificación
     * @param usuario Usuario destinatario
     * @param tipo Tipo de notificación
     * @param recurso Recurso relacionado
     * @param idPrestamo ID del préstamo
     */
    public void enviarNotificacionPrestamo(String mensaje, Usuario usuario, 
                                          Notificacion.TipoNotificacion tipo,
                                          um.prog2.interfaces.RecursoDigital recurso, 
                                          String idPrestamo) {
        enviarNotificacionPrestamo(mensaje, usuario, tipo, recurso, idPrestamo, Notificacion.NivelUrgencia.INFO);
    }

    /**
     * Envía una notificación de préstamo con nivel de urgencia.
     * 
     * @param mensaje Mensaje de la notificación
     * @param usuario Usuario destinatario
     * @param tipo Tipo de notificación
     * @param recurso Recurso relacionado
     * @param idPrestamo ID del préstamo
     * @param nivelUrgencia Nivel de urgencia de la notificación
     */
    public void enviarNotificacionPrestamo(String mensaje, Usuario usuario, 
                                          Notificacion.TipoNotificacion tipo,
                                          um.prog2.interfaces.RecursoDigital recurso, 
                                          String idPrestamo,
                                          Notificacion.NivelUrgencia nivelUrgencia) {
        NotificacionPrestamo notificacion = new NotificacionPrestamo(
                mensaje, usuario, tipo, recurso, idPrestamo, nivelUrgencia);
        enviarNotificacion(notificacion);
    }

    /**
     * Envía una notificación de reserva.
     * 
     * @param mensaje Mensaje de la notificación
     * @param usuario Usuario destinatario
     * @param tipo Tipo de notificación
     * @param recurso Recurso relacionado
     * @param idReserva ID de la reserva
     * @param prioridad Prioridad de la reserva
     */
    public void enviarNotificacionReserva(String mensaje, Usuario usuario, 
                                         Notificacion.TipoNotificacion tipo,
                                         um.prog2.interfaces.RecursoDigital recurso, 
                                         String idReserva, int prioridad) {
        enviarNotificacionReserva(mensaje, usuario, tipo, recurso, idReserva, prioridad, Notificacion.NivelUrgencia.INFO);
    }

    /**
     * Envía una notificación de reserva con nivel de urgencia.
     * 
     * @param mensaje Mensaje de la notificación
     * @param usuario Usuario destinatario
     * @param tipo Tipo de notificación
     * @param recurso Recurso relacionado
     * @param idReserva ID de la reserva
     * @param prioridad Prioridad de la reserva
     * @param nivelUrgencia Nivel de urgencia de la notificación
     */
    public void enviarNotificacionReserva(String mensaje, Usuario usuario, 
                                         Notificacion.TipoNotificacion tipo,
                                         um.prog2.interfaces.RecursoDigital recurso, 
                                         String idReserva, int prioridad,
                                         Notificacion.NivelUrgencia nivelUrgencia) {
        NotificacionReserva notificacion = new NotificacionReserva(
                mensaje, usuario, tipo, recurso, idReserva, prioridad, nivelUrgencia);
        enviarNotificacion(notificacion);
    }

    /**
     * Envía una notificación del sistema.
     * 
     * @param mensaje Mensaje de la notificación
     * @param usuario Usuario destinatario
     * @param tipo Tipo de notificación
     * @param origen Origen de la notificación
     */
    public void enviarNotificacionSistema(String mensaje, Usuario usuario, 
                                         Notificacion.TipoNotificacion tipo,
                                         String origen) {
        enviarNotificacionSistema(mensaje, usuario, tipo, origen, Notificacion.NivelUrgencia.INFO);
    }

    /**
     * Envía una notificación del sistema con nivel de urgencia.
     * 
     * @param mensaje Mensaje de la notificación
     * @param usuario Usuario destinatario
     * @param tipo Tipo de notificación
     * @param origen Origen de la notificación
     * @param nivelUrgencia Nivel de urgencia de la notificación
     */
    public void enviarNotificacionSistema(String mensaje, Usuario usuario, 
                                         Notificacion.TipoNotificacion tipo,
                                         String origen,
                                         Notificacion.NivelUrgencia nivelUrgencia) {
        NotificacionSistema notificacion = new NotificacionSistema(
                mensaje, usuario, tipo, origen, nivelUrgencia);
        enviarNotificacion(notificacion);
    }

    /**
     * Habilita o deshabilita la visualización de notificaciones en consola.
     * 
     * @param mostrar true para mostrar, false para ocultar
     */
    public void setMostrarEnConsola(boolean mostrar) {
        this.mostrarEnConsola = mostrar;
    }

    /**
     * Obtiene el historial de notificaciones.
     * 
     * @return Lista con el historial de notificaciones
     */
    public List<Notificacion> getHistorialNotificaciones() {
        return new ArrayList<>(historialNotificaciones);
    }

    /**
     * Obtiene la configuración de notificaciones.
     * 
     * @return Configuración de notificaciones
     */
    public ConfiguracionNotificaciones getConfiguracionNotificaciones() {
        return configuracionNotificaciones;
    }

    /**
     * Establece la configuración de notificaciones.
     * 
     * @param configuracionNotificaciones Nueva configuración de notificaciones
     */
    public void setConfiguracionNotificaciones(ConfiguracionNotificaciones configuracionNotificaciones) {
        this.configuracionNotificaciones = configuracionNotificaciones;
    }

    /**
     * Inicializa las preferencias por defecto para un usuario.
     * 
     * @param usuario Usuario a inicializar
     */
    public void inicializarPreferenciasUsuario(Usuario usuario) {
        configuracionNotificaciones.inicializarPreferenciasDefecto(usuario);
    }

    /**
     * Implementación del método de la interfaz ServicioNotificaciones.
     * Envía una notificación simple del sistema.
     * 
     * @param mensaje Mensaje de la notificación
     * @param usuario Usuario destinatario
     */
    @Override
    public void enviarNotificacion(String mensaje, Usuario usuario) {
        enviarNotificacionSistema(
            mensaje,
            usuario,
            Notificacion.TipoNotificacion.SISTEMA,
            "Sistema"
        );
    }

    /**
     * Cierra el servicio de envío de notificaciones.
     */
    public void cerrar() {
        procesadorNotificaciones.shutdown();
    }
}
