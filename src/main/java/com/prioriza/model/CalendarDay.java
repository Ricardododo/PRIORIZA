package com.prioriza.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//Esta clase representa un día en el calendario con sus tareas
public class CalendarDay {
    private LocalDate date;
    private List<Task> tasks;
    private boolean hasTasks;
    private int taskCount;
    private int urgentCount;

    public CalendarDay(LocalDate date) {
        this.date = date;
        this.tasks = new ArrayList<>();
        this.hasTasks = false;
        this.taskCount = 0;
        this.urgentCount = 0;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        this.taskCount = tasks.size();
        this.hasTasks = !tasks.isEmpty();
        this.urgentCount = (int) tasks.stream()
                .filter(t -> t.getPriority() == Priority.URGENTE)
                .count();
    }
    public void addTask(Task task){
        this.tasks.add(task);
        this.taskCount = tasks.size();
        this.hasTasks = true;
        if (task.getPriority() == Priority.URGENTE) {
            this.urgentCount++;
        }
    }

    public boolean isHasTasks() {
        return hasTasks;
    }

    public void setHasTasks(boolean hasTasks) {
        this.hasTasks = hasTasks;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public int getUrgentCount() {
        return urgentCount;
    }

    public void setUrgentCount(int urgentCount) {
        this.urgentCount = urgentCount;
    }

    public String getStatusColor() {
        if (urgentCount > 0) return "#dc3545"; // Rojo si hay urgentes
        if (hasTasks) return "#ffc107";        // Amarillo si hay tareas
        return "transparent";                   // Transparente si no hay
    }

    public String getTooltipText() {
        if (!hasTasks) return "Sin tareas";
        StringBuilder sb = new StringBuilder();
        sb.append(date.toString()).append("\n");
        sb.append("Total tareas: ").append(taskCount).append("\n");
        if (urgentCount > 0) {
            sb.append("URGENTES: ").append(urgentCount).append("\n");
        }
        for (Task task : tasks) {
            sb.append("• ").append(task.getTitle());
            if (task.getPriority() == Priority.URGENTE) {
                sb.append(" [URGENTE]");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
