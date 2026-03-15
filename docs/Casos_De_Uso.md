# PRIORIZA – Casos de Uso

## Actor principal
- Usuario (rol: USER o ADMIN)

---

## CU-01 Crear usuario

**Descripción:**  
El usuario se registra en la aplicación indicando su nombre, correo electrónico y contraseña.

**Flujo principal:**
1. El usuario abre la aplicación.
2. Selecciona "Registrarse".
3. Introduce nombre, email y contraseña.
4. El sistema valida el formato del email.
5. El sistema verifica que el email no esté repetido.
6. El sistema crea el usuario con rol USER.
7. El sistema crea listas de tareas por defecto.

---

## CU-02 Iniciar sesión

**Descripción:**  
El usuario accede a la aplicación con sus credenciales.

**Flujo principal:**
1. El usuario abre la aplicación.
2. Introduce email y contraseña.
3. El sistema valida las credenciales.
4. El sistema inicia la sesión y carga el dashboard.

---

## CU-03 Cerrar sesión

**Descripción:**  
El usuario termina su sesión en la aplicación.

**Flujo principal:**
1. El usuario selecciona "Cerrar sesión".
2. El sistema cierra la sesión.
3. El sistema muestra la pantalla de login.

---

## CU-04 Crear tarea

**Descripción:**  
El usuario crea una nueva tarea dentro de una lista de tareas.

**Flujo principal:**
1. El usuario selecciona una lista de tareas.
2. Selecciona "Nueva tarea".
3. Introduce título, descripción, fecha límite y hora.
4. Opcionalmente marca "Importante".
5. El sistema guarda la tarea.
6. El motor heurístico asigna prioridad automáticamente.

---

## CU-05 Editar tarea

**Descripción:**  
El usuario modifica una tarea existente.

**Flujo principal:**
1. El usuario selecciona una tarea.
2. Selecciona "Editar".
3. Modifica los datos.
4. El sistema guarda los cambios.
5. El motor heurístico recalcula la prioridad.

---

## CU-06 Eliminar tarea

**Descripción:**  
El usuario elimina una tarea existente.

**Flujo principal:**
1. El usuario selecciona una tarea.
2. Selecciona "Eliminar".
3. El sistema pide confirmación.
4. El sistema elimina la tarea y sus subtareas asociadas.

---

## CU-07 Completar tarea

**Descripción:**  
El usuario marca una tarea como completada.

**Flujo principal:**
1. El usuario selecciona una tarea.
2. Marca como completada.
3. El sistema actualiza el estado.
4. El sistema recalcula la prioridad (baja para completadas).

---

## CU-08 Crear subtarea

**Descripción:**  
El usuario crea una subtarea asociada a una tarea principal.

**Flujo principal:**
1. El usuario selecciona una tarea.
2. Selecciona "Nueva subtarea".
3. Introduce título, fecha límite y hora.
4. Opcionalmente marca "Importante".
5. El sistema guarda la subtarea.
6. El sistema recalcula la prioridad de la tarea principal.

---

## CU-09 Editar subtarea

**Descripción:**  
El usuario modifica una subtarea existente.

**Flujo principal:**
1. El usuario selecciona una subtarea.
2. Modifica sus datos.
3. El sistema guarda los cambios.
4. Se recalcula el estado de la tarea principal.

---

## CU-10 Completar subtarea

**Descripción:**  
El usuario marca una subtarea como completada.

**Flujo principal:**
1. El usuario marca la subtarea como completada.
2. El sistema actualiza su estado.
3. El sistema recalcula el progreso de la tarea principal.

---

## CU-11 Eliminar subtarea

**Descripción:**  
El usuario elimina una subtarea.

**Flujo principal:**
1. El usuario selecciona una subtarea.
2. Confirma la eliminación.
3. El sistema elimina la subtarea y actualiza la tarea principal.

---

## CU-12 Crear lista de tareas

**Descripción:**  
El usuario crea una nueva lista de tareas.

**Flujo principal:**
1. El usuario selecciona "Nueva lista".
2. Introduce el nombre de la lista.
3. El sistema valida que el nombre no exista.
4. El sistema crea la lista.

---

## CU-13 Editar lista de tareas

