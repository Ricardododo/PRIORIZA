package com.prioriza.service;

import com.prioriza.dao.UserDAO;
import com.prioriza.model.User;

import java.sql.SQLException;
import java.util.List;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    //registrar usuario
    public User registerUser(String name, String email) throws SQLException{

        //Regla de negocio
        User existing = userDAO.getByEmail(email);
        if (existing !=null){
            System.out.println("El email ya existe: " + email);
            return existing;// si ya existe
        }

        User user = new User(name, email);
        userDAO.addUser(user); //esto guarda en la base de datos
        System.out.println("Usuario registrado: " + user);
        return user; //esto retorna el usuario con ID
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

}
