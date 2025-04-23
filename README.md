[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/tc38IXJF)
# 📚 Trabajo Práctico: Sistema de Gestión de Biblioteca Digital (Java 21+)

## 📌 Objetivo General

Desarrollar un sistema de gestión de biblioteca digital que implemente los cinco principios SOLID, programación orientada a objetos, y conceptos avanzados de Java. El sistema deberá manejar diferentes tipos de recursos digitales, préstamos, reservas, y notificaciones en tiempo real.

## 👨‍🎓 Información del Alumno
- **Nombre y Apellido**: Dino Meschini

## 📋 Requisitos Adicionales

### Documentación del Sistema
Como parte del trabajo práctico, deberás incluir en este README una guía de uso que explique:

1. **Cómo funciona el sistema**:
   - Descripción general de la arquitectura
   - Explicación de los componentes principales
   - Flujo de trabajo del sistema

2. **Cómo ponerlo en funcionamiento**:
   - Deberás incluir las instrucciones detalladas de puesta en marcha
   - Explicar los requisitos previos necesarios
   - Describir el proceso de compilación
   - Detallar cómo ejecutar la aplicación

3. **Cómo probar cada aspecto desarrollado**:
   - Deberás proporcionar ejemplos de uso para cada funcionalidad implementada
   - Incluir casos de prueba que demuestren el funcionamiento del sistema
   - Describir flujos de trabajo completos que muestren la interacción entre diferentes componentes

La guía debe ser clara, concisa y permitir a cualquier usuario entender y probar el sistema. Se valorará especialmente:
- La claridad de las instrucciones
- La completitud de la documentación
- La organización de la información
- La inclusión de ejemplos prácticos

# Documentación del Sistema

## 1. Cómo funciona el sistema

### Descripción general de la arquitectura
El Sistema de Gestión de Biblioteca Digital está diseñado siguiendo los principios SOLID y utilizando una arquitectura modular orientada a objetos. El sistema está estructurado en capas lógicas que separan las responsabilidades y facilitan la extensibilidad y mantenimiento del código.

La arquitectura del sistema se compone de:
- **Capa de presentación**: Interfaz de línea de comandos (CLI) que permite la interacción con el usuario.
- **Capa de lógica de negocio**: Gestores y servicios que implementan la funcionalidad principal.
- **Capa de modelo de datos**: Clases que representan las entidades del sistema.

### Explicación de los componentes principales

#### Recursos Digitales
- **RecursoDigital (Interfaz)**: Define el contrato básico para todos los recursos.
- **RecursoBase**: Clase abstracta que implementa funcionalidad común.
- **Libro, Revista, AudioLibro**: Implementaciones concretas de recursos digitales.
- **GestorRecursos**: Administra la colección de recursos, permitiendo agregar, buscar y listar.

#### Usuarios
- **Usuario**: Representa a los usuarios del sistema con sus atributos y comportamientos.
- **GestorUsuarios**: Maneja el registro, búsqueda y gestión de usuarios.

#### Préstamos y Reservas
- **SistemaPrestamos**: Gestiona el proceso de préstamo y devolución de recursos.
- **Prestamo**: Representa un préstamo individual con su información asociada.
- **SistemaReservas**: Administra las reservas de recursos no disponibles.
- **Reserva**: Representa una reserva individual con su información asociada.

#### Alertas y Notificaciones
- **AlertaVencimiento**: Monitorea fechas de devolución y genera alertas.
- **AlertaDisponibilidad**: Notifica cuando un recurso reservado está disponible.
- **SistemaRecordatorios**: Gestiona recordatorios automáticos.
- **HistorialAlertas**: Mantiene un registro de todas las alertas generadas.
- **ServicioNotificaciones (Interfaz)**: Define el contrato para enviar notificaciones.

#### Reportes
- **GestorReportes**: Genera informes y estadísticas sobre el uso del sistema.

### Flujo de trabajo del sistema

1. **Inicio del sistema**:
    - La aplicación se inicia desde la clase `Main`, que lanza la interfaz de línea de comandos (CLI).
    - Se inicializan los componentes principales: gestores de recursos, usuarios, préstamos y reservas.

