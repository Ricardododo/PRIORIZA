package com.prioriza.model;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class EmailNotification {

    private int id;
    private int userId;
    private String userEmail;
    private Integer taskId;
    private Integer subtaskId;
    private String itemType; //TASK, SUBTASK
    private String itemTitle;
    private LocalDate dueDate;
    private int daysRemaining;
    private String status; // PENDING, SENT, FAILED
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private String errorMessage;

    //Constructor para tareas
    public EmailNotification(User user, Task task, int daysRemaining) {
        this.userId = user.getId();
        this.userEmail = user.getEmail();
        this.taskId = task.getId();
        this.itemType = "TASK";
        this.itemTitle = task.getTitle();
        this.dueDate = task.getDueDate();
        this.daysRemaining = daysRemaining;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    //Constructor para subtareas
    public EmailNotification(User user, SubTask subtask, int daysRemaining) {
        this.userId = user.getId();
        this.userEmail = user.getEmail();
        this.subtaskId = subtask.getId();
        this.itemType = "SUBTASK";
        this.itemTitle = subtask.getTitle();
        this.dueDate = subtask.getDueDate();
        this.daysRemaining = daysRemaining;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }
    //constructor vacío para JDBC/mapResultSet (DAO)
    public EmailNotification() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(Integer subtaskId) {
        this.subtaskId = subtaskId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public LocalDate getDuedate() {
        return dueDate;
    }

    public void setDuedate(LocalDate duedate) {
        this.dueDate = duedate;
    }

    public int getDaysRemaining() {
        return daysRemaining;
    }

    public void setDaysRemaining(int daysRemaining) {
        this.daysRemaining = daysRemaining;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
