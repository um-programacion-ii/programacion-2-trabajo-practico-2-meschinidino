package um.prog2.cliente.reportes;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import um.prog2.Enums.CategoriaRecurso;
import um.prog2.interfaces.RecursoDigital;
import um.prog2.reportes.SistemaReportes;
import um.prog2.usuario.Usuario;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class GestorReportesConsolaTest {

    private GestorReportesConsola gestorReportesConsola;
    private MockSistemaReportes mockSistemaReportes;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    private Scanner scanner;

    @BeforeEach
    void setUp() {
        // Configurar captura de salida estándar
        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // Crear mock de SistemaReportes
        mockSistemaReportes = new MockSistemaReportes();

        // Configurar entrada simulada
        String input = "4\n"; // Simular que el usuario selecciona la opción 4 (volver al menú principal)
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        // Crear instancia de GestorReportesConsola
        gestorReportesConsola = new GestorReportesConsola(scanner, mockSistemaReportes);
    }

    @AfterEach
    void tearDown() {
        // Restaurar la salida estándar
        System.setOut(originalOut);
        
        // Cerrar el sistema de reportes
        gestorReportesConsola.cerrarSistemaReportes();
    }

    @Test
    void testMostrarMenuReportes() {
        // Act
        gestorReportesConsola.mostrarMenuReportes();

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("REPORTES Y ESTADÍSTICAS"), "Debe mostrar el título del menú");
        assertTrue(output.contains("1. Recursos más prestados"), "Debe mostrar la opción 1");
        assertTrue(output.contains("2. Usuarios más activos"), "Debe mostrar la opción 2");
        assertTrue(output.contains("3. Estadísticas por categoría"), "Debe mostrar la opción 3");
        assertTrue(output.contains("4. Volver al menú principal"), "Debe mostrar la opción 4");
    }

    @Test
    void testCerrarSistemaReportes() {
        // Act
        gestorReportesConsola.cerrarSistemaReportes();

        // Assert
        assertTrue(mockSistemaReportes.cerrarLlamado, "Debe llamar al método cerrar del SistemaReportes");
    }

    @Test
    void testLimpiarReportesAlSalir() {
        // Act
        gestorReportesConsola.mostrarMenuReportes();

        // Assert
        assertTrue(mockSistemaReportes.limpiarReportesLlamado, 
                "Debe llamar al método limpiarReportes al salir del menú");
    }

    // Clase interna para simular SistemaReportes
    private class MockSistemaReportes extends SistemaReportes {
        boolean cerrarLlamado = false;
        boolean limpiarReportesLlamado = false;
        
        public MockSistemaReportes() {
            super(null, new ArrayList<>(), new HashMap<>());
        }
        
        @Override
        public void cerrar() {
            cerrarLlamado = true;
        }
        
        @Override
        public void limpiarReportes() {
            limpiarReportesLlamado = true;
        }
        
        @Override
        public int obtenerProgresoReporte(String tipoReporte) {
            return 50; // Simular 50% de progreso
        }
        
        @Override
        public <T> T obtenerResultadoReporte(String tipoReporte) {
            // Simular resultados según el tipo de reporte
            if (tipoReporte.equals(SistemaReportes.REPORTE_RECURSOS_MAS_PRESTADOS)) {
                Map<RecursoDigital, Integer> resultado = new HashMap<>();
                // No podemos crear instancias reales de RecursoDigital aquí, así que devolvemos un mapa vacío
                return (T) resultado;
            } else if (tipoReporte.equals(SistemaReportes.REPORTE_USUARIOS_MAS_ACTIVOS)) {
                Map<Usuario, Integer> resultado = new HashMap<>();
                return (T) resultado;
            } else if (tipoReporte.equals(SistemaReportes.REPORTE_ESTADISTICAS_CATEGORIA)) {
                Map<CategoriaRecurso, Integer> resultado = new HashMap<>();
                return (T) resultado;
            }
            return null;
        }
        
        @Override
        public Future<Map<RecursoDigital, Integer>> iniciarReporteRecursosMasPrestados(int limite) {
            // Devolver un Future completado con un mapa vacío
            return CompletableFuture.completedFuture(new HashMap<>());
        }
        
        @Override
        public Future<Map<Usuario, Integer>> iniciarReporteUsuariosMasActivos(int limite) {
            // Devolver un Future completado con un mapa vacío
            return CompletableFuture.completedFuture(new HashMap<>());
        }
        
        @Override
        public Future<Map<CategoriaRecurso, Integer>> iniciarEstadisticasPorCategoria() {
            // Devolver un Future completado con un mapa vacío
            return CompletableFuture.completedFuture(new HashMap<>());
        }
    }
}