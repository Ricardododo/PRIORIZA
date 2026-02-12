package com.prioriza.controller;

import com.prioriza.model.Priority;
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
        //valor por defecto
        statusChoiceBox.setValue(TaskStatus.PENDIENTE);
        //la fecha por defecto (hoy)
        datePicker.setValue(LocalDate.now());

        //checkBox por defecto: false
        importantCheckBox.setSelected(false);
    }

    //cuando se pulsa guardar - editar
    @FXML
    private void handleSave(){

        String title = titleField.getText();
        String description = descriptionField.getText();
        LocalDate date = datePicker.getValue();
        Priority priority = priorityChoiceBox.getValue();
        TaskStatus status = statusChoiceBox.getValue();
        boolean important = importantCheckBox.isSelected();

        //validación
        if(title == null || title.trim().isEmpty()){
            showError("El título no puede estar vacío");
            return;
        }

        if (priority == null){
            showError("Debe seleccionar una prioridad");
            return;
        }

        if(editMode){
            //editar existente
            taskToEdit.setTitle(title);
            taskToEdit.setDescription(description);
            taskToEdit.setDueDate(date);
            taskToEdit.setPriority(priority);
            taskToEdit.setStatus(status);
            taskToEdit.setImportant(important);
            taskResult = taskToEdit;
        }else{
            //crear nueva
            Task task = new Task();
            task.setTitle(title);
            task.setDescription(description);
            task.setDueDate(date);
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
        datePicker.setValue(selectedTask.getDueDate());
        priorityChoiceBox.setValue(selectedTask.getPriority());
        statusChoiceBox.setValue(selectedTask.getStatus());
        importantCheckBox.setSelected(selectedTask.isImportant());
    }


    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
