package com.esperanza.hopecare.modules.medicamentos_lab.model;

public class Medicamento {
    private int idMedicamento;
    private String nombre;
    private int stockActual;

    public Medicamento() {}

    public Medicamento(int idMedicamento, String nombre, int stockActual) {
        this.idMedicamento = idMedicamento;
        this.nombre = nombre;
        this.stockActual = stockActual;
    }

    // Getters and Setters
    public int getIdMedicamento() { return idMedicamento; }
    public void setIdMedicamento(int idMedicamento) { this.idMedicamento = idMedicamento; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getStockActual() { return stockActual; }
    public void setStockActual(int stockActual) { this.stockActual = stockActual; }

    @Override
    public String toString() {
        return nombre + " (Stock: " + stockActual + ")";
    }
}
