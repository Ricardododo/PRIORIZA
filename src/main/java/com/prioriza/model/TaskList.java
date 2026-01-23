package com.prioriza.model;

import java.util.List;

public class TaskList {
    private int id;
    private String name;
    private int userId; // FK a la clase User

    //Constructor
    public TaskList() {
    }

    public TaskList(int id, String name, int userId) {
        this.id = id;
        this.name = name;
        this.userId = userId;
    }

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
}
