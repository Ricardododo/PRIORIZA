package com.prioriza.dao;

import com.prioriza.model.User;
import com.prioriza.service.UserService;

import java.util.List;

public class TestDAO {
    public static void main(String[] args) {
        try{
            //inicializo la base de datos y sus tablas
            DatabaseInitializer.initialize();
            //creo el usuario nuevo
            UserService userService = new UserService();
            //registro el usuario
            userService.registerUser("Luna", "luna@hotmail.com");
            //listar y muestro todos los usuarios
            List<User> users = userService.listUsers();
            System.out.println("\nLista completa de usuarios:");
            for(User u : users){
                System.out.println(u); //usa toString() de la clase User
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
