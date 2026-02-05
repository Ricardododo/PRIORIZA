package com.prioriza.session;

import com.prioriza.model.User;

//esta clase permite guardar en memoria quién es el usuario logeado
public class Session {
    //usuario actual
    //static pues pertenece a la clase, no a un objeto user
    private static User currentUser;

    //este metodo guarda al usuario al hacer login
    public static void setUser(User user){
        currentUser = user;
    }

    //este metodo obtiene el usuario actual
    //asi se sabe quien esta usando la app, que listas cargar y tareas mostrar
    public static User getUser(){
        return currentUser;
    }
    //este metodo es  para cerrar sesión
    public static void clear(){
        currentUser = null;
    }
}
