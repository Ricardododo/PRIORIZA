package com.prioriza.service;

import com.prioriza.dao.UserDAO;
import com.prioriza.model.User;

import java.sql.SQLException;
import java.util.List;

public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    //registrar usuario
    public void registerUser(String name, String email) throws SQLException{
        if (userDAO.getByEmail(email) !=null){
            System.out.println("El email ya existe: " + email);
        }
        User user = new User(name, email);
        userDAO.addUser(user); //esto guarda en la base de datos
        System.out.println("Usuario registrado: " + user);
    }
    //listar todos los usuarios
    public List<User> listUsers() throws SQLException{
        return userDAO.getAllUsers(); // esto devuelve todos los usuarios
    }
    //buscar por id
    public User getUserById(int id) throws SQLException{
        return userDAO.getById(id);
    }
    //buscar por email
    public User getUserByEmail(String email) throws SQLException {
        return userDAO.getByEmail(email);
    }

    //regla de negocio

    //verificar la relación

    //persistencia
}
