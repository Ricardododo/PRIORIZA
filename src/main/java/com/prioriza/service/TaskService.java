package com.prioriza.service;

import com.prioriza.dao.TaskDAO;
import com.prioriza.model.Priority;
import com.prioriza.model.Task;
import com.prioriza.priority.engine.PriorityEngine;
import com.prioriza.priority.model.PriorityLevel;

import java.sql.SQLException;
import java.util.List;

/**
 * Servicio para gestionar operaciones de tareas.
 * 
 * Proporciona métodos para crear, leer, actualizar y eliminar tareas.
 * También calcula automáticamente la prioridad de cada tarea usando el motor heurístico.
 * 
 * @author PRIORIZA
 * @version 1.0
 */
public class TaskService {

    private final TaskDAO taskDAO;
    private final PriorityEngine priorityEngine;

    /**
     * Constructor que inicializa el DAO y el motor de prioridades.
     */
    public TaskService() {
        this.taskDAO = new TaskDAO();
        this.priorityEngine = new PriorityEngine();

    }

    /**
     * Crea una nueva tarea aplicando las reglas de prioridad automáticas.
     * 
     * @param task La tarea a crear
     * @return La tarea creada con su ID asignado
     * @throws SQLException Si ocurre un error de base de datos
     */
    public Task createTask(Task task) throws SQLException{
        applyPriorityRules(task);
        taskDAO.insert(task);
        return task;
    }

    /**
     * Busca una tarea por su identificador.
     * 
     * @param id Identificador de la tarea
     * @return La tarea encontrada o null si no existe
     * @throws SQLException Si ocurre un error de base de datos
     */
    public Task getById(int id) throws SQLException{
        return taskDAO.getById(id);
    }

    /**
     * Obtiene todas las tareas de un usuario para el dashboard.
     * 
     * @param userId Identificador del usuario
     * @return Lista de tareas del usuario
     * @throws SQLException Si ocurre un error de base de datos
     */
    public List<Task> getTasksByUserId(int userId) throws SQLException {
        return taskDAO.getByUserId(userId);
    }

    /**
     * Lista las tareas de una lista de tareas específica.
     * 
     * @param taskListId Identificador de la lista de tareas
     * @return Lista de tareas en la lista
     * @throws SQLException Si ocurre un error de base de datos
     */
    public List<Task> getByTasksListId(int taskListId) throws SQLException{
        return taskDAO.getByTaskListId(taskListId);
    }

    /**
     * Actualiza una tarea recalculando su prioridad automáticamente.
     * 
     * @param task La tarea a actualizar
     * @throws SQLException Si ocurre un error de base de datos
     */
    public void updateTask(Task task) throws SQLException {
        applyPriorityRules(task);
        taskDAO.update(task);
    }

    /**
     * Elimina una tarea por su identificador.
     * 
     * @param taskId Identificador de la tarea a eliminar
     * @throws SQLException Si ocurre un error de base de datos
     */
    public void deleteTask(int taskId) throws SQLException {
        taskDAO.delete(taskId);
    }

    /**
     * Calcula el nivel de prioridad de una tarea usando el motor heurístico.
     * 
     * @param task La tarea a evaluar
     * @return El nivel de prioridad calculado
     */
    private PriorityLevel calculatePriorityLevel(Task task) {
        return priorityEngine.calculate(task);
    }

    /**
     * Calcula la puntuación numérica de prioridad de una tarea.
     * 
     * @param task La tarea a evaluar
     * @return Puntuación numérica de prioridad
     */
    private int calculatePriorityScore(Task task) {
        return priorityEngine.calculateScore(task);
    }

    /**
     * Convierte un nivel de prioridad del motor a prioridad de UI.
     * 
     * @param level Nivel de prioridad del motor
     * @return Prioridad para la interfaz de usuario
     */
    public Priority convertToUIPriority(PriorityLevel level) {
        if (level == null) return Priority.MEDIA;
        return switch(level) {
            case CRITICO, URGENTE -> Priority.URGENTE;
            case ALTO -> Priority.ALTA;
            case MEDIO -> Priority.MEDIA;
            case BAJO -> Priority.BAJA;
        };
    }

    /**
     * Convierte una prioridad de UI al nivel del motor.
     * 
     * @param uiPriority Prioridad de la interfaz de usuario
     * @return Nivel de prioridad del motor
     */
    public PriorityLevel convertToEnginePriority(Priority uiPriority) {
        if (uiPriority == null) return PriorityLevel.MEDIO;
        return switch(uiPriority) {
            case URGENTE -> PriorityLevel.URGENTE;
            case ALTA -> PriorityLevel.ALTO;
            case MEDIA -> PriorityLevel.MEDIO;
            case BAJA -> PriorityLevel.BAJO;
        };
    }
    /**
     * Aplica las reglas de prioridad a una tarea.
     * 
     * @param task La tarea a evaluar
     */
    private void applyPriorityRules(Task task) {
        PriorityLevel level = priorityEngine.calculate(task);
        task.setPriorityLevel(level);
        task.setPriority(convertToUIPriority(level));
        task.setPriorityScore(priorityEngine.calculateScore(task));
    }
}
