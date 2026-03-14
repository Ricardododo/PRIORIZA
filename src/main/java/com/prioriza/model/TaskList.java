package com.prioriza.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa una lista de tareas en el sistema PRIORIZA.
 * 
 * Cada lista de tareas pertenece a un usuario y puede contener múltiples tareas.
 * Es la entidad principal para organizar las tareas del usuario.
 * 
 * @author PRIORIZA
 * @version 1.0
 */
public class TaskList {
    private int id;
    private String name;
    private int userId;

    /**
     * Relación lógica con las tareas de esta lista.
     */
    private List<Task> tasks = new ArrayList<>();

    /**
     * Constructor vacío para JDBC.
     */
    public TaskList() {
    }

    /**
     * Constructor completo para crear una lista de tareas.
     * 
     * @param name   Nombre de la lista de tareas
     * @param userId Identificador del usuario propietario
     */
    public TaskList(String name, int userId) {
        this.name = name;
        this.userId = userId;
    }

    //Getter y Setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Representación en cadena de la lista de tareas.
     * @return Nombre de la lista
     */
    @Override
    public String toString() {

        return name; // solo muestra el nombre
    }
}
