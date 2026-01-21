package com.prioriza.service;

import com.prioriza.dao.UserDAO;
import com.prioriza.model.User;

import java.sql.SQLException;
import java.util.List;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    public void registerUser(String name, String email) throws SQLException{
        User user = new User(name, email);
        userDAO.addUser(user);
    }

    public List<User> listUsers() throws SQLException{
        return userDAO.getAllUsers();
    }

    //regla de negocio

    //verificar la relación

    //persistencia
}
