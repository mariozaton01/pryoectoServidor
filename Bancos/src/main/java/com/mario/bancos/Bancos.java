/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.mario.bancos;
import com.mario.bancos.Ventanas.*;

/**
 *
 * @author MarioZatonToledo
 */
public class Bancos {
    private static Ventana_login v_login;

    public static void main(String[] args) {
        setInicio();
    }
    
    public static void setInicio(){
        v_login = new Ventana_login();
        v_login.setVisible(true);
        v_login.setLocationRelativeTo(null);
    }
}
