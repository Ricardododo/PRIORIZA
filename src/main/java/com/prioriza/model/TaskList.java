package com.prioriza.model;

import java.util.List;

public class TaskList {
    private int id;
    private String name;
    private int userId; // esta es la FK a la clase User para BD

    //aquí debe ir una List<> para las tareas asociadas a la lista de tareas de (Task)
    private List<Task> tasks;

    //Constructor
    public TaskList() {
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
}
