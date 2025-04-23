package um.prog2.notificaciones;

import um.prog2.usuario.Usuario;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Clase que gestiona las preferencias de notificación de los usuarios.
 * Permite configurar qué tipos de notificaciones y niveles de urgencia
 * desea recibir cada usuario.
 */
public class ConfiguracionNotificaciones {
    // Mapa de usuario -> tipos de notificación habilitados
    private final Map<Usuario, Set<Notificacion.TipoNotificacion>> tiposHabilitados;
    
    // Mapa de usuario -> niveles de urgencia habilitados
    private final Map<Usuario, Set<Notificacion.NivelUrgencia>> nivelesHabilitados;
    
    // Mapa de usuario -> canales de notificación habilitados
    private final Map<Usuario, Set<CanalNotificacion>> canalesHabilitados;
    
    /**
     * Constructor de la configuración de notificaciones.
     */
    public ConfiguracionNotificaciones() {
        this.tiposHabilitados = new HashMap<>();
        this.nivelesHabilitados = new HashMap<>();
        this.canalesHabilitados = new HashMap<>();
    }
    
    /**
     * Inicializa las preferencias por defecto para un usuario.
     * Por defecto, se habilitan todos los tipos, niveles y canales.
     *
     * @param usuario Usuario a inicializar
     */
    public void inicializarPreferenciasDefecto(Usuario usuario) {
        // Habilitar todos los tipos de notificación
        Set<Notificacion.TipoNotificacion> todosTipos = new HashSet<>();
        for (Notificacion.TipoNotificacion tipo : Notificacion.TipoNotificacion.values()) {
            todosTipos.add(tipo);
        }
        tiposHabilitados.put(usuario, todosTipos);
        
        // Habilitar todos los niveles de urgencia
        Set<Notificacion.NivelUrgencia> todosNiveles = new HashSet<>();
        for (Notificacion.NivelUrgencia nivel : Notificacion.NivelUrgencia.values()) {
            todosNiveles.add(nivel);
        }
        nivelesHabilitados.put(usuario, todosNiveles);
        
        // Habilitar todos los canales
        Set<CanalNotificacion> todosCanales = new HashSet<>();
        for (CanalNotificacion canal : CanalNotificacion.values()) {
            todosCanales.add(canal);
        }
        canalesHabilitados.put(usuario, todosCanales);
    }
    
    /**
     * Habilita un tipo de notificación para un usuario.
     *
     * @param usuario Usuario
     * @param tipo Tipo de notificación a habilitar
     */
    public void habilitarTipoNotificacion(Usuario usuario, Notificacion.TipoNotificacion tipo) {
        tiposHabilitados.computeIfAbsent(usuario, k -> new HashSet<>()).add(tipo);
    }
    
    /**
     * Deshabilita un tipo de notificación para un usuario.
     *
     * @param usuario Usuario
     * @param tipo Tipo de notificación a deshabilitar
     */
    public void deshabilitarTipoNotificacion(Usuario usuario, Notificacion.TipoNotificacion tipo) {
        if (tiposHabilitados.containsKey(usuario)) {
            tiposHabilitados.get(usuario).remove(tipo);
        }
    }
    
    /**
     * Habilita un nivel de urgencia para un usuario.
     *
     * @param usuario Usuario
     * @param nivel Nivel de urgencia a habilitar
     */
    public void habilitarNivelUrgencia(Usuario usuario, Notificacion.NivelUrgencia nivel) {
        nivelesHabilitados.computeIfAbsent(usuario, k -> new HashSet<>()).add(nivel);
    }
    
    /**
     * Deshabilita un nivel de urgencia para un usuario.
     *
     * @param usuario Usuario
     * @param nivel Nivel de urgencia a deshabilitar
     */
    public void deshabilitarNivelUrgencia(Usuario usuario, Notificacion.NivelUrgencia nivel) {
        if (nivelesHabilitados.containsKey(usuario)) {
            nivelesHabilitados.get(usuario).remove(nivel);
        }
    }
    
    /**
     * Habilita un canal de notificación para un usuario.
     *
     * @param usuario Usuario
     * @param canal Canal de notificación a habilitar
     */
    public void habilitarCanalNotificacion(Usuario usuario, CanalNotificacion canal) {
        canalesHabilitados.computeIfAbsent(usuario, k -> new HashSet<>()).add(canal);
    }
    
    /**
     * Deshabilita un canal de notificación para un usuario.
     *
     * @param usuario Usuario
     * @param canal Canal de notificación a deshabilitar
     */
    public void deshabilitarCanalNotificacion(Usuario usuario, CanalNotificacion canal) {
        if (canalesHabilitados.containsKey(usuario)) {
            canalesHabilitados.get(usuario).remove(canal);
        }
    }
    
    /**
     * Verifica si un tipo de notificación está habilitado para un usuario.
     *
     * @param usuario Usuario
     * @param tipo Tipo de notificación
     * @return true si está habilitado, false en caso contrario
     */
    public boolean esTipoHabilitado(Usuario usuario, Notificacion.TipoNotificacion tipo) {
        return tiposHabilitados.containsKey(usuario) && tiposHabilitados.get(usuario).contains(tipo);
    }
    
    /**
     * Verifica si un nivel de urgencia está habilitado para un usuario.
     *
     * @param usuario Usuario
     * @param nivel Nivel de urgencia
     * @return true si está habilitado, false en caso contrario
     */
    public boolean esNivelHabilitado(Usuario usuario, Notificacion.NivelUrgencia nivel) {
        return nivelesHabilitados.containsKey(usuario) && nivelesHabilitados.get(usuario).contains(nivel);
    }
    
    /**
     * Verifica si un canal de notificación está habilitado para un usuario.
     *
     * @param usuario Usuario
     * @param canal Canal de notificación
     * @return true si está habilitado, false en caso contrario
     */
    public boolean esCanalHabilitado(Usuario usuario, CanalNotificacion canal) {
        return canalesHabilitados.containsKey(usuario) && canalesHabilitados.get(usuario).contains(canal);
    }
    
    /**
     * Verifica si una notificación debe ser enviada a un usuario según sus preferencias.
     *
     * @param notificacion Notificación a verificar
     * @param canal Canal por el que se enviaría
     * @return true si debe enviarse, false en caso contrario
     */
    public boolean debeEnviarNotificacion(Notificacion notificacion, CanalNotificacion canal) {
        Usuario usuario = notificacion.getDestinatario();
        
        return esTipoHabilitado(usuario, notificacion.getTipo()) &&
               esNivelHabilitado(usuario, notificacion.getNivelUrgencia()) &&
               esCanalHabilitado(usuario, canal);
    }
    
    /**
     * Enumeración que define los canales de notificación disponibles.
     */
    public enum CanalNotificacion {
        CONSOLA("Consola"),
        EMAIL("Email"),
        SMS("SMS"),
        PUSH("Notificación Push");
        
        private final String nombre;
        
        CanalNotificacion(String nombre) {
            this.nombre = nombre;
        }
        
        @Override
        public String toString() {
            return nombre;
        }
    }
}