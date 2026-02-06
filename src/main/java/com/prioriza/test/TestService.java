package com.prioriza.test;

import com.prioriza.dao.DatabaseInitializer;
import com.prioriza.model.*;
import com.prioriza.service.SubTaskService;
import com.prioriza.service.TaskListService;
import com.prioriza.service.TaskService;
import com.prioriza.service.UserService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TestService {
    //test de (Reglas de negocio, verificación de relaciones, uso correcto de DAOs)
    public static void main(String[] args) throws SQLException {

        DatabaseInitializer.initialize();

        //services
        UserService userService = new UserService();
        TaskListService taskListService = new TaskListService();
        TaskService taskService = new TaskService();
        SubTaskService subTaskService = new SubTaskService();

        User user;
        //usuario
        try{
            user = userService.registerUser("Lurdes", "lurdes@hotmail.com");
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return; //para cortar el flujo
        }
        //listar listas de usuarios
        System.out.println("\nListas del usuario");
        taskListService.getByUserId(user.getId()).forEach(System.out::println);

        //lista
        TaskList taskList = new TaskList("Lista de Service", user.getId());//crea el objeto
        taskListService.create(taskList);//inserta en la base datos
        int taskListId = taskList.getId();//obtiene el ID que se le asigna
        System.out.println("\nLista creada: " + taskList.getName() + " | ID: " + taskListId);

        //tarea
        Task t1 = new Task("Tarea 1", "Descripción 1", LocalDate.now().plusDays(3), taskListId);
        Task t2 = new Task("Tarea 2", "Descripción 2", LocalDate.now().plusDays(1), taskListId);
        taskService.createTask(t1);
        taskService.createTask(t2);
        System.out.println("\nTareas creadas:");
        System.out.println(t1.getTitle() + " | ID: " + t1.getId());
        System.out.println(t2.getTitle() + " | ID: " + t2.getId());

        //listar tareas por lista
        System.out.println("\ntareas por lista:");
        taskService.getByTasksListId(taskListId).forEach(System.out::println);

        //subtareas
        subTaskService.createSubTask(new SubTask("Diseñar UI", t1.getId()));
        subTaskService.createSubTask(new SubTask("Conectar BD", t2.getId()));

        //leer subtareas
        List<SubTask> subtasksT1 = subTaskService.getSubTasksByTaskId(t1.getId());
        List<SubTask> subtasksT2 = subTaskService.getSubTasksByTaskId(t2.getId());

        System.out.println("\nSubtareas de Tarea 1:");
        subtasksT1.forEach(System.out::println);
        System.out.println("\nSubtareas de Tarea 2:");
        subtasksT2.forEach(System.out::println);

        //cambiar prioridad de t1 a "Alta"
        t1.setPriority(Priority.ALTA);
        taskService.updateTask(t1);
        System.out.println("\nTarea 1 actualizada por prioridad: ");
        System.out.println(t1);

        //cambiar estado de t1 a "Completada"
        t1.setStatus(TaskStatus.COMPLETA);
        taskService.updateTask(t1);
        System.out.println("\nTarea 1 actualizada por estado: ");
        System.out.println(t1);

        //cambiar prioridad subtasksT1 a completada
        for (SubTask sub : subtasksT1){
            subTaskService.completeSubTask(sub);
        }
        System.out.println("\nSubtareas de tarea 1 actualizadas a COMPLETED: ");
        subtasksT1.forEach(System.out::println);


    }
}
