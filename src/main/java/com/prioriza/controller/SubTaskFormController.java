package com.prioriza.controller;

import com.prioriza.model.SubTask;
import com.prioriza.model.SubTaskStatus;
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

    private SubTask result;

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

        if (title.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("El título no puede estar vacío");
            alert.showAndWait();
            return;
        }
        //crear SubTask
        SubTask sub = new SubTask();
        sub.setTitle(title);

        //estado elegido
        sub.setSubTaskStatus(statusChoiceBox.getValue());

        result = sub;

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
}
