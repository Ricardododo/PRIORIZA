package com.prioriza;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception{

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/main-view.fxml")
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("PRIORIZA");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
