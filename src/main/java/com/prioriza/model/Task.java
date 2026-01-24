package com.prioriza.model;

import java.time.LocalDate;

public class Task {
    private int id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Priority priority; // recordar LOW, MEDIUM, HIGH, URGENT
    private TaskStatus status; // PENDING, COMPLETED, CANCELLED
    private int taskListId; // FK a TaskList

    //Constructor
    public Task() {
        this.status = TaskStatus.PENDING;
        this.priority = Priority.MEDIUM;
    }

    //constructor completo
    public Task(int id, String title, String description, LocalDate dueDate, Priority priority, TaskStatus status, int taskListId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
        this.taskListId = taskListId;
    }

    //constructor para crear tareas
    public Task(String title, String description, LocalDate dueDate, int taskListId) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.taskListId = taskListId;
        this.status = TaskStatus.PENDING;
        this.priority = Priority.MEDIUM;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public int getTaskListId() {
        return taskListId;
    }

    public void setTaskListId(int taskListId) {
        this.taskListId = taskListId;
    }
}
