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
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.executeUpdate();
        }
    }

    //Leer
    public User getById(int id){
        try{

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<User> getAllUsers() throws SQLException{
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        try(Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next()){
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setName(rs.getString("name"));
                u.setEmail(rs.getString("email"));
            }
        }
        return users;
    }

    //Modificar
    public void update(User user){

    }

    //Eliminar
    public void delete(int id){

    }


}
