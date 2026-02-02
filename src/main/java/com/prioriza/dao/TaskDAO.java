package com.prioriza.dao;

import com.prioriza.model.Priority;
import com.prioriza.model.Task;
import com.prioriza.model.TaskStatus;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    //Insertar
    public void insert(Task task) {
        String sql = "INSERT INTO task(title, description, due_date, priority, status, task_list_id) VALUES (?, ?, ?, ?, ?, ?)";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getDueDate() != null ? String.valueOf(Date.valueOf(task.getDueDate())) : null);
            ps.setString(4, task.getPriority().name());
            ps.setString(5, task.getStatus().name());
            ps.setInt(6, task.getTaskListId()); //incluyendo la FK

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                task.setId(rs.getInt(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Leer o encontrar por ID
    public Task getById(int id){
        String sql = "SELECT * FROM task WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
              PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            //leer el resultado
            if (rs.next()){
                //convierte la fila en un objeto
                return mapResultSetToTask(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    // encontrar por lista de tareas (TaskList)
    public List<Task> getByTaskListId(int taskListId){
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM task WHERE task_list_id = ?";

         try(Connection conn = DatabaseConnection.getConnection();
              PreparedStatement ps = conn.prepareStatement(sql)){

             ps.setInt(1, taskListId);
             ResultSet rs = ps.executeQuery();

             while(rs.next()){
                 tasks.add(mapResultSetToTask(rs));
             }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }

    //Modificar
    public void update(Task task){
        String sql = "UPDATE task SET title = ?, description = ?, due_date = ?, priority = ?, status = ?, task_list_id = ? WHERE id = ?";

        try(Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getDueDate() != null ? task.getDueDate().toString() : null);
            ps.setString(4, task.getPriority().name());
            ps.setString(5, task.getStatus().name());
            ps.setInt(6, task.getTaskListId());
            ps.setInt(7, task.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Eliminar
    public void delete(int id){
        String sql = "DELETE FROM task WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Mapper privado para leer (convierte fila en objeto)
    private Task mapResultSetToTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getInt("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));

        String dueDate = rs.getString("due_date");
        if (dueDate != null){
            task.setDueDate(LocalDate.parse(dueDate));
        }

        task.setPriority(Priority.valueOf(rs.getString("priority")));
        task.setStatus(TaskStatus.valueOf(rs.getString("status")));
        task.setTaskListId(rs.getInt("task_list_id"));

        return task;
    }
}
