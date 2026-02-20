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
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardController {

    //Servicios
    private final TaskService taskService = new TaskService();
    private final SubTaskService subTaskService = new SubTaskService();

    @FXML private TabPane tabPane;

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

    //Elementos FXML - Calendario
    @FXML private Tab calendarTab;
    @FXML private GridPane calendarGrid;
    @FXML private Label monthYearLabel;
    @FXML private Button prevMonthButton;
    @FXML private Button nextMonthButton;
    @FXML private ListView<Task> selectedDayTasksList;
    @FXML private Label selectedDayLabel;
    @FXML private Label selectedDayCountLabel;

    private User currentUser;
    private YearMonth  currentYearMonth;
    private Map<LocalDate, List<Task>> tasksByDate;
    private LocalDate selectedDate;

    //Initialize
    @FXML
    public void initialize(){
        //1. obtener usuario actual
        currentUser = Session.getUser();
        if (currentUser == null){
            AlertUtil.showError("Error", "No hay usuario autenticado");
            return;
        }
        //2. Mostrar fecha con formato bonito y fecha actual
        dateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy")));

        // Configurar las celdas de las listas
        configureListCells();

        // Cargar datos
        loadDashboardData();

        // Cargar estadísticas
        loadStatistics();

        // Inicializar calendario
        initializeCalendar();

        // Habilitar pestañas
        statsTab.setDisable(false);
        calendarTab.setDisable(false);
    }

    //Selecciona una pestaña específica al abrir el dashboard
    //@param index Índice de la pestaña (0: Resumen, 1: Estadísticas, 2: Calendario)

    public void selectTab(int index) {
        if (tabPane != null && index >= 0 && index < tabPane.getTabs().size()) {
            tabPane.getSelectionModel().select(index);
            System.out.println("Pestaña seleccionada: " + index);
        } else {
            System.err.println("Error: No se pudo seleccionar la pestaña " + index);
        }
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
    // ============= MÉTODOS PARA Estadísticas =============

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
    // ============= MÉTODOS PARA Calendario =============

    private void initializeCalendar() {
        currentYearMonth = YearMonth.now();
        tasksByDate = loadTasksByDate();
        selectedDate = LocalDate.now();

        updateCalendarHeader();
        buildCalendar();

        // Configurar botones de navegación
        prevMonthButton.setOnAction(e -> navigateMonth(-1));
        nextMonthButton.setOnAction(e -> navigateMonth(1));
    }

    private Map<LocalDate, List<Task>> loadTasksByDate() {
        try {
            List<Task> allTasks = taskService.getTasksByUserId(currentUser.getId());

            return allTasks.stream()
                    .filter(t -> t.getDueDateTime() != null)
                    .filter(t -> t.getStatus() != TaskStatus.COMPLETA)
                    .collect(Collectors.groupingBy(
                            t -> t.getDueDateTime().toLocalDate()
                    ));
        } catch (Exception e) {
            AlertUtil.showError("Error", "No se pudieron cargar las tareas para el calendario");
            e.printStackTrace();
            return Map.of();
        }
    }

    private void updateCalendarHeader() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        monthYearLabel.setText(currentYearMonth.format(formatter));
    }

    private void navigateMonth(int delta) {
        currentYearMonth = currentYearMonth.plusMonths(delta);
        updateCalendarHeader();
        buildCalendar();
    }

    private void buildCalendar() {
        calendarGrid.getChildren().clear();

        // Días de la semana
        String[] dayNames = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(dayNames[i]);
            dayLabel.getStyleClass().add("calendar-weekday");
            calendarGrid.add(dayLabel, i, 0);
        }

        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() - 1; // 0 = Lunes

        int daysInMonth = currentYearMonth.lengthOfMonth();
        LocalDate today = LocalDate.now();

        // Rellenar días del mes
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentYearMonth.atDay(day);
            int row = (day + dayOfWeek - 1) / 7 + 1;
            int col = (day + dayOfWeek - 1) % 7;

            VBox dayCell = createDayCell(date, today);
            calendarGrid.add(dayCell, col, row);
        }
    }

    private VBox createDayCell(LocalDate date, LocalDate today) {
        VBox cell = new VBox(5);
        cell.getStyleClass().add("calendar-cell");
        cell.setPrefHeight(80);
        cell.setPrefWidth(100);

        // Número del día
        Label dayNumber = new Label(String.valueOf(date.getDayOfMonth()));
        dayNumber.getStyleClass().add("calendar-day-number");

        // Marcador si es hoy
        if (date.equals(today)) {
            cell.getStyleClass().add("calendar-cell-today");
        }

        // Tareas del día
        List<Task> dayTasks = tasksByDate.getOrDefault(date, List.of());
        long urgentCount = dayTasks.stream()
                .filter(t -> t.getPriority() == Priority.URGENTE)
                .count();

        if (!dayTasks.isEmpty()) {
            // Indicador de tareas
            Label tasksIndicator = new Label(dayTasks.size() + " tareas");
            tasksIndicator.getStyleClass().add("calendar-tasks-indicator");

            if (urgentCount > 0) {
                tasksIndicator.setStyle("-fx-background-color: #dc3545;");
                cell.getStyleClass().add("calendar-cell-urgent");
            } else {
                tasksIndicator.setStyle("-fx-background-color: #ffc107;");
                cell.getStyleClass().add("calendar-cell-has-tasks");
            }

            cell.getChildren().addAll(dayNumber, tasksIndicator);

            // Tooltip con detalles
            Tooltip tooltip = new Tooltip();
            tooltip.setText(buildTooltipText(date, dayTasks));
            Tooltip.install(cell, tooltip);
        } else {
            cell.getChildren().add(dayNumber);
        }

        // Evento de clic para ver tareas del día
        cell.setOnMouseClicked(e -> showDayTasks(date, dayTasks));

        return cell;
    }

    private String buildTooltipText(LocalDate date, List<Task> tasks) {
        StringBuilder sb = new StringBuilder();
        sb.append(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
        sb.append("Tareas: ").append(tasks.size()).append("\n");

        long urgent = tasks.stream().filter(t -> t.getPriority() == Priority.URGENTE).count();
        if (urgent > 0) {
            sb.append("URGENTES: ").append(urgent).append("\n");
        }

        sb.append("\n");
        for (Task task : tasks) {
            sb.append("• ").append(task.getTitle());
            if (task.getPriority() == Priority.URGENTE) {
                sb.append(" [URGENTE]");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private void showDayTasks(LocalDate date, List<Task> tasks) {
        selectedDate = date;
        selectedDayLabel.setText("Tareas del " +
                date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        selectedDayCountLabel.setText(tasks.size() + " tareas");

        ObservableList<Task> taskList = FXCollections.observableArrayList(tasks);
        selectedDayTasksList.setItems(taskList);
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) dateLabel.getScene().getWindow();
        stage.close();
    }

}
