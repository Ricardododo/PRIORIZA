package com.prioriza.dao;

import com.prioriza.model.Priority;
import com.prioriza.model.Task;
import com.prioriza.model.TaskStatus;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    //Insertar, actualiza la SQL
    public void insert(Task task) {
        String sql = "INSERT INTO task(title, description, due_date, priority, status, important, priority_score, task_list_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getDueDate() != null ? task.getDueDate().toString() : null);
            ps.setString(4, task.getPriority() != null ? task.getPriority().name() : "MEDIA");
            ps.setString(5, task.getStatus() != null ? task.getStatus().name() : "PENDIENTE");
            ps.setBoolean(6, task.isImportant());
            ps.setInt(7, task.getPriorityScore());
            ps.setInt(8, task.getTaskListId()); //incluyendo la FK

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
        String sql = "SELECT * FROM task WHERE task_list_id = ? ORDER BY priority_score DESC, due_date ASC";

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
    //buscar tareas por usuario (a través de task_list)
    public List<Task> getByUserId(int userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = """
            SELECT t.* FROM task t
            JOIN task_list tl ON t.task_list_id = tl.id
            WHERE tl.user_id = ?
            ORDER BY t.priority_score DESC, t.due_date ASC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }
    // buscar tarea por estado
    public List<Task> getByStatus(TaskStatus status) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM task WHERE status = ? ORDER BY priority_score DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }
    //buscar tarea por prioridad
    public List<Task> getByPriority(Priority priority) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM task WHERE priority = ? ORDER BY due_date ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, priority.name());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }
    //buscar tarea por importantes
    public List<Task> getImportantTasks(int userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = """
                SELECT t.* FROM task t
                JOIN task_list tl ON t.task_list_id = tl.id
                WHERE tl.user_id = ? AND t.important = 1 AND t.status != 'COMPLETA'
                ORDER BY t.priority_score DESC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }
    //buscar tareas vencidas
    public List<Task> getOverdueTasks(int userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = """
            SELECT t.* FROM task t
            JOIN task_list tl ON t.task_list_id = tl.id
            WHERE tl.user_id = ?
            AND t.due_date < date('now')
            AND t.status != 'COMPLETA'
            ORDER BY t.due_date ASC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }
    //buscar tareas para hoy
    public List<Task> getTodayTasks(int userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = """
            SELECT t.* FROM task t
            JOIN task_list tl ON t.task_list_id = tl.id
            WHERE tl.user_id = ?
            AND t.due_date = date('now')
            AND t.status != 'COMPLETA'
            ORDER BY t.priority_score DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }

    //actualizar completo
    public void update(Task task){
        String sql = "UPDATE task SET title = ?, description = ?, due_date = ?, priority = ?, status = ?, important = ?, priority_score = ?, task_list_id = ? WHERE id = ?";

        try(Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getDueDate() != null ? task.getDueDate().toString() : null);
            ps.setString(4, task.getPriority() != null ? task.getPriority().name() : Priority.MEDIA.name());
            ps.setString(5, task.getStatus() != null ? task.getStatus().name() : TaskStatus.PENDIENTE.name());
            ps.setBoolean(6, task.isImportant());
            ps.setInt(7, task.getPriorityScore());
            ps.setInt(8, task.getTaskListId());
            ps.setInt(9, task.getId());

            int affectedRows = ps.executeUpdate();

            if(affectedRows > 0){
                System.out.println("Tarea ID " + task.getId() + " actualizada correctamente");
            }
        } catch (Exception e) {
            System.out.println("Error al actualizar tarea ID " + task.getId() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    //actualizar solo el estado
    public void updateStatus(int taskId, TaskStatus status) {
        String sql = "UPDATE task SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setInt(2, taskId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //actualizar solo la prioridad heuristica
    public void updatePriorityScore(int taskId, int priorityScore) {
        String sql = "UPDATE task SET priority_score = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, priorityScore);
            ps.setInt(2, taskId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //actualizar solo importante
    public void updateImportant(int taskId, boolean important) {
        String sql = "UPDATE task SET important = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, important);
            ps.setInt(2, taskId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Eliminar
    public void delete(int id){
        String sql = "DELETE FROM task WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0){
                System.out.println("Tarea ID " + id + " eliminada correctamente");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //contar tareas por estado
    public int countByStatus(int userId, TaskStatus status) {
        String sql = """
            SELECT COUNT(*) FROM task t
            JOIN task_list tl ON t.task_list_id = tl.id
            WHERE tl.user_id = ? AND t.status = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, status.name());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    //Mapper privado para leer (convierte fila en objeto)
    private Task mapResultSetToTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getInt("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));

        String dueDate = rs.getString("due_date");
        if (dueDate != null) {
            task.setDueDate(LocalDate.parse(dueDate));
        }

        task.setPriority(Priority.valueOf(rs.getString("priority")));
        task.setStatus(TaskStatus.valueOf(rs.getString("status")));
        task.setImportant(rs.getBoolean("important"));
        task.setPriorityScore(rs.getInt("priority_score"));
        task.setTaskListId(rs.getInt("task_list_id"));

        return task;
    }
}
