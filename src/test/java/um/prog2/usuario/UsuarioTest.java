package um.prog2.usuario;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        String nombre = "Juan";
        String apellido = "Pérez";
        int id = 12345;
        String email = "juan.perez@example.com";
        String telefono = "987654321";

        // Act
        Usuario usuario = new Usuario(nombre, apellido, id, email, telefono);

        // Assert
        assertEquals(nombre, usuario.getNombre(), "El nombre debe coincidir");
        assertEquals(apellido, usuario.getApellido(), "El apellido debe coincidir");
        assertEquals(id, usuario.getID(), "El ID debe coincidir");
        assertEquals(email, usuario.getEmail(), "El email debe coincidir");
        assertEquals(telefono, usuario.getTelefono(), "El teléfono debe coincidir");
    }

    @Test
    void testSetters() {
        // Arrange
        Usuario usuario = new Usuario("Nombre", "Apellido", 1, "email@example.com", "123456789");
        String nuevoNombre = "NuevoNombre";
        String nuevoApellido = "NuevoApellido";
        int nuevoId = 54321;
        String nuevoEmail = "nuevo.email@example.com";
        String nuevoTelefono = "123987456";

        // Act
        usuario.setNombre(nuevoNombre);
        usuario.setApellido(nuevoApellido);
        usuario.setID(nuevoId);
        usuario.setEmail(nuevoEmail);
        usuario.setTelefono(nuevoTelefono);

        // Assert
        assertEquals(nuevoNombre, usuario.getNombre(), "El nombre debe actualizarse correctamente");
        assertEquals(nuevoApellido, usuario.getApellido(), "El apellido debe actualizarse correctamente");
        assertEquals(nuevoId, usuario.getID(), "El ID debe actualizarse correctamente");
        assertEquals(nuevoEmail, usuario.getEmail(), "El email debe actualizarse correctamente");
        assertEquals(nuevoTelefono, usuario.getTelefono(), "El teléfono debe actualizarse correctamente");
    }

    @Test
    void testToString() {
        // Arrange
        Usuario usuario = new Usuario("Juan", "Pérez", 12345, "juan.perez@example.com", "987654321");
        String expectedString = "Usuario{" +
                "nombre='Juan'" +
                ", apellido='Pérez'" +
                ", ID='12345'" +
                ", email='juan.perez@example.com'" +
                ", telefono='987654321'" +
                '}';

        // Act
        String actualString = usuario.toString();

        // Assert
        assertEquals(expectedString, actualString, "El método toString debe devolver la representación correcta");
    }
}
