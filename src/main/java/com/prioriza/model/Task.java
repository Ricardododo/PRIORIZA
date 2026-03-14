package com.prioriza.model;

import com.prioriza.priority.model.PriorityLevel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa una tarea en el sistema PRIORIZA.
 * 
 * Una tarea pertenece a una lista de tareas (TaskList) y puede contener subtareas.
 * El sistema calcula automáticamente su prioridad basándose en fecha de vencimiento,
 * importancia, estado y cantidad de subtareas.
 * 
 * @author PRIORIZA
 * @version 1.0
 */
public class Task {
    private int id;
    private String title;
    private String description;
    private LocalDateTime dueDateTime;
    private boolean important;
    private TaskStatus status;
    private int taskListId;

    private List<SubTask> subTasks =  new ArrayList<>();

    /**
     * Campos derivados (no persisten en la base de datos).
     */
    private int priorityScore;
    private Priority priority;
    private PriorityLevel priorityLevel;

    /**
     * Constructor por defecto.
     * Inicializa la tarea con valores predeterminados:
     * estado PENDIENTE, prioridad MEDIA y nivel de prioridad MEDIO.
     */
    public Task() {
        this.status = TaskStatus.PENDIENTE;
        this.priority = Priority.MEDIA;
        this.priorityLevel = PriorityLevel.MEDIO;
    }

    /**
     * Constructor completo para crear una tarea con todos los atributos.
     * 
     * @param title        Título de la tarea
     * @param description  Descripción detallada de la tarea
     * @param dueDateTime  Fecha y hora de vencimiento
     * @param priority     Prioridad para la interfaz de usuario
     * @param status       Estado de la tarea
     * @param taskListId   Identificador de la lista de tareas
     * @param important    Indica si la tarea es importante
     */
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

    /**
     * Representación en cadena de la tarea.
     * @return Cadena con el título y la prioridad
     */
    @Override
    public String toString() {
        return title + " (" + priority + "(";
    }



}