2. **Gestión de recursos**:
    - Los recursos se pueden agregar, buscar, listar y categorizar.
    - Cada recurso tiene un estado (disponible, prestado, reservado) que determina las operaciones posibles.

3. **Gestión de usuarios**:
    - Los usuarios se registran en el sistema con información básica.
    - Se pueden buscar y gestionar usuarios existentes.

4. **Proceso de préstamo**:
    - Un usuario solicita un préstamo de un recurso disponible.
    - El sistema verifica la disponibilidad y actualiza el estado del recurso.
    - Se establece una fecha de devolución y se registra el préstamo.
    - Se generan alertas automáticas para recordar la fecha de vencimiento.

5. **Proceso de reserva**:
    - Si un recurso no está disponible, un usuario puede reservarlo.
    - El sistema mantiene una cola de reservas para cada recurso.
    - Cuando el recurso se devuelve, se notifica al primer usuario en la cola.

6. **Sistema de alertas**:
    - Monitoreo continuo de fechas de vencimiento.
    - Generación de alertas cuando un recurso reservado está disponible.
    - Envío de notificaciones a los usuarios según sus preferencias.

7. **Generación de reportes**:
    - El sistema puede generar informes sobre recursos más prestados, usuarios más activos, etc.
    - Los reportes se pueden visualizar en la consola.

## 2. Cómo ponerlo en funcionamiento

### Requisitos previos
- Java Development Kit (JDK) 21 o superior
- Apache Maven 3.6.0 o superior
- Git (opcional, para clonar el repositorio)

### Proceso de compilación
1. **Clonar el repositorio** (si no lo has hecho ya):
   ```bash
   git clone https://github.com/um-programacion-ii/programacion-2-trabajo-practico-2-meschinidino
   cd prog2tp2
   ```

2. **Compilar el proyecto con Maven**:
   ```bash
   mvn clean compile
   ```

3. **Empaquetar el proyecto**:
   ```bash
   mvn package
   ```
   Esto generará un archivo JAR ejecutable en el directorio `target/`.

### Cómo ejecutar la aplicación
1. **Ejecutar directamente con Maven**:
   ```bash
   mvn exec:java -Dexec.mainClass="um.prog2.Main"
   ```

2. **Ejecutar el archivo JAR generado**:
   ```bash
   java -jar target/prog2tp2-1.0-SNAPSHOT.jar
   ```

3. **Interactuar con la aplicación**:
    - Una vez iniciada, la aplicación mostrará un menú de opciones en la consola.
    - Sigue las instrucciones en pantalla para navegar por las diferentes funcionalidades.

## 3. Cómo probar cada aspecto desarrollado

### Gestión de Recursos

#### Agregar un nuevo recurso
1. Selecciona la opción "Gestión de Recursos" en el menú principal.
2. Elige "Agregar nuevo recurso".
3. Selecciona el tipo de recurso (Libro, Revista, AudioLibro).
4. Ingresa la información solicitada (título, autor, identificador, categoría).
5. Verifica que el recurso se haya agregado correctamente usando la opción "Listar recursos".

#### Buscar recursos
1. Selecciona la opción "Gestión de Recursos" en el menú principal.
2. Elige "Buscar recursos".
3. Ingresa un término de búsqueda (título, autor, categoría).
4. Verifica que los resultados coincidan con el criterio de búsqueda.

#### Listar recursos por categoría
1. Selecciona la opción "Gestión de Recursos" en el menú principal.
2. Elige "Listar recursos".
3. Selecciona "Filtrar por categoría".
4. Elige una categoría de la lista.
5. Verifica que solo se muestren los recursos de esa categoría.

### Gestión de Usuarios

#### Registrar un nuevo usuario
1. Selecciona la opción "Gestión de Usuarios" en el menú principal.
2. Elige "Registrar nuevo usuario".
3. Ingresa la información solicitada (nombre, apellido, ID, email, teléfono).
4. Verifica que el usuario se haya registrado correctamente usando la opción "Buscar usuario".

#### Buscar usuario
1. Selecciona la opción "Gestión de Usuarios" en el menú principal.
2. Elige "Buscar usuario".
3. Ingresa el ID o nombre del usuario.
4. Verifica que se muestre la información correcta del usuario.

