package com.esperanza.hopecare.citas.view;

import com.esperanza.hopecare.modules.citas_consultas.presenter.CitaPresenter;
import com.esperanza.hopecare.modules.citas_consultas.view.ICitaView;
import com.esperanza.hopecare.modules.pacientes_medicos.dao.MedicoDAO;
import com.esperanza.hopecare.modules.pacientes_medicos.dao.PacienteDAO;
import com.esperanza.hopecare.modules.pacientes_medicos.model.Medico;
import com.esperanza.hopecare.modules.pacientes_medicos.model.Paciente;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class CitasController implements ICitaView {
    @FXML private TableView<Paciente> tvPacientes;
    @FXML private TableView<Medico> tvMedicos;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cbHorarios;
    @FXML private Button btnBuscar;
    @FXML private Button btnReservar;

    private CitaPresenter presenter;
    private List<LocalTime> horariosActuales;
    private int idPacienteSeleccionado = -1;
    private int idMedicoSeleccionado = -1;
    private ObservableList<Paciente> pacientesList;
    private ObservableList<Medico> medicosList;

    @FXML
    public void initialize() {
        presenter = new CitaPresenter(this);

        configurarTablaPacientes();
        configurarTablaMedicos();

        btnBuscar.setOnAction(e -> buscarHorarios());
        btnReservar.setOnAction(e -> presenter.reservarCita());
        cbHorarios.setDisable(true);
        btnReservar.setDisable(true);

        cargarDatos();
    }

    private void configurarTablaPacientes() {
        TableColumn<Paciente, Number> colPacId = (TableColumn<Paciente, Number>) tvPacientes.getColumns().get(0);
        TableColumn<Paciente, String> colPacNombre = (TableColumn<Paciente, String>) tvPacientes.getColumns().get(1);
        TableColumn<Paciente, String> colPacDoc = (TableColumn<Paciente, String>) tvPacientes.getColumns().get(2);

        colPacId.setCellValueFactory(new PropertyValueFactory<>("idPaciente"));
        colPacNombre.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getNombre() + " " + cd.getValue().getApellido()));
        colPacDoc.setCellValueFactory(new PropertyValueFactory<>("documentoIdentidad"));

        tvPacientes.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            idPacienteSeleccionado = (sel != null) ? sel.getIdPaciente() : -1;
        });
    }

    private void configurarTablaMedicos() {
        TableColumn<Medico, Number> colMedId = (TableColumn<Medico, Number>) tvMedicos.getColumns().get(0);
        TableColumn<Medico, String> colMedNombre = (TableColumn<Medico, String>) tvMedicos.getColumns().get(1);
        TableColumn<Medico, String> colMedEsp = (TableColumn<Medico, String>) tvMedicos.getColumns().get(2);
        TableColumn<Medico, String> colMedReg = (TableColumn<Medico, String>) tvMedicos.getColumns().get(3);

        colMedId.setCellValueFactory(new PropertyValueFactory<>("idMedico"));
        colMedNombre.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getNombre() + " " + cd.getValue().getApellido()));
        colMedEsp.setCellValueFactory(new PropertyValueFactory<>("nombreEspecialidad"));
        colMedReg.setCellValueFactory(new PropertyValueFactory<>("registroMedico"));

        tvMedicos.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            idMedicoSeleccionado = (sel != null) ? sel.getIdMedico() : -1;
        });
    }

    private void cargarDatos() {
        MedicoDAO medicoDAO = new MedicoDAO();
        PacienteDAO pacienteDAO = new PacienteDAO();
        pacientesList = FXCollections.observableArrayList(pacienteDAO.listarTodos());
        medicosList = FXCollections.observableArrayList(medicoDAO.listarTodos());
        tvPacientes.setItems(pacientesList);
        tvMedicos.setItems(medicosList);
    }

    private void buscarHorarios() {
        if (idMedicoSeleccionado <= 0) {
            mostrarMensajeError("Seleccione un médico de la tabla");
            return;
        }
        LocalDate fecha = dpFecha.getValue();
        if (fecha == null) {
            mostrarMensajeError("Seleccione una fecha");
            return;
        }
        presenter.actualizarHorariosDisponibles(idMedicoSeleccionado, fecha);
    }

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
            tvPacientes.getSelectionModel().clearSelection();
            tvMedicos.getSelectionModel().clearSelection();
            dpFecha.setValue(null);
            cbHorarios.getItems().clear();
            cbHorarios.setDisable(true);
            btnReservar.setDisable(true);
            idPacienteSeleccionado = -1;
            idMedicoSeleccionado = -1;
        });
    }

    @Override
    public int getIdPacienteSeleccionado() {
        return idPacienteSeleccionado;
    }

    @Override
    public int getIdMedicoSeleccionado() {
        return idMedicoSeleccionado;
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
