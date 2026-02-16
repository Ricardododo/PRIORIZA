package com.prioriza.controller;

import com.prioriza.model.Priority;
import com.prioriza.model.Task;
import com.prioriza.model.TaskStatus;
import com.prioriza.util.AlertUtil;
import com.prioriza.util.DateUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TaskFormController {

    @FXML
    private TextField titleField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField hourField;
    @FXML
    private ChoiceBox<Priority> priorityChoiceBox;
    @FXML
    private ChoiceBox<TaskStatus> statusChoiceBox;
    @FXML
    private CheckBox importantCheckBox;

    private Task taskResult; //Tarea creada
    private boolean editMode = false;
    private Task taskToEdit;

    @FXML
    public void initialize(){
        //cargar prioridades
        priorityChoiceBox.getItems().setAll(Priority.values());
        priorityChoiceBox.setValue(Priority.MEDIA); //valores por defecto

        //cargar estados
        statusChoiceBox.getItems().setAll(TaskStatus.values());
        statusChoiceBox.setValue(TaskStatus.PENDIENTE);

        //la fecha por defecto (hoy)
        datePicker.setValue(LocalDate.now());

        //hora por defecto 09:00
        hourField.setText("09:00");

        //checkBox por defecto: false
        importantCheckBox.setSelected(false);

        // Validación de hora mientras escribe
        hourField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$") && !newVal.isEmpty()) {
                hourField.setStyle("-fx-border-color: red;");
            } else {
                hourField.setStyle("");
            }
        });
    }

    //cuando se pulsa guardar - editar
    @FXML
    private void handleSave(){

        String title = titleField.getText();
        String description = descriptionField.getText();
        LocalDate date = datePicker.getValue();
        String hourText = hourField.getText().trim();
        Priority priority = priorityChoiceBox.getValue();
        TaskStatus status = statusChoiceBox.getValue();
        boolean important = importantCheckBox.isSelected();

        //validación
        if(title == null || title.trim().isEmpty()){
            AlertUtil.showError("Error", "El título no puede estar vacío");
            return;
        }

        if (priority == null){
            AlertUtil.showError("Error", "Debe seleccionar una prioridad");
            return;
        }
        // Validar fecha
        if (date == null) {
            AlertUtil.showError("Error", "Debe seleccionar una fecha");
            return;
        }

        // Validar y convertir hora usando DateUtil
        LocalDateTime dueDateTime = null;
        if (!hourText.isEmpty()) {
            try {
                // Usar DateUtil para parsear la hora
                LocalTime time = DateUtil.parseTime(hourText);
                dueDateTime = LocalDateTime.of(date, time);
            } catch (Exception e) {
                AlertUtil.showError("Error", "Formato de hora incorrecto. Use HH:mm (ej: 14:30)");
                return;
            }
        } else {
            // Si no hay hora, usar medianoche
            dueDateTime = date.atStartOfDay();
        }


        if(editMode){
            //editar existente
            taskToEdit.setTitle(title);
            taskToEdit.setDescription(description);
            taskToEdit.setDueDateTime(dueDateTime);
            taskToEdit.setPriority(priority);
            taskToEdit.setStatus(status);
            taskToEdit.setImportant(important);
            taskResult = taskToEdit;
        }else{
            //crear nueva
            Task task = new Task();
            task.setTitle(title);
            task.setDescription(description);
            task.setDueDateTime(dueDateTime);
            task.setPriority(priority);
            task.setStatus(status);
            task.setImportant(important);
            taskResult = task;
        }
        //cerrar ventana
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel(){
        taskResult = null;

        //cerrar ventana
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    //devolver la tarea creada al MainController
    public Task getTaskResult(){
        return taskResult;
    }

    //metodo editar tarea
    public void setTaskToEdit(Task selectedTask) {
        editMode = true;
        this.taskToEdit = selectedTask;
        this.taskResult = selectedTask;


        //cargar datos en el formulario
        titleField.setText(selectedTask.getTitle());
        descriptionField.setText(selectedTask.getDescription());

        // Usar DateUtil para descomponer la fecha+hora
        LocalDateTime dateTime = selectedTask.getDueDateTime();
        if (dateTime != null) {
            datePicker.setValue(dateTime.toLocalDate());
            hourField.setText(DateUtil.formatTime(dateTime)); // "HH:mm"
        } else {
            datePicker.setValue(null);
            hourField.clear();
        }

        priorityChoiceBox.setValue(selectedTask.getPriority());
        statusChoiceBox.setValue(selectedTask.getStatus());
        importantCheckBox.setSelected(selectedTask.isImportant());
    }
}
