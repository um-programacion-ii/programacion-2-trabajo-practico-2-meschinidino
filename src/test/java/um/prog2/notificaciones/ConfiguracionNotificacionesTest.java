package um.prog2.notificaciones;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import um.prog2.usuario.Usuario;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la configuración de preferencias de notificación.
 */
public class ConfiguracionNotificacionesTest {
    private ConfiguracionNotificaciones configuracion;
    private Usuario usuario;
    private Notificacion notificacionInfo;
    private Notificacion notificacionWarning;
    private Notificacion notificacionError;

    @BeforeEach
    void setUp() {
        configuracion = new ConfiguracionNotificaciones();
        usuario = new Usuario("Test", "User", 12345, "test@example.com", "555123456");

        // Crear notificaciones de prueba con diferentes niveles de urgencia
        notificacionInfo = new NotificacionSistema(
            "Mensaje informativo",
            usuario,
            Notificacion.TipoNotificacion.SISTEMA,
            "Test",
            Notificacion.NivelUrgencia.INFO
        );

        notificacionWarning = new NotificacionSistema(
            "Mensaje de advertencia",
            usuario,
            Notificacion.TipoNotificacion.VENCIMIENTO,
            "Test",
            Notificacion.NivelUrgencia.WARNING
        );

        notificacionError = new NotificacionSistema(
            "Mensaje de error",
            usuario,
            Notificacion.TipoNotificacion.ERROR,
            "Test",
            Notificacion.NivelUrgencia.ERROR
        );
    }

    @Test
    void testInicializarPreferenciasDefecto() {
        // Inicializar preferencias por defecto
        configuracion.inicializarPreferenciasDefecto(usuario);

        // Verificar que todos los tipos están habilitados
        for (Notificacion.TipoNotificacion tipo : Notificacion.TipoNotificacion.values()) {
            assertTrue(configuracion.esTipoHabilitado(usuario, tipo));
        }

        // Verificar que todos los niveles están habilitados
        for (Notificacion.NivelUrgencia nivel : Notificacion.NivelUrgencia.values()) {
            assertTrue(configuracion.esNivelHabilitado(usuario, nivel));
        }

        // Verificar que todos los canales están habilitados
        for (ConfiguracionNotificaciones.CanalNotificacion canal : ConfiguracionNotificaciones.CanalNotificacion.values()) {
            assertTrue(configuracion.esCanalHabilitado(usuario, canal));
        }
    }

    @Test
    void testHabilitarDeshabilitarTipoNotificacion() {
        // Inicializar preferencias por defecto
        configuracion.inicializarPreferenciasDefecto(usuario);

        // Deshabilitar un tipo
        configuracion.deshabilitarTipoNotificacion(usuario, Notificacion.TipoNotificacion.VENCIMIENTO);

        // Verificar que el tipo está deshabilitado
        assertFalse(configuracion.esTipoHabilitado(usuario, Notificacion.TipoNotificacion.VENCIMIENTO));

        // Verificar que otros tipos siguen habilitados
        assertTrue(configuracion.esTipoHabilitado(usuario, Notificacion.TipoNotificacion.SISTEMA));

        // Habilitar el tipo nuevamente
        configuracion.habilitarTipoNotificacion(usuario, Notificacion.TipoNotificacion.VENCIMIENTO);

        // Verificar que el tipo está habilitado de nuevo
        assertTrue(configuracion.esTipoHabilitado(usuario, Notificacion.TipoNotificacion.VENCIMIENTO));
    }

    @Test
    void testHabilitarDeshabilitarNivelUrgencia() {
        // Inicializar preferencias por defecto
        configuracion.inicializarPreferenciasDefecto(usuario);

        // Deshabilitar un nivel
        configuracion.deshabilitarNivelUrgencia(usuario, Notificacion.NivelUrgencia.WARNING);

        // Verificar que el nivel está deshabilitado
        assertFalse(configuracion.esNivelHabilitado(usuario, Notificacion.NivelUrgencia.WARNING));

        // Verificar que otros niveles siguen habilitados
        assertTrue(configuracion.esNivelHabilitado(usuario, Notificacion.NivelUrgencia.INFO));
        assertTrue(configuracion.esNivelHabilitado(usuario, Notificacion.NivelUrgencia.ERROR));

        // Habilitar el nivel nuevamente
        configuracion.habilitarNivelUrgencia(usuario, Notificacion.NivelUrgencia.WARNING);

        // Verificar que el nivel está habilitado de nuevo
        assertTrue(configuracion.esNivelHabilitado(usuario, Notificacion.NivelUrgencia.WARNING));
    }

    @Test
    void testHabilitarDeshabilitarCanalNotificacion() {
        // Inicializar preferencias por defecto
        configuracion.inicializarPreferenciasDefecto(usuario);

        // Deshabilitar un canal
        configuracion.deshabilitarCanalNotificacion(usuario, ConfiguracionNotificaciones.CanalNotificacion.EMAIL);

        // Verificar que el canal está deshabilitado
        assertFalse(configuracion.esCanalHabilitado(usuario, ConfiguracionNotificaciones.CanalNotificacion.EMAIL));

        // Verificar que otros canales siguen habilitados
        assertTrue(configuracion.esCanalHabilitado(usuario, ConfiguracionNotificaciones.CanalNotificacion.CONSOLA));

        // Habilitar el canal nuevamente
        configuracion.habilitarCanalNotificacion(usuario, ConfiguracionNotificaciones.CanalNotificacion.EMAIL);

        // Verificar que el canal está habilitado de nuevo
        assertTrue(configuracion.esCanalHabilitado(usuario, ConfiguracionNotificaciones.CanalNotificacion.EMAIL));
    }

