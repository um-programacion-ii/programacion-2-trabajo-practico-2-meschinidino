package um.prog2.reportes;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import um.prog2.Enums.CategoriaRecurso;
import um.prog2.Enums.EstadoRecurso;
import um.prog2.interfaces.RecursoDigital;
import um.prog2.prestamos.Prestamo;
import um.prog2.prestamos.SistemaPrestamos;
import um.prog2.recursoDigital.Libro;
import um.prog2.usuario.Usuario;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class SistemaReportesTest {

    private SistemaReportes sistemaReportes;
    private SistemaPrestamos mockSistemaPrestamos;
    private List<RecursoDigital> recursos;
    private Map<String, Usuario> usuarios;
    private Usuario usuario1;
    private Usuario usuario2;
    private RecursoDigital libro1;
    private RecursoDigital libro2;
    private RecursoDigital libro3;

    @BeforeEach
    void setUp() {
        // Crear recursos de prueba
        recursos = new ArrayList<>();
        libro1 = new Libro(EstadoRecurso.DISPONIBLE, "Autor1", "Título1", "L001", CategoriaRecurso.CIENCIA_FICCION);
        libro2 = new Libro(EstadoRecurso.DISPONIBLE, "Autor2", "Título2", "L002", CategoriaRecurso.FANTASIA);
        libro3 = new Libro(EstadoRecurso.DISPONIBLE, "Autor3", "Título3", "L003", CategoriaRecurso.CIENCIA_FICCION);
        recursos.add(libro1);
        recursos.add(libro2);
        recursos.add(libro3);

        // Crear usuarios de prueba
        usuarios = new HashMap<>();
        usuario1 = new Usuario("Juan", "Pérez", 1, "juan@example.com", 123456789);
        usuario2 = new Usuario("María", "López", 2, "maria@example.com", 987654321);
        usuarios.put("1", usuario1);
        usuarios.put("2", usuario2);

        // Crear mock de SistemaPrestamos
        mockSistemaPrestamos = new MockSistemaPrestamos();

        // Crear instancia de SistemaReportes
        sistemaReportes = new SistemaReportes(mockSistemaPrestamos, recursos, usuarios);
    }

    @AfterEach
    void tearDown() {
        // Cerrar el ExecutorService para liberar recursos
        sistemaReportes.cerrar();
    }

    @Test
    void testIniciarReporteRecursosMasPrestados() throws ExecutionException, InterruptedException {
        // Act
        Future<Map<RecursoDigital, Integer>> futureReporte = sistemaReportes.iniciarReporteRecursosMasPrestados(10);
        Map<RecursoDigital, Integer> resultado = futureReporte.get();

        // Assert
        assertNotNull(resultado, "El resultado no debe ser nulo");
        assertEquals(2, resultado.size(), "Debe haber 2 recursos con préstamos");
        assertTrue(resultado.containsKey(libro1), "El libro1 debe estar en el resultado");
        assertTrue(resultado.containsKey(libro2), "El libro2 debe estar en el resultado");
        assertEquals(2, resultado.get(libro1), "El libro1 debe tener 2 préstamos");
        assertEquals(1, resultado.get(libro2), "El libro2 debe tener 1 préstamo");
    }

    @Test
    void testIniciarReporteUsuariosMasActivos() throws ExecutionException, InterruptedException {
        // Act
        Future<Map<Usuario, Integer>> futureReporte = sistemaReportes.iniciarReporteUsuariosMasActivos(10);
        Map<Usuario, Integer> resultado = futureReporte.get();

        // Assert
        assertNotNull(resultado, "El resultado no debe ser nulo");
        assertEquals(2, resultado.size(), "Debe haber 2 usuarios con préstamos");
        assertTrue(resultado.containsKey(usuario1), "El usuario1 debe estar en el resultado");
        assertTrue(resultado.containsKey(usuario2), "El usuario2 debe estar en el resultado");
        assertEquals(2, resultado.get(usuario1), "El usuario1 debe tener 2 préstamos");
        assertEquals(1, resultado.get(usuario2), "El usuario2 debe tener 1 préstamo");
    }

    @Test
    void testIniciarEstadisticasPorCategoria() throws ExecutionException, InterruptedException {
        // Act
        Future<Map<CategoriaRecurso, Integer>> futureReporte = sistemaReportes.iniciarEstadisticasPorCategoria();
        Map<CategoriaRecurso, Integer> resultado = futureReporte.get();

        // Assert
        assertNotNull(resultado, "El resultado no debe ser nulo");
        assertTrue(resultado.containsKey(CategoriaRecurso.CIENCIA_FICCION), "Debe contener la categoría CIENCIA_FICCION");
        assertTrue(resultado.containsKey(CategoriaRecurso.FANTASIA), "Debe contener la categoría FANTASIA");
        assertEquals(2, resultado.get(CategoriaRecurso.CIENCIA_FICCION), "CIENCIA_FICCION debe tener 2 préstamos");
        assertEquals(1, resultado.get(CategoriaRecurso.FANTASIA), "FANTASIA debe tener 1 préstamo");
    }

    @Test
    void testObtenerProgresoReporte() throws InterruptedException {
        // Act
        sistemaReportes.iniciarReporteRecursosMasPrestados(10);

        // Esperar un poco para que el reporte comience a procesarse
        Thread.sleep(100);

        // Assert
        int progreso = sistemaReportes.obtenerProgresoReporte(SistemaReportes.REPORTE_RECURSOS_MAS_PRESTADOS);
        assertTrue(progreso >= 0 && progreso <= 100, "El progreso debe estar entre 0 y 100");
    }

    @Test
    void testEsReporteCompleto() throws ExecutionException, InterruptedException {
        // Arrange
        assertFalse(sistemaReportes.esReporteCompleto(SistemaReportes.REPORTE_RECURSOS_MAS_PRESTADOS), 
                "Inicialmente el reporte no debe estar completo");

        // Act
        Future<Map<RecursoDigital, Integer>> futureReporte = sistemaReportes.iniciarReporteRecursosMasPrestados(10);
        futureReporte.get(); // Esperar a que termine

        // Assert
        assertTrue(sistemaReportes.esReporteCompleto(SistemaReportes.REPORTE_RECURSOS_MAS_PRESTADOS), 
                "Después de completarse, el reporte debe estar marcado como completo");
    }

    @Test
    void testObtenerResultadoReporte() throws ExecutionException, InterruptedException {
        // Act
        Future<Map<RecursoDigital, Integer>> futureReporte = sistemaReportes.iniciarReporteRecursosMasPrestados(10);
        futureReporte.get(); // Esperar a que termine

        // Assert
        Map<RecursoDigital, Integer> resultado = sistemaReportes.obtenerResultadoReporte(SistemaReportes.REPORTE_RECURSOS_MAS_PRESTADOS);
        assertNotNull(resultado, "Debe poder obtener el resultado del reporte");
        assertEquals(2, resultado.size(), "El resultado debe tener 2 elementos");
    }

    @Test
    void testLimpiarReportes() throws ExecutionException, InterruptedException {
        // Arrange
        Future<Map<RecursoDigital, Integer>> futureReporte = sistemaReportes.iniciarReporteRecursosMasPrestados(10);
        futureReporte.get(); // Esperar a que termine
        assertTrue(sistemaReportes.esReporteCompleto(SistemaReportes.REPORTE_RECURSOS_MAS_PRESTADOS), 
                "El reporte debe estar completo");

        // Act
        sistemaReportes.limpiarReportes();

        // Assert
        assertFalse(sistemaReportes.esReporteCompleto(SistemaReportes.REPORTE_RECURSOS_MAS_PRESTADOS), 
                "Después de limpiar, el reporte no debe estar completo");
        assertEquals(-1, sistemaReportes.obtenerProgresoReporte(SistemaReportes.REPORTE_RECURSOS_MAS_PRESTADOS), 
                "El progreso debe ser -1 después de limpiar");
    }

    // Clase interna para simular SistemaPrestamos
    private class MockSistemaPrestamos extends SistemaPrestamos {
        public MockSistemaPrestamos() {
            super(null);
        }

        @Override
        public List<Prestamo> obtenerHistorialPrestamos(Usuario usuario) {
            List<Prestamo> prestamos = new ArrayList<>();

            if (usuario.equals(usuario1)) {
                // Usuario1 tiene 2 préstamos: libro1 y libro2
                prestamos.add(new Prestamo("p1", usuario1, libro1, 14));
                prestamos.add(new Prestamo("p2", usuario1, libro2, 14));
            } else if (usuario.equals(usuario2)) {
                // Usuario2 tiene 1 préstamo: libro1
                prestamos.add(new Prestamo("p3", usuario2, libro1, 14));
            }

            return prestamos;
        }
    }
}
