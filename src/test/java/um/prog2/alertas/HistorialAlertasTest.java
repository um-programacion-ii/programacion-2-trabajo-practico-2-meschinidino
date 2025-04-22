package um.prog2.alertas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import um.prog2.notificaciones.Notificacion;
import um.prog2.notificaciones.ServicioEnvioNotificaciones;
import um.prog2.usuario.Usuario;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para el historial de alertas.
 */
public class HistorialAlertasTest {
    private HistorialAlertas historialAlertas;
    private ServicioEnvioNotificaciones servicioNotificaciones;
    private Usuario usuario1;
    private Usuario usuario2;

    // Para capturar la salida de la consola
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        servicioNotificaciones = new ServicioEnvioNotificaciones();
        historialAlertas = new HistorialAlertas(servicioNotificaciones);

        usuario1 = new Usuario("Juan", "Pérez", 1001, "juan@example.com", "555111222");
        usuario2 = new Usuario("María", "López", 1002, "maria@example.com", "555333444");

        // Inicializar preferencias de los usuarios
        servicioNotificaciones.inicializarPreferenciasUsuario(usuario1);
        servicioNotificaciones.inicializarPreferenciasUsuario(usuario2);

        // Redirigir la salida estándar para capturarla
        System.setOut(new PrintStream(outContent));