**Descripción:**  
El usuario modifica el nombre de una lista.

**Flujo principal:**
1. El usuario selecciona una lista.
2. Modifica el nombre.
3. El sistema guarda los cambios.

---

## CU-14 Eliminar lista de tareas

**Descripción:**  
El usuario elimina una lista de tareas.

**Flujo principal:**
1. El usuario selecciona una lista.
2. Confirma la eliminación.
3. El sistema elimina la lista y todas sus tareas.

---

## CU-15 Ver dashboard

**Descripción:**  
El usuario visualiza un resumen de sus tareas.

**Flujo principal:**
1. El usuario accede al dashboard.
2. El sistema muestra: tareas importantes, vencidas, para hoy.
3. El sistema muestra gráficos de prioridades.

---

## CU-16 Ver tareas importantes

**Descripción:**  
El usuario filtra sus tareas importantes.

**Flujo principal:**
1. El usuario selecciona "Importantes".
2. El sistema muestra todas las tareas marcadas como importantes.

---

## CU-17 Ver tareas vencidas

**Descripción:**  
El usuario visualiza tareas que han superado su fecha límite.

**Flujo principal:**
1. El usuario selecciona "Vencidas".
2. El sistema muestra tareas con fecha anterior a hoy.

---

## CU-18 Ver tareas para hoy

**Descripción:**  
El usuario visualiza tareas con fecha de hoy.

**Flujo principal:**
1. El usuario selecciona "Para hoy".
2. El sistema muestra tareas con fecha igual a hoy.

---

## CU-19 Clasificar tareas automáticamente

**Descripción:**  
El sistema clasifica tareas mediante reglas heurísticas.

**Reglas implementadas:**
1. OverdueRule: Tareas vencidas (+30 puntos)
2. DueDateRule: Fecha cercana (10-40 puntos)
3. SubtaskCountRule: Cantidad de subtareas (5-20 puntos)
4. ImportantRule: Tareas importantes (+15 puntos)
5. CompletionStatusRule: Estado de completación (0 o -20 puntos)

**Niveles resultantes:**
- CRÍTICO: >= 80 puntos
- URGENTE: >= 50 puntos
- ALTO: >= 30 puntos
- MEDIO: >= 10 puntos
- BAJO: < 10 puntos

---

## CU-20 Exportar a PDF

**Descripción:**  
El usuario genera un informe en PDF de sus tareas.

**Flujo principal:**
1. El usuario selecciona "Exportar PDF".
2. El sistema genera un PDF con las tareas.
3. El sistema guarda el PDF en la carpeta de descargas.
4. El sistema abre la carpeta.

---

## CU-21 Compartir por WhatsApp

**Descripción:**  
El usuario comparte una lista de tareas por WhatsApp.

**Flujo principal:**
1. El usuario selecciona una lista.
2. Selecciona "Compartir por WhatsApp".
3. El sistema genera un PDF.
4. El sistema abre WhatsApp Web con el mensaje preparado.

---

## CU-22 Compartir por Email

**Descripción:**  
El usuario comparte una lista de tareas por email.

**Flujo principal:**
1. El usuario selecciona una lista.
2. Selecciona "Compartir por Email".
3. El sistema genera un PDF.
4. El sistema abre el cliente de email con el mensaje preparado.

---

## CU-23 Activar modo oscuro

**Descripción:**  
El usuario cambia el tema de la interfaz.

**Flujo principal:**
1. El usuario selecciona el botón de modo oscuro.
2. El sistema cambia el tema de la aplicación.
3. El sistema guarda la preferencia.

---

## CU-24 Configurar notificaciones

**Descripción:**  
El usuario configura las alertas de correo electrónico.

**Flujo principal:**
1. El usuario accede a configuración.
2. Activa/desactiva notificaciones por email.
3. Configura días de anticipación.
4. Configura hora de notificación.
5. Configura días laborables.
6. El sistema guarda la configuración.

---

## CU-25 Generar informes (Admin)

**Descripción:**  
El administrador visualiza estadísticas globales.

**Flujo principal:**
1. El usuario con rol ADMIN accede a "Estadísticas globales".
2. El sistema muestra: total usuarios, total tareas, tareas por estado.
