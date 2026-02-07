package com.prioriza.controller;

import com.prioriza.dao.SubTaskDAO;
import com.prioriza.dao.TaskDAO;
import com.prioriza.dao.TaskListDAO;
import com.prioriza.model.*;
import com.prioriza.service.TaskService;
import com.prioriza.session.Session;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    private Label userLabel;

    @FXML
    public void initialize() {

        //comprobar si hay un usuario
        if (Session.getUser() == null){
            showError("Tu sesión no esta activa. Inicia sesión nuevamente.");
            return; //no sigue si no hay nadie
        }else{
            userLabel.setText(Session.getUser() != null ? Session.getUser().getName() : "Usuario");
        }
        //mostrar usuario activo
        userLabel.setText(" " + Session.getUser().getName());

        taskTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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

        loadTaskLists(); //cargar las listas de tareas(datos)
        setupListeners(); //activar listeners 8seleccio de listas, tareas, subtareas) para los clicks
    }

    //metodo interno, carga listas desde la base de datos
    private void loadTaskLists() {

        try {
            // usuario actual
            int userId = Session.getUser().getId();

            //cargar listas solo de ese uusuario
            List<TaskList> lists = taskListDAO.getByUserId(userId);

            System.out.println("LISTAS ENCONTRADAS: " + lists.size());

            //mostrar en el ListView
            taskListView.getItems().setAll(lists);

        } catch (Exception e) {
            showError("No se pudieron cargar las listas");
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
            showError("No se pudieron cargar las tareas.");
            e.printStackTrace();
        }
    }
    //metodo para cargar subtareas
    private void loadSubTasks(int taskId){

        try{
            List<SubTask> subs = subTaskDAO.getByTaskId(taskId);
            subTaskView.getItems().setAll(subs);

        } catch (Exception e) {
            showError("No se pudieron cargar las subtareas");
            e.printStackTrace();
        }
    }
    //metodo para controlar el popup (+ Nueva Lista)
    @FXML
    private void handleNewList(){

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nueva Lista");
        dialog.setHeaderText("Crear una nueva lista");
        dialog.setContentText("Nombre de la lista: ");

        dialog.showAndWait().ifPresent(name -> {

            if (name == null || name.trim().isEmpty()){
                showError("El nombre de la lista no puede estar vacío");
                return;
            }
            try{
                if (Session.getUser() == null){
                    showError("Tu sesión ha expirado. Inicia sesión nuevamente.");
                    return;
                }
                TaskList newList = new TaskList();
                newList.setName(name);
                //asignar lists al usuario login
                newList.setUserId(Session.getUser().getId());

                taskListDAO.insert(newList);

                //refrescar listas
                loadTaskLists();

                //selecciona automaticamente la nueva lista
                taskListView.getItems().stream()
                        .filter(l -> l.getName().equals(name))
                        .findFirst()
                        .ifPresent(l -> taskListView.getSelectionModel().select(l));

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    //metodo para controlar el popup (+ Nueva Tarea)
    @FXML
    private void handleNewTask(){

        TaskList selectedList = taskListView.getSelectionModel().getSelectedItem();

        if (selectedList == null) {
           showWarning("Primero selecciona una lista para añadir una tarea.");
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
    //metodo para controlar el popup (+ Nueva SubTarea)
    @FXML
    private void handleNewSubTask(){

        Task selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask == null){
           showWarning("Primero selecciona una tarea para añadir una subtarea.");
           return;
        }

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/subtask-form.fxml"));

            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Nueva SubTarea");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            //obtener subtarea creada
            SubTaskFormController controller = loader.getController();
            SubTask newSub = controller.getResult();

            if(newSub != null){

                //asignar FK Tarea
                newSub.setTaskId(selectedTask.getId());

                //guardar bd
                subTaskDAO.insert(newSub);

                //refrescar lista
                loadSubTasks(selectedTask.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //metodo para cerrar sesión
    @FXML
    private void handleLogout(){

        //mensaje de confirmación al cerrar sesión
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cerrar sesión");
        alert.setHeaderText("¿Deseas cerrar la sesión?");
        alert.setContentText("Tendrás que iniciar sesión nuevamente.");

        //condicional para saber si cierra o continua (continua)
        if (alert.showAndWait().orElse(ButtonType.CANCEL) !=ButtonType.OK){
            return;
        }

        //limpiar sesión
        Session.clear();
        taskListView.getItems().clear();
        taskTableView.getItems().clear();
        subTaskView.getItems().clear();

        try{
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/login-view.fxml")
            );
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) taskTableView.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //metodo para alertas
    private void showWarning(String message){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atención");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    //metodo para errores
    private void  showError(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

