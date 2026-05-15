package com.esperanza.hopecare.modules.citas_consultas.view;

import com.esperanza.hopecare.modules.citas_consultas.model.Cita;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ICitaView {
    void mostrarCitasExistentes(List<Cita> citas);
    void mostrarHorariosDisponibles(List<LocalTime> bloques);
    void mostrarDiasDisponibles(List<Integer> diasSemana);
    int getDiaSeleccionado();
    void mostrarMensajeError(String mensaje);
    void mostrarMensajeExito(String mensaje);
    void limpiarCampos();
    int getIdPacienteSeleccionado();
    int getIdMedicoSeleccionado();
    LocalDate getFechaSeleccionada();
    LocalTime getHoraSeleccionada();
}
