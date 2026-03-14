# PRIORIZA - Gestor de Tareas Inteligente

PRIORIZA es una aplicación de escritorio desarrollada en Java/JavaFX para la gestión eficiente de tareas con cálculo automático de prioridades. Permite a los usuarios organizar sus tareas en listas, asignar fechas de vencimiento, marcar tareas importantes y visualizar sus prioridades mediante un motor de reglas heurísticas.

## Características Principales

- **Gestión de Tareas**: Crear, editar, eliminar y organizar tareas en listas personalizadas
- **Cálculo Automático de Prioridades**: Motor heurístico que calcula prioridades (CRÍTICO, URGENTE, ALTO, MEDIO, BAJO) basándose en fecha de vencimiento, importancia, estado y subtareas
- **Subtareas**: División de tareas principales en subtareas más detalladas
- **Estados de Tarea**: PENDIENTE, EN_PROGRESO, COMPLETA
- **Interfaz Intuitiva**: Dashboard con vista de tareas por usuario, tareas importantes, vencidas y de hoy
- **Exportación a PDF**: Generación de informes en PDF de las tareas
- **Notificaciones por Email**: Sistema de notificaciones programables

## Tecnologías

| Categoría | Tecnología |
|-----------|------------|
| Lenguaje | Java 24 |
| UI | JavaFX 21.0.2, CalendarFX 11.12.7 |
| Build | Maven 3.x |
| Base de Datos | SQLite 3.45.2.0 |
| Testing | JUnit 5.10.0, Mockito 5.6.0 |
| PDF | iText 7.2.5 |
| Correo | JavaMail API |

## Estructura del Proyecto

```
src/main/java/com/prioriza/
├── model/          # Entidades (Task, User, TaskList, SubTask...)
├── service/        # Lógica de negocio (TaskService, UserService...)
├── dao/           # Acceso a base de datos
├── controller/    # Controladores JavaFX
├── priority/     # Motor de cálculo de prioridades
├── util/         # Utilidades (DateUtil, AlertUtil)
└── config/       # Configuración
```

## Requisitos

- Java Development Kit (JDK) 24
- Maven 3.x
- Sistema operativo: Windows, macOS o Linux

## Instalación y Configuración

### 1. Clonar el repositorio

```bash
git clone <repositorio>
cd PRIORIZA
```

### 2. Compilar el proyecto

```bash
mvn clean compile
```

### 3. Ejecutar la aplicación

```bash
mvn javafx:run
```

Alternativa (si hay problemas con el plugin JavaFX):

```bash
mvn exec:java
```

### 4. Generar ejecutable (fat JAR)

```bash
mvn clean package
```

El archivo generado estará en: `target/prioriza-1.0-SNAPSHOT-jar-with-dependencies.jar`

## Uso

### Gestionar Tareas

1. **Crear una tarea**: Desde el formulario de tareas, complete el título, descripción, fecha de vencimiento y marque "importante" si es necesario
2. **Editar tarea**: Seleccione una tarea y modifique sus campos
3. **Eliminar tarea**: Seleccione y elimine desde el menú contextual
4. **Completar tarea**: Marque la tarea como completada

### Sistema de Prioridades

El motor de prioridades calcula automáticamente el nivel de prioridad:

| Nivel | Condiciones |
|-------|-------------|
| CRÍTICO | Tarea vencida, importante, con muchas subtareas |
| URGENTE | Vence hoy o está muy próxima |
| ALTO | Vence mañana o es importante |
| MEDIO | Vence en pocos días |
| BAJO | Sin fecha límite ni importancia |

### Dashboard

- **Todas las tareas**: Vista general de todas las tareas del usuario
- **Importantes**: Tareas marcadas como importantes
- **Vencidas**: Tareas que han pasado su fecha límite
- **Para hoy**: Tareas con fecha de vencimiento hoy

### Exportar a PDF

Desde el menú principal, seleccione la opción de exportar para generar un informe PDF de sus tareas.

## Testing

Ejecutar todos los tests:

```bash
mvn test
```

Ejecutar una clase de test específica:

```bash
mvn test -Dtest=PriorityEngineTest
```

Ejecutar un método de test específico:

```bash
mvn test -Dtest=PriorityEngineTest#testUrgentPriority
```

## Base de Datos

La aplicación utiliza SQLite. El archivo de base de datos `prioriza.db` se crea automáticamente en el directorio raíz del proyecto.

## Licencia

Este proyecto es para fines educativos (DAM - Proyecto de Fin de Grado).

---

Desarrollado con Java, JavaFX y ❤️
