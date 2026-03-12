package com.prioriza.service;

import com.prioriza.dao.TaskListDAO;
import com.prioriza.model.TaskList;

import java.sql.SQLException;
import java.util.List;

public class TaskListService {

    private final TaskListDAO taskListDAO = new TaskListDAO();

    //OPERACIONES BASICAS
    //crear una nueva lista para un usuario
    public TaskList createList(String name, int userId) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la lista no puede estar vacío");
        }
        //validar el nombre duplicado
        if(!isListNameAvailable(name, userId)){
            throw new IllegalArgumentException("Ya existe una lista con ese nombre");
        }

        TaskList taskList = new TaskList(name.trim(), userId);
        taskListDAO.insert(taskList);
        System.out.println("Lista creada: " + name + " para usuario " + userId);
        return taskList;
    }
    //obtiene una lista por su ID
    public TaskList getListById(int id) {
        return taskListDAO.getById(id);
    }
    //obtiene todas las listas de un usuario
    public List<TaskList> getListsByUserId(int userId) throws SQLException {
        return taskListDAO.getByUserId(userId);
    }
    //actualiza una lista ya existente
    public void updateList(TaskList taskList) {
        if (taskList == null || taskList.getId() <= 0) {
            throw new IllegalArgumentException("Lista inválida");
        }
        taskListDAO.update(taskList);
        System.out.println("Lista actualizada ID: " + taskList.getId());
    }
    //Elimina una lista y todas sus tareas (on cascade)
    public void deleteList(int listId) {
        // Verificar si existe
        TaskList list = taskListDAO.getById(listId);
        if (list == null) {
            throw new IllegalArgumentException("La lista no existe");
        }
        taskListDAO.delete(listId);
        System.out.println("Lista eliminada ID: " + listId);
    }
    //LOS METODOS ADICIONALES
    //Crea listas por defecto para un usuario nuevo
    public void createDefaultLists(int userId) throws SQLException {
        taskListDAO.createDefaultListsForUser(userId);
    }
    //verifica si un usuario es dueño de una lista o no
    public boolean isOwner(int listId, int userId) {
        return taskListDAO.isOwner(listId, userId);
    }
    //obtiene listas con conteo de tareas
    public List<TaskList> getListsWithTaskCount(int userId) {
        return taskListDAO.getByUserIdWithTaskCount(userId);
    }
    //validar si el nombre de lista esta disponible o no
    public boolean isListNameAvailable(String name, int userId) throws SQLException {
        return !taskListDAO.existsByNameAndUser(name, userId);
    }
}
