package com.prioriza.service;

import com.prioriza.dao.SubTaskDAO;
import com.prioriza.dao.TaskDAO;
import com.prioriza.model.SubTask;
import com.prioriza.model.Task;

import java.sql.SQLException;
import java.util.List;

public class SubTaskService {

    private final SubTaskDAO subTaskDAO = new SubTaskDAO();
    private final TaskDAO taskDAO = new TaskDAO(); //consulta si la Task existe

    //Crear subtareas
    public SubTask createSubTask(SubTask subTask) throws SQLException{
        if (subTask.getTitle() == null || subTask.getTitle().isBlank()) {
            throw new IllegalArgumentException("La subtarea debe tener un título");
        }
        Task task = taskDAO.getById(subTask.getTaskId());
        if (task == null){
            throw new IllegalArgumentException("La tarea asociada no existe");
        }
        subTaskDAO.insert(subTask);
        return subTask;
    }

    //buscar por id
    public SubTask getBySubTaskId(int id) throws SQLException {
        return subTaskDAO.getById(id);
    }

    //Listar subtareas por lista
    public List<SubTask> getSubTasksByTaskId(int taskId) throws SQLException{
        return subTaskDAO.getByTaskId(taskId);
    }

    //actualizar subtareas
    public void updateSubTask(SubTask subTask) throws SQLException{
        if(subTask.getId() <= 0){
            throw  new IllegalArgumentException("ID de subtarea inválido");
        }
        subTaskDAO.update(subTask);
    }

    //eliminar subtareas
    public void deleteSubTask(int id) throws SQLException{
        subTaskDAO.delete(id);
    }
}
