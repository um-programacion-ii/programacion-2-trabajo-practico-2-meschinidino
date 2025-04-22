package um.prog2.recursoDigital;

import org.junit.jupiter.api.Test;
import um.prog2.Enums.CategoriaRecurso;
import um.prog2.Enums.EstadoRecurso;
import um.prog2.usuario.Usuario;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class LibroTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        EstadoRecurso estado = EstadoRecurso.DISPONIBLE;
        String autor = "Gabriel García Márquez";
        String titulo = "Cien años de soledad";
        String identificador = "L001";
        CategoriaRecurso categoria = CategoriaRecurso.REALISMO_MAGICO;

        // Act
        Libro libro = new Libro(estado, autor, titulo, identificador, categoria);

        // Assert
        assertEquals(estado, libro.getEstado(), "El estado debe coincidir");
        assertEquals(autor, libro.getAutor(), "El autor debe coincidir");
        assertEquals(titulo, libro.getTitulo(), "El título debe coincidir");
        assertEquals(identificador, libro.getIdentificador(), "El identificador debe coincidir");
        assertEquals(categoria, libro.getCategoria(), "La categoría debe coincidir");
    }

    @Test
    void testConstructorWithStringCategory() {
        // Arrange
        EstadoRecurso estado = EstadoRecurso.DISPONIBLE;
        String autor = "George Orwell";
        String titulo = "1984";
        String identificador = "L002";
        String genero = "CIENCIA_FICCION";

        // Act
        Libro libro = new Libro(estado, autor, titulo, identificador, genero);

        // Assert
        assertEquals(estado, libro.getEstado(), "El estado debe coincidir");
        assertEquals(autor, libro.getAutor(), "El autor debe coincidir");
        assertEquals(titulo, libro.getTitulo(), "El título debe coincidir");
        assertEquals(identificador, libro.getIdentificador(), "El identificador debe coincidir");
        assertEquals(CategoriaRecurso.CIENCIA_FICCION, libro.getCategoria(), "La categoría debe coincidir");
    }

    @Test
    void testSetters() {
        // Arrange
        Libro libro = new Libro();
        EstadoRecurso nuevoEstado = EstadoRecurso.PRESTADO;
        String nuevoAutor = "J.K. Rowling";
        String nuevoTitulo = "Harry Potter";
        String nuevoIdentificador = "L003";
        CategoriaRecurso nuevaCategoria = CategoriaRecurso.FANTASIA;

        // Act
        libro.setEstado(nuevoEstado);
        libro.setAutor(nuevoAutor);
        libro.setTitulo(nuevoTitulo);
        libro.setIdentificador(nuevoIdentificador);
        libro.setCategoria(nuevaCategoria);

        // Assert
        assertEquals(nuevoEstado, libro.getEstado(), "El estado debe actualizarse correctamente");
        assertEquals(nuevoAutor, libro.getAutor(), "El autor debe actualizarse correctamente");
        assertEquals(nuevoTitulo, libro.getTitulo(), "El título debe actualizarse correctamente");
        assertEquals(nuevoIdentificador, libro.getIdentificador(), "El identificador debe actualizarse correctamente");
        assertEquals(nuevaCategoria, libro.getCategoria(), "La categoría debe actualizarse correctamente");
    }

    @Test
    void testPrestar() {
        // Arrange
        Libro libro = new Libro(EstadoRecurso.DISPONIBLE, "Autor", "Título", "L004", CategoriaRecurso.NO_FICCION);
        Usuario usuario = new Usuario("Juan", "Pérez", 1, "juan@example.com", "123456789");

        // Act
        libro.prestar(usuario);

        // Assert
        assertEquals(EstadoRecurso.PRESTADO, libro.getEstado(), "El estado debe cambiar a PRESTADO");
        assertNotNull(libro.getFechaDevolucion(), "La fecha de devolución no debe ser nula");

        // Verificar que la fecha de devolución es aproximadamente 14 días después de ahora
        LocalDateTime now = LocalDateTime.now();
        long daysBetween = ChronoUnit.DAYS.between(now, libro.getFechaDevolucion());
        assertTrue(daysBetween >= 13 && daysBetween <= 14, "La fecha de devolución debe ser 14 días después");
    }

    @Test
    void testRenovar() {
        // Arrange
        Libro libro = new Libro(EstadoRecurso.DISPONIBLE, "Autor", "Título", "L005", CategoriaRecurso.NO_FICCION);
        Usuario usuario = new Usuario("Juan", "Pérez", 1, "juan@example.com", "123456789");
        libro.prestar(usuario);
        LocalDateTime fechaOriginal = libro.getFechaDevolucion();

        // Act
        libro.renovar();

        // Assert
        LocalDateTime fechaNueva = libro.getFechaDevolucion();
        long daysBetween = ChronoUnit.DAYS.between(fechaOriginal, fechaNueva);
        assertEquals(7, daysBetween, "La renovación debe extender la fecha de devolución por 7 días");
    }

    @Test
    void testEstaDisponible() {
        // Arrange
        Libro libroDisponible = new Libro(EstadoRecurso.DISPONIBLE, "Autor1", "Título1", "L006", CategoriaRecurso.NO_FICCION);
        Libro libroPrestado = new Libro(EstadoRecurso.PRESTADO, "Autor2", "Título2", "L007", CategoriaRecurso.NO_FICCION);

        // Act & Assert
        assertTrue(libroDisponible.estaDisponible(), "Un libro con estado DISPONIBLE debe estar disponible");
        assertFalse(libroPrestado.estaDisponible(), "Un libro con estado PRESTADO no debe estar disponible");
    }
}
