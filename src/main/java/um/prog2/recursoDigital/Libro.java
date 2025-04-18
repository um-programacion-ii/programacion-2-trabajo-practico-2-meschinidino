package um.prog2.recursoDigital;

import um.prog2.Enums.CategoriaRecurso;
import um.prog2.Enums.EstadoRecurso;
import um.prog2.interfaces.Renovable;
import um.prog2.usuario.Usuario;

import java.time.LocalDateTime;

public class Libro extends RecursoBase implements Renovable {
    private String identificador;
    private String titulo;
    private String autor;
    private CategoriaRecurso categoria;
    private LocalDateTime fechaDevolucion;
    private Usuario usuarioPrestamo;

    // Updated constructor to use CategoriaRecurso enum
    public Libro(EstadoRecurso estado, String autor, String titulo, String identificador, CategoriaRecurso categoria) {
        this.estado = estado;
        this.autor = autor;
        this.titulo = titulo;
        this.identificador = identificador;
        this.categoria = categoria;
    }

    // Constructor that takes String for backward compatibility, converts to enum
    public Libro(EstadoRecurso estado, String autor, String titulo, String identificador, String genero) {
        this.estado = estado;
        this.autor = autor;
        this.titulo = titulo;
        this.identificador = identificador;
        try {
            this.categoria = CategoriaRecurso.valueOf(genero.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            // Default to NO_FICCION if genre doesn't match any enum value
            this.categoria = CategoriaRecurso.NO_FICCION;
        }
    }

    // Basic constructor still needed
    public Libro(EstadoRecurso estado, String autor, String titulo, String identificador) {
        this.estado = estado;
        this.autor = autor;
        this.titulo = titulo;
        this.identificador = identificador;
        this.categoria = CategoriaRecurso.NO_FICCION; // Default category
    }

    public Libro() {
        this.categoria = CategoriaRecurso.NO_FICCION; // Default category
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    // Updated getter and setter for categoria
    public CategoriaRecurso getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaRecurso categoria) {
        this.categoria = categoria;
    }

    // For backward compatibility
    public String getGenero() {
        return categoria != null ? categoria.name().replace("_", " ") : null;
    }

    public void setGenero(String genero) {
        try {
            this.categoria = CategoriaRecurso.valueOf(genero.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            this.categoria = CategoriaRecurso.NO_FICCION;
        }
    }

    @Override
    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    @Override
    public String toString() {
        return "Libro{" +
                "identificador='" + identificador + '\'' +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", categoria=" + categoria +
                ", estado=" + estado +
                '}';
    }

    @Override
    public void renovar() {
        if (this.getEstado() == EstadoRecurso.PRESTADO) {
            this.fechaDevolucion = this.fechaDevolucion.plusDays(7);
        }
    }

    @Override
    public boolean estaDisponible() {
        return this.getEstado() == EstadoRecurso.DISPONIBLE;
    }

    @Override
    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    @Override
    public void prestar(Usuario usuario) {
        if (estaDisponible()) {
            this.setEstado(EstadoRecurso.PRESTADO);
            this.usuarioPrestamo = usuario;
            this.fechaDevolucion = LocalDateTime.now().plusDays(14);
        }
    }
}