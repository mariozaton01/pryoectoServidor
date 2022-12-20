package com.mario.bancos.Clases;

import java.io.Serializable;

public class Saldo implements Serializable {
    String metodo;

    public Saldo(String metodo) {
        this.metodo = metodo;
    }
}
