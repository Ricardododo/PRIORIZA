package com.prioriza.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.prioriza.model.*;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PDFExportService {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("ddMMyyyy");

    //Metodo que Exporta una lista de tareas a PDF
    //returna la Ruta del archivo generado

    public String exportTaskList(TaskList taskList, List<Task> tasks, User user) {
        String fileName = String.format("PRIORIZA_%s_%s.pdf",
                taskList.getName().replace(" ", "_"),
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        String filePath = System.getProperty("user.home") + "/Downloads/" + fileName;

        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Encabezado
            document.add(new Paragraph("PRIORIZA")
                    .setFontSize(24)
                    .setBold()
                    .setFontColor(ColorConstants.BLUE));

            document.add(new Paragraph("Gestor Inteligente de Tareas")
                    .setFontSize(12)
                    .setFontColor(ColorConstants.GRAY));

            document.add(new Paragraph("\n"));

            // Información del usuario y lista
            Table infoTable = new Table(2);
            infoTable.setWidth(UnitValue.createPercentValue(100));

            infoTable.addCell(createCell("Usuario:", true));
            infoTable.addCell(createCell(user.getName(), false));

            infoTable.addCell(createCell("Lista:", true));
            infoTable.addCell(createCell(taskList.getName(), false));

            infoTable.addCell(createCell("Fecha exportación:", true));
            infoTable.addCell(createCell(
                    LocalDate.now().format(DATE_FORMATTER), false));

            document.add(infoTable);
            document.add(new Paragraph("\n"));

            // Tareas
            for (Task task : tasks) {
                document.add(createTaskCard(task));
                document.add(new Paragraph("\n"));
            }

            // Resumen
            document.add(createSummary(tasks));

            // Pie de página
            document.add(new Paragraph("\n"));
            Paragraph footer = new Paragraph(
                    "Documento generado automáticamente por PRIORIZA - " +
                            LocalDate.now().format(DATE_FORMATTER))
                    .setFontSize(8)
                    .setFontColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(footer);

            document.close();
            System.out.println("PDF exportado: " + filePath);
            return filePath;

        } catch (Exception e) {
            System.err.println("Error exportando PDF: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    //Exporta una TAREA ESPECÍFICA a PDF
    public String exportSingleTask(Task task, User user) {
        String fileName = String.format("TAREA_%s_%s.pdf",
                task.getTitle().replace(" ", "_"),
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        String filePath = System.getProperty("user.home") + "/Downloads/" + fileName;

        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("PRIORIZA")
                    .setFontSize(24)
                    .setBold()
                    .setFontColor(ColorConstants.BLUE));

            document.add(createTaskCard(task));

            document.add(new Paragraph("\n"));
            document.add(new Paragraph(
                    "Exportado por: " + user.getName() + " - " +
                            LocalDate.now().format(DATE_FORMATTER))
                    .setFontSize(10)
                    .setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.close();
            return filePath;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Div createTaskCard(Task task) {
        Div card = new Div();
        card.setBackgroundColor(ColorConstants.WHITE);
        card.setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1));
        card.setPadding(10);
        card.setMarginBottom(10);

        // Título con prioridad
        String priorityColor = getPriorityColor(task.getPriority());
        Paragraph title = new Paragraph(task.getTitle())
                .setFontSize(14)
                .setBold()
                .setFontColor(getColorFromHex(priorityColor));
        card.add(title);

        // Descripción
        if (task.getDescription() != null && !task.getDescription().isEmpty()) {
            card.add(new Paragraph("Descripción: " + task.getDescription())
                    .setFontSize(11)
                    .setFontColor(ColorConstants.DARK_GRAY));
        }

        // Metadatos
        Table metaTable = new Table(2);
        metaTable.setWidth(UnitValue.createPercentValue(100));

        metaTable.addCell(createCell("Prioridad:", true));
        metaTable.addCell(createCell(task.getPriority().toString(), false));

        if (task.getDueDate() != null) {
            metaTable.addCell(createCell("Fecha límite:", true));
            metaTable.addCell(createCell(
                    task.getDueDate().format(DATE_FORMATTER), false));
        }

        metaTable.addCell(createCell("Estado:", true));
        metaTable.addCell(createCell(task.getStatus().toString(), false));

        if (task.isImportant()) {
            metaTable.addCell(createCell("Importante:", true));
            metaTable.addCell(createCell("SÍ", false));
        }

        card.add(metaTable);

        // Subtareas
        if (task.getSubTasks() != null && !task.getSubTasks().isEmpty()) {
            card.add(new Paragraph("\nSubtareas:")
                    .setFontSize(12)
                    .setBold());

            for (SubTask sub : task.getSubTasks()) {
                String checkBox = sub.getSubTaskStatus() == SubTaskStatus.COMPLETA ? "✓" : "○";
                card.add(new Paragraph(String.format("   %s %s",
                        checkBox, sub.getTitle()))
                        .setFontSize(10));

                if (sub.getDueDate() != null) {
                    card.add(new Paragraph("    Fecha: " +
                            sub.getDueDate().format(DATE_FORMATTER))
                            .setFontSize(9)
                            .setFontColor(ColorConstants.GRAY));
                }
            }
        }
        return card;
    }

    private Paragraph createSummary(List<Task> tasks) {
        long total = tasks.size();
        long completed = tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETA)
                .count();
        long pending = tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.PENDIENTE ||
                        t.getStatus() == TaskStatus.EN_PROGRESO)
                .count();
        long urgent = tasks.stream()
                .filter(t -> t.getPriority() == Priority.URGENTE)
                .count();

        Paragraph summary = new Paragraph()
                .setFontSize(12)
                .setBold();

        summary.add("RESUMEN:\n");
        summary.add(String.format("Total tareas: %d\n", total));
        summary.add(String.format("Completadas: %d\n", completed));
        summary.add(String.format("Pendientes: %d\n", pending));
        summary.add(String.format("Urgentes: %d", urgent));

        return summary;
    }

    private Cell createCell(String text, boolean isHeader) {
        Cell cell = new Cell();
        cell.add(new Paragraph(text));

        if (isHeader) {
            cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            cell.setBold();
        }

        cell.setPadding(5);
        cell.setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));

        return cell;
    }
    //colores prioridad
    private String getPriorityColor(Priority priority) {
        if (priority == null) return "#000000";
        return switch(priority) {
            case URGENTE -> "#ff0000";
            case ALTA -> "#ff9900";
            case MEDIA -> "#00ff00";
            case BAJA -> "#666666";
        };
    }

    private com.itextpdf.kernel.colors.Color getColorFromHex(String hex) {
        // Convertir hex a Color de iText
        int r = Integer.valueOf(hex.substring(1, 3), 16);
        int g = Integer.valueOf(hex.substring(3, 5), 16);
        int b = Integer.valueOf(hex.substring(5, 7), 16);
        return new com.itextpdf.kernel.colors.DeviceRgb(r, g, b);
    }
}

