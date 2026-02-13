package com.prioriza.dao;

import com.prioriza.model.UserSettings;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserSettingsDAO {

    //OBTENER CONFIGURACIÓN DE UN USUARIO
    public UserSettings getByUserId(int userId) {
        String sql = "SELECT * FROM user_settings WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapResultSet(rs);
            } else {
                // Si no existe, crear configuración por defecto
                return createDefaultSettings(userId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return createDefaultSettings(userId); // Fallback a valores por defecto
        }
    }

    //CREAR CONFIGURACIÓN POR DEFECTO
    private UserSettings createDefaultSettings(int userId) {
        UserSettings settings = new UserSettings(userId);
        insert(settings); // Guardar en BD
        return settings;
    }

    //INSERTAR NUEVA CONFIGURACIÓN
    public void insert(UserSettings settings) {
        String sql = """
            INSERT INTO user_settings 
            (user_id, email_enabled, days_before_alert, alert_for_subtasks, 
             alert_only_working_days, max_alerts_per_day, notification_hour)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, settings.getUserId());
            ps.setInt(2, settings.isEmailEnabled() ? 1 : 0);
            ps.setInt(3, settings.getDaysBeforeAlert());
            ps.setInt(4, settings.isAlertForSubtasks() ? 1 : 0);
            ps.setInt(5, settings.isAlertOnlyWorkingDays() ? 1 : 0);
            ps.setInt(6, settings.getMaxAlertsPerDay());
            ps.setInt(7, settings.getNotificationHour());

            ps.executeUpdate();
            System.out.println("Configuración creada para usuario: " + settings.getUserId());

        } catch (SQLException e) {
            System.err.println("Error insertando configuración: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //ACTUALIZAR CONFIGURACIÓN
    public void update(UserSettings settings) {
        String sql = """
            UPDATE user_settings SET 
                email_enabled = ?,
                days_before_alert = ?,
                alert_for_subtasks = ?,
                alert_only_working_days = ?,
                max_alerts_per_day = ?,
                notification_hour = ?,
                last_updated = CURRENT_TIMESTAMP
            WHERE user_id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, settings.isEmailEnabled() ? 1 : 0);
            ps.setInt(2, settings.getDaysBeforeAlert());
            ps.setInt(3, settings.isAlertForSubtasks() ? 1 : 0);
            ps.setInt(4, settings.isAlertOnlyWorkingDays() ? 1 : 0);
            ps.setInt(5, settings.getMaxAlertsPerDay());
            ps.setInt(6, settings.getNotificationHour());
            ps.setInt(7, settings.getUserId());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                System.out.println("Configuración actualizada para usuario: " + settings.getUserId());
            }

        } catch (SQLException e) {
            System.err.println("Error actualizando configuración: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ============= ELIMINAR CONFIGURACIÓN =============
    public void delete(int userId) {
        String sql = "DELETE FROM user_settings WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.executeUpdate();
            System.out.println("Configuración eliminada para usuario: " + userId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //OBTENER TODAS LAS CONFIGURACIONES (ADMIN)
    public List<UserSettings> getAll() {
        List<UserSettings> list = new ArrayList<>();
        String sql = "SELECT * FROM user_settings ORDER BY user_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    //VERIFICAR SI EXISTE CONFIGURACIÓN
    public boolean exists(int userId) {
        String sql = "SELECT COUNT(*) FROM user_settings WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //MAPPER
    private UserSettings mapResultSet(ResultSet rs) throws SQLException {
        UserSettings settings = new UserSettings();
        settings.setUserId(rs.getInt("user_id"));
        settings.setEmailEnabled(rs.getInt("email_enabled") == 1);
        settings.setDaysBeforeAlert(rs.getInt("days_before_alert"));
        settings.setAlertForSubtasks(rs.getInt("alert_for_subtasks") == 1);
        settings.setAlertOnlyWorkingDays(rs.getInt("alert_only_working_days") == 1);
        settings.setMaxAlertsPerDay(rs.getInt("max_alerts_per_day"));
        settings.setNotificationHour(rs.getInt("notification_hour"));
        return settings;
    }
}
