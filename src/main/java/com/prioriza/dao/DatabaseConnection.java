package com.prioriza.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            System.getProperty("db.url", "jdbc:sqlite:prioriza.db");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
