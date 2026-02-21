package com.prioriza.service;

import com.prioriza.dao.UserDAO;
import com.prioriza.model.User;
import com.prioriza.model.UserRole;

import java.sql.SQLException;
import java.util.List;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    //REGISTRO Y LOGIN

    /*
     * Registra un nuevo usuario
     * @return El usuario registrado con ID asignado
     * @throws IllegalArgumentException si el email ya existe
     */
    public User registerUser(String name, String email, String password) {
        // Validar email único
        User existingUser = userDAO.getByEmail(email);
        if (existingUser != null) {
            throw new IllegalArgumentException("El email ya está registrado: " + email);
        }

        // Crear nuevo usuario (rol USER por defecto)
        User user = new User(name, email, password);
        userDAO.addUser(user);

        System.out.println("Usuario registrado: " + user.getEmail() + " (ID: " + user.getId() + ")");
        return user;
    }

    /*
     * Inicia sesión de usuario
     * @return Usuario si credenciales válidas, null si no
     */
    public User login(String email, String password) {
        User user = userDAO.login(email, password);
        if (user != null) {
            System.out.println("Login exitoso: " + user.getName());
        } else {
            System.out.println("Login fallido para: " + email);
        }
        return user;
    }

    //CONSULTAS

    public User getUserById(int id) {
        return userDAO.getById(id);
    }

    public User getUserByEmail(String email) {
        return userDAO.getByEmail(email);
    }

    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }

    //ADMINISTRACIÓN

    public void updateUser(User user) {
        userDAO.update(user);
        System.out.println("Usuario actualizado: " + user.getEmail());
    }

    public void deleteUser(int id) {
        userDAO.delete(id);
        System.out.println("Usuario eliminado ID: " + id);
    }

    public boolean isAdmin(int userId) {
        User user = userDAO.getById(userId);
        return user != null && user.getRole() == UserRole.ADMIN;
    }

    /*
     * Cambia el rol de un usuario (solo para admin)
     */
    public void changeUserRole(int userId, UserRole newRole) {
        User user = userDAO.getById(userId);
        if (user != null) {
            user.setRole(newRole);
            userDAO.update(user);
            System.out.println("Rol cambiado para usuario " + userId + ": " + newRole);
        }
    }
}
