package com.mario.bancos;

import com.mario.bancos.Clases.DB;
import com.mario.bancos.Clases.Movimientos;
import com.mario.bancos.Clases.Saldo;
import com.mario.bancos.Clases.Usuario;
import com.mario.bancos.FirmasDigitales.FirmaServer;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Hilo extends Thread {

    Socket socket;
    //Clave publica del servidor
    PublicKey llavePublicaServer;
    //Clave privada del servidor
    PrivateKey llavePrivadaServer;
    //Cifrador del cliente
    Cipher cipherCliente;
    //Clave simetrica del cliente
    byte[] claveSimetrica = null;
    //Usuario conectado
    Usuario usuarioConectado = null;
    ObjectOutputStream output = null;
    ObjectInputStream input = null;
    public Hilo(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run(){

        // Generar claves privadas y públicas RSA
        KeyPairGenerator generadorRSA = null;
        KeyPair claves = null;
        try {
            generadorRSA = KeyPairGenerator.getInstance("RSA");
            generadorRSA.initialize(1024);
            claves = generadorRSA.genKeyPair();
            System.out.println("Claves RSA generadas");
        } catch (Exception e) {
            System.out.println("Error al generar las claves RSA: " + e.getMessage());
            throw new RuntimeException(e);
        }

        // Crear flujos de entrada y de salida
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            System.out.println("Error al crear los ObjectStreams" + e.getMessage());
            throw new RuntimeException(e);
        }

        // Enviar clave pública al cliente
        try {
            output.writeObject(claves.getPublic());
            System.out.println("Clave pública enviada a cliente");
        } catch (IOException e) {
            System.out.println("No se pudo enviar la clave publica al cliente" + e.getMessage());
            throw new RuntimeException(e);
        }

        // Esperar a recibir la clave simétrica cifrada con la clave pública
        byte[] claveSimetricaCifrada = null;
        try {
            claveSimetricaCifrada = (byte[]) input.readObject();
            System.out.println("Clave simétrica cifrada recibida mediante clave pública");
        } catch (Exception e) {
            System.out.println("Error al recibir la clave simétrica cifrada mediante clave pública: " + e.getMessage());
            throw new RuntimeException(e);
        }

        // Descifrar la clave simétrica con la clave privada
        try {
            Cipher descifradorRSA = Cipher.getInstance("RSA");
            descifradorRSA.init(Cipher.DECRYPT_MODE, claves.getPrivate());
            claveSimetrica = descifradorRSA.doFinal(claveSimetricaCifrada);
            System.out.println("Clave simétrica descifrada mediante clave privada");
        } catch (Exception e) {
            System.out.println("Error al descifrar la clave simétrica mediante clave privada: " + e.getMessage());
            throw new RuntimeException(e);
        }

        //Guardamos tanto la clave publica como la clave  privada del servidor
        llavePublicaServer = claves.getPublic();
        llavePrivadaServer = claves.getPrivate();

        //Despues de haber compartido las claves publicas y privadas entre cliente/servidor, llamamos a la ventana inicial en la que el usuario podrá
        // registrarse o iniciar sesión.
        //Bancos.setInicio(output,input);
        //pantallaPrincipal(output, input);

        //escuchamos
        while(true){
            Object obj = recibirObjeto(input);
            System.out.println("SERVER:" + obj.getClass());

            if (obj instanceof Usuario){
                Usuario u = (Usuario) obj;
                String metodo = u.getMetodo();

                switch (metodo){
                    case "registroUsuario":
                        try{
                            if (u.getFirmaDigital().equals("acepto"))
                                FirmaServer.firmarServidor(output);
                            registroCliente(u);
                        }
                        catch (Exception e){
                            System.out.println("Ha ocurrido un error al insertar el cliente: " + e.getMessage());
                        }

                        break;
                    case "loginUsuario":
                        try{
                            Boolean entra = false;
                                entra =loginCliente(u);
                            if (!entra){ continue;}
                            Boolean cerrarSesion = false;
                            while (!cerrarSesion && entra){
                                //escucho peticion para ver saldo o ingresos/retiros
                                Object mov = recibirObjeto(input);
                                if(mov instanceof Movimientos){

                                    Movimientos m = (Movimientos) mov;
                                    //creamos el movimiento
                                    crearMovimiento(m);

                                }
                                else if(mov instanceof Saldo){
                                    Saldo s = (Saldo) mov;
                                    recogerSaldo(s);


                                }

                            }

                        }
                        catch (Exception e){
                            System.out.println("Ha ocurrido un error al comprobar las credenciales del cliente: " + e.getMessage());
                        }
                }
        }
        }
    }

    private void recogerSaldo(Saldo s) {
        //el objeto saldo no lo utilizamos para nada. Lo hemos utilizado solamente para recoger la peticion del usuario hacia el servidor
        String sql = "SELECT * FROM cuentas where idCliente = '"+usuarioConectado.getId()+"'";
        try {
            Connection conexion = DB.conectar();
            ResultSet result = conexion.createStatement().executeQuery(sql);
            if (result.next()){
                String saldo = String.valueOf(result.getInt("saldo"));

                enviarMensajeCifrado(saldo);
                System.out.println("Saldo enviado");
            }
        }
        catch (Exception e){
            System.out.println("Error al enviar el saldo");
        }
    }

    private void enviarMensajeCifrado(String mensaje) {
        //Cifrar el mensaje
        byte[] mensajeCifrado = cifrarMensajeSimetrico(mensaje);
        //Enviar el mensaje cifrado
        try {
            output.writeObject(mensajeCifrado);
            System.out.println("Enviado el mensaje cifrado al servidor");
        } catch (Exception e) {
            System.out.println("Error al enviar el mensaje cifrado al servidor" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private byte[] cifrarMensajeSimetrico(String mensaje) {
        Cipher cifradorSimetrico = null;
        try {
            cifradorSimetrico = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cifradorSimetrico.init(Cipher.ENCRYPT_MODE, llavePrivadaServer);
        } catch (Exception e) {
            System.out.println("Error al crear el cifrador simétrico" + e.getMessage());
            throw new RuntimeException(e);
        }
        //Ciframos el mensaje en forma de byte
        byte[] contenidoCifrado;
        try {
            contenidoCifrado = cifradorSimetrico.doFinal(mensaje.getBytes());
            System.out.println("Contenido cifrado");
        } catch (Exception e) {
            System.out.println("Error al cifrar el contenido" + e.getMessage());
            throw new RuntimeException(e);
        }
        return contenidoCifrado;
    }

    private void crearMovimiento(Movimientos m) {
        m.setIdCliente(usuarioConectado.getId());
        //get Saldo actual para hace el ingreso/retiro
        String sql = "SELECT * from cuentas where idCliente = '"+usuarioConectado.getId()+"'";
        try{
            Connection conexion = DB.conectar();
            ResultSet result = conexion.createStatement().executeQuery(sql);
            Boolean movimientoOk = true;
            if (result.next()){
                //Preparacion para insertar movimiento en la bd y asi visualizarlo luego.
                //Posiblemente no me de tiempo.
                int saldo = result.getInt("saldo");

                int saldoNuevo = 0;
                if (m.getTipoMovimiento().equals("ingreso")){
                    saldoNuevo = saldo + m.getCantidad();
                }
                else{

                    saldoNuevo = saldo - m.getCantidad();
                    if(saldoNuevo < 0){
                        movimientoOk= false;
                    }
                }
                m.setSaldoAnterior(saldo);
                if(movimientoOk){
                    //Nos aseguramos de que en caso de hacer un retiro, la cuenta no se quede a < 0
                    String update = "UPDATE cuentas set saldo = '"+saldoNuevo+"' where idCliente = '"+usuarioConectado.getId()+"'";
                    conexion.createStatement().executeUpdate(update);

                    //TODO futura insert de movimientos

                    //Mandamos un mensaje al cliente para que se le indique el resultado de la operacion.
                    enviarMensaje(output, "movimientoRealizado");
                }
                else{
                    enviarMensaje(output, "movimientoNoRealizado");
                }
                conexion.close();

            }
        }
        catch (Exception e){
            System.out.println("Error al recoger datos de la cuenta o al efectuar el movimiento en la cuenta: "+ e.getMessage());
        }

    }

    private Boolean loginCliente(Usuario u) {
        Boolean entra = false;

        String pass = u.getContrasena();
        String user = u.getUsuario();

        String sql = "Select * from clientes where usuario = '"+user+"' and contrasena = '"+pass+"'";

        try{
            Connection conexion = DB.conectar();
            ResultSet result = conexion.createStatement().executeQuery(sql);
            if (result.next() ){
                //guardamos el id del usuario que se conecta para futuras sentencias
                usuarioConectado = new Usuario();
                usuarioConectado.setId(result.getInt("id"));
                //enviamos que el usuario ha sido encontrado para que desde el Cliente se carguen las nuevas ventanas.
                enviarMensaje(output,"usuarioEncontrado");
                entra = true;

            }
            else{
                System.out.println("usuario no encontrado");
                enviarMensaje(output,"usuarioNoEncontrado");
            }
        }
        catch (Exception e ){
            System.out.println("Error al consultar el usuario que inicia sesion: " + e.getMessage());
        }

        return entra;



    }

    private void registroCliente(Usuario u) throws SQLException {
        try{
            String sql = "Insert into clientes(nombre,apellido,edad,usuario,contrasena,email,firma_digital) VALUES('"+u.getNombre()+"','"+u.getApellido()+"','"+u.getEdad()+"', '"+ u.getUsuario()+"', '"+u.getContrasena()
                    +"', '"+u.getEmail()+"', '"+u.getFirmaDigital()+"' )";

            Connection conexion = DB.conectar();
            conexion.createStatement().executeUpdate(sql);
            conexion.close();
        }
        catch (Exception e){
            System.out.println("Error al insertar cliente: " + e.getMessage());
        }
        try{
            //Select del ultimo usuario registrado para recoger el id
            String sql = "SELECT MAX(id) as id from clientes";

            Connection conexion = DB.conectar();
            ResultSet result = conexion.createStatement().executeQuery(sql);
            result.next();
            int id = result.getInt("id");
            //crearCuenta en tabla Cuentas para este usuario todo

            int numCuenta =  (int)(Math.random()*(987654321-123456789+1)+123456789);
            //estaría bien realizar una comprobación del numero de cuenta para que no se repita pero no lo hago
            sql = "Insert into cuentas(idCliente, numCuenta, saldo) values('"+id+"','"+numCuenta+"','0')";

            conexion.createStatement().executeUpdate(sql);
            conexion.close();


        }catch (Exception e){
            System.out.println("Error al crear cuenta de cliente: " + e.getMessage());
        }

        enviarMensaje(output, "clienteRegistrado");


    }

    public Object recibirObjeto(ObjectInputStream input) {
        //Esta funcion siempre va a traer un objeto por lo que es distinta a la funcion recibirMensaje
        //recibir mensaje cifrado
        byte[] mensajeCifrado = null;

        try {
            mensajeCifrado = (byte []) input.readObject();

            System.out.println("Objeto recibido");
        } catch (Exception e) {
            System.out.println("Error al recibir el objeto cifrado" + e.getMessage());
            throw new RuntimeException(e);
        }
        //descifrar mensaje
        Object mensaje = null;
        try {
            mensaje = descifrarObjeto(mensajeCifrado);
            System.out.println("Objeto descifrado: " + mensaje);
        } catch (Exception e) {
            System.out.println("Error al descifrar el objeto" + e.getMessage());
            throw new RuntimeException(e);
        }
        return mensaje;
    }

    public Object recibirMensaje(ObjectInputStream input) {
        //Esta funcion sirve para enviar mensajes tipo String que indican la operacion a realizar. Ejemplo: consultar la base de datos para que
        // el servidor devuelva al cliente x datos.
        //recibir mensaje cifrado
        byte[] mensajeCifrado = null;

        try {
            mensajeCifrado = (byte []) input.readObject();

            System.out.println("Mensaje recibido");
        } catch (Exception e) {
            System.out.println("Error al mensaje el mensaje cifrado" + e.getMessage());
            throw new RuntimeException(e);
        }
        //descifrar mensaje
        String mensaje = null;
        try {
            mensaje = descifrarMensaje(mensajeCifrado);
            System.out.println("Mensaje descifrado: " + mensaje);
        } catch (Exception e) {
            System.out.println("Error al descifrar el mensaje" + e.getMessage());
            throw new RuntimeException(e);
        }
        return mensaje;
    }

    public Object descifrarObjeto(byte [] contenido) {
        //Crear el descifrador simétrico
        Cipher descifrador = null;
        try {
            descifrador = Cipher.getInstance("AES/ECB/PKCS5Padding");
            descifrador.init(Cipher.DECRYPT_MODE, new SecretKeySpec(claveSimetrica, "AES"));
            System.out.println("Creado el descifrador simétrico");
        } catch (Exception e) {
            System.out.println("Error al crear el descifrador simétrico" + e.getMessage());
            throw new RuntimeException(e);
        }


        byte[] contenidoDescifrado = null;
        Object o = null;
        
        try {
            //Desciframos el objeto en forma de byte[] para luego montarlo en el ByteArrayInputStream
            contenidoDescifrado = descifrador.doFinal(contenido);
            ByteArrayInputStream bis = new ByteArrayInputStream(contenidoDescifrado);
            ObjectInput in = null;
            try {
                //Convertimos el ByteArrayInputStream en un Object
                in = new ObjectInputStream(bis);
                o = in.readObject();
                //o es el objeto que devolvemos.
                //Asi devuelvo cualquier tipo de objeto independientemente de la clase a la que haga referencia.
                //Mas adelante me encargo de convertir el objeto Object en un objeto de una Clase para luego poder realizar operaciones.

            }
            catch (Exception e){
                System.out.println("Error: " + e.getMessage());

            }
            System.out.println("Objeto descifrado");
        } catch (Exception e) {
            System.out.println("Error al descifrar el objeto" + e.getMessage());
            throw new RuntimeException(e);
        }
        return o;
    }
    public String descifrarMensaje(byte [] contenido) {
        //Crear el descifrador simétrico
        Cipher descifrador = null;
        try {
            descifrador = Cipher.getInstance("AES/ECB/PKCS5Padding");
            descifrador.init(Cipher.DECRYPT_MODE, new SecretKeySpec(claveSimetrica, "AES"));
            System.out.println("Creado el descifrador simétrico");
        } catch (Exception e) {
            System.out.println("Error al crear el descifrador simétrico" + e.getMessage());
            throw new RuntimeException(e);
        }

        String contenidoDescifrado = null;

        try {
            contenidoDescifrado = descifrador.doFinal(contenido).toString();
            System.out.println("Mensaje descifrado");
        } catch (Exception e) {
            System.out.println("Error al descifrar el mensaje" + e.getMessage());
            throw new RuntimeException(e);
        }
        return contenidoDescifrado;
    }

    private static void enviarMensaje(ObjectOutputStream output, String mensaje) {
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
}
