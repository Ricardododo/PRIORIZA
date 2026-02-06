package com.prioriza.controller;

import com.prioriza.dao.UserDAO;
import com.prioriza.model.User;
import com.prioriza.session.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class LoginController {

    @FXML
    private TextField userField;
    @FXML
    private PasswordField passField;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin(){

        String username = userField.getText();
        String password = passField.getText();

        //validar campos vacíos
        if(username == null || username.trim().isEmpty() ||
            password == null || password.isEmpty()){

            showWarning("Introduce usuario y contraseña.");
            return;
        }

        try{
            User user = userDAO.login(username.trim(), password);

            if(user == null){
                showError("Usuario o contraseña incorrectos.");
                passField.clear();
                return;
            }

            //guardar sesión
            Session.setUser(user);

            //cargar vista principal
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/main-view.fxml")
            );

            Stage stage = (Stage) userField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.show();

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
