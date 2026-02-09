package com.prioriza.controller;

import com.prioriza.model.SubTask;
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
    private boolean editMode = false;
    private Task taskToEdit;
    private SubTask subTaskToEdit;

    @FXML
    public void initialize(){

        //cargar estados posibles del enum
        statusChoiceBox.getItems().setAll(TaskStatus.values());
        //valor por defecto
        statusChoiceBox.setValue(TaskStatus.PENDIENTE);
        //la fecha por defecto
        datePicker.setValue(LocalDate.now());
    }

    //cuando se pulsa guardar - editar
    @FXML
    private void handleSave(){

        String title = titleField.getText();
        String description = descriptionField.getText();
        LocalDate date = datePicker.getValue();
        TaskStatus status = statusChoiceBox.getValue();

        //validación
        if(title == null || title.trim().isEmpty()){
            showError("El título no puede estar vacío");
            return;
        }

        if(editMode){
            //editar existente
            taskToEdit.setTitle(title);
            taskToEdit.setDescription(description);
            taskToEdit.setDueDate(date);
            taskToEdit.setStatus(status);
            taskResult = taskToEdit;
        }else{
            //crear nueva
            Task task = new Task();
            task.setTitle(title);
            task.setDescription(description);
            task.setDueDate(date);
            task.setStatus(status);
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
        datePicker.setValue(selectedTask.getDueDate());
        statusChoiceBox.setValue(selectedTask.getStatus());
    }


    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
