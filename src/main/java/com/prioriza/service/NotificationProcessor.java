package com.prioriza.service;

import com.prioriza.config.NotificationConfig;
import com.prioriza.dao.EmailNotificationDAO;
import com.prioriza.model.EmailNotification;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotificationProcessor {

    private final EmailNotificationDAO notificationDAO = new EmailNotificationDAO();
    private final EmailService emailService = new EmailService();
    private final DueDateDetector detector = new DueDateDetector();
    private final NotificationStats stats = NotificationStats.getInstance();

    private ScheduledExecutorService scheduler;
    private boolean running = false;

    //Inicia el procesador con configuración inteligente
    public void start() {
        if (running) {
            System.out.println("El procesador ya está corriendo");
            return;
        }

        running = true;
        scheduler = Executors.newScheduledThreadPool(3);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("SISTEMA INTELIGENTE DE NOTIFICACIONES");
        System.out.println("=".repeat(60));
        System.out.println(NotificationConfig.getStatusMessage());
        System.out.println("=".repeat(60) + "\n");

        //TAREA 1: Escaneo programado
        if (NotificationConfig.DEV_MODE) {
            // MODO DESARROLLO - Escaneos rápidos para prueba
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    detector.scanAllUsers();
                } catch (Exception e) {
                    System.err.println("Error en escaneo: " + e.getMessage());
                }
            }, 0, 30, TimeUnit.SECONDS); // ¡CADA 30 SEGUNDOS para pruebas!

            System.out.println("MODO DESARROLLO: Escaneo cada 30 segundos");

        } else {
            // MODO PRODUCCIÓN - Horario inteligente
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    detector.scanAllUsers();
                } catch (Exception e) {
                    System.err.println("Error en escaneo: " + e.getMessage());
                }
            }, 0, NotificationConfig.SCAN_INTERVAL_HOURS, TimeUnit.HOURS);

            System.out.println("MODO PRODUCCIÓN: Escaneo cada " +
                    NotificationConfig.SCAN_INTERVAL_HOURS + " horas");
        }

        //TAREA 2: Envío de emails
        scheduler.scheduleAtFixedRate(() -> {
            try {
                processPendingNotifications();
            } catch (Exception e) {
                System.err.println("Error enviando emails: " + e.getMessage());
            }
        }, 1, NotificationConfig.SEND_INTERVAL_MINUTES, TimeUnit.MINUTES);

        System.out.println("Envío de emails: cada " +
                NotificationConfig.SEND_INTERVAL_MINUTES + " minutos\n");

        //TAREA 3: Reporte de estadísticas (cada hora)
        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println(stats.getReport());
            } catch (Exception e) {
                // Ignorar errores de reporte
            }
        }, 1, 1, TimeUnit.HOURS);
    }

    //Procesa notificaciones pendientes con límites

    private void processPendingNotifications() {
        if (!NotificationConfig.isWorkingHours() && !NotificationConfig.DEV_MODE) {
            return; // Fuera de horario, no enviar
        }

        List<EmailNotification> pending = notificationDAO.getPendingNotifications();

        if (pending.isEmpty()) {
            return;
        }

        // Limitar cantidad de emails por envío
        int emailsToSend = Math.min(pending.size(), NotificationConfig.MAX_EMAILS_PER_SCAN);

        System.out.println("\n[" + LocalTime.now() +
                "] Procesando " + emailsToSend + "/" + pending.size() +
                " notificaciones...");

        int sent = 0;
        int failed = 0;

        for (int i = 0; i < emailsToSend; i++) {
            EmailNotification notification = pending.get(i);
            boolean success = emailService.sendNotification(notification);

            if (success) {
                notificationDAO.markAsSent(notification.getId());
                sent++;
                stats.registerEmailSent();
            } else {
                notificationDAO.markAsFailed(notification.getId(), "Error SMTP");
                failed++;
                stats.registerEmailFailed();
            }

            // Pequeña pausa entre emails
            try { Thread.sleep(500); } catch (InterruptedException e) {}
        }

        System.out.println("Enviados: " + sent + " | Fallidos: " + failed);

        // Mostrar estadísticas después de cada envío en modo desarrollo
        if (NotificationConfig.DEV_MODE) {
            System.out.println(stats.getReport());
        }
    }

    //Escaneo manual inmediato

    public void scanNow() {
        System.out.println("\nEscaneo manual...");
        detector.scanAllUsers();
    }

    //Envío manual inmediato

    public void sendNow() {
        System.out.println("\nEnvío manual...");
        processPendingNotifications();
    }

    //Muestra estadísticas

    public void showStats() {
        System.out.println(stats.getReport());
    }

    //Detiene el procesador

    public void stop() {
        if (scheduler != null) {
            scheduler.shutdown();
            running = false;
            System.out.println("\nSistema de notificaciones detenido");
            System.out.println(stats.getReport());
        }
    }

    public boolean isRunning() {
        return running;
    }
}
