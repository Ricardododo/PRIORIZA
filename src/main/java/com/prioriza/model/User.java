package com.prioriza.model;

/**
 * Representa un usuario en el sistema PRIORIZA.
 * 
 * Cada usuario tiene un nombre, email, contraseña y rol.
 * Los usuarios pueden crear y gestionar sus propias listas de tareas.
 * 
 * @author PRIORIZA
 * @version 1.0
 */
public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private UserRole role;

    /**
     * Constructor por defecto para JDBC.
     */
    public User() {
    }

    /**
     * Constructor para registro de nuevo usuario.
     * 
     * @param name     Nombre del usuario
     * @param email    Correo electrónico del usuario
     * @param password Contraseña del usuario
     */
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = UserRole.USER;
    }

    /**
     * Constructor con id y datos básicos.
     * 
     * @param id    Identificador del usuario
     * @param name  Nombre del usuario
     * @param email Correo electrónico del usuario
     */
    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    /**
     * Constructor completo con todos los atributos.
     * 
     * @param id       Identificador del usuario
     * @param name     Nombre del usuario
     * @param email    Correo electrónico del usuario
     * @param password Contraseña del usuario
     * @param role     Rol del usuario
     */
    public User(int id, String name, String email, String password, UserRole role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getter y Setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }


    /**
     * Representación en cadena del usuario.
     * @return Cadena con id, nombre y email
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