### Sistema de Préstamos

#### Realizar un préstamo
1. Selecciona la opción "Préstamos" en el menú principal.
2. Elige "Realizar préstamo".
3. Ingresa el ID del usuario y el identificador del recurso.
4. Verifica que el estado del recurso cambie a "PRESTADO".
5. Comprueba que se genere una fecha de devolución (14 días después).

#### Devolver un recurso
1. Selecciona la opción "Préstamos" en el menú principal.
2. Elige "Devolver recurso".
3. Ingresa el identificador del recurso a devolver.
4. Verifica que el estado del recurso cambie a "DISPONIBLE".
5. Si hay reservas para ese recurso, verifica que se notifique al primer usuario en la cola.

#### Renovar un préstamo
1. Selecciona la opción "Préstamos" en el menú principal.
2. Elige "Renovar préstamo".
3. Ingresa el identificador del recurso prestado.
4. Verifica que la fecha de devolución se extienda por 7 días adicionales.

### Sistema de Reservas

#### Realizar una reserva
1. Selecciona la opción "Reservas" en el menú principal.
2. Elige "Realizar reserva".
3. Ingresa el ID del usuario y el identificador del recurso (que debe estar prestado).
4. Verifica que la reserva se registre correctamente.

#### Consultar cola de reservas
1. Selecciona la opción "Reservas" en el menú principal.
2. Elige "Ver cola de reservas".
3. Ingresa el identificador del recurso.
4. Verifica que se muestre la lista de usuarios en la cola de reservas.

### Sistema de Alertas

#### Verificar alertas de vencimiento
1. Realiza un préstamo con una fecha de vencimiento cercana.
2. Selecciona la opción "Alertas" en el menú principal.
3. Elige "Ver alertas de vencimiento".
4. Verifica que se muestren las alertas para préstamos próximos a vencer.

#### Verificar alertas de disponibilidad
1. Realiza una reserva para un recurso prestado.
2. Devuelve el recurso prestado.
3. Selecciona la opción "Alertas" en el menú principal.
4. Elige "Ver alertas de disponibilidad".
5. Verifica que se muestre una alerta para el usuario que realizó la reserva.

### Reportes

#### Generar reporte de recursos más prestados
1. Selecciona la opción "Reportes" en el menú principal.
2. Elige "Recursos más prestados".
3. Verifica que se muestre una lista ordenada de recursos según la frecuencia de préstamos.

#### Generar reporte de usuarios más activos
1. Selecciona la opción "Reportes" en el menú principal.
2. Elige "Usuarios más activos".
3. Verifica que se muestre una lista ordenada de usuarios según su actividad en el sistema.

### Flujos de trabajo completos

#### Flujo completo de préstamo y devolución
1. Registra un nuevo usuario.
2. Agrega un nuevo libro.
3. Realiza un préstamo con ese usuario y libro.
4. Verifica el estado del libro (PRESTADO).
5. Devuelve el libro.
6. Verifica que el estado del libro cambie a DISPONIBLE.

#### Flujo completo de reserva y notificación
1. Registra dos usuarios (Usuario A y Usuario B).
2. Agrega un nuevo libro.
3. Realiza un préstamo del libro al Usuario A.
4. Realiza una reserva del libro para el Usuario B.
5. Devuelve el libro (Usuario A).
6. Verifica que se genere una alerta de disponibilidad para el Usuario B.
7. Verifica que el Usuario B pueda realizar el préstamo del libro reservado.

#### Flujo completo de alertas de vencimiento
1. Registra un usuario.
2. Agrega un nuevo libro.
3. Realiza un préstamo con una fecha de vencimiento cercana.
4. Verifica que se generen alertas de vencimiento.
5. Renueva el préstamo.
6. Verifica que la fecha de vencimiento se actualice y las alertas se ajusten.
### Prueba de Funcionalidades

#### 1. Gestión de Recursos
- **Agregar Libro**: 
  - Proceso para agregar un nuevo libro al sistema
  - Verificación de que el libro se agregó correctamente
  - Validación de los datos ingresados

