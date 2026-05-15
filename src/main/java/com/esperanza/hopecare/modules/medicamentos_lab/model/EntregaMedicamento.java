package com.esperanza.hopecare.modules.medicamentos_lab.model;

import java.time.LocalDateTime;

public class EntregaMedicamento {
    private int idEntrega;
    private int idReceta;
    private int idMedicamento;
    private int cantidad;
    private LocalDateTime fechaEntrega;
    private boolean facturado;

    public EntregaMedicamento() {}

    public EntregaMedicamento(int idEntrega, int idReceta, int idMedicamento, int cantidad) {
        this.idEntrega = idEntrega;
        this.idReceta = idReceta;
        this.idMedicamento = idMedicamento;
        this.cantidad = cantidad;
    }

    public EntregaMedicamento(int idReceta, int idMedicamento, int cantidad) {
        this.idReceta = idReceta;
        this.idMedicamento = idMedicamento;
        this.cantidad = cantidad;
        this.fechaEntrega = LocalDateTime.now();
        this.facturado = false;
    }

    public int getIdEntrega() { return idEntrega; }
    public void setIdEntrega(int idEntrega) { this.idEntrega = idEntrega; }
    public int getIdReceta() { return idReceta; }
    public void setIdReceta(int idReceta) { this.idReceta = idReceta; }
    public int getIdMedicamento() { return idMedicamento; }
    public void setIdMedicamento(int idMedicamento) { this.idMedicamento = idMedicamento; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDateTime fechaEntrega) { this.fechaEntrega = fechaEntrega; }
    public boolean isFacturado() { return facturado; }
    public void setFacturado(boolean facturado) { this.facturado = facturado; }
}
