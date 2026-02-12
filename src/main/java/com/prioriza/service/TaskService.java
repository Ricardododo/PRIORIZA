package com.prioriza.service;

import com.prioriza.dao.TaskDAO;
import com.prioriza.model.Priority;
import com.prioriza.model.Task;
import com.prioriza.priority.engine.PriorityEngine;
import com.prioriza.priority.model.PriorityLevel;

import java.sql.SQLException;
import java.util.List;

public class TaskService {

    private final TaskDAO taskDAO;
    private final PriorityEngine priorityEngine;

    public TaskService() {
        this.taskDAO = new TaskDAO();

        //motor de reglas
        this.priorityEngine = new PriorityEngine();

    }

    //crear tarea aplicando las reglas automaticas
    public Task createTask(Task task) throws SQLException{
        applyPriorityRules(task);
        taskDAO.insert(task);
        return task;
    }

    //buscar tarea por id
    public Task getById(int id) throws SQLException{
        return taskDAO.getById(id);
    }

    //listar tareas por lista
    public List<Task> getByTasksListId(int taskListId) throws SQLException{
        return taskDAO.getByTaskListId(taskListId);
    }

    //actualizar tarea recalculando prioridad
    public void updateTask(Task task) throws SQLException {
        applyPriorityRules(task);
        taskDAO.update(task);
    }
    //Eliminar tarea
    public void deleteTask(int taskId) throws SQLException {
        taskDAO.delete(taskId);
    }

    //metodo calcular motor heuristico

    public PriorityLevel calculatePriorityLevel(Task task) {
        return priorityEngine.calculate(task);
    }

    public int calculatePriorityScore(Task task) {
        return priorityEngine.calculateScore(task);
    }

    public Priority convertToUIPriority(PriorityLevel level) {
        if (level == null) return Priority.MEDIA;
        return switch(level) {
            case CRITICO, URGENTE -> Priority.URGENTE;
            case ALTO -> Priority.ALTA;
            case MEDIO -> Priority.MEDIA;
            case BAJO -> Priority.BAJA;
        };
    }

    public PriorityLevel convertToEnginePriority(Priority uiPriority) {
        if (uiPriority == null) return PriorityLevel.MEDIO;
        return switch(uiPriority) {
            case URGENTE -> PriorityLevel.URGENTE;
            case ALTA -> PriorityLevel.ALTO;
            case MEDIA -> PriorityLevel.MEDIO;
            case BAJA -> PriorityLevel.BAJO;
        };
    }
    //metodo privado
    private void applyPriorityRules(Task task) {
        PriorityLevel level = priorityEngine.calculate(task);
        task.setPriorityLevel(level);
        task.setPriority(convertToUIPriority(level));
        task.setPriorityScore(priorityEngine.calculateScore(task));
    }
}
