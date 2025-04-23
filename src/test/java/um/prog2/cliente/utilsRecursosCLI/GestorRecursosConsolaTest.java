package um.prog2.cliente.utilsRecursosCLI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import um.prog2.Enums.CategoriaRecurso;
import um.prog2.Enums.EstadoRecurso;
import um.prog2.excepciones.RecursoNoDisponibleException;
import um.prog2.interfaces.RecursoDigital;
import um.prog2.interfaces.ServicioNotificaciones;
import um.prog2.recursoDigital.Libro;
import um.prog2.usuario.Usuario;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class GestorRecursosConsolaTest {

    private GestorRecursosConsola gestorRecursos;
    private List<RecursoDigital> recursos;
    private ServicioNotificaciones servicioNotificaciones;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        // Configurar captura de salida estándar
        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // Crear recursos de prueba
        recursos = new ArrayList<>();

        // Mock simple para ServicioNotificaciones
        servicioNotificaciones = new ServicioNotificaciones() {
            @Override
            public void enviarNotificacion(String mensaje, Usuario usuario) {
                // No hace nada en el test
            }
        };

        // Usuario de prueba
        usuario = new Usuario("Test", "User", 1, "test@example.com", 123456789);
    }

    @Test
    void testListarRecursosSinRecursos() {
        // Arrange
        Scanner scanner = new Scanner(new ByteArrayInputStream("".getBytes()));
        gestorRecursos = new GestorRecursosConsola(scanner, recursos, servicioNotificaciones);

        // Act
        gestorRecursos.listarRecursos();

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("No hay recursos registrados"), 
                "Debe mostrar mensaje cuando no hay recursos");
    }

    @Test
    void testListarRecursosConRecursos() {
        // Arrange
        Scanner scanner = new Scanner(new ByteArrayInputStream("".getBytes()));
        recursos.add(new Libro(EstadoRecurso.DISPONIBLE, "Autor", "Título", "L001", CategoriaRecurso.NO_FICCION));
        gestorRecursos = new GestorRecursosConsola(scanner, recursos, servicioNotificaciones);

        // Act
        gestorRecursos.listarRecursos();

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("RECURSOS DISPONIBLES"), 
                "Debe mostrar el título de la lista");
        assertTrue(output.contains("L001"), 
                "Debe mostrar el identificador del recurso");
        assertTrue(output.contains("Título"), 
                "Debe mostrar el título del recurso");
    }

    @Test
    void testPrestarRecursoSinUsuario() {
        // Arrange
        Scanner scanner = new Scanner(new ByteArrayInputStream("".getBytes()));
        gestorRecursos = new GestorRecursosConsola(scanner, recursos, servicioNotificaciones);

        // Act
        gestorRecursos.prestarRecurso(null);

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Debe seleccionar un usuario primero"), 
                "Debe mostrar mensaje cuando no hay usuario seleccionado");
    }

    @Test
    void testPrestarRecursoSinRecursosDisponibles() {
        // Arrange
        Scanner scanner = new Scanner(new ByteArrayInputStream("".getBytes()));
        gestorRecursos = new GestorRecursosConsola(scanner, recursos, servicioNotificaciones);

        // Act
        gestorRecursos.prestarRecurso(usuario);

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("No hay recursos disponibles para préstamo"), 
                "Debe mostrar mensaje cuando no hay recursos disponibles");
    }

    @Test
    void testGetGestorRecursos() {
        // Arrange
        Scanner scanner = new Scanner(new ByteArrayInputStream("".getBytes()));
        gestorRecursos = new GestorRecursosConsola(scanner, recursos, servicioNotificaciones);

        // Act
        var result = gestorRecursos.getGestorRecursos();

        // Assert
        assertNotNull(result, "getGestorRecursos debe retornar una instancia no nula");
    }

    // Restaurar la salida estándar después de cada test
    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}
