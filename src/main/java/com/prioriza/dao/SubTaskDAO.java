package com.prioriza.dao;

import com.prioriza.model.SubTask;
import com.prioriza.model.SubTaskStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubTaskDAO {

    //Crear
    public void insert(SubTask subTask){
        String sql = """
                INSERT INTO sub_task (title, sub_task_status, task_id)
                VALUES (?, ?, ?)
                """;

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, subTask.getTitle());
                ps.setString(2, subTask.getSubTaskStatus().name()); //enum
                ps.setInt(3, subTask.getTaskId());

                ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

                if (rs.next()){
                    subTask.setId(rs.getInt(1));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    //leer por ID
    public SubTask getById(int id){
        String sql = "SELECT * FROM sub_task WEHERE id = ?";
        try(Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return mapResultSet(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //Listar subTareas
    public List<SubTask> getByTaskId(int taskId){
        List<SubTask> subTasks = new ArrayList<>();
        String sql = "SELECT * FROM sub_task WHERE task_id = ?";

        try(Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, taskId);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                subTasks.add(mapResultSet(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return subTasks;
    }

    //Modificar
    public void update(SubTask subTask){
        String sql = """
                UPDATE sub_task
                SET title = ?, sub_task_status = ?
                WHERE id = ?
                """;
        try(Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, subTask.getTitle());
            ps.setString(2, subTask.getSubTaskStatus().name());
            ps.setInt(3, subTask.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    //Eliminar
    public void delete(int id){
        String sql = "DELETE FROM sub_task WHERE id = ?";

        try(Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SubTask mapResultSet(ResultSet rs) throws SQLException{
        SubTask s = new SubTask();
        s.setId(rs.getInt("id"));
        s.setTitle(rs.getString("title"));
        s.setSubTaskStatus(SubTaskStatus.valueOf(rs.getString("sub_task_status")));
        s.setTaskId(rs.getInt("task_id"));
        return s;
    }

    public void deleteByTaskId(int id) {

    }
}
