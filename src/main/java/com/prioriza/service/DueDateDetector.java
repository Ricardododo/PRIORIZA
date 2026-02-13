package com.prioriza.service;

import com.prioriza.config.NotificationConfig;
import com.prioriza.dao.*;
import com.prioriza.model.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class DueDateDetector {
    private final UserDAO userDAO = new UserDAO();
    private final TaskDAO taskDAO = new TaskDAO();
    private final SubTaskDAO subTaskDAO = new SubTaskDAO();
    private final EmailNotificationDAO notificationDAO = new EmailNotificationDAO();
    private final UserSettingsDAO settingsDAO = new UserSettingsDAO();
    private final NotificationStats stats = NotificationStats.getInstance();

    //Usar los días de alerta de la configuración
    private static final int[] ALERT_DAYS = NotificationConfig.ALERT_DAYS;

    //Escanea TODOS los usuarios
    public void scanAllUsers() {
        // Verificar si está en horario laboral
        if (!NotificationConfig.isWorkingHours() && !NotificationConfig.DEV_MODE) {
            System.out.println("[" + java.time.LocalTime.now() +
                    "] Fuera de horario laboral. Escaneo omitido.");
            return;
        }

        // Verificar límite diario
        if (stats.getScansToday() >= NotificationConfig.MAX_SCANS_PER_DAY) {
            System.out.println("[" + java.time.LocalTime.now() +
                    "] Límite diario de escaneos alcanzado (" +
                    NotificationConfig.MAX_SCANS_PER_DAY + ")");
            return;
        }

        System.out.println("\n[" + java.time.LocalTime.now() +
                "] INICIANDO ESCANEO INTELIGENTE");
        System.out.println(NotificationConfig.getStatusMessage());

        List<User> users = userDAO.getAllUsers();
        int totalNotifications = 0;

        for (User user : users) {
            totalNotifications += scanUserTasks(user);
            totalNotifications += scanUserSubtasks(user);
        }

        // Registrar estadísticas
        stats.registerScan();

        System.out.println("[" + java.time.LocalTime.now() +
                "] Escaneo completado. " + totalNotifications +
                " notificaciones generadas.");
        System.out.println("Escaneos hoy: " + stats.getScansToday() +
                "/" + NotificationConfig.MAX_SCANS_PER_DAY);
    }

    //Escanea SOLO 1 usuario específico
    public void scanUser(int userId) {
        User user = userDAO.getById(userId);
        if (user != null) {
            System.out.println("Escaneando usuario: " + user.getEmail());
            scanUserTasks(user);
            scanUserSubtasks(user);
        }
    }

    private int scanUserTasks(User user) {
        int count = 0;

        //OBTENER CONFIGURACIÓN DEL USUARIO (MODELO)
        UserSettings settings = settingsDAO.getByUserId(user.getId());

        if (!settings.isEmailEnabled()) {
            return 0; // Usuario no quiere notificaciones
        }

        List<Task> tasks = taskDAO.getByUserId(user.getId());
        LocalDate today = LocalDate.now();

        for (Task task : tasks) {
            if (task.getDueDate() == null) continue;
            if (task.getStatus() == TaskStatus.COMPLETA ||
                    task.getStatus() == TaskStatus.CANCELADA) continue;

            long daysUntilDue = ChronoUnit.DAYS.between(today, task.getDueDate());

            // Verificar si está en los días de alerta
            for (int alertDay : ALERT_DAYS) {
                if (daysUntilDue == alertDay) {
                    if (!notificationDAO.hasBeenNotifiedToday(task.getId(), alertDay)) {

                        //USAR CONFIGURACIÓN DEL USUARIO
                        int todayCount = notificationDAO.getTodayCount(user.getId());
                        if (todayCount < settings.getMaxAlertsPerDay()) {

                            EmailNotification notification =
                                    new EmailNotification(user, task, alertDay);
                            notificationDAO.insert(notification);
                            count++;
                            stats.registerNotification();

                            if (NotificationConfig.DEV_MODE) {
                                System.out.println("Notificación creada: " +
                                        user.getEmail() + " - " +
                                        task.getTitle() + " (" + alertDay + " días)");
                            }
                        }
                    }
                    break;
                }
            }
        }
        return count;
    }

    private int scanUserSubtasks(User user) {
        int count = 0;

        UserSettings settings = settingsDAO.getByUserId(user.getId());

        if (!settings.isAlertForSubtasks() || !settings.isEmailEnabled()) {
            return 0; // Usuario no quiere notificaciones de subtareas
        }

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
                        EmailNotification notification =
                                new EmailNotification(user, sub, alertDay);
                        notificationDAO.insert(notification);
                        count++;
                        stats.registerNotification();

                        if (NotificationConfig.DEV_MODE) {
                            System.out.println("[DEV] Notificación (subtarea): " +
                                    user.getEmail() + " - " +
                                    sub.getTitle() + " (" + alertDay + " días)");
                        }
                        break;
                    }
                }
            }
        }
        return count;
    }
}

