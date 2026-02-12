package com.prioriza;

import com.prioriza.dao.DatabaseConnection;
import com.prioriza.dao.DatabaseInitializer;
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
    //metodo para verificar la estructura
    private void verifyDatabaseStructure() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();

            System.out.println("VERIFICANDO ESTRUCTURA:");

            // Verificar tabla users
            var rs = meta.getColumns(null, null, "users", null);
            boolean hasUserRole = false;
            while (rs.next()) {
                String colName = rs.getString("COLUMN_NAME");
                if ("user_role".equals(colName)) hasUserRole = true;
                System.out.println("   - " + colName);
            }

            if (!hasUserRole) {
                System.err.println("ERROR: columna 'user_role' no encontrada!");
            } else {
                System.out.println("Estructura correcta");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
