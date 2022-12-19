package com.mario.bancos.Clases;

import java.util.Date;

public class Movimientos {

    int id;
    int saldoAnterior;
    int tipoMovimiento;
    Date fecha;
    int idCliente;
    int cantidad;

    public Movimientos(int saldoAnterior, int tipoMovimiento, Date fecha, int idCliente, int cantidad) {
        this.saldoAnterior = saldoAnterior;
        this.tipoMovimiento = tipoMovimiento;
        this.fecha = fecha;
        this.idCliente = idCliente;
        this.cantidad = cantidad;
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

    public int getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(int tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
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
}
