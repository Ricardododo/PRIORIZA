# AGENTS.md - PRIORIZA Development Guide

This document provides guidance for AI agents working on the PRIORIZA project.

## Project Overview

PRIORIZA is a Java/JavaFX desktop application for task management with priority calculation. The project uses Maven for build management, SQLite for persistence, and follows a layered architecture (Model-Service-DAO-Controller).

## Technology Stack

- Java 24, JavaFX 21.0.2, Maven 3.x
- SQLite (sqlite-jdbc 3.45.2.0)
- JUnit 5.10.0, Mockito 5.6.0
- CalendarFX 11.12.7, iText 7.2.5 (PDF generation)

## Build & Test Commands

| Command | Description |
|---------|-------------|
| `mvn clean compile` | Build the project |
| `mvn javafx:run` | Run the application |
| `mvn clean package` | Package as fat JAR (`target/prioriza-1.0-SNAPSHOT-jar-with-dependencies.jar`) |
| `mvn test` | Run all tests |
| `mvn test -Dtest=PriorityEngineTest` | Run single test class |
| `mvn test -Dtest=PriorityEngineTest#testUrgentPriority` | Run single test method |
| `mvn test -Dtest=PriorityEngineTest,ModelTests` | Run multiple test classes |

## Code Style Guidelines

### General Principles
- Keep code simple and readable; follow Java conventions
- No TODO comments without issue tracking
- Business logic comments in Spanish (project language)

### Naming Conventions
- **Classes/Interfaces**: PascalCase (e.g., `TaskService`, `PriorityEngine`)
- **Methods/Variables**: lowerCamelCase (e.g., `getById`, `taskListId`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_RETRY_COUNT`)
- **Packages**: lowercase, singular (e.g., `com.prioriza.model`)
- **Database columns**: snake_case (e.g., `task_list_id`, `due_date`)

### File Organization
```
src/main/java/com/prioriza/
├── model/        # Entity classes
├── service/      # Business logic
├── dao/          # Database access
├── controller/  # JavaFX controllers
├── priority/     # Priority calculation engine
├── util/         # Utility classes
└── config/       # Configuration
```

### Imports
- Use explicit imports (no wildcard `.*` unless importing >10 classes from same package)
- Order: static imports, java.*, javax.*, external, internal

### Formatting
- 4 spaces for indentation (no tabs), line length: 120 characters max
- One blank line between method definitions, opening brace on same line

### Types
- Use interfaces over concrete types (e.g., `List<Task>` not `ArrayList<Task>`)
- Prefer `LocalDateTime` over `Date`; use primitives (`int`, `boolean`) for simple flags

### Error Handling
- DAO methods: catch exceptions, log with `e.printStackTrace()`, return null/empty collections
- Service methods: throw `SQLException` to controllers
- Controllers: show user-friendly alerts using `AlertUtil`

### Database Access
- Always use try-with-resources for Connections, PreparedStatements, ResultSets
- Use parameterized queries (never String concatenation for SQL)

### JavaFX Guidelines
- Use FXML for UI layouts; controllers should handle UI logic only
- Use `@FXML` annotation for fx:id fields; avoid logic in lambda expressions

### Testing
- Test classes: `<ClassName>Test` (e.g., `PriorityEngineTest`)
- Use `@DisplayName` in Spanish; JUnit 5 assertions: `assertEquals`, `assertNotNull`, `assertTrue`
- Mock external dependencies with Mockito; test one thing per method

### Priority Engine
- Rules in `com.prioriza.priority.rules`, each implements `PriorityRule` interface
- Weights in `com.prioriza.priority.config.RuleWeights`
- Priority levels: CRITICO > URGENTE > ALTO > MEDIO > BAJO

---

## Common Patterns

### Creating a New Service
```java
public class MyService {
    private final MyDAO myDAO;

    public MyService() { this.myDAO = new MyDAO(); }

    public Entity create(Entity e) throws SQLException {
        myDAO.insert(e);
        return e;
    }
}
```

### Adding a Test
```java
@DisplayName("Description in Spanish")
@Test
void testMethodName() {
    Object input = ...;
    Object result = service.method(input);
    assertEquals(expected, result);
}
```

### Database Query Pattern
```java
public List<Entity> findByCondition(int value) {
    String sql = "SELECT * FROM table WHERE condition = ?";
    List<Entity> results = new ArrayList<>();
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, value);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) results.add(mapResultSetToEntity(rs));
    } catch (Exception e) { e.printStackTrace(); }
    return results;
}
```

## Known Issues & Notes

- Java 24 may have compatibility issues with some JavaFX plugins; use `mvn exec:java` as fallback
- The project uses `e.printStackTrace()` for logging (not ideal but consistent)
- Database is SQLite file: `prioriza.db` in project root
- Email notifications use JavaMail API; requires SMTP configuration
- PDF export requires iText 7 library

## Proyecto de Fin de Grado DAM

- **Nombre**: [PRIORIZA]
- **Descripción**: [Gestor de Tareas Inteligente]
- **Tecnologías**: Java, JavaFX, Maven, SQLite,
- **Estructura**:
  - `docs/`: Documentación del proyecto
- **Estándares**: Seguimos Google Java Style, y para commits usamos Conventional Commits.
- **Testing**: Actualmente hay tests unitarios en backend con JUnit, pero faltan tests de integración.
