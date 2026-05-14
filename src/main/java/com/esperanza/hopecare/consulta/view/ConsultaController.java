package com.esperanza.hopecare.consulta.view;

import com.esperanza.hopecare.modules.citas_consultas.model.Cita;
import com.esperanza.hopecare.modules.citas_consultas.dao.CitaDAO;
import com.esperanza.hopecare.modules.citas_consultas.model.Consulta;
import com.esperanza.hopecare.modules.citas_consultas.dao.ConsultaDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConsultaController {
    @FXML private ComboBox<String> cbCitasPendientes;
    @FXML private TextArea txtSintomas, txtDiagnostico, txtTratamiento;
    @FXML private Button btnCargar, btnGuardar, btnSolicitarExamen, btnRecetar;

    private CitaDAO citaDAO;
    private ConsultaDAO consultaDAO;
    private ObservableList<String> citasList;
    private int idCitaSeleccionada = -1;

    @FXML
    public void initialize() {
        citaDAO = new CitaDAO();
        consultaDAO = new ConsultaDAO();
        citasList = FXCollections.observableArrayList();
        cbCitasPendientes.setItems(citasList);

        btnCargar.setOnAction(e -> seleccionarCita());
        btnGuardar.setOnAction(e -> guardarConsulta());
        btnSolicitarExamen.setOnAction(e -> solicitarExamen());
        btnRecetar.setOnAction(e -> recetarMedicamento());

        cargarCitas();
    }

    private void cargarCitas() {
        List<Cita> citas = citaDAO.obtenerCitasPorEstado("PROGRAMADA");
        citasList.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Cita c : citas) {
            String texto = c.getIdCita() + " - Paciente: " + c.getIdPaciente() +
                           " - Médico: " + c.getIdMedico() +
                           " - " + c.getFechaHora().format(formatter);
            citasList.add(texto);
        }
        if (citasList.isEmpty()) {
            cbCitasPendientes.setPromptText("No hay citas pendientes");
        }
    }

    private void seleccionarCita() {
        if (cbCitasPendientes.getSelectionModel().isEmpty()) {
            mostrarAlerta("Error", "Seleccione una cita", Alert.AlertType.ERROR);
            return;
        }
        String selected = cbCitasPendientes.getValue();
        idCitaSeleccionada = Integer.parseInt(selected.split(" - ")[0]);
        mostrarAlerta("Éxito", "Cita cargada", Alert.AlertType.INFORMATION);
    }

    private void guardarConsulta() {
        if (idCitaSeleccionada == -1) {
            mostrarAlerta("Error", "Seleccione una cita primero", Alert.AlertType.ERROR);
            return;
        }
        String sintomas = txtSintomas.getText().trim();
        String diagnostico = txtDiagnostico.getText().trim();
        String tratamiento = txtTratamiento.getText().trim();

        if (sintomas.isEmpty() || diagnostico.isEmpty()) {
            mostrarAlerta("Error", "Síntomas y diagnóstico son obligatorios", Alert.AlertType.ERROR);
            return;
        }

        Consulta consulta = new Consulta(idCitaSeleccionada, diagnostico, sintomas, tratamiento, false);
        boolean ok = consultaDAO.insertarConsultaYActualizarEstado(consulta);
        if (ok) {
            mostrarAlerta("Éxito", "Consulta registrada correctamente", Alert.AlertType.INFORMATION);
            limpiarCampos();
            cargarCitas(); // refrescar lista
        } else {
            mostrarAlerta("Error", "No se pudo registrar la consulta", Alert.AlertType.ERROR);
        }
    }

    private void solicitarExamen() {
        if (idCitaSeleccionada == -1) {
            mostrarAlerta("Error", "Primero seleccione y guarde la consulta", Alert.AlertType.ERROR);
            return;
        }
        mostrarAlerta("Info", "Funcionalidad: abrir diálogo para seleccionar examen", Alert.AlertType.INFORMATION);
    }

    private void recetarMedicamento() {
        if (idCitaSeleccionada == -1) {
            mostrarAlerta("Error", "Primero seleccione y guarde la consulta", Alert.AlertType.ERROR);
            return;
        }
        mostrarAlerta("Info", "Funcionalidad: abrir diálogo para recetar medicamento", Alert.AlertType.INFORMATION);
    }

    private void limpiarCampos() {
        txtSintomas.clear();
        txtDiagnostico.clear();
        txtTratamiento.clear();
        idCitaSeleccionada = -1;
        cbCitasPendientes.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
