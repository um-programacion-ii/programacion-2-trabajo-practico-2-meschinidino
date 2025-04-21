package um.prog2.alertas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import um.prog2.Enums.EstadoRecurso;
import um.prog2.interfaces.RecursoDigital;
import um.prog2.notificaciones.ServicioEnvioNotificaciones;
import um.prog2.prestamos.SistemaPrestamos;
import um.prog2.recursoDigital.GestorRecursos;
import um.prog2.recursoDigital.Libro;
import um.prog2.reservas.Reserva;
import um.prog2.reservas.SistemaReservas;
import um.prog2.usuario.Usuario;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlertaDisponibilidadTest {

    private AlertaDisponibilidad alertaDisponibilidad;
    private TestSistemaReservas sistemaReservas;
    private TestSistemaPrestamos sistemaPrestamos;
    private TestGestorRecursos gestorRecursos;
    private TestServicioNotificaciones servicioNotificaciones;
    private Usuario usuario;
    private Libro recurso;

    @BeforeEach
    void setUp() {
        // Crear implementaciones de prueba
        sistemaReservas = new TestSistemaReservas();
        sistemaPrestamos = new TestSistemaPrestamos();
        gestorRecursos = new TestGestorRecursos();
        servicioNotificaciones = new TestServicioNotificaciones();
        
        // Crear usuario y recurso de prueba
        usuario = new Usuario("Test", "User", 1, "test@example.com", 123456789);
        recurso = new Libro(EstadoRecurso.DISPONIBLE, "Autor", "Título", "L001");
        
        // Configurar datos de prueba
        gestorRecursos.addRecursoDisponible(recurso);
        
        // Crear instancia de AlertaDisponibilidad
        alertaDisponibilidad = new AlertaDisponibilidad(
            sistemaReservas, 
            sistemaPrestamos, 
            gestorRecursos, 
            servicioNotificaciones
        );
    }
    
    @Test
    void testInitialization() {
        // Assert
        assertNotNull(alertaDisponibilidad, "La instancia de AlertaDisponibilidad no debe ser nula");
    }
    
    @Test
    void testObtenerRecursosDisponiblesConReservas_SinReservas() {
        // Act
        List<RecursoDigital> resultado = alertaDisponibilidad.obtenerRecursosDisponiblesConReservas();
        
        // Assert
        assertNotNull(resultado, "El resultado no debe ser nulo");
        assertTrue(resultado.isEmpty(), "El resultado debe estar vacío cuando no hay reservas");
    }
    
    @Test
    void testObtenerRecursosDisponiblesConReservas_ConReservas() {
        // Arrange - Agregar una reserva para el recurso
        TestReserva reserva = new TestReserva("R-1", usuario, recurso);
        sistemaReservas.addReserva(reserva);
        
        // Act
        List<RecursoDigital> resultado = alertaDisponibilidad.obtenerRecursosDisponiblesConReservas();
        
        // Assert
        assertNotNull(resultado, "El resultado no debe ser nulo");
        assertFalse(resultado.isEmpty(), "El resultado no debe estar vacío");
        assertEquals(1, resultado.size(), "Debe haber un recurso disponible con reserva");
        assertEquals(recurso, resultado.get(0), "El recurso debe ser el mismo que se configuró");
    }
    
    @Test
    void testProcesarRespuestaAlerta_ComandoValido() {
        // Act
        boolean resultado = alertaDisponibilidad.procesarRespuestaAlerta("PRESTAR L001", usuario);
        
        // Assert
        assertTrue(resultado, "El procesamiento de la respuesta debe ser exitoso");
        assertTrue(sistemaPrestamos.prestamoSolicitado, "Se debe haber solicitado un préstamo");
        assertEquals(usuario, sistemaPrestamos.ultimoUsuario, "El usuario debe ser el mismo");
        assertEquals(recurso, sistemaPrestamos.ultimoRecurso, "El recurso debe ser el mismo");
    }
    
    @Test
    void testProcesarRespuestaAlerta_ComandoInvalido() {
        // Act
        boolean resultado = alertaDisponibilidad.procesarRespuestaAlerta("COMANDO_INVALIDO", usuario);
        
        // Assert
        assertFalse(resultado, "El procesamiento de una respuesta inválida debe fallar");
        assertFalse(sistemaPrestamos.prestamoSolicitado, "No se debe haber solicitado un préstamo");
    }
    
    // Clases de implementación para pruebas
    
    private static class TestSistemaReservas extends SistemaReservas {
        private List<Reserva> reservas = new ArrayList<>();
        
        public TestSistemaReservas() {
            super(null, null);
        }
        
        public void addReserva(Reserva reserva) {
            reservas.add(reserva);
        }
        
        @Override
        public List<Reserva> obtenerTodasLasReservasActivas() {
            return new ArrayList<>(reservas);
        }
    }
    
    private static class TestSistemaPrestamos extends SistemaPrestamos {
        public boolean prestamoSolicitado = false;
        public Usuario ultimoUsuario = null;
        public RecursoDigital ultimoRecurso = null;
        public int ultimosDias = 0;
        
        public TestSistemaPrestamos() {
            super(null);
        }
        
        @Override
        public void solicitarPrestamo(Usuario usuario, RecursoDigital recurso, int diasPrestamo) {
            prestamoSolicitado = true;
            ultimoUsuario = usuario;
            ultimoRecurso = recurso;
            ultimosDias = diasPrestamo;
        }
    }
    
    private static class TestGestorRecursos extends GestorRecursos {
        private List<RecursoDigital> recursosDisponibles = new ArrayList<>();
        
        public TestGestorRecursos() {
            super(new ArrayList<>(), null);
        }
        
        public void addRecursoDisponible(RecursoDigital recurso) {
            recursosDisponibles.add(recurso);
        }
        
        @Override
        public List<RecursoDigital> obtenerRecursosDisponiblesParaPrestamo() {
            return new ArrayList<>(recursosDisponibles);
        }
    }
    
    private static class TestServicioNotificaciones extends ServicioEnvioNotificaciones {
        public boolean notificacionEnviada = false;
        
        @Override
        public void enviarNotificacionSistema(String mensaje, Usuario usuario, 
                                             um.prog2.notificaciones.Notificacion.TipoNotificacion tipo,
                                             String origen) {
            notificacionEnviada = true;
        }
    }
    
    private static class TestReserva extends Reserva {
        public TestReserva(String id, Usuario usuario, RecursoDigital recurso) {
            super(id, usuario, recurso, 1);
        }
    }
}