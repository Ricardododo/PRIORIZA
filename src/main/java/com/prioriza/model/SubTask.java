package com.prioriza.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa una subtarea en el sistema PRIORIZA.
 * 
 * Una subtarea pertenece a una tarea principal y representa una parte
 * más específica del trabajo a realizar. Las subtareas influyen en el
 * cálculo de prioridad de la tarea padre.
 * 
 * @author PRIORIZA
 * @version 1.0
 */
public class SubTask {
    private int id;
    private String title;
    private SubTaskStatus subTaskStatus;
    private int taskId;

    private LocalDateTime dueDateTime;
    private boolean important;

    /**
     * Constructor por defecto.
     * Inicializa la subtarea con estado PENDIENTE.
     */
    public SubTask() {
        this.subTaskStatus = SubTaskStatus.PENDIENTE;
    }

    /**
     * Constructor para crear una subtarea.
     * 
     * @param title  Título de la subtarea
     * @param taskId Identificador de la tarea padre
     */
    public SubTask(String title, int taskId) {
        this.title = title;
        this.taskId = taskId;
        this.subTaskStatus = SubTaskStatus.PENDIENTE;
    }

    //Getter y Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SubTaskStatus getSubTaskStatus() {
        return subTaskStatus;
    }

    public void setSubTaskStatus(SubTaskStatus subTaskStatus) {
        this.subTaskStatus = subTaskStatus;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public LocalDateTime getDueDateTime() {
        return dueDateTime;
    }

    public void setDueDateTime(LocalDateTime dueDateTime) {
        this.dueDateTime = dueDateTime;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    /**
     * Marca la subtarea como completada.
     */
    public void markCompleted(){
        this.subTaskStatus = SubTaskStatus.COMPLETA;
    }

    @Override
    public String toString() {

        String fecha = dueDateTime != null ?
                " [" + dueDateTime.format(DateTimeFormatter.ofPattern("dd/MM HH:mm")) + "]" : "";
        return title + fecha + " (" + subTaskStatus + ")";
    }
}
