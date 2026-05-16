package com.esperanza.hopecare.modules.medicamentos_lab.model;

public class Medicamento {
    private int idMedicamento;
    private String nombreComercial;
    private String principioActivo;
    private String presentacion;
    private String concentracion;
    private double precioUnitario;
    private int stockActual;
    private int stockMinimo;
    private boolean requiereReceta;

    public Medicamento() {}

    public Medicamento(int idMedicamento, String nombreComercial, String principioActivo, String presentacion, 
                   String concentracion, double precioUnitario, int stockActual, int stockMinimo, boolean requiereReceta) {
        this.idMedicamento = idMedicamento;
        this.nombreComercial = nombreComercial;
        this.principioActivo = principioActivo;
        this.presentacion = presentacion;
        this.concentracion = concentracion;
        this.precioUnitario = precioUnitario;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.requiereReceta = requiereReceta;
    }

    public boolean stockBajo() {
        return stockActual <= stockMinimo;
    }

    public int getIdMedicamento() { return idMedicamento; }
    public void setIdMedicamento(int idMedicamento) { this.idMedicamento = idMedicamento; }
    public String getNombreComercial() { return nombreComercial; }
    public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }
    public String getPrincipioActivo() { return principioActivo; }
    public void setPrincipioActivo(String principioActivo) { this.principioActivo = principioActivo; }
    public String getPresentacion() { return presentacion; }
    public void setPresentacion(String presentacion) { this.presentacion = presentacion; }
    public String getConcentracion() { return concentracion; }
    public void setConcentracion(String concentracion) { this.concentracion = concentracion; }
    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
    public int getStockActual() { return stockActual; }
    public void setStockActual(int stockActual) { this.stockActual = stockActual; }
    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }
    public boolean isRequiereReceta() { return requiereReceta; }
    public void setRequiereReceta(boolean requiereReceta) { this.requiereReceta = requiereReceta; }

    @Override
    public String toString() {
        return nombreComercial + " (Stock: " + stockActual + ", Precio: " + precioUnitario + ")";
    }
}
