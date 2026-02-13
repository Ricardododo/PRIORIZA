package com.prioriza.service;

import com.prioriza.model.EmailNotification;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {

    //CONFIGURACIÓN
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String USERNAME = "wellune@hotmail.com";
    private static final String PASSWORD = "91539454";
    private static final boolean ENABLED = false; // false PARA PRUEBAS SIN ENVIAR

    private javax.mail.Session mailSession;

    public EmailService() {
        if (ENABLED) {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);

            mailSession = javax.mail.Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USERNAME, PASSWORD);
                }
            });
        }
    }

    //Envía un email de notificación
     //return true si se envió (o se simuló) correctamente

    public boolean sendNotification(EmailNotification notification) {

        //MODO SIMULACIÓN - Solo muestra en consola
        if (!ENABLED) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("[SIMULACIÓN DE EMAIL]");
            System.out.println("=".repeat(60));
            System.out.println("Para: " + notification.getUserEmail());
            System.out.println("Asunto: PRIORIZA - " + notification.getItemType() +
                    " por vencer en " + notification.getDaysRemaining() + " días");
            System.out.println("\nDetalle:");
            System.out.println("   Título: " + notification.getItemTitle());
            System.out.println("   Fecha: " + notification.getDuedate());
            System.out.println("   Días restantes: " + notification.getDaysRemaining());
            System.out.println("=".repeat(60) + "\n");
            return true;
        }

        // MODO REAL - Envía email de verdad
        try {
            Message message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(notification.getUserEmail()));

            String subject = String.format("⏰ PRIORIZA - %s por vencer en %d días",
                    notification.getItemType().toLowerCase(),
                    notification.getDaysRemaining());
            message.setSubject(subject);

            String content = String.format(
                    "Hola,\n\n" +
                            "Te recordamos que la siguiente %s está próxima a vencer:\n\n" +
                            "Título: %s\n" +
                            "Fecha límite: %s\n" +
                            "Días restantes: %d\n\n" +
                            "No dejes que se te pase la fecha!\n\n" +
                            "Saludos,\n" +
                            "Equipo PRIORIZA",
                    notification.getItemType().toLowerCase(),
                    notification.getItemTitle(),
                    notification.getDuedate(),
                    notification.getDaysRemaining()
            );

            message.setText(content);
            Transport.send(message);

            System.out.println("Email enviado a: " + notification.getUserEmail());
            return true;

        } catch (MessagingException e) {
            System.err.println("Error enviando email: " + e.getMessage());
            return false;
        }
    }
}

