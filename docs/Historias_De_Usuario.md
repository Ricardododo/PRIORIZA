# PRIORIZA – Historias de Usuario

## Actor principal
- Usuario (puede tener rol USER o ADMIN)

---

### HU-01 Crear usuario

**Como** usuario  
**Quiero** registrarme en la aplicación indicando mi nombre, correo electrónico y contraseña  
**Para** poder gestionar mis tareas y recibir alertas

**Criterios de aceptación:**
- El nombre no puede estar vacío
- El correo debe ser válido y único
- La contraseña debe tener al menos 6 caracteres
- El usuario se guarda en la base de datos con rol USER
- Se crean listas de tareas por defecto automáticamente
- Se notifica al usuario en caso de error

---

### HU-02 Iniciar sesión

**Como** usuario  
**Quiero** iniciar sesión con mi email y contraseña  
**Para** acceder a mis tareas personalizadas

**Criterios de aceptación:**
- Credenciales válidas = acceso correcto
- Credenciales inválidas = mensaje de error
- Sesión válida hasta cerrar o salir de la app

---

### HU-03 Cerrar sesión

**Como** usuario  
**Quiero** cerrar mi sesión  
**Para** proteger mis datos cuando termine de usar la app

**Criterios de aceptación:**
- Sesión terminada correctamente
- Redirección a pantalla de login

---

### HU-04 Crear lista de tareas

**Como** usuario  
**Quiero** crear listas de tareas  
**Para** organizar mis tareas por proyectos o categorías

**Criterios de aceptación:**
- Cada lista tiene un nombre único por usuario
- Se almacena en la base de datos
- Puede contener tareas y subtareas

---

### HU-05 Eliminar lista de tareas

**Como** usuario  
**Quiero** eliminar listas de tareas  
**Para** eliminar proyectos o categorías que ya no necesito

**Criterios de aceptación:**
- Eliminación de la lista y todas sus tareas/subtareas
- Confirmación antes de eliminar

---

### HU-06 Crear tarea

**Como** usuario  
**Quiero** crear tareas  
**Para** organizar mis actividades y asignarles prioridad

**Criterios de aceptación:**
- Se guarda título (obligatorio), descripción, fecha límite y hora
- Se puede marcar como importante
- Motor heurístico asigna prioridad automáticamente
- Tarea asociada a la lista correspondiente

---

### HU-07 Editar tarea

**Como** usuario  
**Quiero** modificar una tarea existente  
**Para** actualizar información o corregir errores

**Criterios de aceptación:**
- Cambios guardados en la base de datos
- Recalculo automático de prioridad si cambia fecha o importancia

---

### HU-08 Eliminar tarea

**Como** usuario  
**Quiero** eliminar tareas  
**Para** mantener mi lista limpia y actualizada

**Criterios de aceptación:**
- Eliminación de la tarea y sus subtareas
- Confirmación antes de eliminar

---

### HU-09 Completar tarea

**Como** usuario  
**Quiero** marcar una tarea como completada  
**Para** llevar el seguimiento de mi progreso

**Criterios de aceptación:**
- Estado de la tarea se actualiza a COMPLETA
- La prioridad baja automáticamente

---

### HU-10 Crear subtarea

**Como** usuario  
**Quiero** crear subtareas dentro de una tarea  
**Para** dividir mejor mi trabajo y ver el progreso

**Criterios de aceptación:**
- Subtarea asociada correctamente a la tarea principal
- Guardado en base de datos
- Recalculo del estado/progreso de la tarea principal

---

### HU-11 Editar subtarea

**Como** usuario  
**Quiero** modificar una subtarea  
**Para** corregir información o cambiar detalles

**Criterios de aceptación:**
- Cambios guardados correctamente
- Recalculo del progreso de la tarea principal

---

### HU-12 Completar subtarea

**Como** usuario  
**Quiero** marcar una subtarea como completada  
**Para** reflejar mi avance en la tarea principal

