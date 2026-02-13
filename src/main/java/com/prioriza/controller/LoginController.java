package com.prioriza.controller;

import com.prioriza.dao.TaskListDAO;
import com.prioriza.dao.UserDAO;
import com.prioriza.model.User;
import com.prioriza.session.Session;
import com.prioriza.util.AlertUtil;
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
    public void initialize() {
        System.out.println("Inicializando LoginController...");

        javafx.application.Platform.runLater(() -> {
            try {
                Scene scene = emailField.getScene();
                if (scene != null) {
                    // Limpiar caché y forzar recarga
                    scene.getStylesheets().clear();
                    String css = getClass().getResource("/css/styles.css").toExternalForm();
                    scene.getStylesheets().add(css);

                    // Forzar re-aplicación de estilos
                    scene.getRoot().applyCss();
                    scene.getRoot().layout();

                    System.out.println("CSS cargado correctamente");
                    System.out.println("Ruta: " + css);
                } else {
                    System.out.println("Scene aún no disponible, se cargará después");
                }
            } catch (Exception e) {
                System.err.println("Error cargando CSS: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleLogin() {


        String email = emailField.getText();
        String password = passField.getText();

        //validar campos vacíos
        if(email == null || email.trim().isEmpty() ||
            password == null || password.isEmpty()){

            AlertUtil.showWarning("Atención", "Introduce email y contraseña.");
            return;
        }

        try{
            User user = userDAO.login(email.trim(), password);

            if(user == null){
                AlertUtil.showError("Error", "Email o contraseña incorrectos.");
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
            AlertUtil.showError("Error", "Error al intentar iniciar sesión.\nInténtalo de nuevo.");
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
            AlertUtil.showError("Error", "No se pudo abrir la ventana de registro.");
            e.printStackTrace();
        }
    }
}
