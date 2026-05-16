package com.esperanza.hopecare.modules.citas_consultas.model;

import java.time.LocalDateTime;

public class Consulta {
    private int idConsulta;
    private int idCita;
    private String diagnostico;
    private String sintomas;
    private String tratamiento;
    private String notasMedicas;
    private LocalDateTime fechaConsulta;
    private boolean facturado;
    private double precio;

    public Consulta() {}

    public Consulta(int idCita, String diagnostico, String sintomas, String tratamiento, boolean facturado, double precio) {
        this.idCita = idCita;
        this.diagnostico = diagnostico;
        this.sintomas = sintomas;
        this.tratamiento = tratamiento;
        this.facturado = facturado;
        this.precio = precio;
    }

    public Consulta(int idCita, String diagnostico, String sintomas, String tratamiento, boolean facturado) {
        this(idCita, diagnostico, sintomas, tratamiento, facturado, 0.0);
    }

    public int getIdConsulta() { return idConsulta; }
    public void setIdConsulta(int idConsulta) { this.idConsulta = idConsulta; }
    public int getIdCita() { return idCita; }
    public void setIdCita(int idCita) { this.idCita = idCita; }
    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }
    public String getSintomas() { return sintomas; }
    public void setSintomas(String sintomas) { this.sintomas = sintomas; }
    public String getTratamiento() { return tratamiento; }
    public void setTratamiento(String tratamiento) { this.tratamiento = tratamiento; }
    public String getNotasMedicas() { return notasMedicas; }
    public void setNotasMedicas(String notasMedicas) { this.notasMedicas = notasMedicas; }
    public LocalDateTime getFechaConsulta() { return fechaConsulta; }
    public void setFechaConsulta(LocalDateTime fechaConsulta) { this.fechaConsulta = fechaConsulta; }
    public boolean isFacturado() { return facturado; }
    public void setFacturado(boolean facturado) { this.facturado = facturado; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
}
