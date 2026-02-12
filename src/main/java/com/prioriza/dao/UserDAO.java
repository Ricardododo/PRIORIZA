package com.prioriza.dao;

import com.prioriza.model.User;
import com.prioriza.model.UserRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    //Crear y Agregar
    public void addUser(User user) {
        String sql = "INSERT INTO users(name, email, password, user_role) VALUES(?, ?, ?, ?)";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole() != null ? user.getRole().name() : UserRole.USER.name());
            stmt.executeUpdate();

            //obtener el id generado
            try(ResultSet rs = stmt.getGeneratedKeys()){
                if (rs.next()){
                    user.setId(rs.getInt(1)); //actualiza el id
                }
            }
            System.out.println("Usuario registrado correctamente: " + user.getEmail());

        } catch (SQLException e) {
            System.err.println("ERROR al registrar usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //Login
    public User login(String email, String password){

        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        try(Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)){

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        UserRole.valueOf(rs.getString("user_role"))
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Leer todos
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, email, user_role FROM users";
        try(Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next()){
                User user= new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        "",
                        UserRole.valueOf(rs.getString("use_role"))
                );
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public User getById(int id){
        String sql = "SELECT id, name, email, user_role FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

             stmt.setInt(1, id);

             try(ResultSet rs = stmt.executeQuery()) {
                 if (rs.next()) {
                     return new User(
                             rs.getInt("id"),
                             rs.getString("name"),
                             rs.getString("email"),
                             "",
                             UserRole.valueOf(rs.getString("user_role"))
                     );
                 }
             }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // si no existe
    }

    public User getByEmail(String email){
        String sql = "SELECT id, name, email, user_role FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try(ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            "",
                            UserRole.valueOf(rs.getString("user_role"))
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //Modificar
    public void update(User user){
        String sql = "UPDATE users SET name = ?, email = ?, user_role = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getRole().name());
            stmt.setInt(4, user.getId());
            stmt.executeUpdate();

            System.out.println("Usuario actualizado: " + user.getEmail());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Eliminar
    public void delete(int id){
        String sql = "DELETE FROM users WHERE id = ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Usuario eliminado: ID " + id);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //verificar si es ADMIN
    public boolean isAdmin(int userId){
        User user = getById(userId);
        return user != null && user.getRole() == UserRole.ADMIN;
    }
}
