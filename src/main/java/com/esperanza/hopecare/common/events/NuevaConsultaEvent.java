package com.esperanza.hopecare.common.events;

public class NuevaConsultaEvent {
    private final int idConsulta;
    private final int idCita;

    public NuevaConsultaEvent(int idConsulta, int idCita) {
        this.idConsulta = idConsulta;
        this.idCita = idCita;
    }

    public int getIdConsulta() { return idConsulta; }
    public int getIdCita() { return idCita; }
}