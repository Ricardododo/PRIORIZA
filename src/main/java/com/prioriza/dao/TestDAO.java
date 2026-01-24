package com.prioriza.dao;

import com.prioriza.model.SubTask;
import com.prioriza.model.Task;
import com.prioriza.model.TaskList;
import com.prioriza.model.User;
import com.prioriza.service.UserService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TestDAO {
    public static void main(String[] args) throws SQLException {
        // inicializar BD
        DatabaseInitializer.initialize();

        // crear usuario
        UserService userService = new UserService();
        User user = userService.registerUser("Fernando", "fernando@hotmail.com");

        if(user != null){
            // crear lista de tareas
            TaskListDAO taskListDAO = new TaskListDAO();
            TaskList list = new TaskList("Lista principal", user.getId());
            taskListDAO.insert(list);

            int taskListId = list.getId();

            // Crear tareas
            TaskDAO taskDAO = new TaskDAO();

            Task t1 = new Task(
                    "Tarea 1",
                    "Descripción 1",
                    LocalDate.now().plusDays(3),
                    taskListId
            );

            Task t2 = new Task(
                    "Tarea 2",
                    "Descripción 2",
                    LocalDate.now().plusDays(1),
                    taskListId
            );

            taskDAO.insert(t1);
            int taskId = t1.getId();
            taskDAO.insert(t2);


            // leer tareas
            List<Task> task = taskDAO.getByTaskListId(taskListId);

            System.out.println("\nTareas de la lista:");
            for (Task t : task) {
                System.out.println(t.getTitle() + " | " + t.getStatus() + " | " + t.getDueDate());
            }


            //declaracion SubTaskDAO
            SubTaskDAO subTaskDAO = new SubTaskDAO();

            //crear subtareas
            SubTask s1 = new SubTask("Diseñar pantalla", taskId);
            SubTask s2 = new SubTask("Implementar DAO", taskId);

            subTaskDAO.insert(s1);
            subTaskDAO.insert(s2);

            //leer subtareas
            List<SubTask> subTasks = subTaskDAO.getByTaskId(taskId);

            System.out.println("\nSubtareas:");
            for (SubTask s : subTasks){
                System.out.println(
                        s.getId() + " | " +
                        s.getTitle() + " | completada: " +
                        s.isCompleted()
                );
            }
            //actulizar
            s1.setCompleted(true);
            subTaskDAO.update(s1);

            //borrar
            subTaskDAO.delete(s2.getId());

        }else{
            System.err.println("No se pudo crear el usuario");
        }

    }
}
