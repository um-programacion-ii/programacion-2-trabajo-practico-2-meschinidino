package um.prog2.excepciones;

/**
 * Excepción que se lanza cuando no se encuentra un usuario en el sistema.
 */
public class UsuarioNoEncontradoException extends Exception {
    
    /**
     * Constructor con mensaje de error.
     * 
     * @param mensaje El mensaje de error
     */
    public UsuarioNoEncontradoException(String mensaje) {
        super(mensaje);
    }
    
    /**
     * Constructor con mensaje de error y causa.
     * 
     * @param mensaje El mensaje de error
     * @param causa La causa de la excepción
     */
    public UsuarioNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}