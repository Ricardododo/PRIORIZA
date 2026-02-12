package com.prioriza.service;

import com.prioriza.dao.EmailNotificationDAO;
import com.prioriza.model.EmailNotification;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotificationProcessor {

    private final EmailNotificationDAO notificationDAO = new EmailNotificationDAO();
    private final EmailService emailService = new EmailService();
    private final DueDateDetector detector = new DueDateDetector();

    private ScheduledExecutorService scheduler;
    private boolean running = false;

    //Inicia el procesador de notificaciones (hilos en background)
    public void start() {
        if (running) {
            System.out.println("El procesador ya está corriendo");
            return;
        }

        running = true;
        scheduler = Executors.newScheduledThreadPool(2);

        //TAREA 1: Escanear tareas cada hora
        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("\n [" + java.time.LocalTime.now() +
                        "] Ejecutando escaneo programado...");
                detector.scanAllUsers();
            } catch (Exception e) {
                System.err.println("Error en escaneo programado: " + e.getMessage());
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.HOURS);

        //TAREA 2: Enviar emails cada 15 minutos
        scheduler.scheduleAtFixedRate(() -> {
            try {
                processPendingNotifications();
            } catch (Exception e) {
                System.err.println("Error enviando emails: " + e.getMessage());
                e.printStackTrace();
            }
        }, 1, 15, TimeUnit.MINUTES);

        System.out.println("Sistema de notificaciones iniciado");
        System.out.println("Escaneo: cada 1 hora");
        System.out.println("Envío: cada 15 minutos");
    }

    //Detiene el procesador

    public void stop() {
        if (scheduler != null) {
            scheduler.shutdown();
            running = false;
            System.out.println("Sistema de notificaciones detenido");
        }
    }

    //Procesa las notificaciones pendientes

    private void processPendingNotifications() {
        List<EmailNotification> pending = notificationDAO.getPendingNotifications();

        if (pending.isEmpty()) {
            return;
        }

        System.out.println("\n[" + java.time.LocalTime.now() +
                "] Procesando " + pending.size() + " notificaciones pendientes...");

        int sent = 0;
        int failed = 0;

        for (EmailNotification notification : pending) {
            boolean success = emailService.sendNotification(notification);

            if (success) {
                notificationDAO.markAsSent(notification.getId());
                sent++;
            } else {
                notificationDAO.markAsFailed(notification.getId(), "Error SMTP");
                failed++;
            }

            // Pequeña pausa entre emails
            try { Thread.sleep(500); } catch (InterruptedException e) {}
        }

        System.out.println("Enviados: " + sent + " | Fallidos: " + failed);
    }

    //Ejecuta un escaneo manual inmediato

    public void scanNow() {
        System.out.println("\nEscaneo manual iniciado...");
        detector.scanAllUsers();
    }

    //Ejecuta envío manual inmediato

    public void sendNow() {
        System.out.println("\nEnvío manual iniciado...");
        processPendingNotifications();
    }

    public boolean isRunning() {
        return running;
    }
}
