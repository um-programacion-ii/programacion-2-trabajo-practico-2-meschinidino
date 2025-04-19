package um.prog2.excepciones;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioNoEncontradoExceptionTest {

    @Test
    void testConstructorWithMessage() {
        // Arrange
        String errorMessage = "Usuario no encontrado";
        
        // Act
        UsuarioNoEncontradoException exception = new UsuarioNoEncontradoException(errorMessage);
        
        // Assert
        assertEquals(errorMessage, exception.getMessage(), "El mensaje de error debe coincidir");
    }
    
    @Test
    void testConstructorWithMessageAndCause() {
        // Arrange
        String errorMessage = "Usuario no encontrado";
        Throwable cause = new RuntimeException("Causa original");
        
        // Act
        UsuarioNoEncontradoException exception = new UsuarioNoEncontradoException(errorMessage, cause);
        
        // Assert
        assertEquals(errorMessage, exception.getMessage(), "El mensaje de error debe coincidir");
        assertEquals(cause, exception.getCause(), "La causa debe coincidir");
    }
}