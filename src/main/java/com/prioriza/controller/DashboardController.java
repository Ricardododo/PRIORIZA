package com.prioriza.controller;

import com.prioriza.model.Priority;
import com.prioriza.model.Task;
import com.prioriza.model.TaskStatus;
import com.prioriza.service.TaskService;
import com.prioriza.service.SubTaskService;
import com.prioriza.model.User;
import com.prioriza.session.Session;
import com.prioriza.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardController {

    //Servicios
    private final TaskService taskService = new TaskService();
    private final SubTaskService subTaskService = new SubTaskService();

    //Elementos FXML - Resumen diario
    @FXML private Label dateLabel;
    @FXML private Label todayTasksLabel;
    @FXML private ListView<Task> todayTasksList;
    @FXML private Label tomorrowTasksLabel;
    @FXML private ListView<Task> tomorrowTasksList;
    @FXML private Label weekTasksLabel;
    @FXML private ListView<Task> weekTasksList;

    //Elementos FXML - Estadísticas
    @FXML private PieChart priorityChart;
    @FXML private Label totalTasksLabel;
    @FXML private Label completedTasksLabel;
    @FXML private Label pendingTasksLabel;
    @FXML private Label urgentTasksLabel;
    @FXML private Tab statsTab;  // Para habilitar la pestaña

    private User currentUser;

    //Initialize
    @FXML
    public void initialize(){
        //1. obtener usuario actual
        currentUser = Session.getUser();
        if (currentUser == null){
            AlertUtil.showError("Error", "No hay usuario autenticado");
            return;
        }
        //2. Mostrar fecha con formato bonito
        dateLabel.setText(LocalDate.now()
                .format(DateTimeFormatter.ofPattern("EEEE, dd 'de ' MMMM 'de' yyyy")));

        //3. Configurar cómo se ven las tareas en las listas
        configureListCells();

        //4. cargar datos reales
        loadDashboardData();

        //5. cargar estadísticas
        loadStatistics();

        //6. Habilitar la pestaña de estadística
        statsTab.setDisable(false);
    }

    private void loadDashboardData() {
        try {
            List<Task> allTasks = taskService.getTasksByUserId(currentUser.getId());
            LocalDate today = LocalDate.now();

            // Filtrar tareas no completadas
            List<Task> pendingTasks = allTasks.stream()
                    .filter(t -> t.getStatus() != TaskStatus.COMPLETA)
                    .filter(t -> t.getStatus() != TaskStatus.CANCELADA)
                    .collect(Collectors.toList());

            // Tareas de HOY
            List<Task> todayTasks = pendingTasks.stream()
                    .filter(t -> t.getDueDateTime() != null)
                    .filter(t -> t.getDueDateTime().toLocalDate().equals(today))
                    .sorted((t1, t2) -> {
                        if (t1.getDueDateTime() == null) return 1;
                        if (t2.getDueDateTime() == null) return -1;
                        return t1.getDueDateTime().compareTo(t2.getDueDateTime());
                    })
                    .collect(Collectors.toList());

            // Tareas de MAÑANA
            List<Task> tomorrowTasks = pendingTasks.stream()
                    .filter(t -> t.getDueDateTime() != null)
                    .filter(t -> t.getDueDateTime().toLocalDate().equals(today.plusDays(1)))
                    .sorted((t1, t2) -> {
                        if (t1.getDueDateTime() == null) return 1;
                        if (t2.getDueDateTime() == null) return -1;
                        return t1.getDueDateTime().compareTo(t2.getDueDateTime());
                    })
                    .collect(Collectors.toList());

            // Tareas de los PRÓXIMOS 7 DÍAS (excluyendo hoy y mañana)
            List<Task> weekTasks = pendingTasks.stream()
                    .filter(t -> t.getDueDateTime() != null)
                    .filter(t -> {
                        LocalDate dueDate = t.getDueDateTime().toLocalDate();
                        return !dueDate.equals(today) &&
                                !dueDate.equals(today.plusDays(1)) &&
                                dueDate.isAfter(today) &&
                                dueDate.isBefore(today.plusDays(8));
                    })
                    .sorted((t1, t2) -> {
                        if (t1.getDueDateTime() == null) return 1;
                        if (t2.getDueDateTime() == null) return -1;
                        return t1.getDueDateTime().compareTo(t2.getDueDateTime());
                    })
                    .collect(Collectors.toList());

            // Actualizar UI
            todayTasksLabel.setText(todayTasks.size() + " tareas para hoy");
            todayTasksList.setItems(FXCollections.observableArrayList(todayTasks));

            tomorrowTasksLabel.setText(tomorrowTasks.size() + " tareas para mañana");
            tomorrowTasksList.setItems(FXCollections.observableArrayList(tomorrowTasks));

            weekTasksLabel.setText(weekTasks.size() + " tareas en los próximos 7 días");
            weekTasksList.setItems(FXCollections.observableArrayList(weekTasks));

        } catch (Exception e) {
            AlertUtil.showError("Error", "No se pudieron cargar los datos del dashboard");
            e.printStackTrace();
        }
    }

    private void configureListCells() {
        //para cada lista, definir cómo mostrar cada tarea
        ListView<?>[] lists = {todayTasksList, tomorrowTasksList, weekTasksList};
        for (ListView<Task> list : (ListView<Task>[]) lists) {
            list.setCellFactory(param -> new ListCell<Task>() {
                @Override
                protected void updateItem(Task task, boolean empty) {
                    super.updateItem(task, empty);

                    if (empty || task == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        // Construir texto manualmente
                        String hora = task.getDueDateTime() != null ?
                                " [" + task.getDueDateTime().format(DateTimeFormatter.ofPattern("HH:mm")) + "]" : "";
                        String prioridad = " [" + task.getPriority() + "]";

                        setText("• " + task.getTitle() + prioridad + hora);

                        switch (task.getPriority()) {
                            case URGENTE -> setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                            case ALTA -> setStyle("-fx-text-fill: #fd7e14; -fx-font-weight: bold;");
                            case MEDIA -> setStyle("-fx-text-fill: #ffc107;");
                            case BAJA -> setStyle("-fx-text-fill: #6c757d;");
                        }
                    }
                }
            });
        }
    }
    // Métodos para Estadísticas
    //Cargar las estadisticas y el gráfico de prioridades
    private void loadStatistics() {
        try {
            List<Task> allTasks = taskService.getTasksByUserId(currentUser.getId());

            // Contar tareas por prioridad
            long urgentCount = allTasks.stream()
                    .filter(t -> t.getPriority() == Priority.URGENTE)
                    .count();

            long altaCount = allTasks.stream()
                    .filter(t -> t.getPriority() == Priority.ALTA)
                    .count();

            long mediaCount = allTasks.stream()
                    .filter(t -> t.getPriority() == Priority.MEDIA)
                    .count();

            long bajaCount = allTasks.stream()
                    .filter(t -> t.getPriority() == Priority.BAJA)
                    .count();

            // Contar tareas completadas vs pendientes
            long completedCount = allTasks.stream()
                    .filter(t -> t.getStatus() == TaskStatus.COMPLETA)
                    .count();

            long pendingCount = allTasks.stream()
                    .filter(t -> t.getStatus() != TaskStatus.COMPLETA &&
                            t.getStatus() != TaskStatus.CANCELADA)
                    .count();

            long totalCount = allTasks.size();

            // Actualizar etiquetas
            totalTasksLabel.setText("Total: " + totalCount);
            completedTasksLabel.setText("Completadas: " + completedCount);
            pendingTasksLabel.setText("Pendientes: " + pendingCount);
            urgentTasksLabel.setText("Urgentes: " + urgentCount);

            // Crear datos para el gráfico
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("URGENTE (" + urgentCount + ")", urgentCount),
                    new PieChart.Data("ALTA (" + altaCount + ")", altaCount),
                    new PieChart.Data("MEDIA (" + mediaCount + ")", mediaCount),
                    new PieChart.Data("BAJA (" + bajaCount + ")", bajaCount)
            );

            // Configurar gráfico
            priorityChart.setData(pieChartData);
            priorityChart.setTitle("Distribución por Prioridad");
            priorityChart.setClockwise(true);
            priorityChart.setLabelLineLength(10);
            priorityChart.setLabelsVisible(true);
            priorityChart.setLegendVisible(true);

        } catch (Exception e) {
            AlertUtil.showError("Error", "No se pudieron cargar las estadísticas");
            e.printStackTrace();
        }
    }


    @FXML
    private void handleClose() {
        Stage stage = (Stage) dateLabel.getScene().getWindow();
        stage.close();
    }

}
