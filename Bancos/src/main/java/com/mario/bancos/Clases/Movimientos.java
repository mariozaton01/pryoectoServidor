package com.mario.bancos.Clases;

import java.io.Serializable;
import java.util.Date;

public class Movimientos implements Serializable {

    int id;
    int saldoAnterior;
    String tipoMovimiento;
    String fecha;
    int idCliente;
    int cantidad;

    public Movimientos(int saldoAnterior, String tipoMovimiento, String fecha, int idCliente, int cantidad) {
        this.saldoAnterior = saldoAnterior;
        this.tipoMovimiento = tipoMovimiento;
        this.fecha = fecha;
        this.idCliente = idCliente;
        this.cantidad = cantidad;
    }

    public Movimientos() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(int saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "Movimientos{" +
                "id=" + id +
                ", saldoAnterior=" + saldoAnterior +
                ", tipoMovimiento='" + tipoMovimiento + '\'' +
                ", fecha=" + fecha +
                ", idCliente=" + idCliente +
                ", cantidad=" + cantidad +
                '}';
    }
}
