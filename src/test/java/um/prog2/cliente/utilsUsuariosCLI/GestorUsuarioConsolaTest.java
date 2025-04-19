package um.prog2.cliente.utilsUsuariosCLI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import um.prog2.excepciones.UsuarioNoEncontradoException;
import um.prog2.interfaces.ServicioNotificaciones;
import um.prog2.usuario.Usuario;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class GestorUsuarioConsolaTest {

    private GestorUsuarioConsola gestorUsuario;
    private Map<String, Usuario> usuarios;
    private ServicioNotificaciones servicioNotificaciones;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        // Configurar captura de salida estándar
        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // Crear mapa de usuarios de prueba
        usuarios = new HashMap<>();

        // Mock simple para ServicioNotificaciones
        servicioNotificaciones = new ServicioNotificaciones() {
            @Override
            public void enviarNotificacion(String mensaje, Usuario usuario) {
                // No hace nada en el test
            }
        };
    }

    @Test
    void testSeleccionarUsuarioSinUsuarios() {
        // Arrange
        Scanner scanner = new Scanner(new ByteArrayInputStream("".getBytes()));
        gestorUsuario = new GestorUsuarioConsola(scanner, usuarios, servicioNotificaciones);

        // Act
        Usuario resultado = gestorUsuario.seleccionarUsuario();

        // Assert
        assertNull(resultado, "Debe devolver null cuando no hay usuarios");
        String output = outputStream.toString();
        assertTrue(output.contains("No hay usuarios registrados"), 
                "Debe mostrar mensaje cuando no hay usuarios");
    }

    @Test
    void testSeleccionarUsuarioConUsuarioExistente() {
        // Arrange
        Usuario usuario = new Usuario("Juan", "Pérez", 1, "juan@example.com", 123456789);
        usuarios.put("1", usuario);
        Scanner scanner = new Scanner(new ByteArrayInputStream("1\n".getBytes()));
        gestorUsuario = new GestorUsuarioConsola(scanner, usuarios, servicioNotificaciones);

        // Act
        Usuario resultado = gestorUsuario.seleccionarUsuario();

        // Assert
        assertNotNull(resultado, "Debe devolver un usuario cuando existe");
        assertEquals(usuario, resultado, "Debe devolver el usuario correcto");
        String output = outputStream.toString();
        assertTrue(output.contains("Usuario seleccionado: Juan Pérez"), 
                "Debe mostrar mensaje de confirmación");
    }

    @Test
    void testSeleccionarUsuarioConUsuarioNoExistente() {
        // Arrange
        Scanner scanner = new Scanner(new ByteArrayInputStream("999\n".getBytes()));
        gestorUsuario = new GestorUsuarioConsola(scanner, usuarios, servicioNotificaciones);

        // Act
        Usuario resultado = gestorUsuario.seleccionarUsuario();

        // Assert
        assertNull(resultado, "Debe devolver null cuando el usuario no existe");
        // Verificamos que se muestra algún mensaje, sin importar el contenido exacto
        String output = outputStream.toString();
        assertFalse(output.isEmpty(), "Debe mostrar algún mensaje");
    }

    @Test
    void testSeleccionarUsuarioCancelado() {
        // Arrange
        Usuario usuario = new Usuario("Juan", "Pérez", 1, "juan@example.com", 123456789);
        usuarios.put("1", usuario);
        Scanner scanner = new Scanner(new ByteArrayInputStream("\n".getBytes()));
        gestorUsuario = new GestorUsuarioConsola(scanner, usuarios, servicioNotificaciones);

        // Act
        Usuario resultado = gestorUsuario.seleccionarUsuario();

        // Assert
        assertNull(resultado, "Debe devolver null cuando se cancela la selección");
        String output = outputStream.toString();
        assertTrue(output.contains("No se seleccionó ningún usuario"), 
                "Debe mostrar mensaje de cancelación");
    }

    // Restaurar la salida estándar después de cada test
    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}
