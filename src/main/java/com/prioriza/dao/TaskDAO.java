package com.prioriza.dao;

import com.prioriza.model.Priority;
import com.prioriza.model.Task;
import com.prioriza.model.TaskStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar operaciones de base de datos de tareas.
 * 
 * Proporciona métodos para insertar, actualizar, eliminar y consultar tareas
 * en la base de datos SQLite.
 * 
 * @author PRIORIZA
 * @version 1.0
 */
public class TaskDAO {

    /**
     * Inserta una nueva tarea en la base de datos.
     * 
     * @param task La tarea a insertar
     */
    public void insert(Task task) {
        String sql = "INSERT INTO task(title, description, due_date, priority, status, important, priority_score, task_list_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());

            //formato fecha/hora para SQLite
            if (task.getDueDateTime() != null){
                ps.setString(3, task.getDueDateTime()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }else{
                ps.setNull(3, Types.VARCHAR);
            }

            ps.setString(4, task.getPriority() != null ? task.getPriority().name() : "MEDIA");
            ps.setString(5, task.getStatus() != null ? task.getStatus().name() : "PENDIENTE");
            ps.setBoolean(6, task.isImportant());
            ps.setInt(7, task.getPriorityScore());
            ps.setInt(8, task.getTaskListId());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                task.setId(rs.getInt(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Busca una tarea por su identificador.
     * 
     * @param id Identificador de la tarea
     * @return La tarea encontrada o null si no existe
     */
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
    /**
     * Busca tareas por identificador de lista de tareas.
     * 
     * @param taskListId Identificador de la lista de tareas
     * @return Lista de tareas de la lista especificada
     */
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
    /**
     * Busca todas las tareas de un usuario a través de sus listas de tareas.
     * 
     * @param userId Identificador del usuario
     * @return Lista de tareas del usuario
     */
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
    /**
     * Busca tareas por estado.
     * 
     * @param status Estado de las tareas a buscar
     * @return Lista de tareas con el estado especificado
     */
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
    /**
     * Busca tareas por prioridad.
     * 
     * @param priority Prioridad de las tareas a buscar
     * @return Lista de tareas con la prioridad especificada
     */
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
    /**
     * Busca tareas importantes de un usuario.
     * 
     * @param userId Identificador del usuario
     * @return Lista de tareas importantes del usuario
     */
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
    /**
     * Busca tareas vencidas de un usuario.
     * 
     * @param userId Identificador del usuario
     * @return Lista de tareas vencidas del usuario
     */
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
    /**
     * Busca tareas con fecha de vencimiento para hoy.
     * 
     * @param userId Identificador del usuario
     * @return Lista de tareas para hoy del usuario
     */
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

    /**
     * Actualiza una tarea existente.
     * 
     * @param task La tarea con los datos actualizados
     */
    public void update(Task task){
        String sql = "UPDATE task SET title = ?, description = ?, due_date = ?, priority = ?, status = ?, important = ?, priority_score = ?, task_list_id = ? WHERE id = ?";

        try(Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());

            //formato fecha/hora para SQLite
            if (task.getDueDateTime() != null){
                ps.setString(3, task.getDueDateTime()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }else{
                ps.setNull(3, Types.VARCHAR);
            }
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

    /**
     * Actualiza solo el estado de una tarea.
     * 
     * @param taskId Identificador de la tarea
     * @param status Nuevo estado de la tarea
     */
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

    /**
     * Actualiza solo la puntuación de prioridad de una tarea.
     * 
     * @param taskId        Identificador de la tarea
     * @param priorityScore Nueva puntuación de prioridad
     */
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

    /**
     * Actualiza solo el campo importante de una tarea.
     * 
     * @param taskId    Identificador de la tarea
     * @param important Nuevo valor de importante
     */
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

    /**
     * Elimina una tarea por su identificador.
     * 
     * @param id Identificador de la tarea a eliminar
     */
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
    /**
     * Cuenta las tareas de un usuario por estado.
     * 
     * @param userId Identificador del usuario
     * @param status Estado de las tareas a contar
     * @return Número de tareas con el estado especificado
     */
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


    /**
     * Mapea una fila del ResultSet a un objeto Task.
     * 
     * @param rs ResultSet con los datos de la tarea
     * @return Objeto Task con los datos del ResultSet
     * @throws SQLException Si ocurre un error al leer los datos
     */
    private Task mapResultSetToTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getInt("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));

        String dueDate = rs.getString("due_date");
        if (dueDate != null) {
            if (dueDate.length() == 10) { //solo fecha (YYYY-MM-DD)
                task.setDueDateTime(LocalDateTime.parse(dueDate).toLocalDate().atStartOfDay());
            }else{ //fecha y hora
                task.setDueDateTime(LocalDateTime.parse(
                        dueDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }

        }

        task.setPriority(Priority.valueOf(rs.getString("priority")));
        task.setStatus(TaskStatus.valueOf(rs.getString("status")));
        task.setImportant(rs.getBoolean("important"));
        task.setPriorityScore(rs.getInt("priority_score"));
        task.setTaskListId(rs.getInt("task_list_id"));

        return task;
    }
}
