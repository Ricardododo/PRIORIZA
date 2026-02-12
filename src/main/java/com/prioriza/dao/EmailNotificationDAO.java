package com.prioriza.dao;

import com.prioriza.model.EmailNotification;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EmailNotificationDAO {

    //Insertar notificaciones
    public void insert(EmailNotification notification){
        String sql = """
                INSERT INTO email_notifications
                (user_id, user_email, task_id, subtask_id, item_type,
                item_title, due_date, days_remaining, status, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            ps.setInt(1, notification.getUserId());
            ps.setString(2, notification.getUserEmail());
            ps.setObject(3, notification.getTaskId());
            ps.setObject(4, notification.getSubtaskId());
            ps.setString(5, notification.getItemType());
            ps.setString(6, notification.getItemTitle());
            ps.setString(7, notification.getDuedate().toString());
            ps.setInt(8, notification.getDaysRemaining());
            ps.setString(9, notification.getStatus());
            ps.setString(10, notification.getCreatedAt().toString());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                notification.setId(rs.getInt(1));
            }
        } catch (Exception e) {
            System.err.println("Error insertando notificación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Obtener notificaciones PENDIENTES List<>
    public List<EmailNotification> getPendingNotifications(){
        List<EmailNotification> list = new ArrayList<>();
        String sql = """
                SELECT * FROM email_notifications
                WHERE status = 'PENDING'
                AND due_date >= date('now')
                ORDER BY days_remaining ASC, due_date ASC
                LIMIT 50
                """;

        try(Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){

            while(rs.next()){
                list.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    //Marcar como enviado
    public void markAsSent(int id){
        String sql = """
                UPDATE email_notifications SET status = 'SENT', sent_at = CURRENT_TIMESTAMP 
                WHERE id = ?
                """;
        try(Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Marcar como envio-fallido
    public void markASFailed(int id, String error){
        String sql = """
                UPDATE email_notificacions SET status = 'FAILED', error_message = ? 
                WHERE id = ?
                """;
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, error);
            ps.setInt(2, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //VERIFICAR si ya se envió notificación hoy para esta tarea
    public boolean hasBeenNotifiedToday(int taskId, int daysRemaining){
        String sql = """
                SELECT COUNT(*) FROM email_notifications
                WHERE task_id = ? AND days_remaining = ?
                AND date(created_at) = date('now')
                """;

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, taskId);
            ps.setInt(2, daysRemaining);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //Mapeo ResultSet -> Objeto
    private EmailNotification mapResultSet(ResultSet rs) throws SQLException{
        EmailNotification n = new EmailNotification();
        n.setId(rs.getInt("id"));
        n.setUserId(rs.getInt("user_id"));
        n.setUserEmail(rs.getString("user_email"));
        n.setTaskId((Integer) rs.getObject("task_id"));
        n.setSubtaskId((Integer) rs.getObject("subtask.id"));
        n.setItemType(rs.getString("item_type"));
        n.setItemTitle(rs.getString("item_title"));
        n.setDuedate(LocalDate.parse(rs.getString("due_date")));
        n.setDaysRemaining(rs.getInt("days_remaining"));
        n.setStatus(rs.getString("status"));
        n.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        n.setSentAt(rs.getObject("sent_at", LocalDateTime.class));
        n.setErrorMessage(rs.getString("error_message"));
        return n;
    }
}
