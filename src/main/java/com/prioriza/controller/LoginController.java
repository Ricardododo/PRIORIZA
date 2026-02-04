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

        User user = userDAO.login(username, password);

        if(user != null){

            Session.setUser(user);

            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/view/main-view.fxml")
                );

                Stage stage = (Stage) userField.getScene().getWindow();
                stage.setScene(new Scene(loader.load()));
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Usuario o contraseña incorrectos");
            alert.showAndWait();
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
            e.printStackTrace();
        }
    }

}
