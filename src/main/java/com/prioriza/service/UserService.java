package com.prioriza.service;

import com.prioriza.dao.UserDAO;
import com.prioriza.model.User;
import com.prioriza.model.UserRole;

import java.sql.SQLException;
import java.util.List;

/**
 * Servicio para gestionar operaciones de usuarios.
 * 
 * Proporciona métodos para registro, login, consulta y administración de usuarios.
 * 
 * @author PRIORIZA
 * @version 1.0
 */
public class UserService {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * @param name     Nombre del usuario
     * @param email    Correo electrónico del usuario
     * @param password Contraseña del usuario
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

    /**
     * Inicia sesión de usuario con email y contraseña.
     * 
     * @param email    Correo electrónico del usuario
     * @param password Contraseña del usuario
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

    // CONSULTAS

    /**
     * Busca un usuario por su identificador.
     * 
     * @param id Identificador del usuario
     * @return El usuario encontrado o null
     */
    public User getUserById(int id) {
        return userDAO.getById(id);
    }

    /**
     * Busca un usuario por su correo electrónico.
     * 
     * @param email Correo electrónico del usuario
     * @return El usuario encontrado o null
     */
    public User getUserByEmail(String email) {
        return userDAO.getByEmail(email);
    }

    /**
     * Obtiene todos los usuarios del sistema.
     * 
     * @return Lista de todos los usuarios
     * @throws SQLException Si ocurre un error de base de datos
     */
    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }

    // ADMINISTRACIÓN

    /**
     * Actualiza los datos de un usuario.
     * 
     * @param user Usuario con los datos actualizados
     */
    public void updateUser(User user) {
        userDAO.update(user);
        System.out.println("Usuario actualizado: " + user.getEmail());
    }

    /**
     * Elimina un usuario por su identificador.
     * 
     * @param id Identificador del usuario a eliminar
     */
    public void deleteUser(int id) {
        userDAO.delete(id);
        System.out.println("Usuario eliminado ID: " + id);
    }

    /**
     * Verifica si un usuario tiene rol de administrador.
     * 
     * @param userId Identificador del usuario
     * @return true si el usuario es administrador
     */
    public boolean isAdmin(int userId) {
        User user = userDAO.getById(userId);
        return user != null && user.getRole() == UserRole.ADMIN;
    }

    /**
     * Cambia el rol de un usuario.
     * 
     * @param userId  Identificador del usuario
     * @param newRole Nuevo rol a asignar
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
