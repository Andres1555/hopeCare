package com.esperanza.hopecare.modules.medicamentos_lab.model;

public class Medicamento {
    private int idMedicamento;
    private String nombre;
    private int stockActual;
    private int stockMinimo;

    public Medicamento() {}

    public Medicamento(int idMedicamento, String nombre, int stockActual) {
        this.idMedicamento = idMedicamento;
        this.nombre = nombre;
        this.stockActual = stockActual;
    }

    public Medicamento(int idMedicamento, String nombre, int stockActual, int stockMinimo) {
        this.idMedicamento = idMedicamento;
        this.nombre = nombre;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
    }

    public boolean stockBajo() {
        return stockActual <= stockMinimo;
    }

    public int getIdMedicamento() { return idMedicamento; }
    public void setIdMedicamento(int idMedicamento) { this.idMedicamento = idMedicamento; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getStockActual() { return stockActual; }
    public void setStockActual(int stockActual) { this.stockActual = stockActual; }
    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }

    @Override
    public String toString() {
        return nombre + " (Stock: " + stockActual + ")";
    }
}
