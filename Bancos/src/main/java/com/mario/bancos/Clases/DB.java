package com.mario.bancos.Clases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {

    public static Connection conectar() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Error al cargar el driver.");
            throw new RuntimeException(e);
        }

        try {
            Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/banco", "root", "");
            //System.out.println("Conectado a la base de datos");
            return conexion;
        } catch (SQLException e) {
            System.out.println("Error al conectar con la base de datos.");
            throw new RuntimeException(e);
        }
    }
}
