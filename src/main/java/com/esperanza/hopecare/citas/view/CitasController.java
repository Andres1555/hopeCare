package com.esperanza.hopecare.citas.view;

import com.esperanza.hopecare.modules.citas_consultas.presenter.CitaPresenter;
import com.esperanza.hopecare.modules.citas_consultas.view.ICitaView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class CitasController implements ICitaView {
    @FXML private TextField txtIdPaciente;
    @FXML private TextField txtIdMedico;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cbHorarios;
    @FXML private javafx.scene.control.Button btnBuscar;
    @FXML private javafx.scene.control.Button btnReservar;

    private CitaPresenter presenter;
    private List<LocalTime> horariosActuales;

    @FXML
    public void initialize() {
        presenter = new CitaPresenter(this);
        btnBuscar.setOnAction(e -> buscarHorarios());
        btnReservar.setOnAction(e -> presenter.reservarCita());
        cbHorarios.setDisable(true);
        btnReservar.setDisable(true);
    }

    private void buscarHorarios() {
        try {
            int idMedico = Integer.parseInt(txtIdMedico.getText());
            LocalDate fecha = dpFecha.getValue();
            if (fecha == null) {
                mostrarMensajeError("Seleccione una fecha");
                return;
            }
            presenter.actualizarHorariosDisponibles(idMedico, fecha);
        } catch (NumberFormatException e) {
            mostrarMensajeError("ID Médico debe ser numérico");
        }
    }

    // Implementación de ICitaView
    @Override
    public void mostrarHorariosDisponibles(List<LocalTime> bloques) {
        Platform.runLater(() -> {
            cbHorarios.getItems().clear();
            if (bloques.isEmpty()) {
                cbHorarios.setDisable(true);
                btnReservar.setDisable(true);
                cbHorarios.getItems().add("No hay horarios disponibles");
            } else {
                horariosActuales = bloques;
                for (LocalTime t : bloques) {
                    cbHorarios.getItems().add(t.toString());
                }
                cbHorarios.setDisable(false);
                btnReservar.setDisable(false);
            }
        });
    }

    @Override
    public void mostrarMensajeError(String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }

    @Override
    public void mostrarMensajeExito(String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Éxito");
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
            limpiarCampos();
        });
    }

    @Override
    public void limpiarCampos() {
        Platform.runLater(() -> {
            txtIdPaciente.clear();
            txtIdMedico.clear();
            dpFecha.setValue(null);
            cbHorarios.getItems().clear();
            cbHorarios.setDisable(true);
            btnReservar.setDisable(true);
        });
    }

    @Override
    public int getIdPacienteSeleccionado() {
        return Integer.parseInt(txtIdPaciente.getText());
    }

    @Override
    public int getIdMedicoSeleccionado() {
        return Integer.parseInt(txtIdMedico.getText());
    }

    @Override
    public LocalDate getFechaSeleccionada() {
        return dpFecha.getValue();
    }

    @Override
    public LocalTime getHoraSeleccionada() {
        String selected = cbHorarios.getValue();
        return LocalTime.parse(selected);
    }
}
