package com.esperanza.hopecare.modules.citas_consultas.view.impl;

import com.esperanza.hopecare.modules.citas_consultas.view.ICitaView;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

public class CitaConsoleView implements ICitaView {
    private Scanner scanner = new Scanner(System.in);
    private List<LocalTime> ultimosHorarios;

    @Override
    public void mostrarHorariosDisponibles(List<LocalTime> bloques) {
        this.ultimosHorarios = bloques;
        if (bloques.isEmpty()) {
            System.out.println("No hay horarios disponibles para esa fecha.");
        } else {
            System.out.println("Horarios disponibles:");
            for (int i = 0; i < bloques.size(); i++) {
                System.out.println((i+1) + ". " + bloques.get(i));
            }
        }
    }

    @Override
    public void mostrarMensajeError(String mensaje) {
        System.err.println("ERROR: " + mensaje);
    }

    @Override
    public void mostrarMensajeExito(String mensaje) {
        System.out.println("ÉXITO: " + mensaje);
    }

    @Override
    public void limpiarCampos() {
        // No necesario en consola
    }

    @Override
    public int getIdPacienteSeleccionado() {
        System.out.print("ID del paciente: ");
        return Integer.parseInt(scanner.nextLine());
    }

    @Override
    public int getIdMedicoSeleccionado() {
        System.out.print("ID del médico: ");
        return Integer.parseInt(scanner.nextLine());
    }

    @Override
    public LocalDate getFechaSeleccionada() {
        System.out.print("Fecha (YYYY-MM-DD): ");
        return LocalDate.parse(scanner.nextLine());
    }

    @Override
    public void mostrarDiasDisponibles(List<Integer> diasSemana) {
        if (diasSemana.isEmpty()) {
            System.out.println("El médico no tiene días de atención configurados.");
            return;
        }
        String[] nombres = {"", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        System.out.println("Días disponibles del médico:");
        for (int d : diasSemana) {
            System.out.println(" - " + nombres[d]);
        }
    }

    @Override
    public int getDiaSeleccionado() {
        System.out.print("Ingrese día de la semana (1=Lunes...7=Domingo): ");
        return Integer.parseInt(scanner.nextLine());
    }

    @Override
    public LocalTime getHoraSeleccionada() {
        if (ultimosHorarios == null || ultimosHorarios.isEmpty()) {
            System.out.print("Hora (HH:MM): ");
            return LocalTime.parse(scanner.nextLine());
        } else {
            System.out.print("Seleccione número de horario: ");
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            return ultimosHorarios.get(idx);
        }
    }
}
