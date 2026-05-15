package com.esperanza.hopecare.modules.medicamentos_lab.model;

public class DetalleReceta {
    private int idDetalle;
    private int idReceta;
    private int idMedicamento;
    private int cantidad;
    private String dosisIndicacion;

    public DetalleReceta() {}

    public DetalleReceta(int idDetalle, int idReceta, int idMedicamento, int cantidad, String dosisIndicacion) {
        this.idDetalle = idDetalle;
        this.idReceta = idReceta;
        this.idMedicamento = idMedicamento;
        this.cantidad = cantidad;
        this.dosisIndicacion = dosisIndicacion;
    }

    public DetalleReceta(int idReceta, int idMedicamento, int cantidad, String dosisIndicacion) {
        this.idReceta = idReceta;
        this.idMedicamento = idMedicamento;
        this.cantidad = cantidad;
        this.dosisIndicacion = dosisIndicacion;
    }

    public int getIdDetalle() { return idDetalle; }
    public void setIdDetalle(int idDetalle) { this.idDetalle = idDetalle; }
    public int getIdReceta() { return idReceta; }
    public void setIdReceta(int idReceta) { this.idReceta = idReceta; }
    public int getIdMedicamento() { return idMedicamento; }
    public void setIdMedicamento(int idMedicamento) { this.idMedicamento = idMedicamento; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public String getDosisIndicacion() { return dosisIndicacion; }
    public void setDosisIndicacion(String dosisIndicacion) { this.dosisIndicacion = dosisIndicacion; }
}
