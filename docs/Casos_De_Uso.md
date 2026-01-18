# PRIORIZA – Casos de Uso

## Actor principal
- Usuario

---

## CU-01 Crear usuario

**Descripción:**  
El usuario se registra en la aplicación indicando su correo electrónico.

**Flujo principal:**
1. El usuario abre la aplicación.
2. Introduce su email.
3. El sistema valida el formato.
4. El sistema crea el usuario.

---

## CU-02 Crear tarea

**Descripción:**  
El usuario crea una nueva tarea dentro de una lista de tareas.

**Flujo principal:**
1. El usuario selecciona “Nueva tarea”.
2. Introduce título, descripción y fecha límite.
3. El sistema guarda la tarea.
4. El motor heurístico asigna prioridad automáticamente.

---

## CU-03 Editar tarea

**Descripción:**  
El usuario modifica una tarea existente.

**Flujo principal:**
1. El usuario selecciona una tarea.
2. Modifica sus datos.
3. El sistema guarda los cambios.
4. El motor heurístico recalcula la prioridad.

---

## CU-04 Eliminar tarea

**Descripción:**  
El usuario elimina una tarea existente.

**Flujo principal:**
1. El usuario selecciona una tarea.
2. Confirma la eliminación.
3. El sistema elimina la tarea y sus subtareas asociadas.

---

## CU-05 Crear subtarea

**Descripción:**  
El usuario crea una subtarea asociada a una tarea principal.

**Flujo principal:**
1. El usuario selecciona una tarea.
2. Selecciona “Nueva subtarea”.
3. Introduce los datos de la subtarea.
4. El sistema guarda la subtarea asociándola a la tarea principal.

---

## CU-06 Editar subtarea

**Descripción:**  
El usuario modifica una subtarea existente.

**Flujo principal:**
1. El usuario selecciona una subtarea.
2. Modifica sus datos.
3. El sistema guarda los cambios.
4. Se recalcula el estado de la tarea principal.

---

## CU-07 Completar subtarea

**Descripción:**  
El usuario marca una subtarea como completada.

**Flujo principal:**
1. El usuario marca la subtarea como completada.
2. El sistema actualiza su estado.
3. El sistema recalcula el progreso de la tarea principal.

---

## CU-08 Eliminar subtarea

**Descripción:**  
El usuario elimina una subtarea.

**Flujo principal:**
1. El usuario selecciona una subtarea.
2. Confirma la eliminación.
3. El sistema elimina la subtarea y actualiza la tarea principal.

---

## CU-09 Clasificar tareas automáticamente

**Descripción:**  
El sistema clasifica tareas y subtareas como urgentes, importantes o lejanas mediante reglas heurísticas.

---

## CU-10 Generar informes

**Descripción:**  
El usuario genera informes de tareas y subtareas.

---

## CU-11 Enviar alertas por email

**Descripción:**  
El sistema envía alertas por email cuando una tarea o subtarea es urgente o próxima a vencer.
