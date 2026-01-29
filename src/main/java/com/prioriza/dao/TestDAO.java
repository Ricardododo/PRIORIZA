package com.prioriza.dao;

import com.prioriza.model.*;
import com.prioriza.service.TaskService;
import com.prioriza.service.UserService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
//test de persistencia DAO (conexión BD, CRUD DAO, Relaciones reales en SQLite)
public class TestDAO {
    public static void main(String[] args) throws SQLException {
        //1. inicializar BD
        DatabaseInitializer.initialize();

        //2. crear usuario
        UserService userService = new UserService();
        User user = userService.registerUser("Sandra", "sandra@hotmail.com");

        if(user == null){
            System.err.println("No se pudo crear el usuario");
            return;
        }

        //3. crear lista de tareas
        TaskListDAO taskListDAO = new TaskListDAO();
        TaskList list = new TaskList("Lista principal", user.getId());
        taskListDAO.insert(list);

        int taskListId = list.getId();

        //4. Crear tareas (RuleEngine)
        TaskService taskService = new TaskService();

        //5.crear tareas (sin prioridad manual)

        Task t1 = new Task(
                "Entregar DAM",
                "Proyecto final",
                LocalDate.now().plusDays(1),
                taskListId
        );

        Task t2 = new Task(
                "Leer documentación",
                "Revisión general",
                LocalDate.now().plusDays(15),
                taskListId
        );
        //6. Guardar usando TaskService (las reglas se ejecutan)
        taskService.createTask(t1);
        taskService.createTask(t2);


        //7. mostrar prioridades calculadas
        System.out.println("\nTareas con prioridad automática: ");
        List<Task> task = taskService.getByTasksListId(taskListId);

        for (Task t : task) {
            System.out.println(t.getTitle() + " | Due: " + t.getDueDate() + " | Priority: " + t.getPriority());
        }


        //8. SubTaskDAO
        SubTaskDAO subTaskDAO = new SubTaskDAO();

        SubTask s1 = new SubTask("Diseñar pantalla", t1.getId());
        SubTask s2 = new SubTask("Implementar DAO", t1.getId());

        subTaskDAO.insert(s1);
        subTaskDAO.insert(s2);

        //leer subtareas
        List<SubTask> subTasks = subTaskDAO.getByTaskId(t1.getId());

        System.out.println("\nSubtareas:");
        for (SubTask s : subTasks){
            System.out.println(s.getId() + " | " + s.getTitle() + " | completada: " + s.getSubTaskStatus());
        }
    }
}