- **Buscar Recurso**:
  - Proceso de búsqueda de recursos
  - Verificación de resultados de búsqueda
  - Manejo de casos donde no se encuentran resultados

- **Listar Recursos**:
  - Visualización de todos los recursos
  - Filtrado por diferentes criterios
  - Ordenamiento de resultados

#### 2. Gestión de Usuarios
- **Registrar Usuario**:
  - Proceso de registro de nuevos usuarios
  - Validación de datos del usuario
  - Verificación del registro exitoso

- **Buscar Usuario**:
  - Proceso de búsqueda de usuarios
  - Visualización de información del usuario
  - Manejo de usuarios no encontrados

#### 3. Préstamos
- **Realizar Préstamo**:
  - Proceso completo de préstamo
  - Verificación de disponibilidad
  - Actualización de estados

- **Devolver Recurso**:
  - Proceso de devolución
  - Actualización de estados
  - Liberación del recurso

#### 4. Reservas
- **Realizar Reserva**:
  - Proceso de reserva de recursos
  - Gestión de cola de reservas
  - Notificación de disponibilidad

#### 5. Reportes
- **Ver Reportes**:
  - Generación de diferentes tipos de reportes
  - Visualización de estadísticas
  - Exportación de datos

#### 6. Alertas
- **Verificar Alertas**:
  - Sistema de notificaciones
  - Diferentes tipos de alertas
  - Gestión de recordatorios

### Ejemplos de Prueba
1. **Flujo Completo de Préstamo**:
   - Registrar un usuario
   - Agregar un libro
   - Realizar un préstamo
   - Verificar el estado del recurso
   - Devolver el recurso
   - Verificar la actualización del estado

2. **Sistema de Reservas**:
   - Registrar dos usuarios
   - Agregar un libro
   - Realizar una reserva con cada usuario
   - Verificar la cola de reservas
   - Procesar las reservas

3. **Alertas y Notificaciones**:
   - Realizar un préstamo
   - Esperar a que se acerque la fecha de vencimiento
   - Verificar las alertas generadas
   - Probar la renovación del préstamo

## 🧩 Tecnologías y Herramientas

- Java 21+ (LTS)
- Git y GitHub
- GitHub Projects
- GitHub Issues
- GitHub Pull Requests

## 📘 Etapas del Trabajo

### Etapa 1: Diseño Base y Principios SOLID
- **SRP**: 
  - Crear clase `Usuario` con atributos básicos (nombre, ID, email)
  - Crear clase `RecursoDigital` como clase base abstracta
  - Implementar clase `GestorUsuarios` separada de `GestorRecursos`
  - Cada clase debe tener una única responsabilidad clara
  - Implementar clase `Consola` para manejar la interacción con el usuario

- **OCP**: 
  - Diseñar interfaz `RecursoDigital` con métodos comunes
  - Implementar clases concretas `Libro`, `Revista`, `Audiolibro`
  - Usar herencia para extender funcionalidad sin modificar código existente
  - Ejemplo: agregar nuevo tipo de recurso sin cambiar clases existentes
  - Implementar menú de consola extensible para nuevos tipos de recursos

- **LSP**: 
  - Asegurar que todas las subclases de `RecursoDigital` puedan usarse donde se espera `RecursoDigital`
  - Implementar métodos comunes en la clase base
  - Validar que el comportamiento sea consistente en todas las subclases
  - Crear métodos de visualización en consola para todos los tipos de recursos

- **ISP**: 
  - Crear interfaz `Prestable` para recursos que se pueden prestar
  - Crear interfaz `Renovable` para recursos que permiten renovación
  - Implementar solo las interfaces necesarias en cada clase
  - Diseñar menús de consola específicos para cada tipo de operación

- **DIP**: 
  - Crear interfaz `ServicioNotificaciones`
  - Implementar `ServicioNotificacionesEmail` y `ServicioNotificacionesSMS`
  - Usar inyección de dependencias en las clases que necesitan notificaciones
  - Implementar visualización de notificaciones en consola

