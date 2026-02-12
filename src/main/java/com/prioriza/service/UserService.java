package com.prioriza.service;

import com.prioriza.dao.UserDAO;
import com.prioriza.model.User;

import java.sql.SQLException;
import java.util.List;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    //registrar usuario
    public User registerUser(String name, String email) {
        User user = null;
        try{
            //Regla de negocio: email unico
            if (userDAO.getByEmail(email) !=null){
                throw new IllegalArgumentException("El email ya existe: " + email);
            }
            user = new User(name, email);
            userDAO.addUser(user); //esto guarda en la base de datos
            System.out.println("Usuario registrado: " + user);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al registrar usuario");
        }

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
