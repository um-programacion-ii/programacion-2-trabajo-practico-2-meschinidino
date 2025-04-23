package um.prog2.excepciones;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RecursoNoDisponibleExceptionTest {

    @Test
    void testConstructorWithMessage() {
        // Arrange
        String errorMessage = "El recurso no está disponible";
        
        // Act
        RecursoNoDisponibleException exception = new RecursoNoDisponibleException(errorMessage);
        
        // Assert
        assertEquals(errorMessage, exception.getMessage(), "El mensaje de error debe coincidir");
    }
    
    @Test
    void testConstructorWithMessageAndCause() {
        // Arrange
        String errorMessage = "El recurso no está disponible";
        Throwable cause = new RuntimeException("Causa original");
        
        // Act
        RecursoNoDisponibleException exception = new RecursoNoDisponibleException(errorMessage, cause);
        
        // Assert
        assertEquals(errorMessage, exception.getMessage(), "El mensaje de error debe coincidir");
        assertEquals(cause, exception.getCause(), "La causa debe coincidir");
    }
}