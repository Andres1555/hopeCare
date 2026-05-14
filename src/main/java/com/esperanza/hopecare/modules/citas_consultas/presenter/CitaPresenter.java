package com.esperanza.hopecare.modules.citas_consultas.presenter;

import com.esperanza.hopecare.modules.citas_consultas.dao.HorarioAtencionDAO;
import com.esperanza.hopecare.modules.citas_consultas.dao.CitaDAO;
import com.esperanza.hopecare.modules.citas_consultas.model.Cita;
import com.esperanza.hopecare.modules.citas_consultas.model.HorarioAtencion;
import com.esperanza.hopecare.modules.citas_consultas.view.ICitaView;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import com.esperanza.hopecare.common.events.EventBus;
import com.esperanza.hopecare.common.events.NuevaCitaEvent;

public class CitaPresenter {
    private ICitaView view;
    private HorarioAtencionDAO horarioDAO;
    private CitaDAO citaDAO;

    public CitaPresenter(ICitaView view) {
        this.view = view;
        this.horarioDAO = new HorarioAtencionDAO();
        this.citaDAO = new CitaDAO();
    }

    /**
     * Calcula los bloques de tiempo disponibles para un médico en una fecha específica.
     * Lee el horario de atención (día de semana, hora_inicio, hora_fin, intervalo_minutos)
     * y luego filtra las horas ya ocupadas por citas existentes.
     * @param idMedico ID del médico
     * @param fecha fecha seleccionada
     */
    public void actualizarHorariosDisponibles(int idMedico, LocalDate fecha) {
        // 1. Obtener día de la semana (1=lunes, 7=domingo, según SQLite)
        int diaSemana = fecha.getDayOfWeek().getValue(); // lunes=1, domingo=7
        HorarioAtencion horario = horarioDAO.obtenerHorarioPorMedicoYDia(idMedico, diaSemana);
        
        if (horario == null) {
            view.mostrarMensajeError("El médico no atiende ese día.");
            view.mostrarHorariosDisponibles(new ArrayList<>());
            return;
        }
        
        // 2. Generar todos los bloques según intervalo_minutos
        List<LocalTime> todosBloques = generarBloques(
            horario.getHoraInicio(),
            horario.getHoraFin(),
            horario.getIntervaloMinutos()
        );
        
        // 3. Obtener citas ya reservadas para ese médico y fecha
        List<Cita> citasOcupadas = citaDAO.obtenerCitasPorMedicoYFecha(idMedico, fecha);
        List<LocalTime> horariosOcupados = new ArrayList<>();
        for (Cita c : citasOcupadas) {
            horariosOcupados.add(c.getFechaHora().toLocalTime());
        }
        
        // 4. Filtrar bloques disponibles (los que no están ocupados)
        List<LocalTime> disponibles = new ArrayList<>();
        for (LocalTime bloque : todosBloques) {
            if (!horariosOcupados.contains(bloque)) {
                disponibles.add(bloque);
            }
        }
        
        view.mostrarHorariosDisponibles(disponibles);
    }
    
    /**
     * Genera los bloques de tiempo desde horaInicio hasta horaFin,
     * saltando cada intervaloMinutos.
     */
    private List<LocalTime> generarBloques(LocalTime inicio, LocalTime fin, int intervaloMinutos) {
        List<LocalTime> bloques = new ArrayList<>();
        LocalTime actual = inicio;
        while (!actual.isAfter(fin)) {
            bloques.add(actual);
            actual = actual.plusMinutes(intervaloMinutos);
        }
        return bloques;
    }
    
    /**
     * Reserva una cita (llamado desde la vista).
     */
    public void reservarCita() {
        int idPaciente = view.getIdPacienteSeleccionado();
        int idMedico = view.getIdMedicoSeleccionado();
        LocalDate fecha = view.getFechaSeleccionada();
        LocalTime hora = view.getHoraSeleccionada();
        
        if (idPaciente <= 0 || idMedico <= 0 || fecha == null || hora == null) {
            view.mostrarMensajeError("Complete todos los campos.");
            return;
        }
        
        // Validar que el horario esté dentro del bloque permitido (se podría repetir la lógica)
        // pero asumimos que solo se muestran disponibles.
        Cita nuevaCita = new Cita(idPaciente, idMedico, fecha.atTime(hora), "PROGRAMADA");
        boolean exito = citaDAO.insertarCita(nuevaCita);
        if (exito) {
            EventBus.getInstance().post(new NuevaCitaEvent(nuevaCita.getIdCita(), nuevaCita.getFechaHora()));
            view.mostrarMensajeExito("Cita reservada exitosamente.");
            view.limpiarCampos();
            // Refrescar horarios disponibles
            actualizarHorariosDisponibles(idMedico, fecha);
        } else {
            view.mostrarMensajeError("Error al reservar la cita.");
        }
    }
}
