/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.mario.bancos;
import com.mario.bancos.Ventanas.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author MarioZatonToledo
 */
public class Bancos {
    private static Ventana_login v_login;
    private static ObjectOutputStream output;
    private static ObjectInputStream input;

    
    public static void setInicio(ObjectOutputStream out, ObjectInputStream in){
        output = out;
        input = in;

        v_login = new Ventana_login();
        v_login.setVisible(true);
        v_login.setLocationRelativeTo(null);
    }
}
