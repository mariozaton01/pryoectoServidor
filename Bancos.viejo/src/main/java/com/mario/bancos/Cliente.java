package com.mario.bancos;

import com.mario.bancos.Clases.Usuario;
import com.mario.bancos.FirmasDigitales.FirmaCliente;
import com.mario.bancos.Ventanas.Paneles.Panel_registro;
import com.mario.bancos.Ventanas.Ventana_inicio;
import com.mario.bancos.Ventanas.Ventana_login;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Scanner;

public class Cliente {
    //Conexion related
    public static final int PUERTO = 50003;
    public static final String HOST = "localhost";

    //Clave publica del servidor
    static PublicKey publicKeyServidor = null;
    //Clave simetrica del cliente
    static SecretKey secretKeyCliente;

    private static ObjectOutputStream output;
    private static ObjectInputStream input;
    //Ventanas a utilizar related
    private static Ventana_login v_login;
    private static Ventana_inicio v_inicio;
    private static Socket socket;



    public static void main(String[] args) {

        try {
            socket = new Socket(HOST, PUERTO);
        } catch (IOException e) {
            System.out.println("Error de conexion al servidor" + e.getMessage());
            throw new RuntimeException(e);
        }
        System.out.println("Conectado al servidor");


        //Crear una clave simétrica AES
        KeyGenerator generadorAES = null;
        SecretKey claveSimetrica = null;
        try {
            generadorAES = KeyGenerator.getInstance("AES");
            generadorAES.init(128);
            claveSimetrica = generadorAES.generateKey();
            System.out.println("Generada la clave simétrica AES");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error al generar la clave simétrica AES" + e.getMessage());
            throw new RuntimeException(e);
        }
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            System.out.println("Error al crear el flujo de salida" + e.getMessage());
            throw new RuntimeException(e);
        }

        //Recibir la clave publica del servidor
        try {
            publicKeyServidor = (PublicKey) input.readObject();
            System.out.println("Recibida la clave publica del servidor");
        } catch (Exception e) {
            System.out.println("Error al recibir la clave publica del servidor" + e.getMessage());
            throw new RuntimeException(e);
        }

        //Encriptar la clave simétrica con la clave publica del servidor
        Cipher cipherCliente = null;
        byte[] claveSimetricaEncriptada = null;
        try {
            cipherCliente = Cipher.getInstance("RSA");
            cipherCliente.init(Cipher.ENCRYPT_MODE, publicKeyServidor);
            claveSimetricaEncriptada = cipherCliente.doFinal(claveSimetrica.getEncoded());
            System.out.println("Encriptada la clave simétrica con la clave publica del servidor");
        } catch (Exception e) {
            System.out.println("Error al encriptar la clave simétrica con la clave publica del servidor" + e.getMessage());
            throw new RuntimeException(e);
        }

        //Guardar la clave en la variable global
        secretKeyCliente = claveSimetrica;
        //Guardar la clave publica del servidor en la variable global
        publicKeyServidor = publicKeyServidor;


        //Enviar la clave simétrica encriptada al servidor
        try {
            output.writeObject(claveSimetricaEncriptada);
            System.out.println("Enviada la clave simétrica encriptada al servidor");
        } catch (Exception e) {
            System.out.println("Error al enviar la clave simétrica encriptada al servidor" + e.getMessage());
            throw new RuntimeException(e);
        }


        //Pruebas de envio y recepcion
        //pruebasEnvioRecepcion(output, input);

