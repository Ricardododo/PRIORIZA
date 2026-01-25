package com.prioriza.service;

import com.prioriza.dao.TaskDAO;
import com.prioriza.model.Task;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TaskService {

    private final TaskDAO taskDAO = new TaskDAO();

    //crear tarea
    public Task createTask(Task task) throws SQLException{
        taskDAO.insert(task);
        return task;
    }

    //buscar tarea por id
    public Task getByTaskId(int id) throws SQLException{
        return taskDAO.getById(id);
    }

    //listar tareas por lista
    public List<Task> getByTasksListId(int taskListId) throws SQLException{
        return taskDAO.getByTaskListId(taskListId);
    }

    //actualizar tarea
    public void updateTask(Task task) throws SQLException {
        taskDAO.update(task);
    }

    //Eliminar tarea
    public void deleteTask(int taskId) throws SQLException {
        taskDAO.delete(taskId);
    }
}
