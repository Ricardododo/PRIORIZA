package com.prioriza.controller;

import com.prioriza.model.SubTask;
import com.prioriza.model.SubTaskStatus;
import com.prioriza.model.TaskStatus;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SubTaskFormController {

    @FXML
    private TextField titleField;
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
    }

    @FXML
    private void handleSave() {

        String title = titleField.getText();

        if (title == null || title.trim().isEmpty()) {
            showError("El título no puede estar vacío");
            return;
        }
        if(editMode){
            subTaskToEdit.setTitle(title);
            subTaskToEdit.setSubTaskStatus(statusChoiceBox.getValue());
            result = subTaskToEdit;
        }else{
            SubTask subtask = new SubTask();
            subtask.setTitle(title);
            subtask.setSubTaskStatus(statusChoiceBox.getValue());
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
        statusChoiceBox.setValue(selectedSubTask.getSubTaskStatus());
    }
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
