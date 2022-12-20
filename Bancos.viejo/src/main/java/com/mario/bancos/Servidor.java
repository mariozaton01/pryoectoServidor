package com.mario.bancos;

import com.mario.bancos.Clases.Usuario;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Servidor {

    public static final int PUERTO = 50003;

    //Usuarios


    public static void main(String[] args) {
        //Crear el socket del servidor
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PUERTO);
        } catch (Exception e) {
            System.out.println("Error al crear el socket del servidor" + e.getMessage());
            throw new RuntimeException(e);
        }
        System.out.println("Servidor iniciado");
        Socket socket = null;

        // Aceptar conexiones
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (Exception e) {
                System.out.println("Error al aceptar conexiones" + e.getMessage());
                throw new RuntimeException(e);
            }
            System.out.println("Cliente conectado");
            Hilo hilo = new Hilo(socket);
            hilo.start();
        }

    }
}
