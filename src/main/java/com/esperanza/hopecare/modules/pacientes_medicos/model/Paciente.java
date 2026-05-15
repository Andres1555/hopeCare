package com.esperanza.hopecare.modules.pacientes_medicos.model;

import com.esperanza.hopecare.common.model.Persona;

public class Paciente extends Persona {
    private int idPaciente;
    private String historiaClinica;

    public Paciente() {}

    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }

    public String getHistoriaClinica() { return historiaClinica; }
    public void setHistoriaClinica(String historiaClinica) { this.historiaClinica = historiaClinica; }
}
