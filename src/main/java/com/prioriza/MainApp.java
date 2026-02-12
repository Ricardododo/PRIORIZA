package com.prioriza;

import com.prioriza.dao.DatabaseConnection;
import com.prioriza.dao.DatabaseInitializer;
import com.prioriza.service.NotificationProcessor;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class MainApp extends Application {

    private NotificationProcessor notificationProcessor;

    @Override
    public void start(Stage stage) throws Exception{

        //1. Inicializar Base de Datos
        DatabaseInitializer.initialize();

        //2. Iniciar Sistema de Notificaciones
        notificationProcessor = new NotificationProcessor();
        notificationProcessor.start();

        //3. cargar UI
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/login-view.fxml")
        );
        Parent root = loader.load();
        Scene scene = new Scene(root);

        //CSS
        scene.getStylesheets().add(
                getClass().getResource("/css/styles.css").toExternalForm()
        );
        //Icono
        stage.getIcons().add(
                new Image(getClass().getResourceAsStream("/img/prioriza-icono.png"))
        );

        stage.setWidth(420);
        stage.setHeight(360);
        stage.setResizable(false);
        stage.setTitle("PRIORIZA");
        stage.setScene(scene);
        stage.show();

        //4. Detener notificaciones al cerrar
        stage.setOnCloseRequest(event -> {
            if (notificationProcessor != null){
                notificationProcessor.stop();
            }
        });
    }
    @Override
    public void stop()throws Exception{
        if (notificationProcessor != null){
            notificationProcessor.stop();
        }
        super.stop();
    }


    public static void main(String[] args) {
        launch();
    }
}
