package com.prioriza.model;

import java.time.LocalDate;

public class SubTask {
    private int id;
    private String title;
    private SubTaskStatus subTaskStatus;
    private int taskId; // esta es la FK a la clase Task para la BD

    private LocalDate dueDate;
    private boolean important;

    //Constructor
    public SubTask() {
        this.subTaskStatus = SubTaskStatus.PENDIENTE;
    }

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

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    //metodo para marcar completado
    public void markCompleted(){
        this.subTaskStatus = SubTaskStatus.COMPLETA;
    }

    @Override
    public String toString() {

        return title + " (" + subTaskStatus + ")";
    }
}
