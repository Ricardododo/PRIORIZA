package com.prioriza.dao;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize(){
        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE
                );
                """;
        try(Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement()){

            stmt.execute(sql);
            System.out.println("Tabla USERS creada");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
