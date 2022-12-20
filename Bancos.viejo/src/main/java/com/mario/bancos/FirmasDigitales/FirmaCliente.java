package com.mario.bancos.FirmasDigitales;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PublicKey;
import java.security.Signature;

public class FirmaCliente {

    public static void firmaCliente(ObjectInputStream input) {

        //Obtener la clave publica del servidor
        PublicKey clavePublica = null;
        try {
            clavePublica = (PublicKey) input.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }

        String mensaje = null;
        try {
            // Obtencion del documento a firmar
            mensaje = (String) input.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Documento a firmar " + mensaje);

        //Obtener la firma del servidor del mensaje
        byte[] firmaBytes = null;
        try {
            //El usuario lee la firma del documento por parte del servidor
            firmaBytes = (byte[]) input.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }

        Signature firma = null;
        try {
            //Comprobacion de que el servidor ha firmado el documento.
            firma = Signature.getInstance("SHA1WITHRSA");
            firma.initVerify(clavePublica);
            firma.update(mensaje.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        boolean verificacion = false;
        try {
            verificacion = firma.verify(firmaBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if(verificacion){
            System.out.println("Firma correcta");
        } else {
            System.out.println("Firma incorrecta");
        }


    }


}
