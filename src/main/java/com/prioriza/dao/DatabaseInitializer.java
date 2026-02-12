package com.prioriza.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        // 1. FUERZA BORRADO - Ignorar si no existe
        try {
            java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get("prioriza.db"));
            System.out.println("Base de datos eliminada");
        } catch (Exception e) {
            // Ignorar
        }

        // 2. NUEVA CONEXIÓN
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // 3. ACTIVAR FOREIGN KEYS
            stmt.execute("PRAGMA foreign_keys = ON;");
            System.out.println("Foreign keys activadas");

            // 4. IMPORTANTE: NADA DE TEXT BLOCKS - Strings tradicionales
            String createUsers =
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "name TEXT NOT NULL, " +
                            "email TEXT NOT NULL UNIQUE, " +
                            "password TEXT NOT NULL, " +
                            "user_role TEXT NOT NULL DEFAULT 'USER', " +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ");";

            stmt.executeUpdate(createUsers);
            System.out.println("Tabla 'users' creada");

            // 5. TASK_LIST
            String createTaskList =
                    "CREATE TABLE IF NOT EXISTS task_list (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "name TEXT NOT NULL, " +
                            "user_id INTEGER NOT NULL, " +
                            "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE" +
                            ");";

            stmt.executeUpdate(createTaskList);
            System.out.println("Tabla 'task_list' creada");

            // 6. TASK
            String createTask =
                    "CREATE TABLE IF NOT EXISTS task (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "title TEXT NOT NULL, " +
                            "description TEXT, " +
                            "due_date TEXT, " +
                            "priority TEXT DEFAULT 'MEDIA', " +
                            "status TEXT DEFAULT 'PENDIENTE', " +
                            "important INTEGER DEFAULT 0, " +
                            "priority_score INTEGER DEFAULT 0, " +
                            "task_list_id INTEGER NOT NULL, " +
                            "FOREIGN KEY(task_list_id) REFERENCES task_list(id) ON DELETE CASCADE" +
                            ");";

            stmt.executeUpdate(createTask);
            System.out.println("Tabla 'task' creada");

            // 7. SUB_TASK
            String createSubTask =
                    "CREATE TABLE IF NOT EXISTS sub_task (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "title TEXT NOT NULL, " +
                            "sub_task_status TEXT NOT NULL DEFAULT 'PENDIENTE', " +
                            "due_date TEXT, " +
                            "important INTEGER DEFAULT 0, " +
                            "task_id INTEGER NOT NULL, " +
                            "FOREIGN KEY(task_id) REFERENCES task(id) ON DELETE CASCADE" +
                            ");";

            stmt.executeUpdate(createSubTask);
            System.out.println("Tabla 'sub_task' creada");

            // 8. ADMIN
            String insertAdmin =
                    "INSERT OR IGNORE INTO users (name, email, password, user_role) " +
                            "VALUES ('Administrador', 'admin@prioriza.com', 'admin123', 'ADMIN');";

            int inserted = stmt.executeUpdate(insertAdmin);
            if (inserted > 0) {
                System.out.println("Usuario ADMIN creado");
            }

            System.out.println("¡TODO CORRECTO! Base de datos lista");

        } catch (SQLException e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();

            // 9. DIAGNÓSTICO AUTOMÁTICO
            System.err.println("\nDIAGNÓSTICO:");
            System.err.println("   Versión SQLite: " + findSQLiteVersion());
            System.err.println("   Java Version: " + System.getProperty("java.version"));
            System.err.println("\nSOLUCIÓN: Elimina los TEXT BLOCKS (\"\"\") del código");
        }
    }

    private static String findSQLiteVersion() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             var rs = stmt.executeQuery("SELECT sqlite_version();")) {
            if (rs.next()) return rs.getString(1);
        } catch (Exception e) {
            return "Desconocida";
        }
        return "Desconocida";
    }
}