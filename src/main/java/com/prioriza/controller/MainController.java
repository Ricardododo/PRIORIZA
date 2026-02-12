package com.prioriza.controller;

import com.prioriza.dao.SubTaskDAO;
import com.prioriza.dao.TaskDAO;
import com.prioriza.dao.TaskListDAO;
import com.prioriza.model.*;
import com.prioriza.priority.engine.PriorityEngine;
import com.prioriza.priority.model.PriorityLevel;
import com.prioriza.service.TaskService;
import com.prioriza.session.Session;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;



import java.time.LocalDate;
import java.util.List;

public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private final TaskListDAO taskListDAO = new TaskListDAO();
    private final TaskDAO taskDAO = new TaskDAO();
    private final SubTaskDAO subTaskDAO = new SubTaskDAO();
    private final TaskService taskService = new TaskService();

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
    private BorderPane rootpane;

    //aplicar en los métodos
    public void aplicarReglasHeuristicas(){
        logger.debug("Aplicando reglas heurísticas...");
        // Tu código
        logger.info("Reglas aplicadas exitosamente");
    }

    @FXML
    public void initialize() {

        //comprobar si hay un usuario
        if (Session.getUser() == null){
            showError("Tu sesión no esta activa. Inicia sesión nuevamente.");
            userLabel.setText("Invitado");
            return; //no sigue si no hay nadie
        }else{
            userLabel.setText(Session.getUser().getName());
        }

        //ajustar columnas automaticamente
        taskTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        //columnas
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

        //Colores por prioridad
        taskTableView.setRowFactory(tv -> new TableRow<Task>(){
            @Override
            protected void updateItem(Task task, boolean empty){
                super.updateItem(task, empty);

                if(task == null || empty){
                    setStyle("");
                }else if(task.getPriority() != null){
                    switch (task.getPriority()){
                        case URGENTE -> setStyle("-fx-background-color: #ffb3b3;");
                        case ALTA -> setStyle("-fx-background-color: #ffd966;");
                        case MEDIA -> setStyle("-fx-background-color: #c6efce;");
                        case BAJA -> setStyle("-fx-background-color: #d9e1f2;");
                    }
                }
            }
        });
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

            //tamaño para login
            stage.setWidth(400);
            stage.setHeight(400);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //metodo del botón eliminar lista
    @FXML
    private void handleDeleteListTask(){

        TaskList selectedList = taskListView.getSelectionModel().getSelectedItem();

        if(selectedList == null){
            showWarning("Selecciona una lista para eliminar");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Eliminar Lista?");
        confirm.setContentText("Se eliminarán todas las tareas y subtareas de esta lista.");

        if(confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try{
                //borrar primero las subtareas y tares
                List<Task> tasks = taskDAO.getByTaskListId(selectedList.getId());
                for (Task t : tasks){
                    subTaskDAO.deleteByTaskId(t.getId());
                    taskDAO.delete(t.getId());
                }

                //luego borrar la lista
                taskListDAO.delete(selectedList.getId());

                //recargar ListView
                loadTaskLists();
                taskTableView.getItems().clear();
                subTaskView.getItems().clear();

                showInfo("Lista eliminada correctamente");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //metodo del boton eliminar tarea
    @FXML
    private void handleDeleteTask(){

        Task selectedTask = taskTableView.getSelectionModel().getSelectedItem();

        if(selectedTask == null){
            showWarning("Selecciona una tarea para eliminar");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Eliminar tarea?");
        confirm.setContentText("Esta acción no se puede deshacer.");

        if(confirm.showAndWait().get() == ButtonType.OK){
            try{
                taskDAO.delete(selectedTask.getId());
                loadTasks(selectedTask.getTaskListId());
                showInfo("Tarea eliminada correctamente");
            } catch (Exception e) {
                e.printStackTrace();
                showError("Error al eliminar tarea");
            }
        }
    }
    //metodo del boton eliminar subtarea
    @FXML
    private void handleDeleteSubTask(){

        SubTask sub = subTaskView.getSelectionModel().getSelectedItem();

        if (sub == null){
            showWarning("Selecciona una subtarea para eliminar");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmación");
        confirm.setHeaderText("¿Eliminar subtarea?");
        confirm.setContentText("Esta acción no se puede deshacer.");

        if(confirm.showAndWait().get() == ButtonType.OK){
            try{
                subTaskDAO.delete(sub.getId());
                loadSubTasks(sub.getTaskId());
                showInfo("Subtarea eliminada");
            } catch (Exception e) {
                e.printStackTrace();
                showError("Error al eliminar subtarea");
            }
        }
    }
    //metodo del boton editar lista
    @FXML
    private void handleEditList(){

        TaskList selectedList = taskListView.getSelectionModel().getSelectedItem();

        if(selectedList == null){
            showWarning("Selecciona una lista para editar");
            return;
        }

        //reutilizar TextInputDialog como popup
        TextInputDialog dialog = new TextInputDialog(selectedList.getName());
        dialog.setTitle("Editar Lista");
        dialog.setHeaderText("Modificar el Nombre de la lista");
        dialog.setContentText("Nuevo nombre:");

        dialog.showAndWait().ifPresent(name -> {
            if (name == null || name.trim().isEmpty()){
                showWarning("El nombre no puede estar vacío");
                return;
            }
            try{
                selectedList.setName(name);
                taskListDAO.update(selectedList); //metodo update que actualiza solo el nombre
                loadTaskLists();
                taskListView.getSelectionModel().select(selectedList);
                showInfo("Nombre de lista actualizado");
            } catch (Exception e) {
                e.printStackTrace();
                showError("Error al actualizar la lista");
            }
        });
    }
    //metodo del boton editar tarea
    @FXML
    private void handleEditTask(){

        Task selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if(selectedTask == null){
            showWarning("Selecciona una tarea para editar");
            return;
        }
        //reutiliza task-form.fxml
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/task-form.fxml"));
            Scene scene = new Scene(loader.load());

            TaskFormController controller = loader.getController();
            controller.setTaskToEdit(selectedTask);

            Stage stage = new Stage();
            stage.setTitle("Editar tarea");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            Task updatedTask = controller.getTaskResult();

            if(updatedTask != null){
                updatedTask.setId(selectedTask.getId());
                updatedTask.setTaskListId(selectedTask.getTaskListId());


                taskService.updateTask(updatedTask); //recalcular prioridad
                loadTasks(selectedTask.getTaskListId());

                showInfo("Tarea actualizada correctamente");
            }
        } catch (Exception e) {
            showError("Error al editar la tarea");
            e.printStackTrace();
        }
    }
    //metodo del boton editar subtareas
    @FXML
    private void handleEditSubTask(){

        SubTask selectedSubTask = subTaskView.getSelectionModel().getSelectedItem();
        if(selectedSubTask == null){
            showWarning("Selecciona una Subtarea para editar");
            return;
        }
        //reutiliza subtask-form.fxml
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/subtask-form.fxml"));
            Scene scene = new Scene(loader.load());

            SubTaskFormController controller = loader.getController();
            controller.setSubTaskToEdit(selectedSubTask);

            Stage stage = new Stage();
            stage.setTitle("Editar subtarea");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            SubTask updatedSubTask = controller.getResult();

            if(updatedSubTask != null){
                updatedSubTask.setId(selectedSubTask.getId());
                updatedSubTask.setTaskId(selectedSubTask.getTaskId());

                subTaskDAO.update(updatedSubTask);
                loadSubTasks(selectedSubTask.getTaskId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //recalcular o refrescar prioridades de todas las tareas
    @FXML
    private void handleRecalculatePriorities() {
        TaskList selectedList = taskListView.getSelectionModel().getSelectedItem();
        if (selectedList == null) {
            showWarning("Selecciona una lista para recalcular prioridades");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Recalcular prioridades");
        confirm.setHeaderText("¿Recalcular prioridades de todas las tareas?");
        confirm.setContentText("Esto actualizará las prioridades según las reglas heurísticas.");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                List<Task> tasks = taskDAO.getByTaskListId(selectedList.getId());
                for (Task task : tasks) {
                    taskService.updateTask(task);
                }
                loadTasks(selectedList.getId());
                showInfo("Prioridades recalculadas correctamente");
            } catch (Exception e) {
                showError("Error al recalcular prioridades");
                e.printStackTrace();
            }
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
    private void showError(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    //metodo para informar
    private void showInfo(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    //modo oscuro
    @FXML
    public void toggleDarkMode(){
        if (rootpane.getStyleClass().contains("dark")){
            rootpane.getStyleClass().remove("dark");
        }else{
            rootpane.getStyleClass().add("dark");
        }
    }
    //metodo prueba del motor
    @FXML
    private void testPriorityEngine() {
        Task selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showWarning("Selecciona una tarea para probar");
            return;
        }

        //TODO AHORA ES CLARO Y ACCESIBLE
        PriorityLevel level = taskService.calculatePriorityLevel(selectedTask);
        int score = taskService.calculatePriorityScore(selectedTask);
        Priority uiPriority = taskService.convertToUIPriority(level);

        showInfo(String.format(
                " Puntuación: %d\n Nivel motor: %s\n Prioridad UI: %s",
                score, level, uiPriority
        ));
    }

}

