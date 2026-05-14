package com.esperanza.hopecare.modules.citas_consultas.model;

public class Consulta {
    private int idCita;
    private String diagnostico;
    private String sintomas;
    private String tratamiento;
    private boolean facturado;

    public Consulta() {}

    public Consulta(int idCita, String diagnostico, String sintomas, String tratamiento, boolean facturado) {
        this.idCita = idCita;
        this.diagnostico = diagnostico;
        this.sintomas = sintomas;
        this.tratamiento = tratamiento;
        this.facturado = facturado;
    }

    // Getters and Setters
    public int getIdCita() { return idCita; }
    public void setIdCita(int idCita) { this.idCita = idCita; }
    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }
    public String getSintomas() { return sintomas; }
    public void setSintomas(String sintomas) { this.sintomas = sintomas; }
    public String getTratamiento() { return tratamiento; }
    public void setTratamiento(String tratamiento) { this.tratamiento = tratamiento; }
    public boolean isFacturado() { return facturado; }
    public void setFacturado(boolean facturado) { this.facturado = facturado; }
}