        //Pantalla Principal
        setInicio();
    }

    private static void pruebasEnvioRecepcion(ObjectOutputStream output, ObjectInputStream input) {
        //Enviar un mensaje al servidor
        String mensaje = "Hola servidor";
        //Cifrar el mensaje
        byte[] mensajeCifrado = cifrarObjetoSimetrico(mensaje);
        //Enviar el mensaje cifrado
        try {
            output.writeObject(mensajeCifrado);
            System.out.println("Enviado el mensaje cifrado al servidor");
        } catch (Exception e) {
            System.out.println("Error al enviar el mensaje cifrado al servidor" + e.getMessage());
            throw new RuntimeException(e);
        }
        //Esperar la respuesta del servidor
        byte[] mensajeRecibido = null;
        try {
            mensajeRecibido = (byte[]) input.readObject();
            System.out.println("Recibido el mensaje cifrado del servidor");
        } catch (Exception e) {
            System.out.println("Error al recibir el mensaje cifrado del servidor" + e.getMessage());
            throw new RuntimeException(e);
        }
        //Descifrar el mensaje
        String mensajeDescifrado = descifrarObjetoSimetrico(mensajeRecibido);
        //Mostrar el mensaje
        System.out.println("Mensaje recibido del servidor: " + mensajeDescifrado);

    }

    /**
     * Cifra un texto con la clave simétrica
     *
     * @param
     * @return byte[]
     */
    private static byte[] cifrarObjetoSimetrico(Object obj) {
        //Crear el cifrador simétrico con la clave simétrica
        Cipher cifradorSimetrico = null;
        try {
            cifradorSimetrico = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cifradorSimetrico.init(Cipher.ENCRYPT_MODE, secretKeyCliente);
        } catch (Exception e) {
            System.out.println("Error al crear el cifrador simétrico" + e.getMessage());
            throw new RuntimeException(e);
        }
        //Ciframos el objeto en forma de byte
        byte[] contenidoCifrado;
        try {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(obj);
                oos.flush();
            } catch (Exception e) {
                System.out.println("Error al cifrar objeto: " + e.getMessage());

            } finally {
                bos.close();
            }
            System.out.println(bos.toByteArray().toString());
            contenidoCifrado = cifradorSimetrico.doFinal(bos.toByteArray());
            System.out.println("Contenido cifrado");
        } catch (Exception e) {
            System.out.println("Error al cifrar el contenido" + e.getMessage());
            throw new RuntimeException(e);
        }
        return contenidoCifrado;
    }

    /**
     * Descifra un texto con la clave simétrica
     *
     * @param contenidoCifrado
     * @return
     */
    private static String descifrarObjetoSimetrico(byte[] contenidoCifrado) {
        //Crear el cifrador simétrico con la clave simétrica
        Cipher cifradorSimetrico = null;
        try {
            cifradorSimetrico = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cifradorSimetrico.init(Cipher.DECRYPT_MODE, secretKeyCliente);
            System.out.println("Creado el cifrador simétrico");
        } catch (Exception e) {
            System.out.println("Error al crear el cifrador simétrico" + e.getMessage());
            throw new RuntimeException(e);
        }
        //Descifrar el contenido
        byte[] contenidoDescifrado = null;
        try {
            contenidoDescifrado = cifradorSimetrico.doFinal(contenidoCifrado);
            System.out.println("Descifrado el contenido");
        } catch (Exception e) {
            System.out.println("Error al descifrar el contenido" + e.getMessage());
            throw new RuntimeException(e);
        }
        return new String(contenidoDescifrado);


    }
    private static String descifrarMensajeSimetrico(byte[] contenidoCifrado) {
        //Crear el cifrador simétrico con la clave simétrica
        Cipher cifradorSimetrico = null;
        try {
            cifradorSimetrico = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cifradorSimetrico.init(Cipher.DECRYPT_MODE, secretKeyCliente);
            System.out.println("Creado el cifrador simétrico");
        } catch (Exception e) {
            System.out.println("Error al crear el cifrador simétrico" + e.getMessage());
            throw new RuntimeException(e);
        }
        //Descifrar el contenido
        byte[] contenidoDescifrado = null;
        try {
            contenidoDescifrado = cifradorSimetrico.doFinal(contenidoCifrado);
            System.out.println("Descifrado el contenido");
        } catch (Exception e) {
            System.out.println("Error al descifrar el contenido" + e.getMessage());
            throw new RuntimeException(e);
        }
        return new String(contenidoDescifrado);


    }

    //Método para enviar un mensaje cifrado al servidor
    private static void enviarObjetoCifrado(ObjectOutputStream output, Object mensaje) {
        //Cifrar el mensaje
        byte[] mensajeCifrado = cifrarObjetoSimetrico(mensaje);
        //Enviar el mensaje cifrado
        try {
            output.writeObject(mensajeCifrado);
            System.out.println("Enviado el mensaje cifrado al servidor");
        } catch (Exception e) {
            System.out.println("Error al enviar el mensaje cifrado al servidor" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    private static void enviarMensaje(ObjectOutputStream output, String mensaje) { // sin cifrar, para recibir queries del servidor
        //Cifrar el mensaje
        byte[] mensajeByte = mensaje.getBytes();
        //Enviar el mensaje cifrado
        try {
            output.writeObject(mensajeByte);
            System.out.println("Mensaje enviado a cliente");
        } catch (Exception e) {
            System.out.println("Error al enviar el mensaje  al cliente" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Método para recibir un mensaje cifrado del servidor
    private static String recibirObjetoCifrado(ObjectInputStream input) {
        //Esperar la respuesta del servidor
        byte[] mensajeRecibido = null;
        try {
            mensajeRecibido = (byte[]) input.readObject();
            System.out.println("Recibido el mensaje cifrado del servidor");
        } catch (Exception e) {
            System.out.println("Error al recibir el mensaje cifrado del servidor" + e.getMessage());
            throw new RuntimeException(e);
        }
        //Descifrar el mensaje
        String mensajeDescifrado = descifrarObjetoSimetrico(mensajeRecibido);
        return mensajeDescifrado;
    }
    private static String recibirMensajeCifrado(ObjectInputStream input) {
        //Esperar la respuesta del servidor
        byte[] mensajeRecibido = null;
        try {
            mensajeRecibido = (byte[]) input.readObject();
            System.out.println("Recibido el mensaje cifrado del servidor");
        } catch (Exception e) {
            System.out.println("Error al recibir el mensaje cifrado del servidor" + e.getMessage());
            throw new RuntimeException(e);
        }
        //Descifrar el mensaje
        String mensajeDescifrado = descifrarMensajeSimetrico(mensajeRecibido);
        return mensajeDescifrado;
    }

    private static String recibirMensaje(ObjectInputStream input) {
        //Esperar la respuesta del servidor
        byte[] mensajeRecibido = null;
        try {
            mensajeRecibido = (byte[]) input.readObject();
            System.out.println("Recibido el mensaje  del servidor");
        } catch (Exception e) {
            System.out.println("Error al recibir el mensaje del servidor" + e.getMessage());
            throw new RuntimeException(e);
        }
        //Descifrar el mensaje
        String mensajeDescifrado = new String(mensajeRecibido);
        return mensajeDescifrado;
    }

    public static void setInicio(){

        v_login = new Ventana_login();
        v_login.setVisible(true);
        v_login.setLocationRelativeTo(null);
    }

    public static void insertarCliente(Usuario c) {
        try {
            enviarObjetoCifrado(output, c);
            //Verificacion de la firma digital por parte del servidor.
            FirmaCliente.firmaCliente(input);

            //recibimos el mensaje de comprobación que verifica que el Registro ha sido correcto
            String mensaje = recibirMensaje(input);
            System.out.println(mensaje);

            //cerramos el socket para volver a establecer conexion con el servidor y poder loguear correctamente.
            if(mensaje.equals("clienteRegistrado")){
                System.exit(0);
            }


        }
        catch (Exception e){
            System.out.println("Error al enviar datos de cliente: "+ e.getMessage());
        }



    }


    public static void login(Usuario u) {
        try{
            //mandamos al servidor los datos del usuario que inicia sesión
            enviarObjetoCifrado(output,u);
            //escuchamos lo que nos manda el servidor
            String mensaje = recibirMensaje(input);
            if(mensaje.equals("usuarioEncontrado")){
                 v_login.dispose();
                 v_inicio = new Ventana_inicio();
                 v_inicio.setVisible(true);
                 v_inicio.setLocationRelativeTo(null);

            }
            else {
                JOptionPane.showMessageDialog(null, "Cliente no encontrado,");

            }

        }
        catch (Exception e){
            System.out.println("Error al iniciar sesion: " +e.getMessage());
        }
    }
}
