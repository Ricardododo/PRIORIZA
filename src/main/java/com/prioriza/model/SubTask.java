package com.prioriza.model;

public class SubTask {
    private int id;
    private String title;
    private SubTaskStatus subTaskStatus;
    private int taskId; // esta es la FK a la clase Task para la BD

    //Constructor
    public SubTask() {
        this.subTaskStatus = SubTaskStatus.PENDIENTE;
    }

    public SubTask(int id, String title, SubTaskStatus subTaskStatus, int taskId) {
        this.id = id;
        this.title = title;
        this.subTaskStatus = subTaskStatus;
        this.taskId = taskId;
    }

    public SubTask(String title, int taskId) {
        this.title = title;
        this.subTaskStatus = SubTaskStatus.PENDIENTE;
        this.taskId = taskId;
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

    //metodo para marcar completado
    public void markCompleted(){
        this.subTaskStatus = SubTaskStatus.COMPLETA;
    }

    @Override
    public String toString() {

        return title + " (" + subTaskStatus + ")";
    }
}