### Etapa 2: Gestión de Recursos y Colecciones
- Implementar colecciones:
  - Usar `ArrayList<RecursoDigital>` para almacenar recursos
  - Usar `Map<String, Usuario>` para gestionar usuarios
  - Implementar métodos de búsqueda básicos
  - Crear menú de consola para gestión de recursos

- Crear servicios de búsqueda:
  - Implementar búsqueda por título usando Streams
  - Implementar filtrado por categoría
  - Crear comparadores personalizados para ordenamiento
  - Diseñar interfaz de consola para búsquedas con filtros

- Sistema de categorización:
  - Crear enum `CategoriaRecurso`
  - Implementar método de asignación de categorías
  - Crear búsqueda por categoría
  - Mostrar categorías disponibles en consola

- Manejo de excepciones:
  - Crear `RecursoNoDisponibleException`
  - Crear `UsuarioNoEncontradoException`
  - Implementar manejo adecuado de excepciones en los servicios
  - Mostrar mensajes de error amigables en consola

### Etapa 3: Sistema de Préstamos y Reservas
- Implementar sistema de préstamos:
  - Crear clase `Prestamo` con atributos básicos
  - Implementar lógica de préstamo y devolución
  - Manejar estados de los recursos (disponible, prestado, reservado)
  - Diseñar menú de consola para préstamos

- Sistema de reservas:
  - Crear clase `Reserva` con atributos necesarios
  - Implementar cola de reservas usando `BlockingQueue`
  - Manejar prioridad de reservas
  - Mostrar estado de reservas en consola

- Notificaciones:
  - Implementar sistema básico de notificaciones
  - Crear diferentes tipos de notificaciones
  - Usar `ExecutorService` para enviar notificaciones
  - Mostrar notificaciones en consola

- Concurrencia:
  - Implementar sincronización en operaciones de préstamo
  - Usar `synchronized` donde sea necesario
  - Manejar condiciones de carrera
  - Mostrar estado de operaciones concurrentes en consola

### Etapa 4: Reportes y Análisis
- Generar reportes básicos:
  - Implementar reporte de recursos más prestados
  - Crear reporte de usuarios más activos
  - Generar estadísticas de uso por categoría
  - Diseñar visualización de reportes en consola

- Sistema de alertas:
  - Implementar alertas por vencimiento de préstamos:
    - Crear clase `AlertaVencimiento` que monitorea fechas de devolución
    - Implementar lógica de recordatorios (1 día antes, día del vencimiento)
    - Mostrar alertas en consola con formato destacado
    - Permitir renovación desde la alerta
  
  - Crear notificaciones de disponibilidad:
    - Implementar `AlertaDisponibilidad` para recursos reservados
    - Notificar cuando un recurso reservado está disponible
    - Mostrar lista de recursos disponibles en consola
    - Permitir préstamo inmediato desde la notificación
  
  - Manejar recordatorios automáticos:
    - Implementar sistema de recordatorios periódicos
    - Crear diferentes niveles de urgencia (info, warning, error)
    - Mostrar historial de alertas en consola
    - Permitir configuración de preferencias de notificación

- Concurrencia en reportes:
  - Implementar generación de reportes en segundo plano
  - Usar `ExecutorService` para tareas asíncronas
  - Manejar concurrencia en acceso a datos
  - Mostrar progreso de generación de reportes en consola

## 📋 Detalle de Implementación

### 1. Estructura Base
```java
// Interfaces principales
public interface RecursoDigital {
    String getIdentificador();
    EstadoRecurso getEstado();
    void actualizarEstado(EstadoRecurso estado);
}

public interface Prestable {
    boolean estaDisponible();
    LocalDateTime getFechaDevolucion();
    void prestar(Usuario usuario);
}

public interface Notificable {
    void enviarNotificacion(String mensaje);
    List<Notificacion> getNotificacionesPendientes();
}

// Clase base abstracta
public abstract class RecursoBase implements RecursoDigital, Prestable {
    // Implementación común
}
```

### 2. Gestión de Biblioteca
```java
public class GestorBiblioteca {
    private final Map<String, RecursoDigital> recursos;
    private final List<Prestamo> prestamos;
    private final ExecutorService notificador;
    // Implementación de gestión
}
```

