package com.prioriza.service;

import com.prioriza.dao.TaskListDAO;
import com.prioriza.model.TaskList;

import java.sql.SQLException;
import java.util.List;

public class TaskListService {

    private final TaskListDAO taskListDAO = new TaskListDAO();

    // OPERACIONES BÁSICAS

    /*
     * Crea una nueva lista para un usuario
     */
    public TaskList createList(String name, int userId) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la lista no puede estar vacío");
        }

        TaskList taskList = new TaskList(name.trim(), userId);
        taskListDAO.insert(taskList);
        System.out.println("Lista creada: " + name + " para usuario " + userId);
        return taskList;
    }

    /*
     * Obtiene una lista por su ID
     */
    public TaskList getListById(int id) {
        return taskListDAO.getById(id);
    }

    /*
     * Obtiene todas las listas de un usuario
     */
    public List<TaskList> getListsByUserId(int userId) throws SQLException {
        return taskListDAO.getByUserId(userId);
    }

    /*
     * Actualiza una lista existente
     */
    public void updateList(TaskList taskList) {
        if (taskList == null || taskList.getId() <= 0) {
            throw new IllegalArgumentException("Lista inválida");
        }
        taskListDAO.update(taskList);
        System.out.println("Lista actualizada ID: " + taskList.getId());
    }

    /*
     * Elimina una lista y todas sus tareas (cascada)
     */
    public void deleteList(int listId) {
        // Verificar si existe
        TaskList list = taskListDAO.getById(listId);
        if (list == null) {
            throw new IllegalArgumentException("La lista no existe");
        }

        taskListDAO.delete(listId);
        System.out.println("Lista eliminada ID: " + listId);
    }

    // MÉTODOS ADICIONALES

    /*
     * Crea las listas por defecto para un usuario nuevo
     */
    public void createDefaultLists(int userId) throws SQLException {
        taskListDAO.createDefaultListsForUser(userId);
    }

    /*
     * Verifica si un usuario es dueño de una lista
     */
    public boolean isOwner(int listId, int userId) {
        return taskListDAO.isOwner(listId, userId);
    }

    /*
     * Obtiene listas con conteo de tareas
     */
    public List<TaskList> getListsWithTaskCount(int userId) {
        return taskListDAO.getByUserIdWithTaskCount(userId);
    }

    /*
     * Valida si un nombre de lista está disponible
     */
    public boolean isListNameAvailable(String name, int userId) {
        return taskListDAO.isListNameAvailable(name, userId);
    }
}
