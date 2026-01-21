package com.prioriza.dao;

import com.prioriza.service.UserService;

public class TestDAO {
    public static void main(String[] args) {
        try{
            DatabaseInitializer.initialize();
            UserService userService = new UserService();
            userService.registerUser("Ricardo", "wellune@hotmail.com");
            System.out.println(userService.listUsers());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
