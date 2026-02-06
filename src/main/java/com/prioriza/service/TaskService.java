package com.prioriza.service;

import com.prioriza.dao.TaskDAO;
import com.prioriza.model.Priority;
import com.prioriza.model.Task;
import com.prioriza.rule.RuleEngine;
import com.prioriza.rule.rules.DistantRule;
import com.prioriza.rule.rules.ImportantRule;
import com.prioriza.rule.rules.UrgentRule;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TaskService {

    private final TaskDAO taskDAO;
    private final RuleEngine engine;

    public TaskService() {
        this.taskDAO = new TaskDAO();

        //motor de reglas
        this.engine = new RuleEngine();
        engine.addRule(new UrgentRule());
        engine.addRule(new ImportantRule());
        engine.addRule(new DistantRule());
    }

    //crear tarea aplicando las reglas automaticas
    public Task createTask(Task task) throws SQLException{

        //1. evaluar tarea
        int score = engine.evaluate(task);

        //2. convertir score a Priority enum
        Priority priority = convertScoreToPriority(score);

        //3. asignar prioridad final
        task.setPriority(priority);

        //4. guardar en BD
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

    //actualizar tarea calculando prioridad
    public void updateTask(Task task) throws SQLException {

        int score = engine.evaluate(task);
        task.setPriority(convertScoreToPriority(score));

        taskDAO.update(task);
    }

    //Eliminar tarea
    public void deleteTask(int taskId) throws SQLException {
        taskDAO.delete(taskId);
    }

    //metodo interno para covertir el score en enum Priority
    private Priority convertScoreToPriority(int score){

        if (score >= 4) return Priority.URGENTE;
        if (score >= 2) return Priority.ALTA;
        if (score >= 1) return Priority.MEDIA;

        return Priority.BAJA;
    }
}
