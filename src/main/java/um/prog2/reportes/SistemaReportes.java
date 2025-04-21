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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;
import java.util.stream.Collectors;

/**
 * Sistema de generación de reportes para la biblioteca digital.
 * Permite generar reportes sobre recursos, usuarios y categorías.
 */
public class SistemaReportes {
    private final SistemaPrestamos sistemaPrestamos;
    private final List<RecursoDigital> recursos;
    private final Map<String, Usuario> usuarios;

    // ExecutorService para manejar tareas asíncronas
    private final ExecutorService executorService;

    // Mapa para almacenar el progreso de generación de reportes
    private final Map<String, AtomicInteger> progresoReportes;

    // Mapa para almacenar los resultados de los reportes
    private final Map<String, Object> resultadosReportes;

    // Lock para acceso concurrente a los datos
    private final ReadWriteLock rwLock;

    // Constantes para identificar los tipos de reportes
    public static final String REPORTE_RECURSOS_MAS_PRESTADOS = "recursos_mas_prestados";
    public static final String REPORTE_USUARIOS_MAS_ACTIVOS = "usuarios_mas_activos";
    public static final String REPORTE_ESTADISTICAS_CATEGORIA = "estadisticas_categoria";

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

        // Inicializar ExecutorService con un pool de hilos fijo
        this.executorService = Executors.newFixedThreadPool(3);

        // Inicializar mapas para progreso y resultados
        this.progresoReportes = new ConcurrentHashMap<>();
        this.resultadosReportes = new ConcurrentHashMap<>();

