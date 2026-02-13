package com.prioriza.controller;

import com.prioriza.model.SubTask;
import com.prioriza.model.SubTaskStatus;
import com.prioriza.util.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

public class SubTaskFormController {

    @FXML
    private TextField titleField;
    @FXML
    private DatePicker dueDatePicker;
    @FXML
    private CheckBox importantCheckBox;
    @FXML
    private ChoiceBox<SubTaskStatus> statusChoiceBox;

    private SubTask subTaskToEdit;
    private SubTask result;
    private boolean editMode = false;

    @FXML
    public void initialize(){
        //carga enum en ChoiceBox
        statusChoiceBox.getItems().setAll(SubTaskStatus.values());
        //valor por defecto
        statusChoiceBox.setValue(SubTaskStatus.PENDIENTE);
        //fecha por defecto
        dueDatePicker.setValue(LocalDate.now());
        //checkBox por defecto: false
        importantCheckBox.setSelected(false);
    }

    @FXML
    private void handleSave() {

        String title = titleField.getText();
        LocalDate dueDate = dueDatePicker.getValue();
        boolean important = importantCheckBox.isSelected();
        SubTaskStatus status = statusChoiceBox.getValue();

        if (title == null || title.trim().isEmpty()) {
            AlertUtil.showError("Error", "El título no puede estar vacío");
            return;
        }

        if(editMode){
            subTaskToEdit.setTitle(title);
            subTaskToEdit.setDueDate(dueDate);
            subTaskToEdit.setImportant(important);
            subTaskToEdit.setSubTaskStatus(status);
            result = subTaskToEdit;
        }else{
            SubTask subtask = new SubTask();
            subtask.setTitle(title);
            subtask.setDueDate(dueDate);
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
        dueDatePicker.setValue(selectedSubTask.getDueDate());
        importantCheckBox.setSelected(selectedSubTask.isImportant());
        statusChoiceBox.setValue(selectedSubTask.getSubTaskStatus());
    }
}
