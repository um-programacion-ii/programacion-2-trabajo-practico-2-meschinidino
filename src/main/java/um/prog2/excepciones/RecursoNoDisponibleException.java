package um.prog2.excepciones;

/**
 * Excepción que se lanza cuando un recurso no está disponible para préstamo o renovación.
 */
public class RecursoNoDisponibleException extends Exception {
    
    /**
     * Constructor con mensaje de error.
     * 
     * @param mensaje El mensaje de error
     */
    public RecursoNoDisponibleException(String mensaje) {
        super(mensaje);
    }
    
    /**
     * Constructor con mensaje de error y causa.
     * 
     * @param mensaje El mensaje de error
     * @param causa La causa de la excepción
     */
    public RecursoNoDisponibleException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}