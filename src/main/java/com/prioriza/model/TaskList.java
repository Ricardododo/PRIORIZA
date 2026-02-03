package com.prioriza.model;

import java.util.ArrayList;
import java.util.List;

public class TaskList {
    private int id;
    private String name;
    private int userId; // FK a la clase User

    //relación lógica
    private List<Task> tasks = new ArrayList<>();

    //Constructor vacio (JDBC)
    public TaskList() {
    }
    //constructor completo (lectura desde BD)
    public TaskList(int id, String name, int userId) {
        this.id = id;
        this.name = name;
        this.userId = userId;
    }
    //constructor para crear desde la app
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

    public void addTask(Task task) {
        this.tasks.add(task);
    }


    //toString

    @Override
    public String toString() {

        return name; // solo muestra el nombre
    }
}
