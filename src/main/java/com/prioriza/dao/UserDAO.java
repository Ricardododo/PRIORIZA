package com.prioriza.dao;

import com.prioriza.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    //Crear y Agregar
    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO users(name, email) VALUES(?, ?)";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.executeUpdate();

            //obtener el id generado y asignarlo al objeto User
            try(ResultSet rs = stmt.getGeneratedKeys()){
                if (rs.next()){
                    user.setId(rs.getInt(1)); //actualiza el id
                }
            }
        }
    }

    //Leer
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, email FROM users";
        try(Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next()){
                User user= new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email")
                );
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public User getById(int id){
        String sql = "SELECT id, name, email FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

             stmt.setInt(1, id);

             try(ResultSet rs = stmt.executeQuery()) {
                 if (rs.next()) {
                     return new User(
                             rs.getInt("id"),
                             rs.getString("name"),
                             rs.getString("email")
                     );
                 }
             }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // si no existe
    }

    public User getByEmail(String email){
        String sql = "SELECT id, name, email FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try(ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email")
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

    }

    //Eliminar
    public void delete(int id){

    }


}
