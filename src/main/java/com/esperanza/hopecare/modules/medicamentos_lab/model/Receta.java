package com.esperanza.hopecare.modules.medicamentos_lab.model;

import java.time.LocalDateTime;

public class Receta {
    private int idReceta;
    private int idConsulta;
    private LocalDateTime fechaEmision;
    private String instrucciones;
    private boolean activa;

    public Receta() {}

    public Receta(int idReceta, int idConsulta, LocalDateTime fechaEmision, String instrucciones, boolean activa) {
        this.idReceta = idReceta;
        this.idConsulta = idConsulta;
        this.fechaEmision = fechaEmision;
        this.instrucciones = instrucciones;
        this.activa = activa;
    }

    public Receta(int idConsulta, String instrucciones) {
        this.idConsulta = idConsulta;
        this.fechaEmision = LocalDateTime.now();
        this.instrucciones = instrucciones;
        this.activa = true;
    }

    public int getIdReceta() { return idReceta; }
    public void setIdReceta(int idReceta) { this.idReceta = idReceta; }
    public int getIdConsulta() { return idConsulta; }
    public void setIdConsulta(int idConsulta) { this.idConsulta = idConsulta; }
    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }
    public String getInstrucciones() { return instrucciones; }
    public void setInstrucciones(String instrucciones) { this.instrucciones = instrucciones; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
}
