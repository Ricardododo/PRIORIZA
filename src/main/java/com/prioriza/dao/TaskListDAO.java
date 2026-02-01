package com.prioriza.dao;

import com.prioriza.model.Task;
import com.prioriza.model.TaskList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskListDAO {
    //insertar nueva lista de tareas
    public void insert(TaskList list){
        String sql = "INSERT INTO task_list (name, user_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, list.getName());
            stmt.setInt(2, list.getUserId());
            stmt.executeUpdate();

            // Obtener el ID generado y asignarlo al objeto
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    list.setId(generatedKeys.getInt(1));
                }
            }

        } catch (
        SQLException e) {
            e.printStackTrace();
        }
    }
    // Leer lista por ID
    public TaskList getById(int id) {
        String sql = "SELECT * FROM task_list WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                TaskList list = new TaskList();
                list.setId(rs.getInt("id"));
                list.setName(rs.getString("name"));
                list.setUserId(rs.getInt("user_id"));
                return list;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Listar todas las listas
    public List<TaskList> getAllTaskList() {
        List<TaskList> lists = new ArrayList<>();
        String sql = "SELECT * FROM task_list";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TaskList list = new TaskList();
                list.setId(rs.getInt("id"));
                list.setName(rs.getString("name"));
                list.setUserId(rs.getInt("user_id"));
                lists.add(list);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lists;
    }
    // Eliminar lista
    public void delete(int id) {
        String sql = "DELETE FROM task_list WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Actualizar lista
    public void update(TaskList list) {
        String sql = "UPDATE task_list SET name = ?, user_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, list.getName());
            ps.setInt(2, list.getUserId());
            ps.setInt(3, list.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Listar listas de un usuario
    public List<TaskList> getByUserId(int userId) throws SQLException{
        List<TaskList> lists = new ArrayList<>();
        String sql = "SELECT * FROM task_list WHERE user_id = ?";
        try(Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                TaskList list = new TaskList(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("user_id")
                );
                lists.add(list);
            }
            return lists;
        }
    }
}
