package com.prioriza.controller;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.prioriza.dao.*;
import com.prioriza.model.*;
import com.prioriza.service.*;
import com.prioriza.session.Session;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;



import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private final TaskListDAO taskListDAO = new TaskListDAO();
    private final TaskDAO taskDAO = new TaskDAO();
    private final SubTaskDAO subTaskDAO = new SubTaskDAO();
    private final TaskService taskService = new TaskService();
    private final EmailNotificationDAO emailNotificationDAO = new EmailNotificationDAO();
    private final PDFExportService pdfService = new PDFExportService();
    private final ShareService shareService = new ShareService();


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

    //Elementos del menú para ocultar o mostrar segun role
    @FXML
    private Menu adminMenu;
    @FXML
    private MenuItem scanNowMenuItem;
    @FXML
    private MenuItem sendNowMenuItem;
    @FXML
    private MenuItem testNotificationsMenuItem;
    @FXML
    private MenuItem globalStatsMenuItem;

    private UserSettings currentUserSettings; //esto es para mantener la config actual

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

        //cargar configuración del usuario
        UserSettingsDAO settingsDAO = new UserSettingsDAO();
        currentUserSettings = settingsDAO.getByUserId(Session.getUser().getId());
        System.out.println("Configuración cargarda: " + currentUserSettings);

        //verificar rol y configurar menú
        configureMenuByRole();

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
    //configuración menú según el rol del usuario
    private void configureMenuByRole() {
        User currentUser = Session.getUser();
        if (currentUser == null) return;

        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;

        // Mostrar/ocultar menú de administración
        if (adminMenu != null) {
            adminMenu.setVisible(isAdmin);
        }

        // También podemos ocultar items individuales
        if (scanNowMenuItem != null) {
            scanNowMenuItem.setVisible(isAdmin);
        }

        if (sendNowMenuItem != null) {
            sendNowMenuItem.setVisible(isAdmin);
        }

        if (testNotificationsMenuItem != null) {
            testNotificationsMenuItem.setVisible(isAdmin);
        }

        if (globalStatsMenuItem != null) {
            globalStatsMenuItem.setVisible(isAdmin);
        }

        System.out.println("Usuario: " + currentUser.getName() +
                " | Rol: " + currentUser.getRole() +
                " | Menú admin: " + (isAdmin ? "VISIBLE" : "OCULTO"));
    }

    //metodo para ADMIN - Estadísticas globales
    @FXML
    private void handleGlobalStats() {
        if (!isAdmin()) {
            showError("No tienes permisos para esta acción");
            return;
        }
        showInfo("Estadísticas globales - Próximamente");
    }

    //Verificar si es admin
    private boolean isAdmin() {
        User user = Session.getUser();
        return user != null && user.getRole() == UserRole.ADMIN;
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
    // metodos de ADMIN (solo los ve el ADMIN)
    @FXML
    private void handleScanNow() {
        if(!isAdmin()){
            showError("No tienes permiso para esta acción");
            return;
        }
        try {
            NotificationProcessor processor = new NotificationProcessor();

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Escanear tareas");
            confirm.setHeaderText("¿Ejecutar escaneo manual?");
            confirm.setContentText("Se buscarán tareas próximas a vencer y se crearán notificaciones.");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    processor.scanNow();
                    showInfo("Escaneo completado.\nRevisa la consola para ver los resultados.");
                }
            });

        } catch (Exception e) {
            showError("Error en escaneo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSendNow() {
        if (!isAdmin()) {
            showError("No tienes permisos para esta acción");
            return;
        }
        try {
            NotificationProcessor processor = new NotificationProcessor();

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Enviar emails");
            confirm.setHeaderText("¿Enviar notificaciones pendientes?");
            confirm.setContentText("Se enviarán todos los emails pendientes.");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    processor.sendNow();
                    showInfo("Envío completado.\nRevisa la consola para ver los resultados.");
                }
            });

        } catch (Exception e) {
            showError("Error en envío: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    private void handleTestNotifications() {
        if (!isAdmin()) {
            showError("No tienes permisos para esta acción");
            return;
        }
        try {
            NotificationProcessor processor = new NotificationProcessor();
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Prueba de Notificaciones");
            confirm.setHeaderText("¿Qué deseas probar?");

            ButtonType btnScan = new ButtonType("Escanear tareas");
            ButtonType btnSend = new ButtonType("Enviar emails");
            ButtonType btnStats = new ButtonType("Estadísticas");
            ButtonType btnCancel = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

            confirm.getButtonTypes().setAll(btnScan, btnSend, btnStats, btnCancel);

            confirm.showAndWait().ifPresent(response -> {
                if (response == btnScan) {
                    processor.scanNow();
                    showInfo("Escaneo completado. Revisa la consola.");
                } else if (response == btnSend) {
                    processor.sendNow();
                    showInfo("Envío completado. Revisa la consola.");
                } else if (response == btnStats) {
                    NotificationStats stats = NotificationStats.getInstance();
                    showInfo(stats.getReport());
                }
            });
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
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
    //Mostrar estadísticas de notificaciones
    @FXML
    private void handleNotificationStats() {
        try {
            NotificationStats stats = NotificationStats.getInstance();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Estadísticas de Notificaciones");
            alert.setHeaderText("Sistema Inteligente de Notificaciones");
            alert.setContentText(stats.getReport());

            ButtonType btnRefresh = new ButtonType("Actualizar");
            ButtonType btnClose = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(btnRefresh, btnClose);

            alert.showAndWait().ifPresent(response -> {
                if (response == btnRefresh) {
                    handleNotificationStats(); // Recargar
                }
            });

        } catch (Exception e) {
            showError("Error al cargar estadísticas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //FUNCIONALIDADES PARA USUARIO NORMAL

    //Mis estadísticas - Ver estadísticas personales
    @FXML
    private void handleUserNotificationStats() {
        User currentUser = Session.getUser();
        if (currentUser == null) return;

        // Obtener notificaciones del usuario
        List<EmailNotification> notifications = emailNotificationDAO.getByUserId(currentUser.getId());
        int unreadCount = emailNotificationDAO.getUnreadCount(currentUser.getId());

        // Calcular estadísticas
        long total = notifications.size();
        long sent = notifications.stream().filter(n -> "SENT".equals(n.getStatus())).count();
        long pending = notifications.stream().filter(n -> "PENDING".equals(n.getStatus())).count();
        long failed = notifications.stream().filter(n -> "FAILED".equals(n.getStatus())).count();

        // Crear mensaje
        String stats = String.format(
                """
                ESTADÍSTICAS DE NOTIFICACIONES
                ════════════════════════════════
                Usuario: %s
                Email: %s
                
                Total notificaciones: %d
                No leídas: %d
                Enviadas: %d
                Pendientes: %d
                Fallidas: %d
                
                Últimas notificaciones:
                """,
                currentUser.getName(),
                currentUser.getEmail(),
                total,
                unreadCount,
                sent,
                pending,
                failed
        );

        // Agregar últimas 5 notificaciones
        int count = 0;
        for (EmailNotification n : notifications) {
            if (count++ >= 5) break;
            stats += String.format("\n   • %s: %s (%d días) [%s]",
                    n.getItemType(),
                    n.getItemTitle(),
                    n.getDaysRemaining(),
                    n.getStatus());
        }

        // Mostrar en ventana personalizada
        showNotificationStats(stats, notifications, unreadCount);
    }

    //Ventana de estadísticas personalizada
    private void showNotificationStats(String stats, List<EmailNotification> notifications, int unreadCount) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Mis estadísticas");
        dialog.setHeaderText("Estadísticas de notificaciones");

        // Botones
        ButtonType markReadButton = new ButtonType("Marcar todas como leídas", ButtonBar.ButtonData.LEFT);
        ButtonType closeButton = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(markReadButton, closeButton);

        // Contenido
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        TextArea statsArea = new TextArea(stats);
        statsArea.setEditable(false);
        statsArea.setPrefRowCount(15);
        statsArea.setStyle("-fx-font-family: 'Consolas', monospace;");

        Label unreadLabel = new Label("Tienes " + unreadCount + " notificaciones no leídas");
        unreadLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #e94560;");

        content.getChildren().addAll(unreadLabel, statsArea);
        dialog.getDialogPane().setContent(content);

        // Manejar acción
        dialog.showAndWait().ifPresent(response -> {
            if (response == markReadButton) {
                emailNotificationDAO.markAllAsRead(Session.getUser().getId());
                showInfo("Todas las notificaciones marcadas como leídas");
            }
        });
    }

    //Próximas tareas - Ver tareas que vencen pronto
    @FXML
    private void handleUpcomingTasks() {
        User currentUser = Session.getUser();
        if (currentUser == null) return;

        List<Task> allTasks = taskDAO.getByUserId(currentUser.getId());
        LocalDate today = LocalDate.now();

        List<Task> upcomingTasks = allTasks.stream()
                .filter(t -> t.getDueDate() != null)
                .filter(t -> t.getStatus() != TaskStatus.COMPLETA)
                .filter(t -> {
                    long days = ChronoUnit.DAYS.between(today, t.getDueDate());
                    return days >= 0 && days <= 7;
                })
                .sorted((t1, t2) -> {
                    if (t1.getDueDate() == null) return 1;
                    if (t2.getDueDate() == null) return -1;
                    return t1.getDueDate().compareTo(t2.getDueDate());
                })
                .collect(Collectors.toList());

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Próximas tareas");
        dialog.setHeaderText("Tareas que vencen en los próximos 7 días");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        if (upcomingTasks.isEmpty()) {
            content.getChildren().add(new Label("No tienes tareas próximas a vencer"));
        } else {
            for (Task task : upcomingTasks) {
                long daysUntilDue = ChronoUnit.DAYS.between(today, task.getDueDate());

                //TEXTO CLARO
                String urgencia;
                String color;

                if (daysUntilDue == 0) {
                    urgencia = "VENCE HOY";
                    color = "#ff6b6b";  // Rojo
                } else if (daysUntilDue == 1) {
                    urgencia = "VENCE MAÑANA";
                    color = "#ffb347";  // Naranja
                } else if (daysUntilDue <= 3) {
                    urgencia = "VENCE PRONTO";
                    color = "#ffd966";  // Amarillo
                } else {
                    urgencia = "PROGRAMADA";
                    color = "#4caf50";  // Verde
                }

                Label taskLabel = new Label(String.format("%s - %s (%d días) [%s]",
                        task.getTitle(), task.getDueDate(), daysUntilDue, urgencia));
                taskLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
                taskLabel.setPadding(new Insets(5));

                List<SubTask> subs = subTaskDAO.getByTaskId(task.getId());
                long pendingSubs = subs.stream()
                        .filter(s -> s.getSubTaskStatus() != SubTaskStatus.COMPLETA)
                        .count();

                if (pendingSubs > 0) {
                    Label subLabel = new Label("   • " + pendingSubs + " subtareas pendientes");
                    subLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 11px;");
                    content.getChildren().addAll(taskLabel, subLabel);
                } else {
                    content.getChildren().add(taskLabel);
                }
            }
        }

        Button notifyButton = new Button("Recordarme estas tareas");
        notifyButton.setOnAction(e -> {
            for (Task task : upcomingTasks) {
                long days = ChronoUnit.DAYS.between(today, task.getDueDate());
                if (days >= 0) {
                    EmailNotification notification = new EmailNotification(currentUser, task, (int) days);
                    emailNotificationDAO.insert(notification);
                }
            }
            showInfo("Notificaciones creadas para " + upcomingTasks.size() + " tareas");
            dialog.close();
        });

        if (!upcomingTasks.isEmpty()) {
            content.getChildren().add(notifyButton);
        }

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE));
        dialog.showAndWait();
    }

    //Configurar alertas - Preferencias de notificación
    @FXML
    private void handleNotificationSettings() {
        User currentUser = Session.getUser();
        if (currentUser == null) return;

        // Cargar configuración actual
        UserSettingsDAO settingsDAO = new UserSettingsDAO();
        UserSettings currentSettings = settingsDAO.getByUserId(currentUser.getId());

        // Crear diálogo
        Dialog<UserSettings> dialog = new Dialog<>();
        dialog.setTitle("Configurar alertas");
        dialog.setHeaderText("Personaliza tus notificaciones");

        // Botones
        ButtonType saveButton = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        // Contenido
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        // Email
        CheckBox emailCheck = new CheckBox("Recibir notificaciones por email");
        emailCheck.setSelected(currentSettings.isEmailEnabled());
        grid.add(emailCheck, 0, 0, 2, 1);

        // Días de anticipación
        grid.add(new Label("Días antes de vencer:"), 0, 1);
        ComboBox<Integer> daysBox = new ComboBox<>();
        daysBox.getItems().addAll(1, 2, 3, 5, 7);
        daysBox.setValue(currentSettings.getDaysBeforeAlert());
        grid.add(daysBox, 1, 1);

        // Checkbox: Alertas de subtareas
        CheckBox subtaskCheck = new CheckBox("Incluir subtareas");
        subtaskCheck.setSelected(currentSettings.isAlertForSubtasks());
        grid.add(subtaskCheck, 0, 2, 2, 1);

        // Checkbox: Solo días laborables
        CheckBox workingDaysCheck = new CheckBox("Solo días laborables");
        workingDaysCheck.setSelected(currentSettings.isAlertOnlyWorkingDays());
        grid.add(workingDaysCheck, 0, 3, 2, 1);

        // Límite diario
        grid.add(new Label("Límite diario de alertas:"), 0, 4);
        Spinner<Integer> limitSpinner = new Spinner<>(1, 20, currentSettings.getMaxAlertsPerDay());
        limitSpinner.setEditable(true);
        grid.add(limitSpinner, 1, 4);

        // Hora de notificación
        grid.add(new Label("Hora de notificación:"), 0, 5);
        ComboBox<Integer> hourBox = new ComboBox<>();
        hourBox.getItems().addAll(8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
        hourBox.setValue(currentSettings.getNotificationHour());
        hourBox.setPromptText("Selecciona hora");
        grid.add(hourBox, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Convertir resultado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                UserSettings newSettings = new UserSettings(currentUser.getId());
                newSettings.setEmailEnabled(emailCheck.isSelected());
                newSettings.setDaysBeforeAlert(daysBox.getValue());
                newSettings.setAlertForSubtasks(subtaskCheck.isSelected());
                newSettings.setAlertOnlyWorkingDays(workingDaysCheck.isSelected());
                newSettings.setMaxAlertsPerDay(limitSpinner.getValue());
                newSettings.setNotificationHour(hourBox.getValue());
                return newSettings;
            }
            return null;
        });

        // Guardar configuración
        dialog.showAndWait().ifPresent(newSettings -> {
            settingsDAO.update(newSettings);
            showInfo("Configuración guardada correctamente");

            // Aplicar cambios en tiempo real al sistema de notificaciones
            aplicarConfiguracionNotificaciones(newSettings);
        });
    }
    //Metodo para aplicar la configuración al sistema
    private void aplicarConfiguracionNotificaciones(UserSettings settings) {
        this.currentUserSettings = settings; //guardar en variable de instancia
        // Aquí puedes actualizar variables globales o reiniciar servicios
        System.out.println("Nueva configuración aplicada:");
        System.out.println("Email: " + (settings.isEmailEnabled() ? "SÍ" : "NO"));
        System.out.println("Días alerta: " + settings.getDaysBeforeAlert());
        System.out.println("Subtareas: " + (settings.isAlertForSubtasks() ? "SÍ" : "NO"));
        System.out.println("Máx alertas/día: " + settings.getMaxAlertsPerDay());
    }
    //Exportar lista seleccionada a PDF
    @FXML
    private void handleExportListToPDF() {
        TaskList selectedList = taskListView.getSelectionModel().getSelectedItem();

        if (selectedList == null) {
            showWarning("Selecciona una lista para exportar");
            return;
        }

        try {
            List<Task> tasks = taskDAO.getByTaskListId(selectedList.getId());

            // Cargar subtareas
            for (Task task : tasks) {
                task.setSubTasks(subTaskDAO.getByTaskId(task.getId()));
            }

            String pdfPath = pdfService.exportTaskList(selectedList, tasks, Session.getUser());

            if (pdfPath != null) {
                showSuccess("PDF generado:\n" + pdfPath);

                // Preguntar qué hacer
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("PDF Generado");
                confirm.setHeaderText("¿Qué deseas hacer?");

                ButtonType btnOpen = new ButtonType("Abrir carpeta");
                ButtonType btnShare = new ButtonType("Compartir");
                ButtonType btnClose = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);

                confirm.getButtonTypes().setAll(btnOpen, btnShare, btnClose);

                confirm.showAndWait().ifPresent(response -> {
                    try {
                        if (response == btnOpen) {
                            Desktop.getDesktop().open(new File(pdfPath).getParentFile());
                        } else if (response == btnShare) {
                            shareService.shareViaWhatsApp(selectedList, tasks, Session.getUser());
                        }
                    } catch (Exception e) {
                        showError("Error: " + e.getMessage());
                    }
                });
            }

        } catch (Exception e) {
            showError("Error exportando PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //Exportar tarea seleccionada a PDF (botón rápido)
    @FXML
    private void handleExportTaskToPDF() {
        Task selectedTask = taskTableView.getSelectionModel().getSelectedItem();

        if (selectedTask == null) {
            showWarning("Selecciona una tarea para exportar");
            return;
        }

        selectedTask.setSubTasks(subTaskDAO.getByTaskId(selectedTask.getId()));

        String pdfPath = pdfService.exportSingleTask(selectedTask, Session.getUser());
        if (pdfPath != null) {
            showSuccess("PDF guardado en:\n" + pdfPath);

            try {
                Desktop.getDesktop().open(new File(pdfPath).getParentFile());
            } catch (Exception e) {
                // Ignorar
            }
        }
    }
    //Exportar Subtask seleccionada a PDF
    @FXML
    private void handleExportSubtaskToPDF() {
        SubTask selectedSubtask = subTaskView.getSelectionModel().getSelectedItem();

        if (selectedSubtask == null) {
            showWarning("Selecciona una subtarea para exportar");
            return;
        }

        try {
            // Crear una tarea temporal para la subtarea
            Task tempTask = new Task();
            tempTask.setTitle("Subtarea: " + selectedSubtask.getTitle());
            tempTask.setDescription(selectedSubtask.getTitle());
            tempTask.setDueDate(selectedSubtask.getDueDate());
            tempTask.setImportant(selectedSubtask.isImportant());
            tempTask.setStatus(TaskStatus.PENDIENTE);

            // Crear lista de subtareas
            List<SubTask> subtasks = new ArrayList<>();
            subtasks.add(selectedSubtask);
            tempTask.setSubTasks(subtasks);

            // Generar PDF
            String pdfPath = exportSingleTaskToPDF(tempTask, Session.getUser());

            if (pdfPath != null) {
                showSuccess("PDF de subtarea guardado en:\n" + pdfPath);

                // Preguntar si quiere abrir la carpeta
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("PDF Generado");
                confirm.setHeaderText("¿Deseas abrir la carpeta?");
                confirm.setContentText("PDF: " + pdfPath);

                ButtonType btnOpen = new ButtonType("Abrir carpeta");
                ButtonType btnClose = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
                confirm.getButtonTypes().setAll(btnOpen, btnClose);

                confirm.showAndWait().ifPresent(response -> {
                    if (response == btnOpen) {
                        try {
                            Desktop.getDesktop().open(new File(pdfPath).getParentFile());
                        } catch (Exception e) {
                            showError("No se pudo abrir la carpeta");
                        }
                    }
                });
            }
        } catch (Exception e) {
            showError("Error exportando subtarea: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //Exportar
    @FXML
    private void handleExportEverythingToPDF() {
        User currentUser = Session.getUser();
        if (currentUser == null) return;

        try {
            // Obtener todas las listas del usuario
            List<TaskList> allLists = taskListDAO.getByUserId(currentUser.getId());

            if (allLists.isEmpty()) {
                showWarning("No hay listas para exportar");
                return;
            }

            // Crear PDF combinado
            String fileName = String.format("PRIORIZA_COMPLETO_%s.pdf",
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            String filePath = System.getProperty("user.home") + "/Downloads/" + fileName;

            PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Encabezado general
            document.add(new Paragraph("PRIORIZA")
                    .setFontSize(24)
                    .setBold()
                    .setFontColor(ColorConstants.BLUE));

            document.add(new Paragraph("Exportación completa - Usuario: " + currentUser.getName())
                    .setFontSize(14));

            document.add(new Paragraph("Fecha: " + LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")))
                    .setFontSize(12)
                    .setFontColor(ColorConstants.GRAY));

            document.add(new Paragraph("\n"));

            int totalLists = 0;
            int totalTasks = 0;
            int totalSubtasks = 0;

            // Exportar cada lista
            for (TaskList list : allLists) {
                List<Task> tasks = taskDAO.getByTaskListId(list.getId());

                // Cargar subtareas para cada tarea
                for (Task task : tasks) {
                    task.setSubTasks(subTaskDAO.getByTaskId(task.getId()));
                    totalSubtasks += task.getSubTasks().size();
                }

                totalLists++;
                totalTasks += tasks.size();

                // Título de la lista
                document.add(new Paragraph("LISTA: " + list.getName())
                        .setFontSize(16)
                        .setBold()
                        .setFontColor(ColorConstants.DARK_GRAY));

                // Tareas de la lista
                for (Task task : tasks) {
                    document.add(createTaskCard(task));
                }
                document.add(new Paragraph("\n"));
            }

            // Resumen final
            document.add(new Paragraph("RESUMEN TOTAL:")
                    .setFontSize(14)
                    .setBold());
            document.add(new Paragraph("Listas: " + totalLists + " | Tareas: " + totalTasks + " | Subtareas: " + totalSubtasks)
                    .setFontSize(12));

            document.close();

            showSuccess("Exportación completa guardada en:\n" + filePath);

            // Preguntar si quiere abrir la carpeta
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Exportación completada");
            confirm.setHeaderText("¿Deseas abrir la carpeta?");

            ButtonType btnOpen = new ButtonType("Abrir carpeta");
            ButtonType btnClose = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirm.getButtonTypes().setAll(btnOpen, btnClose);

            confirm.showAndWait().ifPresent(response -> {
                if (response == btnOpen) {
                    try {
                        Desktop.getDesktop().open(new File(filePath).getParentFile());
                    } catch (Exception e) {
                        showError("No se pudo abrir la carpeta");
                    }
                }
            });

        } catch (Exception e) {
            showError("Error en exportación completa: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //compartir lista
    @FXML
    private void handleShareList() {
        TaskList selectedList = taskListView.getSelectionModel().getSelectedItem();

        if (selectedList == null) {
            showWarning("Selecciona una lista para compartir");
            return;
        }

        try {
            List<Task> tasks = taskDAO.getByTaskListId(selectedList.getId());
            for (Task task : tasks) {
                task.setSubTasks(subTaskDAO.getByTaskId(task.getId()));
            }

            // Preguntar metodo de compartición
            ChoiceDialog<String> dialog = new ChoiceDialog<>("WhatsApp",
                    "WhatsApp", "Email", "Solo guardar PDF");
            dialog.setTitle("Compartir lista");
            dialog.setHeaderText("¿Cómo quieres compartir la lista?");
            dialog.setContentText("Método:");

            dialog.showAndWait().ifPresent(method -> {
                switch (method) {
                    case "WhatsApp":
                        shareService.shareViaWhatsApp(selectedList, tasks, Session.getUser());
                        break;
                    case "Email":
                        shareService.shareViaEmail(selectedList, tasks, Session.getUser());
                        break;
                    case "Solo guardar PDF":
                        String pdfPath = exportTaskListToPDF(selectedList, tasks, Session.getUser());
                        if (pdfPath != null) {
                            showSuccess("PDF guardado en:\n" + pdfPath);
                            try {
                                Desktop.getDesktop().open(new File(pdfPath).getParentFile());
                            } catch (Exception e) {
                                // Ignorar
                            }
                        }
                        break;
                }
            });

        } catch (Exception e) {
            showError("Error al compartir lista: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Compartir tarea seleccionada
    @FXML
    private void handleShareTask() {
        Task selectedTask = taskTableView.getSelectionModel().getSelectedItem();

        if (selectedTask == null) {
            showWarning("Selecciona una tarea para compartir");
            return;
        }

        // Cargar subtareas
        selectedTask.setSubTasks(subTaskDAO.getByTaskId(selectedTask.getId()));

        // Mostrar opciones de compartir
        Alert choice = new Alert(Alert.AlertType.CONFIRMATION);
        choice.setTitle("Compartir tarea");
        choice.setHeaderText("¿Cómo quieres compartir?");

        ButtonType btnWhatsApp = new ButtonType("WhatsApp");
        ButtonType btnEmail = new ButtonType("Email");
        ButtonType btnPDF = new ButtonType("Solo PDF");
        ButtonType btnCancel = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        choice.getButtonTypes().setAll(btnWhatsApp, btnEmail, btnPDF, btnCancel);

        choice.showAndWait().ifPresent(response -> {
            if (response == btnWhatsApp) {
                shareService.shareTaskViaWhatsApp(selectedTask, Session.getUser());
            } else if (response == btnEmail) {
                // Crear lista temporal para email
                List<Task> singleTask = List.of(selectedTask);
                TaskList dummyList = new TaskList("Tarea: " + selectedTask.getTitle(),
                        Session.getUser().getId());
                shareService.shareViaEmail(dummyList, singleTask, Session.getUser());
            } else if (response == btnPDF) {
                String pdfPath = pdfService.exportSingleTask(selectedTask, Session.getUser());
                if (pdfPath != null) {
                    showSuccess("PDF guardado en:\n" + pdfPath);
                }
            }
        });
    }
    @FXML
    private void handleShareSubtask() {
        SubTask selectedSubtask = subTaskView.getSelectionModel().getSelectedItem();

        if (selectedSubtask == null) {
            showWarning("Selecciona una subtarea para compartir");
            return;
        }

        try {
            // Crear tarea temporal para la subtarea
            Task tempTask = new Task();
            tempTask.setTitle("Subtarea: " + selectedSubtask.getTitle());
            tempTask.setDescription(selectedSubtask.getTitle());
            tempTask.setDueDate(selectedSubtask.getDueDate());
            tempTask.setImportant(selectedSubtask.isImportant());
            tempTask.setStatus(TaskStatus.PENDIENTE);

            List<SubTask> subtasks = new ArrayList<>();
            subtasks.add(selectedSubtask);
            tempTask.setSubTasks(subtasks);

            // Preguntar metodo de compartición
            ChoiceDialog<String> dialog = new ChoiceDialog<>("WhatsApp",
                    "WhatsApp", "Email", "Solo guardar PDF");
            dialog.setTitle("Compartir subtarea");
            dialog.setHeaderText("¿Cómo quieres compartir la subtarea?");
            dialog.setContentText("Método:");

            dialog.showAndWait().ifPresent(method -> {
                switch (method) {
                    case "WhatsApp":
                        shareService.shareTaskViaWhatsApp(tempTask, Session.getUser());
                        break;
                    case "Email":
                        List<Task> singleTask = List.of(tempTask);
                        TaskList dummyList = new TaskList("Subtarea", Session.getUser().getId());
                        shareService.shareViaEmail(dummyList, singleTask, Session.getUser());
                        break;
                    case "Solo guardar PDF":
                        String pdfPath = exportSingleTaskToPDF(tempTask, Session.getUser());
                        if (pdfPath != null) {
                            showSuccess("PDF guardado en:\n" + pdfPath);
                            try {
                                Desktop.getDesktop().open(new File(pdfPath).getParentFile());
                            } catch (Exception e) {
                                // Ignorar
                            }
                        }
                        break;
                }
            });

        } catch (Exception e) {
            showError("Error al compartir subtarea: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //MÉTODOS AUXILIARES DE EXPORTACIÓN PDF

    //Exporta una lista de tareas a PDF

    private String exportTaskListToPDF(TaskList taskList, List<Task> tasks, User user) {
        String fileName = String.format("PRIORIZA_%s_%s.pdf",
                taskList.getName().replace(" ", "_"),
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        String filePath = System.getProperty("user.home") + "/Downloads/" + fileName;

        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Encabezado
            document.add(new Paragraph("PRIORIZA")
                    .setFontSize(24)
                    .setBold()
                    .setFontColor(ColorConstants.BLUE));

            document.add(new Paragraph("Gestor Inteligente de Tareas")
                    .setFontSize(12)
                    .setFontColor(ColorConstants.GRAY));

            document.add(new Paragraph("\n"));

            // Información del usuario y lista
            Table infoTable = new Table(2);
            infoTable.setWidth(UnitValue.createPercentValue(100));

            infoTable.addCell(createCell("Usuario:", true));
            infoTable.addCell(createCell(user.getName(), false));

            infoTable.addCell(createCell("Lista:", true));
            infoTable.addCell(createCell(taskList.getName(), false));

            infoTable.addCell(createCell("Fecha exportación:", true));
            infoTable.addCell(createCell(
                    LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")), false));

            document.add(infoTable);
            document.add(new Paragraph("\n"));

            // Tareas
            for (Task task : tasks) {
                document.add(createTaskCard(task));
                document.add(new Paragraph("\n"));
            }

            // Resumen
            document.add(createSummary(tasks));

            // Pie de página
            document.add(new Paragraph("\n"));
            Paragraph footer = new Paragraph(
                    "Documento generado automáticamente por PRIORIZA - " +
                            LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")))
                    .setFontSize(8)
                    .setFontColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(footer);

            document.close();
            System.out.println("PDF exportado: " + filePath);
            return filePath;

        } catch (Exception e) {
            System.err.println("Error exportando PDF: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    //Exporta una tarea específica a PDF
    private String exportSingleTaskToPDF(Task task, User user) {
        String fileName = String.format("TAREA_%s_%s.pdf",
                task.getTitle().replace(" ", "_"),
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        String filePath = System.getProperty("user.home") + "/Downloads/" + fileName;

        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("PRIORIZA")
                    .setFontSize(24)
                    .setBold()
                    .setFontColor(ColorConstants.BLUE));

            document.add(createTaskCard(task));

            document.add(new Paragraph("\n"));
            document.add(new Paragraph(
                    "Exportado por: " + user.getName() + " - " +
                            LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")))
                    .setFontSize(10)
                    .setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.close();
            return filePath;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //Crea una tarjeta visual para una tarea en el PDF

    //crea celda para tabla PDF
    private Cell createCell(String text, boolean isHeader) {
        Cell cell = new Cell();
        cell.add(new Paragraph(text));

        if (isHeader) {
            cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            cell.setBold();
        }
        cell.setPadding(5);
        cell.setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));

        return cell;
    }

    //crea tarjeta visual para tareas en PDF
    private Div createTaskCard(Task task) {
        Div card = new Div();
        card.setBackgroundColor(ColorConstants.WHITE);
        card.setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1));
        card.setPadding(10);
        card.setMarginBottom(10);

        // Título con prioridad
        String priorityColor = getPriorityColor(task.getPriority());

        Paragraph title = new Paragraph(task.getTitle())
                .setFontSize(14)
                .setBold()
                .setFontColor(getColorFromHex(priorityColor));
        card.add(title);

        // Descripción
        if (task.getDescription() != null && !task.getDescription().isEmpty()) {
            card.add(new Paragraph("Descripción: " + task.getDescription())
                    .setFontSize(11)
                    .setFontColor(ColorConstants.DARK_GRAY));
        }

        // Metadatos (tabla)
        Table metaTable = new Table(2);
        metaTable.setWidth(UnitValue.createPercentValue(100));

        // Prioridad
        metaTable.addCell(createCell("Prioridad:", true));
        metaTable.addCell(createCell(task.getPriority().toString(), false));

        // Fecha límite
        if (task.getDueDate() != null) {
            metaTable.addCell(createCell("Fecha límite:", true));
            metaTable.addCell(createCell(
                    task.getDueDate().format(DateTimeFormatter.ofPattern("ddMMyyyy")), false));
        }

        // Estado
        metaTable.addCell(createCell("Estado:", true));
        String estadoColor = getStatusColor(task.getStatus());
        Cell estadoCell = createCell(task.getStatus().toString(), false);
        estadoCell.setFontColor(getColorFromHex(estadoColor));
        metaTable.addCell(estadoCell);

        // Importante
        if (task.isImportant()) {
            metaTable.addCell(createCell("Importante:", true));
            metaTable.addCell(createCell("SÍ", false));
        }

        card.add(metaTable);

        // Subtareas
        if (task.getSubTasks() != null && !task.getSubTasks().isEmpty()) {
            card.add(new Paragraph("Subtareas:")
                    .setFontSize(12)
                    .setBold());

            for (SubTask sub : task.getSubTasks()) {
                String estado = sub.getSubTaskStatus() == SubTaskStatus.COMPLETA ? "[X]" : "[ ]";
                String subText = String.format("   %s %s", estado, sub.getTitle());

                // USO DEL METODO AUXILIAR PARA TEXTO COMPLETADO
                if (sub.getSubTaskStatus() == SubTaskStatus.COMPLETA) {
                    // Subtarea completada - con efecto tachado simulado
                    Paragraph subPara = createStrikethroughText(subText);
                    card.add(subPara);
                } else {
                    // Subtarea pendiente - texto normal
                    Paragraph subPara = new Paragraph(subText)
                            .setFontSize(10);
                    card.add(subPara);
                }

                // Fecha de subtarea si existe (solo para no completadas)
                if (sub.getDueDate() != null && sub.getSubTaskStatus() != SubTaskStatus.COMPLETA) {
                    card.add(new Paragraph(
                            "      Fecha: " + sub.getDueDate().format(DateTimeFormatter.ofPattern("ddMMyyyy")))
                            .setFontSize(9)
                            .setFontColor(ColorConstants.GRAY));
                }
            }
        }

        // Subtareas pendientes
        long pendingSubtasks = task.getSubTasks() != null ?
                task.getSubTasks().stream()
                        .filter(s -> s.getSubTaskStatus() != SubTaskStatus.COMPLETA)
                        .count() : 0;

        if (pendingSubtasks > 0) {
            card.add(new Paragraph(
                    pendingSubtasks + " subtareas pendientes")
                    .setFontSize(10)
                    .setFontColor(ColorConstants.ORANGE));
        }

        return card;
    }
    //crea un párrafo con texto tachado (simulado) para createTaskCard
    private Paragraph createStrikethroughText(String text) {
        Paragraph p = new Paragraph(text);
        p.setFontColor(ColorConstants.GRAY);
        p.setUnderline(0.5f, -3f); // Grosor 0.5, posición -3 (por debajo del texto)
        return p;
    }

    //crea resumen de tareas
    private Paragraph createSummary(List<Task> tasks) {
        long total = tasks.size();
        long completed = tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETA)
                .count();
        long pending = tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.PENDIENTE ||
                        t.getStatus() == TaskStatus.EN_PROGRESO)
                .count();
        long urgent = tasks.stream()
                .filter(t -> t.getPriority() == Priority.URGENTE)
                .count();

        Paragraph summary = new Paragraph()
                .setFontSize(12)
                .setBold();

        summary.add("RESUMEN:\n");
        summary.add(String.format("Total tareas: %d\n", total));
        summary.add(String.format("Completadas: %d\n", completed));
        summary.add(String.format("Pendientes: %d\n", pending));
        summary.add(String.format("Urgentes: %d", urgent));

        return summary;
    }

    //color segun proioridad
    private String getPriorityColor(Priority priority) {
        if (priority == null) return "#000000";
        return switch(priority) {
            case URGENTE -> "#ff0000";    // Rojo
            case ALTA -> "#ff9900";       // Naranja
            case MEDIA -> "#00cc00";       // Verde
            case BAJA -> "#666666";        // Gris
        };
    }

    //color segun estado
    private String getStatusColor(TaskStatus status) {
        if (status == null) return "#000000";
        return switch(status) {
            case PENDIENTE -> "#ff9900";      // Naranja
            case EN_PROGRESO -> "#0066ff";     // Azul
            case COMPLETA -> "#00cc00";        // Verde
            case CANCELADA -> "#666666";       // Gris
        };
    }

   //de hex a iText (colores)
    private DeviceRgb getColorFromHex(String hex) {
        try {
            int r = Integer.valueOf(hex.substring(1, 3), 16);
            int g = Integer.valueOf(hex.substring(3, 5), 16);
            int b = Integer.valueOf(hex.substring(5, 7), 16);
            return new DeviceRgb(r, g, b);
        } catch (Exception e) {
            return new DeviceRgb(0, 0, 0);
        }
    }


    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
}

