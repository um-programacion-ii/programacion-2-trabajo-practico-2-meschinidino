package um.prog2.recursoDigital;

import um.prog2.usuario.Usuario;
import java.time.LocalDateTime;

public class Revista extends RecursoBase implements Prestable {
    private String identificador;
    private String titulo;
    private String editorial;
    private String categoria;
    private String fechaPublicacion;
    private String issn;
    private int numeroPaginas;
    private LocalDateTime fechaDevolucion;
    private Usuario usuarioPrestamo;

    public Revista() {
    }

    public Revista(EstadoRecurso estado, int numeroPaginas, String issn, String fechaPublicacion, String categoria, String editorial, String titulo, String identificador) {
        this.estado = estado;
        this.numeroPaginas = numeroPaginas;
        this.issn = issn;
        this.fechaPublicacion = fechaPublicacion;
        this.categoria = categoria;
        this.editorial = editorial;
        this.titulo = titulo;
        this.identificador = identificador;
    }

    // Implement Prestable methods
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
            this.fechaDevolucion = LocalDateTime.now().plusDays(7); // Shorter loan period for magazines
        }
    }

    public int getNumeroPaginas() {
        return numeroPaginas;
    }

    public void setNumeroPaginas(int numeroPaginas) {
        this.numeroPaginas = numeroPaginas;
    }

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public String getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(String fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
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
        return "Revista{" +
                "identificador='" + identificador + '\'' +
                ", titulo='" + titulo + '\'' +
                ", editorial='" + editorial + '\'' +
                ", categoria='" + categoria + '\'' +
                ", fechaPublicacion='" + fechaPublicacion + '\'' +
                ", issn='" + issn + '\'' +
                ", numeroPaginas=" + numeroPaginas +
                ", estado=" + estado +
                '}';
    }
}