**Criterios de aceptación:**
- Subtarea marcada como completada
- Actualización del progreso de la tarea principal

---

### HU-13 Eliminar subtarea

**Como** usuario  
**Quiero** eliminar una subtarea  
**Para** eliminar tareas pequeñas que ya no son necesarias

**Criterios de aceptación:**
- Subtarea eliminada
- Recalculo del progreso de la tarea principal

---

### HU-14 Clasificación automática de tareas

**Como** usuario  
**Quiero** que las tareas se clasifiquen automáticamente  
**Para** priorizar lo urgente, importante o lejano

**Criterios de aceptación:**
- Motor heurístico evalúa: fecha límite, importancia, estado, subtareas
- Clasificación visible en la interfaz con colores
- Niveles: CRÍTICO, URGENTE, ALTO, MEDIO, BAJO

---

### HU-15 Recalcular prioridad al cambiar subtareas

**Como** usuario  
**Quiero** que la prioridad de la tarea principal se actualice automáticamente  
**Para** reflejar el estado real según las subtareas

**Criterios de aceptación:**
- Cambios en subtareas actualizan la prioridad de la tarea
- Interfaz refleja cambios en tiempo real

---

### HU-16 Ver dashboard

**Como** usuario  
**Quiero** ver un dashboard con resumen de mis tareas  
**Para** tener una visión general de mi situación

**Criterios de aceptación:**
- Muestra tareas importantes
- Muestra tareas vencidas
- Muestra tareas para hoy
- Muestra gráficos de distribución por prioridad

---

### HU-17 Exportar a PDF

**Como** usuario  
**Quiero** exportar mis tareas a PDF  
**Para** tener una copia física o digital de mis tareas

**Criterios de aceptación:**
- Exportación en formato PDF
- Incluye datos de tareas y subtareas
- Archivo guardado en carpeta de descargas

---

### HU-18 Compartir por WhatsApp

**Como** usuario  
**Quiero** compartir una lista de tareas por WhatsApp  
**Para** enviar mis tareas a alguien rápidamente

**Criterios de aceptación:**
- Se genera un PDF automáticamente
- Se abre WhatsApp Web con mensaje preparado
- Se abre la carpeta con el PDF

---

### HU-19 Compartir por Email

**Como** usuario  
**Quiero** compartir una lista de tareas por email  
**Para** enviar mis tareas como adjunto

**Criterios de aceptación:**
- Se genera un PDF automáticamente
- Se abre el cliente de email con asunto y cuerpo preparados

---

### HU-20 Activar modo oscuro

**Como** usuario  
**Quiero** cambiar entre modo claro y oscuro  
**Para** adaptar la interfaz a mis preferencias

**Criterios de aceptación:**
- Botón para cambiar de tema
- Preferencias guardadas entre sesiones
- Colores adaptados a cada tema

---

### HU-21 Recibir alertas por email

**Como** usuario  
**Quiero** recibir alertas por tareas urgentes  
**Para** no olvidar tareas importantes

**Criterios de aceptación:**
- Alertas enviadas a correo registrado
- Notificaciones solo de tareas urgentes o próximas a vencer
- Configurable por el usuario

---

### HU-22 Configurar notificaciones

**Como** usuario  
**Quiero** configurar las notificaciones  
**Para** personalizar cuándo y cómo recibo alertas

**Criterios de aceptación:**
- Activar/desactivar notificaciones por email
- Configurar días de anticipación (1-7 días)
- Configurar hora de notificación (0-23)
- Opción de solo días laborables
- Configurar máximo de alertas por día

---

### HU-23 Gestionar usuarios (Admin)

**Como** administrador  
**Quiero** gestionar usuarios del sistema  
**Para** mantener el control del sistema

**Criterios de aceptación:**
- Ver lista de usuarios
- Cambiar rol de usuario (USER/ADMIN)
- Ver estadísticas globales
