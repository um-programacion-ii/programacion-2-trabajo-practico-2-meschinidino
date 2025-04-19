package um.prog2.reportes;

import um.prog2.Enums.CategoriaRecurso;
import um.prog2.interfaces.RecursoDigital;
import um.prog2.prestamos.Prestamo;
import um.prog2.prestamos.SistemaPrestamos;
import um.prog2.recursoDigital.Libro;
import um.prog2.recursoDigital.AudioLibro;
import um.prog2.recursoDigital.Revista;
import um.prog2.usuario.Usuario;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Sistema de generación de reportes para la biblioteca digital.
 * Permite generar reportes sobre recursos, usuarios y categorías.
 */
public class SistemaReportes {
    private final SistemaPrestamos sistemaPrestamos;
    private final List<RecursoDigital> recursos;
    private final Map<String, Usuario> usuarios;

    /**
     * Constructor del sistema de reportes.
     * 
     * @param sistemaPrestamos Sistema de préstamos para obtener información sobre préstamos
     * @param recursos Lista de recursos de la biblioteca
     * @param usuarios Mapa de usuarios de la biblioteca
     */
    public SistemaReportes(SistemaPrestamos sistemaPrestamos, List<RecursoDigital> recursos, Map<String, Usuario> usuarios) {
        this.sistemaPrestamos = sistemaPrestamos;
        this.recursos = recursos;
        this.usuarios = usuarios;
    }

    /**
     * Genera un reporte de los recursos más prestados.
     * 
     * @param limite Número máximo de recursos a incluir en el reporte
     * @return Mapa ordenado con los recursos más prestados y la cantidad de préstamos
     */
    public Map<RecursoDigital, Integer> generarReporteRecursosMasPrestados(int limite) {
        Map<RecursoDigital, Integer> contadorPrestamos = new HashMap<>();

        // Obtener todos los préstamos históricos para todos los usuarios
        List<Prestamo> todosLosPrestamos = new ArrayList<>();
        for (Usuario usuario : usuarios.values()) {
            todosLosPrestamos.addAll(sistemaPrestamos.obtenerHistorialPrestamos(usuario));
        }

        // Contar préstamos por recurso
        for (Prestamo prestamo : todosLosPrestamos) {
            RecursoDigital recurso = prestamo.getRecurso();
            contadorPrestamos.put(recurso, contadorPrestamos.getOrDefault(recurso, 0) + 1);
        }

        // Ordenar por cantidad de préstamos (descendente) y limitar resultados
        return contadorPrestamos.entrySet().stream()
                .sorted(Map.Entry.<RecursoDigital, Integer>comparingByValue().reversed())
                .limit(limite)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * Genera un reporte de los usuarios más activos.
     * 
     * @param limite Número máximo de usuarios a incluir en el reporte
     * @return Mapa ordenado con los usuarios más activos y la cantidad de préstamos
     */
    public Map<Usuario, Integer> generarReporteUsuariosMasActivos(int limite) {
        Map<Usuario, Integer> contadorPrestamos = new HashMap<>();

        // Contar préstamos por usuario
        for (Usuario usuario : usuarios.values()) {
            List<Prestamo> historialPrestamos = sistemaPrestamos.obtenerHistorialPrestamos(usuario);
            contadorPrestamos.put(usuario, historialPrestamos.size());
        }

        // Ordenar por cantidad de préstamos (descendente) y limitar resultados
        return contadorPrestamos.entrySet().stream()
                .sorted(Map.Entry.<Usuario, Integer>comparingByValue().reversed())
                .limit(limite)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * Genera estadísticas de uso por categoría.
     * 
     * @return Mapa con las categorías y la cantidad de préstamos
     */
    public Map<CategoriaRecurso, Integer> generarEstadisticasPorCategoria() {
        Map<CategoriaRecurso, Integer> contadorCategorias = new HashMap<>();

        // Inicializar contador para todas las categorías
        for (CategoriaRecurso categoria : CategoriaRecurso.values()) {
            contadorCategorias.put(categoria, 0);
        }

        // Obtener todos los préstamos históricos para todos los usuarios
        List<Prestamo> todosLosPrestamos = new ArrayList<>();
        for (Usuario usuario : usuarios.values()) {
            todosLosPrestamos.addAll(sistemaPrestamos.obtenerHistorialPrestamos(usuario));
        }

        // Contar préstamos por categoría
        for (Prestamo prestamo : todosLosPrestamos) {
            RecursoDigital recurso = prestamo.getRecurso();
            CategoriaRecurso categoria = obtenerCategoriaRecurso(recurso);
            if (categoria != null) {
                contadorCategorias.put(categoria, contadorCategorias.getOrDefault(categoria, 0) + 1);
            }
        }

        // Ordenar por cantidad de préstamos (descendente)
        return contadorCategorias.entrySet().stream()
                .sorted(Map.Entry.<CategoriaRecurso, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * Obtiene la categoría de un recurso.
     * 
     * @param recurso Recurso del que se quiere obtener la categoría
     * @return Categoría del recurso o null si no se puede determinar
     */
    private CategoriaRecurso obtenerCategoriaRecurso(RecursoDigital recurso) {
        if (recurso instanceof Libro) {
            return ((Libro) recurso).getCategoria();
        } else if (recurso instanceof AudioLibro) {
            return ((AudioLibro) recurso).getCategoria();
        } else if (recurso instanceof Revista) {
            return ((Revista) recurso).getCategoria();
        }
        return CategoriaRecurso.NO_FICCION; // Default category if type is unknown
    }
}
