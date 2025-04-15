package um.prog2.recursoDigital;

import um.prog2.usuario.Usuario;

import java.time.LocalDateTime;

public class Libro extends RecursoBase implements Renovable {
    private String identificador;
    private String titulo;
    private String autor;
    private LocalDateTime fechaDevolucion;
    private Usuario usuarioPrestamo;



    public Libro(EstadoRecurso estado, String autor, String titulo, String identificador) {
        this.estado = estado;
        this.autor = autor;
        this.titulo = titulo;
        this.identificador = identificador;
    }

    public Libro() {
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