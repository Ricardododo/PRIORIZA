package com.prioriza.model;

import com.prioriza.priority.model.PriorityLevel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Task {
    private int id;
    private String title;
    private String description;
    private LocalDateTime dueDateTime;
    private boolean important;
    private TaskStatus status;
    private int taskListId; // FK a TaskList

    private List<SubTask> subTasks =  new ArrayList<>();

    // campos derivados (no dominio persistente)
    private int priorityScore;              // heuristica
    private Priority priority;             // UI
    private PriorityLevel priorityLevel;  // motor

    //Constructor
    public Task() {
        this.status = TaskStatus.PENDIENTE;
        this.priority = Priority.MEDIA;
        this.priorityLevel = PriorityLevel.MEDIO;
    }

    //constructor completo
    public Task(String title, String description, LocalDateTime dueDateTime, Priority priority, TaskStatus status, int taskListId, boolean important) {
        this();
        this.title = title;
        this.description = description;
        this.dueDateTime = dueDateTime;
        this.priority = priority;
        this.status = status;
        this.taskListId = taskListId;
        this.important = important;
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

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    public int getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(int priorityScore) {
        this.priorityScore = priorityScore;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public PriorityLevel getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(PriorityLevel priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    @Override
    public String toString() {
        return title + " (" + priority + "(";
    }



}
