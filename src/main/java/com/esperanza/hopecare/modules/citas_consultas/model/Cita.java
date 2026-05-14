package com.esperanza.hopecare.modules.citas_consultas.model;

import java.time.LocalDateTime;

public class Cita {
    private int idCita;
    private int idPaciente;
    private int idMedico;
    private LocalDateTime fechaHora;
    private String estado;

    public Cita() {}

    public Cita(int idPaciente, int idMedico, LocalDateTime fechaHora, String estado) {
        this.idPaciente = idPaciente;
        this.idMedico = idMedico;
        this.fechaHora = fechaHora;
        this.estado = estado;
    }

    // Getters and Setters
    public int getIdCita() { return idCita; }
    public void setIdCita(int idCita) { this.idCita = idCita; }
    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }
    public int getIdMedico() { return idMedico; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
