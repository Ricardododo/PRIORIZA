package com.prioriza;

import com.prioriza.dao.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception{

        //crear las tablas al iniciar
        DatabaseInitializer.initialize();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/login-view.fxml")
        );

        Parent root = loader.load(); //linea donde se aplica el CSS

        Scene scene = new Scene(root);

        scene.getStylesheets().add(
                getClass().getResource("/css/styles.css").toExternalForm()
        );
        stage.getIcons().add(
                new Image(getClass().getResourceAsStream("/img/prioriza-icono.png"))
        );
        stage.setWidth(420);
        stage.setHeight(360);
        stage.setResizable(false);
        stage.setTitle("PRIORIZA");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
