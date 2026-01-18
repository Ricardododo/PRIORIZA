\# PRIORIZA – Historias de Usuario



\## Actor principal

\- Usuario



---



\### HU-01 Crear usuario



\*\*Como\*\* usuario  

\*\*Quiero\*\* registrarme en la aplicación indicando mi correo electrónico  

\*\*Para\*\* poder gestionar mis tareas y recibir alertas



\*\*Criterios de aceptación:\*\*

\- El correo debe ser válido

\- El usuario se guarda en la base de datos

\- Se notifica al usuario en caso de error



---



\### HU-02 Editar email de usuario



\*\*Como\*\* usuario  

\*\*Quiero\*\* poder modificar mi correo electrónico  

\*\*Para\*\* mantener actualizado mi contacto



\*\*Criterios de aceptación:\*\*

\- Validación de formato de correo

\- Actualización en la base de datos

\- Notificación al usuario sobre el cambio



---



\### HU-03 Crear lista de tareas



\*\*Como\*\* usuario  

\*\*Quiero\*\* crear listas de tareas  

\*\*Para\*\* organizar mis tareas por proyectos o categorías



\*\*Criterios de aceptación:\*\*

\- Cada lista tiene un nombre único

\- Se almacena en la base de datos

\- Puede contener tareas y subtareas



---



\### HU-04 Eliminar lista de tareas



\*\*Como\*\* usuario  

\*\*Quiero\*\* eliminar listas de tareas  

\*\*Para\*\* eliminar proyectos o categorías que ya no necesito



\*\*Criterios de aceptación:\*\*

\- Eliminación de la lista y todas sus tareas/subtareas

\- Confirmación antes de eliminar



---



\### HU-05 Crear tarea



\*\*Como\*\* usuario  

\*\*Quiero\*\* crear tareas  

\*\*Para\*\* organizar mis actividades y asignarles prioridad



\*\*Criterios de aceptación:\*\*

\- Se guarda título, descripción y fecha límite

\- Motor heurístico asigna prioridad automáticamente

\- Tarea asociada a la lista correspondiente



---



\### HU-06 Editar tarea



\*\*Como\*\* usuario  

\*\*Quiero\*\* modificar una tarea existente  

\*\*Para\*\* actualizar información o corregir errores



\*\*Criterios de aceptación:\*\*

\- Cambios guardados en la base de datos

\- Recalculo automático de prioridad si es necesario



---



\### HU-07 Eliminar tarea



\*\*Como\*\* usuario  

\*\*Quiero\*\* eliminar tareas  

\*\*Para\*\* mantener mi lista limpia y actualizada



\*\*Criterios de aceptación:\*\*

\- Eliminación de la tarea y sus subtareas

\- Confirmación antes de eliminar



---



\### HU-08 Completar tarea



\*\*Como\*\* usuario  

\*\*Quiero\*\* marcar una tarea como completada  

\*\*Para\*\* llevar el seguimiento de mi progreso



\*\*Criterios de aceptación:\*\*

\- Estado de la tarea se actualiza

\- Revisión automática de subtareas completadas



---



\### HU-09 Crear subtarea



\*\*Como\*\* usuario  

\*\*Quiero\*\* crear subtareas dentro de una tarea  

\*\*Para\*\* dividir mejor mi trabajo y ver el progreso



\*\*Criterios de aceptación:\*\*

\- Subtarea asociada correctamente a la tarea principal

\- Guardado en base de datos

\- Recalculo del estado/progreso de la tarea principal



---



\### HU-10 Editar subtarea



\*\*Como\*\* usuario  

\*\*Quiero\*\* modificar una subtarea  

\*\*Para\*\* corregir información o cambiar detalles



\*\*Criterios de aceptación:\*\*

\- Cambios guardados correctamente

\- Recalculo del progreso de la tarea principal



---



\### HU-11 Completar subtarea



\*\*Como\*\* usuario  

\*\*Quiero\*\* marcar una subtarea como completada  

\*\*Para\*\* reflejar mi avance en la tarea principal



\*\*Criterios de aceptación:\*\*

\- Subtarea marcada como completada

\- Actualización del progreso de la tarea principal



---



\### HU-12 Eliminar subtarea



\*\*Como\*\* usuario  

\*\*Quiero\*\* eliminar una subtarea  

\*\*Para\*\* eliminar tareas pequeñas que ya no son necesarias



\*\*Criterios de aceptación:\*\*

\- Subtarea eliminada

\- Recalculo del progreso de la tarea principal



---



\### HU-13 Clasificación automática de tareas



\*\*Como\*\* usuario  

\*\*Quiero\*\* que las tareas se clasifiquen automáticamente  

\*\*Para\*\* priorizar lo urgente, importante o lejano



\*\*Criterios de aceptación:\*\*

\- Motor heurístico evalúa fecha límite y prioridad manual

\- Clasificación visible en la interfaz



---



\### HU-14 Recalcular prioridad al cambiar subtareas



\*\*Como\*\* usuario  

\*\*Quiero\*\* que la prioridad de la tarea principal se actualice automáticamente  

\*\*Para\*\* reflejar el estado real según las subtareas



\*\*Criterios de aceptación:\*\*

\- Cambios en subtareas actualizan la prioridad de la tarea

\- Interfaz refleja cambios en tiempo real



---



\### HU-15 Generar informes de tareas



\*\*Como\*\* usuario  

\*\*Quiero\*\* generar informes de tareas  

\*\*Para\*\* revisar tareas completadas, pendientes o por prioridad



\*\*Criterios de aceptación:\*\*

\- Posibilidad de exportar en PDF o CSV

\- Incluye datos de tareas y subtareas



---



\### HU-16 Informes con subtareas



\*\*Como\*\* usuario  

\*\*Quiero\*\* que los informes incluyan subtareas  

\*\*Para\*\* tener una visión completa del progreso



\*\*Criterios de aceptación:\*\*

\- Subtareas se muestran con su estado

\- Datos completos en exportación



---



\### HU-17 Recibir alertas por email



\*\*Como\*\* usuario  

\*\*Quiero\*\* recibir alertas por tareas urgentes  

\*\*Para\*\* no olvidar tareas importantes



\*\*Criterios de aceptación:\*\*

\- Alertas enviadas a correo registrado

\- Notificaciones solo de tareas urgentes o próximas a vencer



---



\### HU-18 Recibir recordatorios de vencimiento



\*\*Como\*\* usuario  

\*\*Quiero\*\* recibir recordatorios antes de la fecha límite  

\*\*Para\*\* completar mis tareas a tiempo



\*\*Criterios de aceptación:\*\*

\- Envío programado de alertas

\- Permite gestionar frecuencia de recordatorio



