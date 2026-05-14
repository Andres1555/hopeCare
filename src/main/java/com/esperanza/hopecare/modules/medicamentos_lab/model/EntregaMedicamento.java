package com.esperanza.hopecare.modules.medicamentos_lab.model;

public class EntregaMedicamento {
    private int idEntrega;
    private int idReceta;
    private int idMedicamento;
    private int cantidad;

    public EntregaMedicamento() {}

    public EntregaMedicamento(int idEntrega, int idReceta, int idMedicamento, int cantidad) {
        this.idEntrega = idEntrega;
        this.idReceta = idReceta;
        this.idMedicamento = idMedicamento;
        this.cantidad = cantidad;
    }

    // Getters and Setters
    public int getIdEntrega() { return idEntrega; }
    public void setIdEntrega(int idEntrega) { this.idEntrega = idEntrega; }
    public int getIdReceta() { return idReceta; }
    public void setIdReceta(int idReceta) { this.idReceta = idReceta; }
    public int getIdMedicamento() { return idMedicamento; }
    public void setIdMedicamento(int idMedicamento) { this.idMedicamento = idMedicamento; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
