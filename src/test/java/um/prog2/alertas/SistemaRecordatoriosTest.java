package um.prog2.alertas;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import um.prog2.notificaciones.Notificacion;
import um.prog2.notificaciones.ServicioEnvioNotificaciones;
import um.prog2.usuario.Usuario;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para el sistema de recordatorios periódicos.
 */
public class SistemaRecordatoriosTest {
    private SistemaRecordatorios sistemaRecordatorios;
    private ServicioEnvioNotificaciones servicioNotificaciones;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        servicioNotificaciones = new ServicioEnvioNotificaciones();
        sistemaRecordatorios = new SistemaRecordatorios(servicioNotificaciones);
        usuario = new Usuario("Test", "User", 12345, "test@example.com", 555123456);

        // Inicializar preferencias del usuario
        servicioNotificaciones.inicializarPreferenciasUsuario(usuario);
    }

    @AfterEach
    void tearDown() {
        sistemaRecordatorios.cerrar();
        servicioNotificaciones.cerrar();
    }

    @Test
    void testProgramarRecordatorio() {
        // Programar un recordatorio
        String idRecordatorio = sistemaRecordatorios.programarRecordatorio(
            "Recordatorio de prueba",
            "Este es un mensaje de prueba",
            usuario,
            1, // 1 minuto de intervalo (para pruebas rápidas)
            Notificacion.NivelUrgencia.INFO
        );

        // Verificar que el recordatorio se haya creado correctamente
        assertNotNull(idRecordatorio);
        assertTrue(idRecordatorio.startsWith("REC-"));

        // Verificar que el recordatorio esté en la lista de activos
        List<SistemaRecordatorios.Recordatorio> activos = sistemaRecordatorios.getRecordatoriosActivos();
        assertEquals(1, activos.size());
        assertEquals("Recordatorio de prueba", activos.get(0).getTitulo());
        assertEquals("Este es un mensaje de prueba", activos.get(0).getMensaje());
        assertEquals(usuario, activos.get(0).getUsuario());
        assertEquals(1, activos.get(0).getIntervaloMinutos());
        assertEquals(Notificacion.NivelUrgencia.INFO, activos.get(0).getNivelUrgencia());
        assertTrue(activos.get(0).isActivo());
    }

    @Test
    void testCancelarRecordatorio() {
        // Programar un recordatorio
        String idRecordatorio = sistemaRecordatorios.programarRecordatorio(
            "Recordatorio a cancelar",
            "Este recordatorio será cancelado",
            usuario,
            5,
            Notificacion.NivelUrgencia.WARNING
        );

        // Verificar que el recordatorio esté activo
        List<SistemaRecordatorios.Recordatorio> activos = sistemaRecordatorios.getRecordatoriosActivos();
        assertEquals(1, activos.size());

        // Cancelar el recordatorio
        boolean resultado = sistemaRecordatorios.cancelarRecordatorio(idRecordatorio);

        // Verificar que se haya cancelado correctamente
        assertTrue(resultado);

        // Verificar que ya no esté en la lista de activos
        activos = sistemaRecordatorios.getRecordatoriosActivos();
        assertEquals(0, activos.size());

        // Verificar que siga en el historial pero marcado como inactivo
        List<SistemaRecordatorios.Recordatorio> historial = sistemaRecordatorios.getHistorialRecordatorios();
        assertEquals(1, historial.size());
        assertFalse(historial.get(0).isActivo());
    }

    @Test
    void testRecordatorioConDiferentesNivelesUrgencia() {
        // Programar recordatorios con diferentes niveles de urgencia
        String idInfo = sistemaRecordatorios.programarRecordatorio(
            "Recordatorio INFO",
            "Mensaje informativo",
            usuario,
            10,
            Notificacion.NivelUrgencia.INFO
        );

        String idWarning = sistemaRecordatorios.programarRecordatorio(
            "Recordatorio WARNING",
            "Mensaje de advertencia",
            usuario,
            10,
            Notificacion.NivelUrgencia.WARNING
        );

        String idError = sistemaRecordatorios.programarRecordatorio(
            "Recordatorio ERROR",
            "Mensaje de error",
            usuario,
            10,
            Notificacion.NivelUrgencia.ERROR
        );

        // Verificar que se hayan creado correctamente
        List<SistemaRecordatorios.Recordatorio> activos = sistemaRecordatorios.getRecordatoriosActivos();
        assertEquals(3, activos.size());

        // Verificar los niveles de urgencia usando el título para identificar los recordatorios
        boolean foundInfo = false;
        boolean foundWarning = false;
        boolean foundError = false;

        for (SistemaRecordatorios.Recordatorio r : activos) {
            if (r.getTitulo().equals("Recordatorio INFO")) {
                assertEquals(Notificacion.NivelUrgencia.INFO, r.getNivelUrgencia());
                foundInfo = true;
            } else if (r.getTitulo().equals("Recordatorio WARNING")) {
                assertEquals(Notificacion.NivelUrgencia.WARNING, r.getNivelUrgencia());
                foundWarning = true;
            } else if (r.getTitulo().equals("Recordatorio ERROR")) {
                assertEquals(Notificacion.NivelUrgencia.ERROR, r.getNivelUrgencia());
                foundError = true;
            }
        }

        // Verificar que se encontraron todos los recordatorios
        assertTrue(foundInfo, "No se encontró el recordatorio INFO");
        assertTrue(foundWarning, "No se encontró el recordatorio WARNING");
        assertTrue(foundError, "No se encontró el recordatorio ERROR");
    }

    @Test
    void testEnvioRecordatorio() throws InterruptedException {
        // Programar un recordatorio con intervalo muy corto para la prueba
        sistemaRecordatorios.programarRecordatorio(
            "Recordatorio inmediato",
            "Este recordatorio debería enviarse inmediatamente",
            usuario,
            1, // 1 minuto
            Notificacion.NivelUrgencia.INFO
        );

        // Esperar un poco para que se envíe el recordatorio
        TimeUnit.SECONDS.sleep(2);

        // Verificar que se haya enviado la notificación
        List<Notificacion> historial = servicioNotificaciones.getHistorialNotificaciones();
        assertFalse(historial.isEmpty());

        // Verificar el contenido de la notificación
        boolean encontrada = false;
        for (Notificacion n : historial) {
            if (n.getMensaje().contains("Recordatorio inmediato")) {
                encontrada = true;
                assertEquals(Notificacion.TipoNotificacion.SISTEMA, n.getTipo());
                assertEquals(Notificacion.NivelUrgencia.INFO, n.getNivelUrgencia());
                assertEquals(usuario, n.getDestinatario());
                break;
            }
        }
        assertTrue(encontrada, "No se encontró la notificación del recordatorio");
    }
}
