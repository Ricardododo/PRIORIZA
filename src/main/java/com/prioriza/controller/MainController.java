package com.prioriza.controller;

import com.prioriza.dao.SubTaskDAO;
import com.prioriza.dao.TaskDAO;
import com.prioriza.dao.TaskListDAO;
import com.prioriza.model.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;


import java.time.LocalDate;
import java.util.List;

public class MainController {

    private final TaskListDAO taskListDAO = new TaskListDAO();
    private final TaskDAO taskDAO = new TaskDAO();
    private final SubTaskDAO subTaskDAO = new SubTaskDAO();

    @FXML
    private ListView<TaskList> taskListView;
    @FXML
    private TableView<Task> taskTableView;
    @FXML
    private TableColumn<Task, String> colTitle;
    @FXML
    private TableColumn<Task, Priority> colPriority;
    @FXML
    private TableColumn<Task, LocalDate> colDueDate;
    @FXML
    private TableColumn<Task, TaskStatus> colStatus;
    @FXML
    private ListView<SubTask> subTaskView;

    @FXML
    public void initialize() {

        //conectar columnas con atributos del model/Task
        colTitle.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getTitle())
        );

        colPriority.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getPriority())
        );

        colDueDate.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getDueDate())
        );

        colStatus.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getStatus())
        );

        loadTaskLists(); //cargar las listas de tareas
        setupListeners(); //mostrar
    }

    //metodo interno, carga listas desde la base de datos
    private void loadTaskLists() {
        try {
            List<TaskList> lists = taskListDAO.getAllTaskList();
            taskListView.getItems().setAll(lists);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //metodo listener, selecciona lista y carga tareas
    private void setupListeners() {
        taskListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldList, newList) -> {

                    if (newList != null) {
                        loadTasks(newList.getId());
                    }
                }

        );
        taskTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldTask, newTask) -> {

                    if (newTask != null) {
                        loadSubTasks(newTask.getId());
                    }
                }
        );
    }

    //metodo para cargar las tareas
    private void loadTasks(int taskListId) {

        try {
            List<Task> tasks = taskDAO.getByTaskListId(taskListId);

            //la tabla se llena
            taskTableView.getItems().setAll(tasks);

            //limpiar subtareas cuando cambia de lista
            subTaskView.getItems().clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //metodo para cargar subtareas
    private void loadSubTasks(int taskId){

        try{
            List<SubTask> subs = subTaskDAO.getByTaskId(taskId);
            subTaskView.getItems().setAll(subs);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

