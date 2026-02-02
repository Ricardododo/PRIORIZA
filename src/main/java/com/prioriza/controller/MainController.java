package com.prioriza.controller;

import com.prioriza.dao.SubTaskDAO;
import com.prioriza.dao.TaskDAO;
import com.prioriza.dao.TaskListDAO;
import com.prioriza.model.*;
import com.prioriza.service.TaskService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;



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
    private TableColumn<Task, String> colDescription;
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
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getTitle())
        );

        colDescription.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getDescription())
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
    //metodo para controlar el popup (+ Nueva Tarea)
    @FXML
    private void handleNewTask(){

        TaskList selectedList = taskListView.getSelectionModel().getSelectedItem();

        if (selectedList == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Selecciona una lista antes de crear una tarea");
            alert.showAndWait();
            return;
        }

        try{
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/task-form.fxml")
            );

            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setTitle("Nueva Tarea");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            //obtener tarea creada
            TaskFormController controller = loader.getController();
            Task newTask = controller.getTaskResult();

            if(newTask != null){

                //asignar a una lista
                newTask.setTaskListId(selectedList.getId());

                //usar service para aplicar reglas
                TaskService taskService = new TaskService();
                taskService.createTask(newTask);

                //refrescar tabla
                loadTasks(selectedList.getId());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

