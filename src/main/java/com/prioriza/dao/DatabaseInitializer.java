package com.prioriza.dao;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {


    public static void initialize(){

        try(Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement()){

            //crear tabla usuarios
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE
                );
                """);

            //crear tabla TaskList
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS task_list (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    user_id INTEGER NOT NULL,
                    FOREIGN KEY(user_id) REFERENCES user(id)
                );
                """);

            //crear tabla Task
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS task (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    description TEXT,
                    due_date TEXT,
                    priority TEXT, 
                    status TEXT, 
                    task_list_id INTEGER NOT NULL,
                    FOREIGN KEY(task_list_id) REFERENCES task_list(id)
                );
                """);

            //crear tabla SubTask
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS sub_task (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    complete INTEGER DEFAULT 0,
                    task_id INTEGER NOT NULL,
                    FOREIGN KEY(task_id) REFERENCES task(id)
                );
                """);

            System.out.println("Tablas creadas");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
