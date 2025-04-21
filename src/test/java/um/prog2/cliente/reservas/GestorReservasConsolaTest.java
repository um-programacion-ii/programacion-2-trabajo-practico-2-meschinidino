package um.prog2.cliente.reservas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import um.prog2.interfaces.RecursoDigital;
import um.prog2.interfaces.ServicioNotificaciones;
import um.prog2.prestamos.SistemaPrestamos;
import um.prog2.reservas.SistemaReservas;
import um.prog2.usuario.Usuario;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class GestorReservasConsolaTest {

    private GestorReservasConsola gestorReservas;
    private ServicioNotificaciones servicioNotificaciones;
    private SistemaPrestamos sistemaPrestamos;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        // Configurar captura de salida estándar
        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        
        // Mock simple para ServicioNotificaciones
        servicioNotificaciones = new ServicioNotificaciones() {
            @Override
            public void enviarNotificacion(String mensaje, Usuario usuario) {
                // No hace nada en el test
            }
        };
        
        // Crear SistemaPrestamos
        sistemaPrestamos = new SistemaPrestamos(servicioNotificaciones);
        
        // Usuario de prueba
        usuario = new Usuario("Test", "User", 1, "test@example.com", 123456789);
    }
    
    @Test
    void testGetSistemaReservas() {
        // Arrange
        Scanner scanner = new Scanner(new ByteArrayInputStream("".getBytes()));
        gestorReservas = new GestorReservasConsola(servicioNotificaciones, sistemaPrestamos);
        
        // Act
        SistemaReservas result = gestorReservas.getSistemaReservas();
        
        // Assert
        assertNotNull(result, "getSistemaReservas debe retornar una instancia no nula");
    }

    // Restaurar la salida estándar después de cada test
    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}