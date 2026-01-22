package com.prioriza.test;

import com.prioriza.dao.DatabaseConnection;

import java.sql.Connection;

public class MainTest {
    public static void main(String[] args) {
        try{
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null && !conn.isClosed()){
                System.out.println("Conexión SQLite OK");
            }
            conn.close();
        } catch (Exception e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }
}
