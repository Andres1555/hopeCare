package com.esperanza.hopecare.modules.medicamentos_lab.model;

public class ExamenLaboratorio {
    private int idExamen;
    private String nombreExamen;
    private String descripcion;
    private double precio;
    private int tiempoResultadoHoras;
    private byte[] resultadoArchivo;

    public ExamenLaboratorio() {}

    public ExamenLaboratorio(int idExamen, String nombreExamen, String descripcion, double precio, int tiempoResultadoHoras) {
        this.idExamen = idExamen;
        this.nombreExamen = nombreExamen;
        this.descripcion = descripcion;
        this.precio = precio;
        this.tiempoResultadoHoras = tiempoResultadoHoras;
    }

    public ExamenLaboratorio(String nombreExamen, double precio) {
        this.nombreExamen = nombreExamen;
        this.precio = precio;
    }

    public int getIdExamen() { return idExamen; }
    public void setIdExamen(int idExamen) { this.idExamen = idExamen; }
    public String getNombreExamen() { return nombreExamen; }
    public void setNombreExamen(String nombreExamen) { this.nombreExamen = nombreExamen; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public int getTiempoResultadoHoras() { return tiempoResultadoHoras; }
    public void setTiempoResultadoHoras(int tiempoResultadoHoras) { this.tiempoResultadoHoras = tiempoResultadoHoras; }
    public byte[] getResultadoArchivo() { return resultadoArchivo; }
    public void setResultadoArchivo(byte[] resultadoArchivo) { this.resultadoArchivo = resultadoArchivo; }

    @Override
    public String toString() {
        return nombreExamen + " ($" + String.format("%.2f", precio) + ")";
    }
}
