package com.esperanza.hopecare.modules.citas_consultas.view;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Interfaz que define los métodos que debe implementar cualquier vista
 * (Swing, JavaFX, consola) para gestionar citas.
 */
public interface ICitaView {
    void mostrarHorariosDisponibles(List<LocalTime> bloques);
    void mostrarMensajeError(String mensaje);
    void mostrarMensajeExito(String mensaje);
    void limpiarCampos();
    int getIdPacienteSeleccionado();
    int getIdMedicoSeleccionado();
    LocalDate getFechaSeleccionada();
    LocalTime getHoraSeleccionada();
}
