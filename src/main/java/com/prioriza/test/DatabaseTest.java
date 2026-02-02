package com.prioriza.test;

import com.prioriza.dao.DatabaseInitializer;

public class DatabaseTest {

    public static void main(String[] args) {
//        try (Connection conn = DatabaseConnection.getConnection()){
//            System.out.println("Conexión a SQLite correcta!");
//        } catch (Exception e) {
//           e.printStackTrace();
//        }

        DatabaseInitializer.initialize();


    }
}
