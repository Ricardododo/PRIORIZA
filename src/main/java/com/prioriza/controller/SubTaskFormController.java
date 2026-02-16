package com.prioriza.controller;

import com.prioriza.model.SubTask;
import com.prioriza.model.SubTaskStatus;
import com.prioriza.util.AlertUtil;
import com.prioriza.util.DateUtil;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SubTaskFormController {

    @FXML
    private TextField titleField;
    @FXML
    private DatePicker dueDatePicker;
    @FXML
    private TextField hourField;
    @FXML
    private CheckBox importantCheckBox;
    @FXML
    private ChoiceBox<SubTaskStatus> statusChoiceBox;

    private SubTask subTaskToEdit;
    private SubTask result;
    private boolean editMode = false;

    @FXML
    public void initialize(){
        // Carga enum en ChoiceBox
        statusChoiceBox.getItems().setAll(SubTaskStatus.values());
        statusChoiceBox.setValue(SubTaskStatus.PENDIENTE);

        // Fecha por defecto: hoy
        dueDatePicker.setValue(LocalDate.now());

        // Hora por defecto: 09:00
        hourField.setText("09:00");

        // CheckBox por defecto: false
        importantCheckBox.setSelected(false);

        // Validación de formato de hora
        hourField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$") && !newVal.isEmpty()) {
                hourField.setStyle("-fx-border-color: red;");
            } else {
                hourField.setStyle("");
            }
        });
    }

    @FXML
    private void handleSave() {

        String title = titleField.getText();
        LocalDate dueDate = dueDatePicker.getValue();
        String hourText = hourField.getText().trim();
        boolean important = importantCheckBox.isSelected();
        SubTaskStatus status = statusChoiceBox.getValue();

        if (title == null || title.trim().isEmpty()) {
            AlertUtil.showError("Error", "El título no puede estar vacío");
            return;
        }

        // Crear LocalDateTime combinando fecha y hora
        LocalDateTime dueDateTime;
        try {
            if (!hourText.isEmpty()) {
                LocalTime time = DateUtil.parseTime(hourText);
                dueDateTime = LocalDateTime.of(dueDate, time);
            } else {
                dueDateTime = dueDate.atStartOfDay(); // Si no hay hora, usar medianoche
            }
        } catch (Exception e) {
            AlertUtil.showError("Error", "Formato de hora incorrecto. Use HH:mm (ej: 14:30)");
            return;
        }

        if(editMode){
            subTaskToEdit.setTitle(title);
            subTaskToEdit.setDueDateTime(dueDateTime);
            subTaskToEdit.setImportant(important);
            subTaskToEdit.setSubTaskStatus(status);
            result = subTaskToEdit;
        }else{
            SubTask subtask = new SubTask();
            subtask.setTitle(title);
            subtask.setDueDateTime(dueDateTime);
            subtask.setImportant(important);
            subtask.setSubTaskStatus(status);
            result = subtask;
        }
        //cerrar el popup
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void handleCancel() {
        result = null;
        Stage stage = (Stage) titleField.getScene().getWindow();
        stage.close();
    }

    public SubTask getResult() {
        return result;
    }

    public void setSubTaskToEdit(SubTask selectedSubTask) {

        if(selectedSubTask == null) return;

        editMode = true;
        this.subTaskToEdit = selectedSubTask;
        this.result = selectedSubTask;

        titleField.setText(selectedSubTask.getTitle());

        // Usar DateUtil para descomponer fecha y hora
        LocalDateTime dateTime = selectedSubTask.getDueDateTime();
        if (dateTime != null) {
            dueDatePicker.setValue(dateTime.toLocalDate());
            hourField.setText(DateUtil.formatTime(dateTime));  // "HH:mm"
        } else {
            dueDatePicker.setValue(null);
            hourField.clear();
        }
        importantCheckBox.setSelected(selectedSubTask.isImportant());
        statusChoiceBox.setValue(selectedSubTask.getSubTaskStatus());
    }
}
