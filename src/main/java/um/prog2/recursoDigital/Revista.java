package um.prog2.recursoDigital;

import um.prog2.Enums.CategoriaRecurso;
import um.prog2.Enums.EstadoRecurso;
import um.prog2.interfaces.Prestable;
import um.prog2.usuario.Usuario;
import java.time.LocalDateTime;

public class Revista extends RecursoBase implements Prestable {
    private String identificador;
    private String titulo;
    private String editorial;
    private CategoriaRecurso categoria; // Changed from String to enum
    private String fechaPublicacion;
    private String issn;
    private int numeroPaginas;
    private LocalDateTime fechaDevolucion;
    private Usuario usuarioPrestamo;

    public Revista() {
        this.categoria = CategoriaRecurso.NO_FICCION; // Default category
    }

    // Updated constructor with CategoriaRecurso
    public Revista(EstadoRecurso estado, int numeroPaginas, String issn, String fechaPublicacion,
                   CategoriaRecurso categoria, String editorial, String titulo, String identificador) {
        this.estado = estado;
        this.numeroPaginas = numeroPaginas;
        this.issn = issn;
        this.fechaPublicacion = fechaPublicacion;
        this.categoria = categoria;
        this.editorial = editorial;
        this.titulo = titulo;
        this.identificador = identificador;
    }

    // Constructor with String for backward compatibility
    public Revista(EstadoRecurso estado, int numeroPaginas, String issn, String fechaPublicacion,
                   String categoriaStr, String editorial, String titulo, String identificador) {
        this.estado = estado;
        this.numeroPaginas = numeroPaginas;
        this.issn = issn;
        this.fechaPublicacion = fechaPublicacion;
        try {
            this.categoria = CategoriaRecurso.valueOf(categoriaStr.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            this.categoria = CategoriaRecurso.NO_FICCION;
        }
        this.editorial = editorial;
        this.titulo = titulo;
        this.identificador = identificador;
    }

    // Implement Prestable methods - unchanged
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

    @Override
    public boolean devolver() {
        if (this.getEstado() == EstadoRecurso.PRESTADO) {
            this.setEstado(EstadoRecurso.DISPONIBLE);
            this.usuarioPrestamo = null;
            this.fechaDevolucion = null;
            return true;
        }
        return false;
    }

    // Other getters and setters - unchanged
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

    // Updated getter and setter for categoria
    public CategoriaRecurso getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaRecurso categoria) {
        this.categoria = categoria;
    }

    // String version for backward compatibility
    public void setCategoria(String categoriaStr) {
        try {
            this.categoria = CategoriaRecurso.valueOf(categoriaStr.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            this.categoria = CategoriaRecurso.NO_FICCION;
        }
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
                ", categoria=" + categoria +
                ", fechaPublicacion='" + fechaPublicacion + '\'' +
                ", issn='" + issn + '\'' +
                ", numeroPaginas=" + numeroPaginas +
                ", estado=" + estado +
                '}';
    }
}
