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
                    System.out.println("Lista creada: " + list.getName() + " (ID: " + list.getId() + ")");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar lista: " + e.getMessage());
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
            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Lista ID " + list.getId() + " actualizada: " + list.getName());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //actualizar solo el nombre de la lista
    public void updateName(int listId, String newName) {
        String sql = "UPDATE task_list SET name = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newName);
            ps.setInt(2, listId);
            ps.executeUpdate();

            System.out.println("Lista ID " + listId + " renombrada a: " + newName);

        } catch (SQLException e) {
            System.err.println("Error al renombrar lista: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // buscar por ID
    public TaskList getById(int id) {
        String sql = "SELECT * FROM task_list WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    //buscar por nombre exacto
    public TaskList getByNameAndUser(String name, int userId) {
        String sql = "SELECT * FROM task_list WHERE name = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    // Listar todas las listas
    public List<TaskList> getAllTaskList() {
        List<TaskList> lists = new ArrayList<>();
        String sql = "SELECT * FROM task_list ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lists.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lists;
    }
    //Listar listas por usuario
    public List<TaskList> getByUserId(int userId) throws SQLException{
        List<TaskList> lists = new ArrayList<>();
        String sql = "SELECT * FROM task_list WHERE user_id = ? ORDER BY name";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                lists.add(mapResultSet(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lists;
    }
    //listar listar con conteo de tareas
    public List<TaskList> getByUserIdWithTaskCount(int userId) {
        List<TaskList> lists = new ArrayList<>();
        String sql = """
            SELECT tl.*, COUNT(t.id) as task_count
            FROM task_list tl
            LEFT JOIN task t ON tl.id = t.task_list_id
            WHERE tl.user_id = ?
            GROUP BY tl.id
            ORDER BY tl.name
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                TaskList list = mapResultSet(rs);
                // agregar el conteo como propiedad temporal
                lists.add(list);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lists;
    }
    //buscar lista por usuario con sus tareas
    public TaskList getByIdWithTasks(int id) {
        TaskList list = getById(id);
        if (list != null) {
            TaskDAO taskDAO = new TaskDAO();
            list.setTasks(taskDAO.getByTaskListId(id));
        }
        return list;
    }
    //creación de listas por defecto para nuevo usuario
    public void createDefaultListsForUser(int userId) throws SQLException {
        // Verificar si ya tiene listas
        List<TaskList> existingLists = getByUserId(userId);
        if (existingLists.isEmpty()) {
            // Crear lista "General" por defecto
            TaskList defaultList = new TaskList("General", userId);
            insert(defaultList);

            // Crear lista "Trabajo" por defecto
            TaskList workList = new TaskList("Trabajo", userId);
            insert(workList);

            // Crear lista "Personal" por defecto
            TaskList personalList = new TaskList("Personal", userId);
            insert(personalList);

            System.out.println("Listas por defecto creadas para usuario ID: " + userId);
        }
    }
    //contar listas por usuario
    public int countByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM task_list WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    //buscar si el usuario es dueño de la lista
    public boolean isOwner(int listId, int userId) {
        String sql = "SELECT COUNT(*) FROM task_list WHERE id = ? AND user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, listId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    //transferir listas (cuando se elimina un usuario)
    public void transferListsToAdmin(int userId, int adminId) {
        String sql = "UPDATE task_list SET user_id = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, adminId);
            ps.setInt(2, userId);
            int affectedRows = ps.executeUpdate();

            System.out.println(" " + affectedRows + " listas transferidas del usuario " + userId + " al admin " + adminId);

        } catch (SQLException e) {
            System.err.println("Error al transferir listas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // Eliminar lista
    public void delete(int id) {
        //primero verificar si la lista existe
        TaskList list = getById(id);
        if (list == null){
            System.out.println("La lista ID " + id + " no existe");
            return;
        }
        String sql = "DELETE FROM task_list WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0){
                System.out.println("Lista eliminada: " + list.getName() + " (ID: " + id + ")");
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar lista ID " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    //eliminar todas las listas de un usuario
    public void deleteByUserId(int userId) {
        String sql = "DELETE FROM task_list WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            int affectedRows = ps.executeUpdate();

            System.out.println(" " + affectedRows + " listas eliminadas del usuario ID: " + userId);

        } catch (SQLException e) {
            System.err.println("Error al eliminar listas del usuario " + userId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    //validar nombre de lista
    public boolean isListNameAvailable(String name, int userId) {
        String sql = "SELECT COUNT(*) FROM task_list WHERE name = ? AND user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) == 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //mapper
    private TaskList mapResultSet(ResultSet rs) throws SQLException {
        TaskList list = new TaskList();
        list.setId(rs.getInt("id"));
        list.setName(rs.getString("name"));
        list.setUserId(rs.getInt("user_id"));
        return list;
    }
    //Metodo de ejemplo para usar en Controller
    public static void ejemploUso(int userId) throws SQLException {
        TaskListDAO listDAO = new TaskListDAO();

        // 1. Crear lista por defecto si no tiene
        listDAO.createDefaultListsForUser(userId);

        // 2. Obtener todas las listas del usuario
        List<TaskList> lists = listDAO.getByUserId(userId);

        // 3. Para cada lista, cargar sus tareas
        TaskDAO taskDAO = new TaskDAO();
        for (TaskList list : lists) {
            list.setTasks(taskDAO.getByTaskListId(list.getId()));
            System.out.println("Lista: " + list.getName() + " - " +
                    list.getTasks().size() + " tareas");
        }
    }
}