    @Test
    void testDebeEnviarNotificacion() {
        // Inicializar preferencias por defecto
        configuracion.inicializarPreferenciasDefecto(usuario);

        // Verificar que todas las notificaciones deben enviarse por defecto
        assertTrue(configuracion.debeEnviarNotificacion(notificacionInfo, ConfiguracionNotificaciones.CanalNotificacion.CONSOLA));
        assertTrue(configuracion.debeEnviarNotificacion(notificacionWarning, ConfiguracionNotificaciones.CanalNotificacion.EMAIL));
        assertTrue(configuracion.debeEnviarNotificacion(notificacionError, ConfiguracionNotificaciones.CanalNotificacion.SMS));

        // Deshabilitar un nivel de urgencia
        configuracion.deshabilitarNivelUrgencia(usuario, Notificacion.NivelUrgencia.WARNING);

        // Verificar que las notificaciones de ese nivel no deben enviarse
        assertTrue(configuracion.debeEnviarNotificacion(notificacionInfo, ConfiguracionNotificaciones.CanalNotificacion.CONSOLA));
        assertFalse(configuracion.debeEnviarNotificacion(notificacionWarning, ConfiguracionNotificaciones.CanalNotificacion.EMAIL));
        assertTrue(configuracion.debeEnviarNotificacion(notificacionError, ConfiguracionNotificaciones.CanalNotificacion.SMS));

        // Deshabilitar un tipo de notificación
        configuracion.deshabilitarTipoNotificacion(usuario, Notificacion.TipoNotificacion.ERROR);

        // Verificar que las notificaciones de ese tipo no deben enviarse
        assertTrue(configuracion.debeEnviarNotificacion(notificacionInfo, ConfiguracionNotificaciones.CanalNotificacion.CONSOLA));
        assertFalse(configuracion.debeEnviarNotificacion(notificacionWarning, ConfiguracionNotificaciones.CanalNotificacion.EMAIL));
        assertFalse(configuracion.debeEnviarNotificacion(notificacionError, ConfiguracionNotificaciones.CanalNotificacion.SMS));

        // Deshabilitar un canal
        configuracion.deshabilitarCanalNotificacion(usuario, ConfiguracionNotificaciones.CanalNotificacion.CONSOLA);

        // Verificar que las notificaciones por ese canal no deben enviarse
        assertFalse(configuracion.debeEnviarNotificacion(notificacionInfo, ConfiguracionNotificaciones.CanalNotificacion.CONSOLA));
        assertTrue(configuracion.debeEnviarNotificacion(notificacionInfo, ConfiguracionNotificaciones.CanalNotificacion.EMAIL));
    }

    @Test
    void testUsuarioSinPreferencias() {
        // Crear un usuario sin preferencias inicializadas
        Usuario usuarioNuevo = new Usuario("Nuevo", "Usuario", 9999, "nuevo@example.com", "555999888");

        // Verificar que por defecto nada está habilitado
        assertFalse(configuracion.esTipoHabilitado(usuarioNuevo, Notificacion.TipoNotificacion.SISTEMA));
        assertFalse(configuracion.esNivelHabilitado(usuarioNuevo, Notificacion.NivelUrgencia.INFO));
        assertFalse(configuracion.esCanalHabilitado(usuarioNuevo, ConfiguracionNotificaciones.CanalNotificacion.CONSOLA));

        // Habilitar manualmente algunas preferencias
        configuracion.habilitarTipoNotificacion(usuarioNuevo, Notificacion.TipoNotificacion.SISTEMA);
        configuracion.habilitarNivelUrgencia(usuarioNuevo, Notificacion.NivelUrgencia.INFO);
        configuracion.habilitarCanalNotificacion(usuarioNuevo, ConfiguracionNotificaciones.CanalNotificacion.CONSOLA);

        // Verificar que ahora están habilitadas
        assertTrue(configuracion.esTipoHabilitado(usuarioNuevo, Notificacion.TipoNotificacion.SISTEMA));
        assertTrue(configuracion.esNivelHabilitado(usuarioNuevo, Notificacion.NivelUrgencia.INFO));
        assertTrue(configuracion.esCanalHabilitado(usuarioNuevo, ConfiguracionNotificaciones.CanalNotificacion.CONSOLA));

        // Pero otras preferencias siguen deshabilitadas
        assertFalse(configuracion.esTipoHabilitado(usuarioNuevo, Notificacion.TipoNotificacion.ERROR));
        assertFalse(configuracion.esNivelHabilitado(usuarioNuevo, Notificacion.NivelUrgencia.ERROR));
        assertFalse(configuracion.esCanalHabilitado(usuarioNuevo, ConfiguracionNotificaciones.CanalNotificacion.EMAIL));
    }
}
