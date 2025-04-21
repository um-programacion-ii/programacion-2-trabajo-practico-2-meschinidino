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
 */
public class ServicioEnvioNotificaciones implements ServicioNotificaciones {
    private final BlockingQueue<Notificacion> colaNotificaciones;
    private final ExecutorService procesadorNotificaciones;
    private final List<ServicioNotificaciones> serviciosNotificacion;
    private final List<Notificacion> historialNotificaciones;
    private boolean mostrarEnConsola;

    /**
     * Constructor del servicio de envío de notificaciones.
     */
    public ServicioEnvioNotificaciones() {
        this.colaNotificaciones = new LinkedBlockingQueue<>();
        this.procesadorNotificaciones = Executors.newSingleThreadExecutor();
        this.serviciosNotificacion = new ArrayList<>();
        this.historialNotificaciones = new ArrayList<>();
        this.mostrarEnConsola = true;

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
     * Procesa una notificación enviándola a través de todos los servicios registrados.
     * 
     * @param notificacion La notificación a procesar
     */
    private void procesarNotificacion(Notificacion notificacion) {
        // Guardar en el historial
        historialNotificaciones.add(notificacion);

        // Mostrar en consola si está habilitado
        if (mostrarEnConsola) {
            System.out.println("NOTIFICACIÓN: " + notificacion);
        }

        // Enviar a través de todos los servicios registrados
        for (ServicioNotificaciones servicio : serviciosNotificacion) {
            servicio.enviarNotificacion(notificacion.getMensaje(), notificacion.getDestinatario());
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
        NotificacionPrestamo notificacion = new NotificacionPrestamo(
                mensaje, usuario, tipo, recurso, idPrestamo);
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
        NotificacionReserva notificacion = new NotificacionReserva(
                mensaje, usuario, tipo, recurso, idReserva, prioridad);
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
        NotificacionSistema notificacion = new NotificacionSistema(
                mensaje, usuario, tipo, origen);
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