        // Inicializar lock para acceso concurrente
        this.rwLock = new ReentrantReadWriteLock();
    }

    /**
     * Obtiene el progreso actual de generación de un reporte.
     * 
     * @param tipoReporte Tipo de reporte
     * @return Porcentaje de progreso (0-100) o -1 si el reporte no está en progreso
     */
    public int obtenerProgresoReporte(String tipoReporte) {
        AtomicInteger progreso = progresoReportes.get(tipoReporte);
        return progreso != null ? progreso.get() : -1;
    }

    /**
     * Verifica si un reporte está completo.
     * 
     * @param tipoReporte Tipo de reporte
     * @return true si el reporte está completo, false en caso contrario
     */
    public boolean esReporteCompleto(String tipoReporte) {
        return resultadosReportes.containsKey(tipoReporte);
    }

    /**
     * Obtiene el resultado de un reporte.
     * 
     * @param tipoReporte Tipo de reporte
     * @return Resultado del reporte o null si no está disponible
     */
    @SuppressWarnings("unchecked")
    public <T> T obtenerResultadoReporte(String tipoReporte) {
        return (T) resultadosReportes.get(tipoReporte);
    }

    /**
     * Cancela todos los reportes en progreso y cierra el ExecutorService.
     */
    public void cerrar() {
        executorService.shutdownNow();
    }

    /**
     * Limpia todos los reportes en progreso y resultados almacenados.
     * Útil para liberar memoria cuando ya no se necesitan los reportes.
     */
    public void limpiarReportes() {
        progresoReportes.clear();
        resultadosReportes.clear();
    }

    /**
     * Inicia la generación de un reporte de los recursos más prestados en segundo plano.
     * 
     * @param limite Número máximo de recursos a incluir en el reporte
     * @return Future que representa la tarea asíncrona
     */
    public Future<Map<RecursoDigital, Integer>> iniciarReporteRecursosMasPrestados(int limite) {
        // Limpiar resultados anteriores y reiniciar progreso
        resultadosReportes.remove(REPORTE_RECURSOS_MAS_PRESTADOS);
        progresoReportes.put(REPORTE_RECURSOS_MAS_PRESTADOS, new AtomicInteger(0));

        // Crear y ejecutar tarea asíncrona
        return executorService.submit(() -> {
            try {
                Map<RecursoDigital, Integer> resultado = generarReporteRecursosMasPrestados(limite);
                // Almacenar resultado
                resultadosReportes.put(REPORTE_RECURSOS_MAS_PRESTADOS, resultado);
                // Marcar como completado
                progresoReportes.get(REPORTE_RECURSOS_MAS_PRESTADOS).set(100);
                return resultado;
            } catch (Exception e) {
                // En caso de error, eliminar progreso
                progresoReportes.remove(REPORTE_RECURSOS_MAS_PRESTADOS);
                throw e;
            }
        });
    }

    /**
     * Genera un reporte de los recursos más prestados.
     * Este método es privado y se ejecuta en segundo plano.
     * 
     * @param limite Número máximo de recursos a incluir en el reporte
     * @return Mapa ordenado con los recursos más prestados y la cantidad de préstamos
     */
    private Map<RecursoDigital, Integer> generarReporteRecursosMasPrestados(int limite) {
        Map<RecursoDigital, Integer> contadorPrestamos = new HashMap<>();
        AtomicInteger progreso = progresoReportes.get(REPORTE_RECURSOS_MAS_PRESTADOS);

        // Adquirir lock de lectura para acceder a los datos
        Lock readLock = rwLock.readLock();
        readLock.lock();
        try {
            // Obtener todos los préstamos históricos para todos los usuarios
            List<Prestamo> todosLosPrestamos = new ArrayList<>();
            int totalUsuarios = usuarios.size();
            int usuariosProcesados = 0;

            for (Usuario usuario : usuarios.values()) {
                // Verificar si la tarea ha sido interrumpida
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException("Generación de reporte cancelada");
                }

                todosLosPrestamos.addAll(sistemaPrestamos.obtenerHistorialPrestamos(usuario));
                usuariosProcesados++;

                // Actualizar progreso (50% del progreso total es recolectar datos)
                if (progreso != null && totalUsuarios > 0) {
                    progreso.set((usuariosProcesados * 50) / totalUsuarios);
                }
            }

            // Contar préstamos por recurso
            int totalPrestamos = todosLosPrestamos.size();
            int prestamosProcesados = 0;

            for (Prestamo prestamo : todosLosPrestamos) {
                // Verificar si la tarea ha sido interrumpida
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException("Generación de reporte cancelada");
                }

                RecursoDigital recurso = prestamo.getRecurso();
                contadorPrestamos.put(recurso, contadorPrestamos.getOrDefault(recurso, 0) + 1);
                prestamosProcesados++;

                // Actualizar progreso (del 50% al 90%)
                if (progreso != null && totalPrestamos > 0) {
                    progreso.set(50 + (prestamosProcesados * 40) / totalPrestamos);
                }
            }

            // Ordenar por cantidad de préstamos (descendente) y limitar resultados
            // Actualizar progreso (90% a 100% es ordenar y formatear)
            if (progreso != null) {
                progreso.set(90);
            }

            Map<RecursoDigital, Integer> resultado = contadorPrestamos.entrySet().stream()
                    .sorted(Map.Entry.<RecursoDigital, Integer>comparingByValue().reversed())
                    .limit(limite)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));

            if (progreso != null) {
                progreso.set(100);
            }

            return resultado;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Generación de reporte cancelada", e);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Inicia la generación de un reporte de los usuarios más activos en segundo plano.
     * 
     * @param limite Número máximo de usuarios a incluir en el reporte
     * @return Future que representa la tarea asíncrona
     */
    public Future<Map<Usuario, Integer>> iniciarReporteUsuariosMasActivos(int limite) {
        // Limpiar resultados anteriores y reiniciar progreso
        resultadosReportes.remove(REPORTE_USUARIOS_MAS_ACTIVOS);
        progresoReportes.put(REPORTE_USUARIOS_MAS_ACTIVOS, new AtomicInteger(0));

        // Crear y ejecutar tarea asíncrona
        return executorService.submit(() -> {
            try {
                Map<Usuario, Integer> resultado = generarReporteUsuariosMasActivos(limite);
                // Almacenar resultado
                resultadosReportes.put(REPORTE_USUARIOS_MAS_ACTIVOS, resultado);
                // Marcar como completado
                progresoReportes.get(REPORTE_USUARIOS_MAS_ACTIVOS).set(100);
                return resultado;
            } catch (Exception e) {
                // En caso de error, eliminar progreso
                progresoReportes.remove(REPORTE_USUARIOS_MAS_ACTIVOS);
                throw e;
            }
        });
    }

    /**
     * Genera un reporte de los usuarios más activos.
     * Este método es privado y se ejecuta en segundo plano.
     * 
     * @param limite Número máximo de usuarios a incluir en el reporte
     * @return Mapa ordenado con los usuarios más activos y la cantidad de préstamos
     */
    private Map<Usuario, Integer> generarReporteUsuariosMasActivos(int limite) {
        Map<Usuario, Integer> contadorPrestamos = new HashMap<>();
        AtomicInteger progreso = progresoReportes.get(REPORTE_USUARIOS_MAS_ACTIVOS);

        // Adquirir lock de lectura para acceder a los datos
        Lock readLock = rwLock.readLock();
        readLock.lock();
        try {
            // Contar préstamos por usuario
            int totalUsuarios = usuarios.size();
            int usuariosProcesados = 0;

            for (Usuario usuario : usuarios.values()) {
                // Verificar si la tarea ha sido interrumpida
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException("Generación de reporte cancelada");
                }

                List<Prestamo> historialPrestamos = sistemaPrestamos.obtenerHistorialPrestamos(usuario);
                contadorPrestamos.put(usuario, historialPrestamos.size());
                usuariosProcesados++;

                // Actualizar progreso (80% del progreso total es recolectar datos)
                if (progreso != null && totalUsuarios > 0) {
                    progreso.set((usuariosProcesados * 80) / totalUsuarios);
                }
            }

            // Ordenar por cantidad de préstamos (descendente) y limitar resultados
            // Actualizar progreso (80% a 100% es ordenar y formatear)
            if (progreso != null) {
                progreso.set(80);
            }

            Map<Usuario, Integer> resultado = contadorPrestamos.entrySet().stream()
                    .sorted(Map.Entry.<Usuario, Integer>comparingByValue().reversed())
                    .limit(limite)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));

            if (progreso != null) {
                progreso.set(100);
            }

            return resultado;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Generación de reporte cancelada", e);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Inicia la generación de estadísticas de uso por categoría en segundo plano.
     * 
     * @return Future que representa la tarea asíncrona
     */
    public Future<Map<CategoriaRecurso, Integer>> iniciarEstadisticasPorCategoria() {
        // Limpiar resultados anteriores y reiniciar progreso
        resultadosReportes.remove(REPORTE_ESTADISTICAS_CATEGORIA);
        progresoReportes.put(REPORTE_ESTADISTICAS_CATEGORIA, new AtomicInteger(0));

        // Crear y ejecutar tarea asíncrona
        return executorService.submit(() -> {
            try {
                Map<CategoriaRecurso, Integer> resultado = generarEstadisticasPorCategoria();
                // Almacenar resultado
                resultadosReportes.put(REPORTE_ESTADISTICAS_CATEGORIA, resultado);
                // Marcar como completado
                progresoReportes.get(REPORTE_ESTADISTICAS_CATEGORIA).set(100);
                return resultado;
            } catch (Exception e) {
                // En caso de error, eliminar progreso
                progresoReportes.remove(REPORTE_ESTADISTICAS_CATEGORIA);
                throw e;
            }
        });
    }

    /**
     * Genera estadísticas de uso por categoría.
     * Este método es privado y se ejecuta en segundo plano.
     * 
     * @return Mapa con las categorías y la cantidad de préstamos
     */
    private Map<CategoriaRecurso, Integer> generarEstadisticasPorCategoria() {
        Map<CategoriaRecurso, Integer> contadorCategorias = new HashMap<>();
        AtomicInteger progreso = progresoReportes.get(REPORTE_ESTADISTICAS_CATEGORIA);

        // Adquirir lock de lectura para acceder a los datos
        Lock readLock = rwLock.readLock();
        readLock.lock();
        try {
            // Inicializar contador para todas las categorías
            for (CategoriaRecurso categoria : CategoriaRecurso.values()) {
                contadorCategorias.put(categoria, 0);
            }

            // Actualizar progreso
            if (progreso != null) {
                progreso.set(10);
            }

            // Obtener todos los préstamos históricos para todos los usuarios
            List<Prestamo> todosLosPrestamos = new ArrayList<>();
            int totalUsuarios = usuarios.size();
            int usuariosProcesados = 0;

            for (Usuario usuario : usuarios.values()) {
                // Verificar si la tarea ha sido interrumpida
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException("Generación de reporte cancelada");
                }

                todosLosPrestamos.addAll(sistemaPrestamos.obtenerHistorialPrestamos(usuario));
                usuariosProcesados++;

                // Actualizar progreso (del 10% al 60% es recolectar datos)
                if (progreso != null && totalUsuarios > 0) {
                    progreso.set(10 + (usuariosProcesados * 50) / totalUsuarios);
                }
            }

            // Contar préstamos por categoría
            int totalPrestamos = todosLosPrestamos.size();
            int prestamosProcesados = 0;

            for (Prestamo prestamo : todosLosPrestamos) {
                // Verificar si la tarea ha sido interrumpida
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException("Generación de reporte cancelada");
                }

                RecursoDigital recurso = prestamo.getRecurso();
                CategoriaRecurso categoria = obtenerCategoriaRecurso(recurso);
                if (categoria != null) {
                    contadorCategorias.put(categoria, contadorCategorias.getOrDefault(categoria, 0) + 1);
                }
                prestamosProcesados++;

                // Actualizar progreso (del 60% al 90%)
                if (progreso != null && totalPrestamos > 0) {
                    progreso.set(60 + (prestamosProcesados * 30) / totalPrestamos);
                }
            }

            // Ordenar por cantidad de préstamos (descendente)
            // Actualizar progreso (90% a 100% es ordenar y formatear)
            if (progreso != null) {
                progreso.set(90);
            }

            Map<CategoriaRecurso, Integer> resultado = contadorCategorias.entrySet().stream()
                    .sorted(Map.Entry.<CategoriaRecurso, Integer>comparingByValue().reversed())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));

            if (progreso != null) {
                progreso.set(100);
            }

            return resultado;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Generación de reporte cancelada", e);
        } finally {
            readLock.unlock();
        }
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
