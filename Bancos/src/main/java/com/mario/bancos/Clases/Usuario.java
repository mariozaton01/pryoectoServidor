package com.mario.bancos.Clases;

import java.io.Serializable;
import java.util.Arrays;

public class Usuario implements Serializable {
    String nombre;
    String apellido;
    int edad;
    String usuario;
    String contrasena;
    String email;
    int id;
    String firmaDigital;
    String metodo;


    public Usuario(String nombre, String apellido, int edad, String usuario, String contrasena, String email){

        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.email = email;

    }

    public Usuario() {

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", edad=" + edad +
                ", usuario='" + usuario + '\'' +
                ", contrasena=" + contrasena +
                ", email='" + email + '\'' +
                ", id=" + id +
                ", firmaDigital='" + firmaDigital + '\'' +
                ", metodo='" + metodo + '\'' +
                '}';
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirmaDigital() {
        return firmaDigital;
    }

    public void setFirmaDigital(String firmaDigital) {
        this.firmaDigital = firmaDigital;
    }
}
