package um.prog2.recursoDigital;

import um.prog2.usuario.Usuario;
import java.time.LocalDateTime;

public class AudioLibro extends RecursoBase implements Renovable {
    private String identificador;
    private String titulo;
    private String autor;
    private String narrador;
    private double duracion;
    private String idioma;
    private String isbn;
    private LocalDateTime fechaDevolucion;
    private Usuario usuarioPrestamo;

    public AudioLibro() {
    }

    public AudioLibro(String identificador, String titulo, String autor, String narrador,
                      double duracion, String idioma, String isbn,
                      EstadoRecurso estado) {
        this.identificador = identificador;
        this.titulo = titulo;
        this.autor = autor;
        this.narrador = narrador;
        this.duracion = duracion;
        this.idioma = idioma;
        this.isbn = isbn;
        this.estado = estado;
    }

    // Implementation of Prestable methods
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

    // Implementation of Renovable method
    @Override
    public void renovar() {
        if (this.getEstado() == EstadoRecurso.PRESTADO) {
            this.fechaDevolucion = this.fechaDevolucion.plusDays(7);
        }
    }

    @Override
    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }


    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getNarrador() {
        return narrador;
    }

    public void setNarrador(String narrador) {
        this.narrador = narrador;
    }

    public double getDuracion() {
        return duracion;
    }

    public void setDuracion(double duracion) {
        this.duracion = duracion;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @Override
    public String toString() {
        return "AudioLibro{" +
                "identificador='" + identificador + '\'' +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", narrador='" + narrador + '\'' +
                ", duracion=" + duracion +
                ", idioma='" + idioma + '\'' +
                ", isbn='" + isbn + '\'' +
                ", estado=" + estado +
                '}';
    }
}