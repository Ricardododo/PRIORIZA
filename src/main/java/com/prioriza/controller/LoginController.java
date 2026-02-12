package com.prioriza.controller;

import com.prioriza.dao.TaskListDAO;
import com.prioriza.dao.UserDAO;
import com.prioriza.model.User;
import com.prioriza.session.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class LoginController {

    @FXML
    private TextField emailField; //cambio a emailField, más seguro
    @FXML
    private PasswordField passField;

    private final UserDAO userDAO = new UserDAO();
    private final TaskListDAO taskListDAO = new TaskListDAO();

    @FXML
    private void handleLogin() {


        String email = emailField.getText();
        String password = passField.getText();

        //validar campos vacíos
        if(email == null || email.trim().isEmpty() ||
            password == null || password.isEmpty()){

            showWarning("Introduce email y contraseña.");
            return;
        }

        try{
            User user = userDAO.login(email.trim(), password);

            if(user == null){
                showError("Email o contraseña incorrectos.");
                passField.clear();
                return;
            }

            //guardar sesión
            Session.setUser(user);

            //crear lista por defecto para nuevo usuario (TaskListDAO)
            taskListDAO.createDefaultListsForUser(user.getId());

            Stage stage = (Stage) emailField.getScene().getWindow();

            //cargar vista principal
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/main-view.fxml")
            );
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);

            //tamaño distinto para la ventana principal
            stage.setWidth(1200);
            stage.setHeight(800);
            stage.centerOnScreen();

        } catch (Exception e) {
            showError("Error al intentar iniciar sesión.\nInténtalo de nuevo.");
            e.printStackTrace();
        }
    }
    //metodo para abrir el registro
    @FXML
    private void handleOpenRegister(){
        try{
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/register-view.fxml")
            );

            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setTitle("Registro de Usuario");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            showError("No se pudo abrir la ventana de registro.");
            e.printStackTrace();
        }
    }
    //metodos alerta , mensajes
    private void showWarning(String message){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atención");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
