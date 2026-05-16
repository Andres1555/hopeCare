package com.esperanza.hopecare.modules.medicamentos_lab.model;

import java.time.LocalDateTime;

public class EntregaMedicamento {
    private int idEntrega;
    private int idDetalleReceta;
    private int cantidadEntregada;
    private int entregadoPor;
    private LocalDateTime fechaEntrega;
    private boolean facturado;

    public EntregaMedicamento() {}

    public EntregaMedicamento(int idEntrega, int idDetalleReceta, int cantidadEntregada, int entregadoPor) {
        this.idEntrega = idEntrega;
        this.idDetalleReceta = idDetalleReceta;
        this.cantidadEntregada = cantidadEntregada;
        this.entregadoPor = entregadoPor;
        this.fechaEntrega = LocalDateTime.now();
        this.facturado = false;
    }

    public EntregaMedicamento(int idDetalleReceta, int cantidadEntregada, int entregadoPor) {
        this.idDetalleReceta = idDetalleReceta;
        this.cantidadEntregada = cantidadEntregada;
        this.entregadoPor = entregadoPor;
        this.fechaEntrega = LocalDateTime.now();
        this.facturado = false;
    }

    public int getIdEntrega() { return idEntrega; }
    public void setIdEntrega(int idEntrega) { this.idEntrega = idEntrega; }
    public int getIdDetalleReceta() { return idDetalleReceta; }
    public void setIdDetalleReceta(int idDetalleReceta) { this.idDetalleReceta = idDetalleReceta; }
    public int getCantidadEntregada() { return cantidadEntregada; }
    public void setCantidadEntregada(int cantidadEntregada) { this.cantidadEntregada = cantidadEntregada; }
    public int getEntregadoPor() { return entregadoPor; }
    public void setEntregadoPor(int entregadoPor) { this.entregadoPor = entregadoPor; }
    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDateTime fechaEntrega) { this.fechaEntrega = fechaEntrega; }
    public boolean isFacturado() { return facturado; }
    public void setFacturado(boolean facturado) { this.facturado = facturado; }
}
