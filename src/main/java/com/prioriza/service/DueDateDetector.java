package com.prioriza.service;

import com.prioriza.dao.EmailNotificationDAO;
import com.prioriza.dao.SubTaskDAO;
import com.prioriza.dao.TaskDAO;
import com.prioriza.dao.UserDAO;
import com.prioriza.model.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class DueDateDetector {
    private final UserDAO userDAO = new UserDAO();
    private final TaskDAO taskDAO = new TaskDAO();
    private final SubTaskDAO subTaskDAO = new SubTaskDAO();
    private final EmailNotificationDAO notificationDAO = new EmailNotificationDAO();

    //Días para los que se enviará alerta
    private static final int[] ALERT_DAYS = {1, 2, 3, 5, 7};

    //Escanea TODOS los usuarios y genera notificaciones

    public void scanAllUsers() {
        System.out.println("\nIniciando escaneo de tareas por vencer...");
        List<User> users = userDAO.getAllUsers();

        int totalNotifications = 0;

        for (User user : users) {
            totalNotifications += scanUserTasks(user);
            totalNotifications += scanUserSubtasks(user);
        }

        System.out.println("Escaneo completado. " + totalNotifications +
                " notificaciones generadas.\n");
    }

    //Escanea las tareas de un usuario

    private int scanUserTasks(User user) {
        int count = 0;
        List<Task> tasks = taskDAO.getByUserId(user.getId());
        LocalDate today = LocalDate.now();

        for (Task task : tasks) {
            // Solo tareas con fecha y no completadas
            if (task.getDueDate() == null) continue;
            if (task.getStatus() == TaskStatus.COMPLETA ||
                    task.getStatus() == TaskStatus.CANCELADA) continue;

            long daysUntilDue = ChronoUnit.DAYS.between(today, task.getDueDate());

            // Verificar si está en los días de alerta
            for (int alertDay : ALERT_DAYS) {
                if (daysUntilDue == alertDay) {
                    // Evitar duplicados (misma tarea, mismo día)
                    if (!notificationDAO.hasBeenNotifiedToday(task.getId(), alertDay)) {
                        EmailNotification notification =
                                new EmailNotification(user, task, alertDay);
                        notificationDAO.insert(notification);
                        count++;
                        System.out.println("Notificación creada: " +
                                user.getEmail() + " - " +
                                task.getTitle() + " (" + alertDay + " días)");
                    }
                    break;
                }
            }
        }
        return count;
    }

    //Escanea las subtareas de un usuario

    private int scanUserSubtasks(User user) {
        int count = 0;
        List<Task> tasks = taskDAO.getByUserId(user.getId());
        LocalDate today = LocalDate.now();

        for (Task task : tasks) {
            List<SubTask> subtasks = subTaskDAO.getByTaskId(task.getId());

            for (SubTask sub : subtasks) {
                if (sub.getDueDate() == null) continue;
                if (sub.getSubTaskStatus() == SubTaskStatus.COMPLETA) continue;

                long daysUntilDue = ChronoUnit.DAYS.between(today, sub.getDueDate());

                for (int alertDay : ALERT_DAYS) {
                    if (daysUntilDue == alertDay) {
                        // Por simplicidad, no chequeamos duplicados de subtareas
                        EmailNotification notification =
                                new EmailNotification(user, sub, alertDay);
                        notificationDAO.insert(notification);
                        count++;
                        System.out.println("Notificación creada (subtarea): " +
                                user.getEmail() + " - " +
                                sub.getTitle() + " (" + alertDay + " días)");
                        break;
                    }
                }
            }
        }
        return count;
    }

    //Escanea SOLO un usuario específico

    public void scanUser(int userId) {
        User user = userDAO.getById(userId);
        if (user != null) {
            System.out.println("Escaneando usuario: " + user.getEmail());
            scanUserTasks(user);
            scanUserSubtasks(user);
        }
    }
}

