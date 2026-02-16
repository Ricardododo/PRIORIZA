package com.prioriza.controller;

import com.prioriza.dao.TaskListDAO;
import com.prioriza.model.User;
import com.prioriza.service.TaskListService;
import com.prioriza.service.UserService;
import com.prioriza.util.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;



public class RegisterController {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;

    private final UserService userService = new UserService();
    private final TaskListService taskListService = new TaskListService();

    //metodo cuando se hace click al registrarse
    @FXML
    private void handleRegister(){

        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        //validar campos vacíos
        if(username == null || username.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                password == null || password.isEmpty() ||
                confirm == null || confirm.isEmpty()){

            AlertUtil.showWarning("Atención", "Todos los campos son obligatorios");
            return;
        }
        //validar email básico
        if(!email.contains("@") || !email.contains(".")){
            AlertUtil.showError("Error", "Introduce un correo electrónico válido");
            return;
        }
        //validar contraseñas
        if(!password.equals(confirm)){
            AlertUtil.showError("Error", "Las contraseñas no coinciden.");
            return;
        }

        try{
            User user = userService.registerUser(username.trim(), email.trim(), password);
            taskListService.createDefaultLists(user.getId());

            AlertUtil.showInfo("Información",
                    "Usuario registrado correctamente.\nYa puedes iniciar sesión");

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            AlertUtil.showError("Error", "No se pudo registrar el usuario.\nPuede que el nombre o email ya existan.");
            e.printStackTrace();
        }

    }

    //metodo volver a login
    @FXML
    private void handleBackToLogin(){
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
}
