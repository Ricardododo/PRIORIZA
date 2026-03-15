# PRIORIZA – Análisis de Requisitos

## Objetivo

Desarrollar una aplicación de escritorio para la gestión inteligente de tareas con cálculo automático de prioridades mediante reglas heurísticas.

---

## Tecnologías y versiones

- **Java**: 24
- **JavaFX**: 21.0.2 (interfaz gráfica)
- **Maven**: 3.x (gestión de proyectos)
- **SQLite**: 3.45.2.0 (base de datos)
- **JavaMail API**: 1.6.2 (envío de notificaciones)
- **iText**: 7.2.5 (generación de PDFs)
- **CalendarFX**: 11.12.7 (componente de calendario)
- **JUnit**: 5.10.0 (testing)
- **Mockito**: 5.6.0 (mocking en tests)
- **SpotBugs**: 4.8.3 (análisis estático)

---

## Arquitectura

La aplicación sigue el patrón **Model-Service-DAO-Controller**:

```
src/main/java/com/prioriza/
├── model/          # Entidades y objetos de dominio
├── service/        # Lógica de negocio
├── dao/           # Acceso a base de datos SQLite
├── controller/    # Controladores JavaFX
├── priority/      # Motor de cálculo de prioridades
├── util/          # Clases utilitarias
└── config/        # Configuración
```

---

## Requisitos del sistema

- **JDK**: 24
- **RAM**: Mínimo 4GB
- **Sistema operativo**: Windows, macOS o Linux
- **Espacio en disco**: 100MB (incluyendo dependencias)

---

## Características principales

1. Gestión de usuarios (registro, login, roles USER/ADMIN)
2. Listas de tareas personalizadas
3. Tareas con prioridades automáticas
4. Subtareas asociadas a tareas
5. Cálculo heurístico de prioridades (5 reglas)
6. Estados: PENDIENTE, EN_PROGRESO, COMPLETA, CANCELADA
7. Niveles de prioridad: CRÍTICO > URGENTE > ALTO > MEDIO > BAJO
8. Exportación a PDF
9. Compartir por WhatsApp y Email
10. Notificaciones por email
11. Modo oscuro
12. Dashboard con tareas importantes, vencidas y para hoy
