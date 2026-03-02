package com.prioriza.service;

import com.prioriza.model.EmailNotification;
import com.prioriza.util.DateUtil;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {

    // Configuración para Gmail
    private static final String SMTP_HOST = "smtp.gmail.com";  // ← dominio
    private static final String SMTP_PORT = "587";
    private static final String USERNAME = "ricardodevelop85@gmail.com";     // ← EMAIL
    private static final String PASSWORD = "cugg xmoa coeu eqng";       // ← CONTRASEÑA
    private static final boolean ENABLED = true;

    private javax.mail.Session mailSession;

    public EmailService() {
        if (ENABLED) {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true"); //activado (para puerto 587)
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.trust", SMTP_HOST); //evita errores de ceritificado


            mailSession = javax.mail.Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USERNAME, PASSWORD);
                }
            });

            mailSession.setDebug(true);
            System.out.println("Servicio de email configurado para GMAIL");
            System.out.println("Host: " + SMTP_HOST);
            System.out.println("Usuario: " + USERNAME);
        }
    }

    public boolean sendNotification(EmailNotification notification) {
        if (!ENABLED) {
            return simulateEmail(notification);
        }

        try {
            System.out.println("\nENVIANDO EMAIL A: " + notification.getUserEmail());

            Message message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(notification.getUserEmail()));

            String subject = String.format("PRIORIZA - %s por vencer en %d días",
                    notification.getItemType().toLowerCase(),
                    notification.getDaysRemaining());
            message.setSubject(subject);

            String content = buildPlainTextContent(notification);
            message.setText(content);

            Transport.send(message);

            System.out.println("EMAIL ENVIADO a: " + notification.getUserEmail());
            return true;

        } catch (MessagingException e) {
            System.err.println("Error enviando email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean simulateEmail(EmailNotification notification) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("[SIMULACIÓN] Email para: " + notification.getUserEmail());
        System.out.println("=".repeat(60) + "\n");
        return true;
    }

    private String buildPlainTextContent(EmailNotification notification) {
        return String.format(
                "PRIORIZA - Notificación de tarea\n" +
                        "================================\n\n" +
                        "Hola,\n\n" +
                        "Tienes una tarea próxima a vencer:\n\n" +
                        "Tipo: %s\n" +
                        "Título: %s\n" +
                        "Fecha límite: %s\n" +
                        "Días restantes: %d\n\n" +
                        "No dejes que se te pase la fecha límite.\n\n" +
                        "---\n" +
                        "Este es un mensaje automático de PRIORIZA",
                notification.getItemType().toLowerCase(),
                notification.getItemTitle(),
                DateUtil.formatDateTime(notification.getDueDateTime()),
                notification.getDaysRemaining()
        );
    }
}

