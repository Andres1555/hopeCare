package com.esperanza.hopecare.modules.dashboard.observer.events;

import java.time.LocalDateTime;
import java.util.EventObject;

public class NuevaCitaEvent extends EventObject {
    private final int idCita;
    private final LocalDateTime fechaHora;

    public NuevaCitaEvent(Object source, int idCita, LocalDateTime fechaHora) {
        super(source);
        this.idCita = idCita;
        this.fechaHora = fechaHora;
    }

    public int getIdCita() { return idCita; }
    public LocalDateTime getFechaHora() { return fechaHora; }
}
