package um.prog2.recursoDigital;

import um.prog2.Enums.CategoriaRecurso;
import um.prog2.Enums.EstadoRecurso;
import um.prog2.interfaces.RecursoDigital;

import java.util.Comparator;

public class ComparadorRecurso {
    // Title comparators
    public static class PorTituloAsc implements Comparator<RecursoDigital> {
        @Override
        public int compare(RecursoDigital r1, RecursoDigital r2) {
            String titulo1 = getTitulo(r1);
            String titulo2 = getTitulo(r2);

            if (titulo1 == null && titulo2 == null) return 0;
            if (titulo1 == null) return -1;
            if (titulo2 == null) return 1;

            return titulo1.compareToIgnoreCase(titulo2);
        }

        private String getTitulo(RecursoDigital recurso) {
            if (recurso instanceof Libro) {
                return ((Libro) recurso).getTitulo();
            } else if (recurso instanceof AudioLibro) {
                return ((AudioLibro) recurso).getTitulo();
            } else if (recurso instanceof Revista) {
                return ((Revista) recurso).getTitulo();
            }
            return null;
        }
    }

    public static class PorTituloDesc implements Comparator<RecursoDigital> {
        @Override
        public int compare(RecursoDigital r1, RecursoDigital r2) {
            return new PorTituloAsc().compare(r2, r1);
        }
    }

    // Estado comparator (DISPONIBLE first)
    public static class PorEstado implements Comparator<RecursoDigital> {
        @Override
        public int compare(RecursoDigital r1, RecursoDigital r2) {
            EstadoRecurso estado1 = r1.getEstado();
            EstadoRecurso estado2 = r2.getEstado();

            if (estado1 == estado2) return 0;
            if (estado1 == EstadoRecurso.DISPONIBLE) return -1;
            if (estado2 == EstadoRecurso.DISPONIBLE) return 1;
            if (estado1 == EstadoRecurso.RESERVADO) return -1;
            return 1;
        }
    }

    // Author comparator
    public static class PorAutorAsc implements Comparator<RecursoDigital> {
        @Override
        public int compare(RecursoDigital r1, RecursoDigital r2) {
            String autor1 = getAutor(r1);
            String autor2 = getAutor(r2);

            if (autor1 == null && autor2 == null) return 0;
            if (autor1 == null) return -1;
            if (autor2 == null) return 1;

            return autor1.compareToIgnoreCase(autor2);
        }

        private String getAutor(RecursoDigital recurso) {
            if (recurso instanceof Libro) {
                return ((Libro) recurso).getAutor();
            } else if (recurso instanceof AudioLibro) {
                return ((AudioLibro) recurso).getAutor();
            }
            return null;
        }
    }

    public static class PorCategoriaAsc implements Comparator<RecursoDigital> {
        @Override
        public int compare(RecursoDigital r1, RecursoDigital r2) {
            CategoriaRecurso cat1 = getCategoria(r1);
            CategoriaRecurso cat2 = getCategoria(r2);

            if (cat1 == null && cat2 == null) return 0;
            if (cat1 == null) return -1;
            if (cat2 == null) return 1;

            return cat1.name().compareTo(cat2.name());
        }

        private CategoriaRecurso getCategoria(RecursoDigital recurso) {
            if (recurso instanceof Libro) {
                return ((Libro) recurso).getCategoria();
            } else if (recurso instanceof AudioLibro) {
                return ((AudioLibro) recurso).getCategoria();
            } else if (recurso instanceof Revista) {
                return ((Revista) recurso).getCategoria();
            }
            return null;
        }
    }

    public static class PorTipo implements Comparator<RecursoDigital> {
        @Override
        public int compare(RecursoDigital r1, RecursoDigital r2) {
            return r1.getClass().getSimpleName().compareTo(r2.getClass().getSimpleName());
        }
    }
}