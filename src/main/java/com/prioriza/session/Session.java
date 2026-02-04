package com.prioriza.session;

import com.prioriza.model.User;

//esta clase permite guardar en memoriia quien es el usuario que ha iniciado sesión
public class Session {
    //usuario actual
    //static pues pertenece a la clase, no a un objeto user
    private static User currentUser;

    //este metodo se usa para llamar al usuario luego del login
    public static void setUser(User user){
        currentUser = user;
    }

    //este metodo devuelve el usuario que esta logeado actualmente
    //asi se sabe quien esta usando la app, que listas cargar y tareas mostrar
    public static User getUser(){
        return currentUser;
    }
}
