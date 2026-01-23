package com.prioriza.model;

public class SubTask {
    private int id;
    private String title;
    private boolean completed;
    private int taskId; // esta es la FK a la clase Task para la BD

    //Constructor
    public SubTask() {
    }

    public SubTask(int id, String title, boolean completed, int taskId) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.taskId = taskId;
    }

    public SubTask(String title, int taskId) {
        this.title = title;
        this.taskId = taskId;
        this.completed = false;
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

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
}
