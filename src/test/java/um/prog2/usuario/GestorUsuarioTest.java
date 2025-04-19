package um.prog2.usuario;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GestorUsuarioTest {

    @Test
    void testCrearUsuario() {
        // Arrange
        String nombre = "Ana";
        String apellido = "García";
        int id = 54321;
        String email = "ana.garcia@example.com";
        int telefono = 123456789;
        
        // Act
        Usuario usuario = GestorUsuario.crearUsuario(nombre, apellido, id, email, telefono);
        
        // Assert
        assertNotNull(usuario, "El usuario creado no debe ser nulo");
        assertEquals(nombre, usuario.getNombre(), "El nombre debe coincidir");
        assertEquals(apellido, usuario.getApellido(), "El apellido debe coincidir");
        assertEquals(id, usuario.getID(), "El ID debe coincidir");
        assertEquals(email, usuario.getEmail(), "El email debe coincidir");
        assertEquals(telefono, usuario.getTelefono(), "El teléfono debe coincidir");
    }
}