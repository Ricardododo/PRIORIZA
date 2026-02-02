package com.prioriza.controller;

import com.prioriza.model.Task;
import com.prioriza.model.TaskStatus;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class TaskFormController {

    @FXML
    private TextField titleField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ChoiceBox<TaskStatus> statusChoiceBox;

    private Task taskResult; //Tarea creada

    @FXML
    public void initialize(){

        //cargar estados posibles del enum
        statusChoiceBox.getItems().setAll(TaskStatus.values());
        //valor por defecto
        statusChoiceBox.setValue(TaskStatus.PENDING);
        //la fecha por defecto
        datePicker.setValue(LocalDate.now());
    }

    //cuando se pulsa guardar
    @FXML
    private void handleSave(){

        String title = titleField.getText();
        String description = descriptionField.getText();
        LocalDate date = datePicker.getValue();
        TaskStatus status = statusChoiceBox.getValue();

        //validación
        if(title.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("El título no puede estar vacío");
            alert.showAndWait();
            return;
        }

        //crear tarea
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(date);
        task.setStatus(status);

        taskResult = task;

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
}
