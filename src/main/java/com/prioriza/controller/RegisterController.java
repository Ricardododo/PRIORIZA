package com.prioriza.controller;

import com.prioriza.dao.UserDAO;
import com.prioriza.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

    private final UserDAO userDAO = new UserDAO();

    //metodo cuando se hace click al registrarse
    @FXML
    private void handleRegister(){

        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        //validaciones
        if(username.isEmpty() || email.isEmpty() || password.isEmpty()){
            showAlert("Todos los campos son obligatorios");
            return;
        }

        //crear usuario
        User user = new User();
        user.setName(username);
        user.setEmail(email);
        user.setPassword(password);

        //guardar usuario
        userDAO.addUser(user);

        showAlert("Usuario registrado correctamente");

        //cerrar ventana y vovler a login
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    //metodo volver a login
    @FXML
    private void handleBackToLogin(){
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