        // Generar algunas notificaciones de prueba
        generarNotificacionesPrueba();
    }

    @Test
    void tearDown() {
        // Restaurar la salida estándar
        System.setOut(originalOut);
    }

    /**
     * Genera notificaciones de prueba con diferentes tipos y niveles de urgencia.
     */
    private void generarNotificacionesPrueba() {
        // Notificaciones para usuario1
        servicioNotificaciones.enviarNotificacionSistema(
            "Mensaje informativo para Juan",
            usuario1,
            Notificacion.TipoNotificacion.SISTEMA,
            "Test",
            Notificacion.NivelUrgencia.INFO
        );

        servicioNotificaciones.enviarNotificacionSistema(
            "Advertencia para Juan",
            usuario1,
            Notificacion.TipoNotificacion.VENCIMIENTO,
            "Test",
            Notificacion.NivelUrgencia.WARNING
        );

        // Notificaciones para usuario2
        servicioNotificaciones.enviarNotificacionSistema(
            "Mensaje informativo para María",
            usuario2,
            Notificacion.TipoNotificacion.SISTEMA,
            "Test",
            Notificacion.NivelUrgencia.INFO
        );

        servicioNotificaciones.enviarNotificacionSistema(
            "Error para María",
            usuario2,
            Notificacion.TipoNotificacion.ERROR,
            "Test",
            Notificacion.NivelUrgencia.ERROR
        );
    }

    @Test
    void testMostrarHistorialCompleto() {
        // Limpiar el buffer de salida
        outContent.reset();

        // Mostrar el historial completo
        historialAlertas.mostrarHistorialCompleto();

        // Verificar que la salida contiene información de todas las notificaciones
        String output = outContent.toString();
        assertTrue(output.contains("HISTORIAL COMPLETO DE ALERTAS"));
        assertTrue(output.contains("Mensaje informativo para Juan"));
        assertTrue(output.contains("Advertencia para Juan"));
        assertTrue(output.contains("Mensaje informativo para María"));
        assertTrue(output.contains("Error para María"));
        assertTrue(output.contains("Total: 4 alertas"));
    }

    @Test
    void testMostrarHistorialPorNivelUrgencia() {
        // Limpiar el buffer de salida
        outContent.reset();

        // Mostrar el historial filtrado por nivel de urgencia INFO
        historialAlertas.mostrarHistorialPorNivelUrgencia(Notificacion.NivelUrgencia.INFO);

        // Verificar que la salida contiene solo las notificaciones de nivel INFO
        String output = outContent.toString();
        System.out.println("[DEBUG_LOG] Output for INFO level: " + output);

        assertTrue(output.contains("HISTORIAL DE ALERTAS - NIVEL: ℹ️ Información"));
        assertTrue(output.contains("Mensaje informativo para Juan"));
        assertTrue(output.contains("Mensaje informativo para María"));

        // Verificar que no contiene mensajes de otros niveles (solo verificamos el texto del mensaje)
        assertFalse(output.contains("Advertencia para Juan"));
        assertFalse(output.contains("Error para María"));
        assertTrue(output.contains("Total: 2 alertas"));

        // Limpiar el buffer de salida
        outContent.reset();

        // Mostrar el historial filtrado por nivel de urgencia WARNING
        historialAlertas.mostrarHistorialPorNivelUrgencia(Notificacion.NivelUrgencia.WARNING);

        // Verificar que la salida contiene solo las notificaciones de nivel WARNING
        output = outContent.toString();
        System.out.println("[DEBUG_LOG] Output for WARNING level: " + output);

        assertTrue(output.contains("HISTORIAL DE ALERTAS - NIVEL: ⚠️ Advertencia"));
        assertTrue(output.contains("Advertencia para Juan"));

        // Verificar que no contiene mensajes de otros niveles (solo verificamos el texto del mensaje)
        assertFalse(output.contains("Mensaje informativo para Juan"));
        assertFalse(output.contains("Mensaje informativo para María"));
        assertFalse(output.contains("Error para María"));
        assertTrue(output.contains("Total: 1 alertas"));
    }

    @Test
    void testMostrarHistorialPorTipo() {
        // Limpiar el buffer de salida
        outContent.reset();

        // Mostrar el historial filtrado por tipo SISTEMA
        historialAlertas.mostrarHistorialPorTipo(Notificacion.TipoNotificacion.SISTEMA);

        // Verificar que la salida contiene solo las notificaciones de tipo SISTEMA
        String output = outContent.toString();
        assertTrue(output.contains("HISTORIAL DE ALERTAS - TIPO: Sistema"));
        assertTrue(output.contains("Mensaje informativo para Juan"));
        assertTrue(output.contains("Mensaje informativo para María"));
        assertFalse(output.contains("Advertencia para Juan"));
        assertFalse(output.contains("Error para María"));

        // Limpiar el buffer de salida
        outContent.reset();

        // Mostrar el historial filtrado por tipo ERROR
        historialAlertas.mostrarHistorialPorTipo(Notificacion.TipoNotificacion.ERROR);

        // Verificar que la salida contiene solo las notificaciones de tipo ERROR
        output = outContent.toString();
        assertTrue(output.contains("HISTORIAL DE ALERTAS - TIPO: Error"));
        assertTrue(output.contains("Error para María"));
        assertFalse(output.contains("Mensaje informativo para Juan"));
        assertFalse(output.contains("Mensaje informativo para María"));
        assertFalse(output.contains("Advertencia para Juan"));
    }

    @Test
    void testMostrarHistorialPorUsuario() {
        // Limpiar el buffer de salida
        outContent.reset();

        // Mostrar el historial filtrado por usuario1
        historialAlertas.mostrarHistorialPorUsuario(usuario1);

        // Verificar que la salida contiene solo las notificaciones para usuario1
        String output = outContent.toString();
        System.out.println("[DEBUG_LOG] Output for user Juan: " + output);

        assertTrue(output.contains("HISTORIAL DE ALERTAS - USUARIO: Juan"));

        // Verificar que contiene los mensajes para Juan
        assertTrue(output.contains("Mensaje informativo para Juan"), "Output should contain 'Mensaje informativo para Juan'");
        assertTrue(output.contains("Advertencia para Juan"), "Output should contain 'Advertencia para Juan'");

        // Verificar que no contiene mensajes para María
        assertFalse(output.contains("Mensaje informativo para María"), "Output should not contain 'Mensaje informativo para María'");
        assertFalse(output.contains("Error para María"), "Output should not contain 'Error para María'");

        assertTrue(output.contains("Total: 2 alertas"), "Output should contain 'Total: 2 alertas'");
    }

    @Test
    void testMostrarAlertasRecientes() {
        // Limpiar el buffer de salida
        outContent.reset();

        // Mostrar las 2 alertas más recientes
        historialAlertas.mostrarAlertasRecientes(2);

        // Verificar que la salida contiene solo las 2 alertas más recientes
        String output = outContent.toString();
        assertTrue(output.contains("ALERTAS RECIENTES (2)"));
        assertTrue(output.contains("Total: 2 alertas"));
    }

    @Test
    void testObtenerHistorialCompleto() {
        // Verificar que el método devuelve todas las notificaciones
        assertEquals(4, historialAlertas.obtenerHistorialCompleto().size());
    }
}
