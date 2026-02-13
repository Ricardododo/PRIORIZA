package com.prioriza.service;

import com.prioriza.model.*;
import com.prioriza.util.AlertUtil;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ShareService {

    private final PDFExportService pdfService = new PDFExportService();

    //Compartir lista por WhatsApp
    public void shareViaWhatsApp(TaskList taskList, List<Task> tasks, User user) {
        try {
            // Generar PDF
            String pdfPath = pdfService.exportTaskList(taskList, tasks, user);

            if (pdfPath == null) {
                AlertUtil.showError("Error", "No se pudo generar el PDF");
                return;
            }

            // Crear mensaje
            String message = createShareMessage(taskList, tasks);
            String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);

            // Abrir WhatsApp Web
            String url = "https://web.whatsapp.com/send?text=" + encodedMessage;
            Desktop.getDesktop().browse(new URI(url));

            // Abrir carpeta con el PDF
            Desktop.getDesktop().open(new File(pdfPath).getParentFile());

            AlertUtil.showInfo(
                    "Información", "PDF generado: " + pdfPath + "\n" +
                            "WhatsApp Web abierto - Pega el mensaje y adjunta el PDF"
            );

        } catch (Exception e) {
            AlertUtil.showError("Error", "Error al compartir: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Compartir tarea específica por WhatsApp
    public void shareTaskViaWhatsApp(Task task, User user) {
        try {
            // Generar PDF de la tarea
            String pdfPath = pdfService.exportSingleTask(task, user);

            if (pdfPath == null) {
                AlertUtil.showError("Error", "No se pudo generar el PDF");
                return;
            }

            // Crear mensaje
            String message = createTaskMessage(task);
            String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);

            String url = "https://web.whatsapp.com/send?text=" + encodedMessage;
            Desktop.getDesktop().browse(new URI(url));

            Desktop.getDesktop().open(new File(pdfPath).getParentFile());

            AlertUtil.showInfo("Información", "PDF generado - Comparte el archivo desde la carpeta Descargas");

        } catch (Exception e) {
            AlertUtil.showError("Error", "Error al compartir: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Compartir por Email
    public void shareViaEmail(TaskList taskList, List<Task> tasks, User user) {
        try {
            String pdfPath = pdfService.exportTaskList(taskList, tasks, user);

            if (pdfPath == null) {
                AlertUtil.showError("Error", "No se pudo generar el PDF");
                return;
            }

            String subject = URLEncoder.encode("Lista: " + taskList.getName(), StandardCharsets.UTF_8);
            String body = URLEncoder.encode(createShareMessage(taskList, tasks), StandardCharsets.UTF_8);

            String mailto = String.format("mailto:?subject=%s&body=%s", subject, body);
            Desktop.getDesktop().mail(new URI(mailto));

            AlertUtil.showInfo("Información", "Se abrirá tu cliente de email con el mensaje preparado");

        } catch (Exception e) {
            AlertUtil.showError("Error", "Error al enviar email: " + e.getMessage());
        }
    }

    private String createShareMessage(TaskList taskList, List<Task> tasks) {
        StringBuilder msg = new StringBuilder();

        msg.append("PRIORIZA - ").append(taskList.getName()).append("\n");
        msg.append("================================\n\n");

        long total = tasks.size();
        long completed = tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETA)
                .count();
        long pending = total - completed;

        msg.append("Resumen:\n");
        msg.append("Total: ").append(total).append("\n");
        msg.append("Completadas: ").append(completed).append("\n");
        msg.append("Pendientes: ").append(pending).append("\n\n");

        msg.append("Tareas:\n");
        for (Task t : tasks) {
            msg.append("- ").append(t.getTitle());
            msg.append(" [").append(t.getPriority()).append("]");
            if (t.getDueDate() != null) {
                msg.append(" (").append(t.getDueDate()).append(")");
            }
            msg.append("\n");
        }

        msg.append("\nCompartido desde PRIORIZA");
        return msg.toString();
    }

    private String createTaskMessage(Task task) {
        StringBuilder msg = new StringBuilder();

        msg.append("TAREA: ").append(task.getTitle()).append("\n");
        msg.append("================================\n");

        if (task.getDescription() != null && !task.getDescription().isEmpty()) {
            msg.append("\n").append(task.getDescription()).append("\n");
        }

        msg.append("\nPrioridad: ").append(task.getPriority());
        if (task.getDueDate() != null) {
            msg.append("\nFecha límite: ").append(task.getDueDate());
        }
        msg.append("\nEstado: ").append(task.getStatus());

        if (!task.getSubTasks().isEmpty()) {
            msg.append("\n\nSubtareas:\n");
            for (SubTask s : task.getSubTasks()) {
                String estado = s.getSubTaskStatus() == SubTaskStatus.COMPLETA ? "✓" : "○";
                msg.append(estado).append(" ").append(s.getTitle()).append("\n");
            }
        }

        return msg.toString();
    }
}
