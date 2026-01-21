package com.prioriza.dao;

import com.prioriza.model.SubTask;

import java.util.ArrayList;
import java.util.List;

public class SubTaskDAO {

    //Crear
    public void create(SubTask subTask){

    }

    //Leer
    public List<SubTask> getByTaskId(int taskId){
        List<SubTask> subTasks = new ArrayList<>();
        try{

        } catch (Exception e) {
            e.printStackTrace();
        }
        return subTasks;
    }

    //Modificar
    public void update(SubTask subTask){

    }

    //Eliminar
    public void delete(int id){

    }
}
