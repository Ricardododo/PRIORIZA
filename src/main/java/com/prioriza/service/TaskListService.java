package com.prioriza.service;

import com.prioriza.dao.TaskListDAO;
import com.prioriza.model.TaskList;

import java.sql.SQLException;
import java.util.List;

public class TaskListService {

    private final TaskListDAO taskListDAO = new TaskListDAO();

    //creación
    public TaskList create(TaskList taskList) throws SQLException{
        taskListDAO.insert(taskList);
        return taskList;
    }

    //buscar lista de tareas por id
    public TaskList getById(int id) throws SQLException{
        return taskListDAO.getById(id);
    }

    //Listar Lista de tareas por id
    public List<TaskList> getAll(int id) throws SQLException{
        return taskListDAO.getAllTaskList();
    }

    //actualizar lista de tareas
    public void updateTaskList(TaskList taskList) throws SQLException{
        taskListDAO.update(taskList);
    }

    //Listar listas de un usuario
    public List<TaskList> getByUserId(int userId)throws SQLException{
        return taskListDAO.getByUserId(userId);
    }

    //eliminar lista de tareas
    public void deleteTaskList(int id) throws SQLException{
        taskListDAO.delete(id);
    }

}
