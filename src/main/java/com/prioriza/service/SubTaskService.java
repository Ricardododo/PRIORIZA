package com.prioriza.service;

import com.prioriza.dao.SubTaskDAO;
import com.prioriza.dao.TaskDAO;
import com.prioriza.model.SubTask;
import com.prioriza.model.SubTaskStatus;
import com.prioriza.model.Task;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class SubTaskService {

    private final SubTaskDAO subTaskDAO = new SubTaskDAO();
    private final TaskDAO taskDAO = new TaskDAO();

    //OPERACIONES BÁSICAS

    /*
     * Crea una nueva subtarea
     */
    public SubTask createSubTask(String title, int taskId, LocalDateTime dueDateTime, boolean important) {
        // Validar título
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("El título de la subtarea no puede estar vacío");
        }

        // Validar que la tarea padre existe
        Task parentTask = taskDAO.getById(taskId);
        if (parentTask == null) {
            throw new IllegalArgumentException("La tarea asociada no existe (ID: " + taskId + ")");
        }

        SubTask subTask = new SubTask();
        subTask.setTitle(title.trim());
        subTask.setTaskId(taskId);
        subTask.setDueDateTime(dueDateTime);
        subTask.setImportant(important);
        subTask.setSubTaskStatus(SubTaskStatus.PENDIENTE);

        subTaskDAO.insert(subTask);
        System.out.println("Subtarea creada: " + title + " para tarea " + taskId);
        return subTask;
    }

    /*
     * Obtiene una subtarea por su ID
     */
    public SubTask getSubTaskById(int id) {
        return subTaskDAO.getById(id);
    }

    /*
     * Obtiene todas las subtareas de una tarea
     */
    public List<SubTask> getSubTasksByTaskId(int taskId) {
        return subTaskDAO.getByTaskId(taskId);
    }

    /*
     * Actualiza una subtarea existente
     */
    public void updateSubTask(SubTask subTask) {
        if (subTask == null || subTask.getId() <= 0) {
            throw new IllegalArgumentException("Subtarea inválida");
        }
        subTaskDAO.update(subTask);
        System.out.println("Subtarea actualizada ID: " + subTask.getId());
    }

    /*
     * Elimina una subtarea
     */
    public void deleteSubTask(int subTaskId) {
        subTaskDAO.delete(subTaskId);
        System.out.println("Subtarea eliminada ID: " + subTaskId);
    }

    //OPERACIONES DE ESTADO

    /*
     * Marca una subtarea como completada
     */
    public void completeSubTask(int subTaskId) {
        SubTask subTask = subTaskDAO.getById(subTaskId);
        if (subTask == null) {
            throw new IllegalArgumentException("Subtarea no encontrada");
        }
        subTask.setSubTaskStatus(SubTaskStatus.COMPLETA);
        subTaskDAO.update(subTask);
        System.out.println("Subtarea completada ID: " + subTaskId);
    }

    /*
     * Marca una subtarea como pendiente
     */
    public void pendSubTask(int subTaskId) {
        SubTask subTask = subTaskDAO.getById(subTaskId);
        if (subTask == null) {
            throw new IllegalArgumentException("Subtarea no encontrada");
        }
        subTask.setSubTaskStatus(SubTaskStatus.PENDIENTE);
        subTaskDAO.update(subTask);
        System.out.println("Subtarea pendiente ID: " + subTaskId);
    }

    /*
     * Elimina todas las subtareas de una tarea
     */
    public void deleteByTaskId(int taskId) {
        subTaskDAO.deleteByTaskId(taskId);
        System.out.println("Subtareas eliminadas para tarea ID: " + taskId);
    }

    //MÉTODOS DE UTILIDAD

    /*
     * Cuenta subtareas pendientes de una tarea
     */
    public long countPendingByTaskId(int taskId) {
        return subTaskDAO.getByTaskId(taskId).stream()
                .filter(s -> s.getSubTaskStatus() == SubTaskStatus.PENDIENTE)
                .count();
    }

    /*
     * Verifica si todas las subtareas de una tarea están completadas
     */
    public boolean areAllCompleted(int taskId) {
        List<SubTask> subtasks = subTaskDAO.getByTaskId(taskId);
        return !subtasks.isEmpty() && subtasks.stream()
                .allMatch(s -> s.getSubTaskStatus() == SubTaskStatus.COMPLETA);
    }
}