### 3. Sistema de Préstamos
```java
public class SistemaPrestamos {
    private final BlockingQueue<SolicitudPrestamo> colaSolicitudes;
    private final ExecutorService procesadorPrestamos;
    // Implementación de préstamos
}
```

## ✅ Entrega y Flujo de Trabajo con GitHub

1. **Configuración del Repositorio**
   - Proteger la rama `main`
   - Crear template de Issues y Pull Requests

2. **Project Kanban**
   - `To Do`
   - `In Progress`
   - `Code Review`
   - `Done`

3. **Milestones**
   - Etapa 1: Diseño Base
   - Etapa 2: Gestión de Recursos
   - Etapa 3: Sistema de Préstamos
   - Etapa 4: Reportes

4. **Issues y Pull Requests**
   - Crear Issues detallados para cada funcionalidad
   - Asociar cada Issue a un Milestone
   - Implementar en ramas feature
   - Revisar código antes de merge

## 📝 Ejemplo de Issue

### Título
Implementar sistema de préstamos concurrente

### Descripción
Crear el sistema de préstamos que utilice hilos y el patrón productor-consumidor para procesar solicitudes de préstamo en tiempo real.

#### Requisitos
- Implementar `BlockingQueue` para solicitudes de préstamo
- Crear procesador de solicitudes usando `ExecutorService`
- Implementar sistema de notificaciones
- Asegurar thread-safety en operaciones de préstamo

#### Criterios de Aceptación
- [ ] Sistema procesa préstamos concurrentemente
- [ ] Manejo adecuado de excepciones
- [ ] Documentación de diseño

### Labels
- `enhancement`
- `concurrency`

## ✅ Requisitos para la Entrega

- ✅ Implementación completa de todas las etapas
- ✅ Código bien documentado
- ✅ Todos los Issues cerrados
- ✅ Todos los Milestones completados
- ✅ Pull Requests revisados y aprobados
- ✅ Project actualizado

> ⏰ **Fecha de vencimiento**: 23/04/2025 a las 13:00 hs

## 📚 Recursos Adicionales

- Documentación oficial de Java 21
- Guías de estilo de código
- Ejemplos de implementación concurrente
- Patrones de diseño aplicados

## 📝 Consideraciones Éticas

### Uso de Inteligencia Artificial
El uso de herramientas de IA en este trabajo práctico debe seguir las siguientes pautas:

1. **Transparencia**
   - Documentar claramente qué partes del código fueron generadas con IA
   - Explicar las modificaciones realizadas al código generado
   - Mantener un registro de las herramientas utilizadas

2. **Aprendizaje**
   - La IA debe usarse como herramienta de aprendizaje, no como reemplazo
   - Comprender y ser capaz de explicar el código generado
   - Utilizar la IA para mejorar la comprensión de conceptos

3. **Integridad Académica**
   - El trabajo final debe reflejar tu aprendizaje y comprensión personal
   - No se permite la presentación de código generado sin comprensión
   - Debes poder explicar y defender cualquier parte del código

4. **Responsabilidad**
   - Verificar la corrección y seguridad del código generado
   - Asegurar que el código cumple con los requisitos del proyecto
   - Mantener la calidad y estándares de código establecidos

5. **Desarrollo Individual**
   - La IA puede usarse para facilitar tu proceso de aprendizaje
   - Documentar tu proceso de desarrollo y decisiones tomadas
   - Mantener un registro de tu progreso y aprendizaje

### Consecuencias del Uso Inadecuado
El uso inadecuado de IA puede resultar en:
- Calificación reducida o nula
- Sanciones académicas
- Pérdida de oportunidades de aprendizaje
- Impacto negativo en tu desarrollo profesional

## 📝 Licencia

Este trabajo es parte del curso de Programación Avanzada de Ingeniería en Informática. Uso educativo únicamente.


## Uso de Inteligencia Artificial en este proyecto:
- Los tests se agregaron utilizando Junie para manejar la integración de nuevas partes al proyecto sin que explote todo
- Se utiliza Junie también para agregar el sistema de préstamos y reservas
- Más info sobre Junie https://www.jetbrains.com/junie/
- Con eso me quedé sin tokens (: