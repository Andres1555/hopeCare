package com.esperanza.hopecare.citas.view;

import com.esperanza.hopecare.modules.citas_consultas.dao.CitaDAO;
import com.esperanza.hopecare.modules.citas_consultas.model.Cita;
import com.esperanza.hopecare.modules.citas_consultas.presenter.CitaPresenter;
import com.esperanza.hopecare.modules.citas_consultas.view.ICitaView;
import com.esperanza.hopecare.modules.pacientes_medicos.dao.EspecialidadDAO;
import com.esperanza.hopecare.modules.pacientes_medicos.dao.MedicoDAO;
import com.esperanza.hopecare.modules.pacientes_medicos.dao.PacienteDAO;
import com.esperanza.hopecare.modules.pacientes_medicos.model.Especialidad;
import com.esperanza.hopecare.modules.pacientes_medicos.model.Medico;
import com.esperanza.hopecare.modules.pacientes_medicos.model.Paciente;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CitasController implements ICitaView {
    @FXML private TableView<Cita> tvCitas;
    @FXML private Button btnNuevaCita;

    private CitaPresenter presenter;
    private ObservableList<Cita> citasList;

    private static final String[] NOMBRES_DIAS = {"", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};

    @FXML
    public void initialize() {
        presenter = new CitaPresenter(this);

        configurarTablaCitas();
        btnNuevaCita.setOnAction(e -> abrirDialogoNuevaCita());

        presenter.cargarCitasExistentes();
    }

    private void configurarTablaCitas() {
        TableColumn<Cita, Number> colId = (TableColumn<Cita, Number>) tvCitas.getColumns().get(0);
        TableColumn<Cita, String> colPac = (TableColumn<Cita, String>) tvCitas.getColumns().get(1);
        TableColumn<Cita, String> colMed = (TableColumn<Cita, String>) tvCitas.getColumns().get(2);
        TableColumn<Cita, String> colFecha = (TableColumn<Cita, String>) tvCitas.getColumns().get(3);
        TableColumn<Cita, String> colEstado = (TableColumn<Cita, String>) tvCitas.getColumns().get(4);

        colId.setCellValueFactory(new PropertyValueFactory<>("idCita"));
        colPac.setCellValueFactory(new PropertyValueFactory<>("pacienteNombre"));
        colMed.setCellValueFactory(new PropertyValueFactory<>("medicoNombre"));
        colFecha.setCellValueFactory(cd -> new SimpleStringProperty(
            cd.getValue().getFechaHora().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        citasList = FXCollections.observableArrayList();
        tvCitas.setItems(citasList);

        tvCitas.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Cita sel = tvCitas.getSelectionModel().getSelectedItem();
                if (sel != null) abrirDialogoEditarCita(sel);
            }
        });
    }

    private void abrirDialogoNuevaCita() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Nueva Cita Médica");
        dialog.setHeaderText("Complete los datos para agendar una nueva cita");

        PacienteDAO pacienteDAO = new PacienteDAO();
        MedicoDAO medicoDAO = new MedicoDAO();
        EspecialidadDAO espDAO = new EspecialidadDAO();

        ObservableList<Paciente> pacientesList = FXCollections.observableArrayList(pacienteDAO.listarTodos());
        ObservableList<Medico> medicosList = FXCollections.observableArrayList(medicoDAO.listarTodos());

        TextField txtBuscarPac = new TextField();
        txtBuscarPac.setPromptText("Buscar paciente por nombre...");

        FilteredList<Paciente> pacientesFiltrados = new FilteredList<>(pacientesList, p -> true);
        TableView<Paciente> tvPacientes = new TableView<>();
        tvPacientes.setPrefHeight(150);
        tvPacientes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Paciente, Number> colPacId = new TableColumn<>("ID");
        colPacId.setCellValueFactory(new PropertyValueFactory<>("idPaciente"));
        TableColumn<Paciente, String> colPacNombre = new TableColumn<>("Nombre");
        colPacNombre.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getNombre() + " " + cd.getValue().getApellido()));
        TableColumn<Paciente, String> colPacDoc = new TableColumn<>("Documento");
        colPacDoc.setCellValueFactory(new PropertyValueFactory<>("documentoIdentidad"));
        tvPacientes.getColumns().addAll(colPacId, colPacNombre, colPacDoc);
        tvPacientes.setItems(pacientesFiltrados);

        ComboBox<Especialidad> cbEsp = new ComboBox<>();
        cbEsp.setPrefWidth(200);
        List<Especialidad> especialidades = espDAO.listarTodas();
        Especialidad todas = new Especialidad(0, "Todas");
        cbEsp.getItems().add(todas);
        cbEsp.getItems().addAll(especialidades);
        cbEsp.setValue(todas);

        TextField txtBuscarMed = new TextField();
        txtBuscarMed.setPromptText("Buscar médico por nombre...");

        FilteredList<Medico> medicosFiltrados = new FilteredList<>(medicosList, m -> true);
        TableView<Medico> tvMedicos = new TableView<>();
        tvMedicos.setPrefHeight(150);
        tvMedicos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Medico, Number> colMedId = new TableColumn<>("ID");
        colMedId.setCellValueFactory(new PropertyValueFactory<>("idMedico"));
        TableColumn<Medico, String> colMedNombre = new TableColumn<>("Nombre");
        colMedNombre.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getNombre() + " " + cd.getValue().getApellido()));
        TableColumn<Medico, String> colMedEsp = new TableColumn<>("Especialidad");
        colMedEsp.setCellValueFactory(new PropertyValueFactory<>("nombreEspecialidad"));
        TableColumn<Medico, String> colMedReg = new TableColumn<>("Registro");
        colMedReg.setCellValueFactory(new PropertyValueFactory<>("registroMedico"));
        tvMedicos.getColumns().addAll(colMedId, colMedNombre, colMedEsp, colMedReg);
        tvMedicos.setItems(medicosFiltrados);

        ComboBox<String> cbDias = new ComboBox<>();
        cbDias.setPrefWidth(200);
        cbDias.setDisable(true);
        cbDias.setPromptText("Seleccione un médico");

        DatePicker dpFecha = new DatePicker();

        ComboBox<String> cbHorarios = new ComboBox<>();
        cbHorarios.setPrefWidth(200);
        cbHorarios.setDisable(true);

        Button btnBuscar = new Button("Buscar horarios");
        Button btnReservar = new Button("Reservar cita");
        btnReservar.setDisable(true);

        txtBuscarPac.textProperty().addListener((obs, old, val) -> {
            String texto = val.toLowerCase().trim();
            pacientesFiltrados.setPredicate(p -> {
                String nc = (p.getNombre() + " " + p.getApellido()).toLowerCase();
                return texto.isEmpty() || nc.contains(texto);
            });
        });

        Runnable filtrarMedicos = () -> {
            String texto = txtBuscarMed.getText().toLowerCase().trim();
            Especialidad esp = cbEsp.getValue();
            medicosFiltrados.setPredicate(m -> {
                boolean coincideNombre = texto.isEmpty() ||
                    (m.getNombre() + " " + m.getApellido()).toLowerCase().contains(texto);
                boolean coincideEsp = esp == null || esp.getIdEspecialidad() == 0 ||
                    m.getIdEspecialidad() == esp.getIdEspecialidad();
                return coincideNombre && coincideEsp;
            });
        };

        cbEsp.setOnAction(e -> filtrarMedicos.run());
        txtBuscarMed.textProperty().addListener((obs, old, val) -> filtrarMedicos.run());

        final int[] idPacSeleccionado = {-1};
        final int[] idMedSeleccionado = {-1};

        tvPacientes.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            idPacSeleccionado[0] = sel != null ? sel.getIdPaciente() : -1;
        });

        CitaPresenter dialogPresenter = new CitaPresenter(new ICitaView() {
            @Override public void mostrarCitasExistentes(List<Cita> citas) {}
            @Override public void mostrarHorariosDisponibles(List<LocalTime> bloques) {
                cbHorarios.getItems().clear();
                if (bloques.isEmpty()) {
                    cbHorarios.setDisable(true);
                    btnReservar.setDisable(true);
                    cbHorarios.getItems().add("No hay horarios disponibles");
                } else {
                    for (LocalTime t : bloques) cbHorarios.getItems().add(t.toString());
                    cbHorarios.setDisable(false);
                    btnReservar.setDisable(false);
                }
            }
            @Override public void mostrarDiasDisponibles(List<Integer> diasSemana) {
                cbDias.getItems().clear();
                if (diasSemana.isEmpty()) {
                    cbDias.setDisable(true);
                    cbDias.setPromptText("Sin días disponibles");
                    return;
                }
                for (int d : diasSemana) cbDias.getItems().add(d + " - " + NOMBRES_DIAS[d]);
                cbDias.setDisable(false);
                cbDias.setPromptText("Seleccione un día");
            }
            @Override public int getDiaSeleccionado() { return 0; }
            @Override public void mostrarMensajeError(String mensaje) {
                Alert alert = new Alert(Alert.AlertType.ERROR, mensaje);
                alert.showAndWait();
            }
            @Override public void mostrarMensajeExito(String mensaje) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, mensaje);
                alert.showAndWait();
                dialog.close();
            }
            @Override public void limpiarCampos() {}
            @Override public int getIdPacienteSeleccionado() { return idPacSeleccionado[0]; }
            @Override public int getIdMedicoSeleccionado() { return idMedSeleccionado[0]; }
            @Override public LocalDate getFechaSeleccionada() { return dpFecha.getValue(); }
            @Override public LocalTime getHoraSeleccionada() {
                String s = cbHorarios.getValue();
                return s != null ? LocalTime.parse(s) : null;
            }
        });

        tvMedicos.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            idMedSeleccionado[0] = sel != null ? sel.getIdMedico() : -1;
            if (sel != null) {
                dialogPresenter.cargarDiasDisponibles(sel.getIdMedico());
            } else {
                cbDias.getItems().clear();
                cbDias.setDisable(true);
                cbDias.setPromptText("Seleccione un médico");
            }
        });

        cbDias.setOnAction(e -> {
            String val = cbDias.getValue();
            if (val == null) return;
            int diaSemana = Integer.parseInt(val.split(" - ")[0]);
            LocalDate today = LocalDate.now();
            LocalDate nextDate = today.with(DayOfWeek.of(diaSemana));
            if (!nextDate.isAfter(today)) nextDate = nextDate.plusWeeks(1);
            dpFecha.setValue(nextDate);
        });

        btnBuscar.setOnAction(e -> {
            if (idMedSeleccionado[0] <= 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Seleccione un médico");
                alert.showAndWait();
                return;
            }
            if (dpFecha.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Seleccione una fecha");
                alert.showAndWait();
                return;
            }
            dialogPresenter.actualizarHorariosDisponibles(idMedSeleccionado[0], dpFecha.getValue());
        });

        btnReservar.setOnAction(e -> {
            dialogPresenter.reservarCita();
        });

        VBox pacienteSection = new VBox(5,
            new Label("Seleccionar paciente:"),
            txtBuscarPac,
            tvPacientes
        );

        HBox filtrosMedicos = new HBox(10, cbEsp, txtBuscarMed);
        HBox.setHgrow(txtBuscarMed, javafx.scene.layout.Priority.ALWAYS);
        VBox medicoSection = new VBox(5,
            new Label("Seleccionar médico:"),
            filtrosMedicos,
            tvMedicos
        );

        HBox tablesRow = new HBox(20, pacienteSection, medicoSection);
        HBox.setHgrow(pacienteSection, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(medicoSection, javafx.scene.layout.Priority.ALWAYS);

        GridPane horarioGrid = new GridPane();
        horarioGrid.setHgap(10);
        horarioGrid.setVgap(10);
        horarioGrid.add(new Label("Día disponible:"), 0, 0);
        horarioGrid.add(cbDias, 1, 0);
        horarioGrid.add(new Label("Fecha:"), 2, 0);
        horarioGrid.add(dpFecha, 3, 0);
        horarioGrid.add(btnBuscar, 0, 1, 4, 1);
        horarioGrid.add(new Label("Horario:"), 0, 2);
        horarioGrid.add(cbHorarios, 1, 2);
        horarioGrid.add(btnReservar, 2, 2, 2, 1);

        VBox content = new VBox(15, tablesRow, horarioGrid);
        content.setStyle("-fx-padding: 15;");

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefWidth(750);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        dialog.showAndWait();

        presenter.cargarCitasExistentes();
    }

    private void abrirDialogoEditarCita(Cita cita) {
        CitaDAO citaDAO = new CitaDAO();
        MedicoDAO medicoDAO = new MedicoDAO();
        List<Medico> medicos = medicoDAO.listarTodos();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Cita #" + cita.getIdCita());
        dialog.setHeaderText("Paciente: " + cita.getPacienteNombre());

        Label lblMedicoActual = new Label("Médico: " + cita.getMedicoNombre());
        Label lblFechaActual = new Label("Fecha/Hora: " + cita.getFechaHora().format(dtf));
        Label lblEstadoActual = new Label("Estado: " + cita.getEstado());

        ComboBox<Medico> cbMedico = new ComboBox<>();
        cbMedico.setPrefWidth(300);
        cbMedico.getItems().addAll(medicos);
        for (Medico m : medicos) {
            if (m.getIdMedico() == cita.getIdMedico()) {
                cbMedico.setValue(m);
                break;
            }
        }

        DatePicker dpFecha = new DatePicker(cita.getFechaHora().toLocalDate());

        ObservableList<String> slots = FXCollections.observableArrayList();
        for (int h = 7; h <= 19; h++) {
            slots.add(String.format("%02d:00", h));
            slots.add(String.format("%02d:30", h));
        }
        ComboBox<String> cbHora = new ComboBox<>(slots);
        cbHora.setValue(cita.getFechaHora().toLocalTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));

        ComboBox<String> cbEstado = new ComboBox<>();
        cbEstado.getItems().addAll("PROGRAMADA", "CANCELADA", "ATENDIDA", "NO_ASISTIO");
        cbEstado.setValue(cita.getEstado());

        Button btnGuardar = new Button("Guardar cambios");

        btnGuardar.setOnAction(e -> {
            Medico medSel = cbMedico.getValue();
            LocalDate nuevaFecha = dpFecha.getValue();
            String horaStr = cbHora.getValue();
            String nuevoEstado = cbEstado.getValue();

            if (medSel == null || nuevaFecha == null || horaStr == null || nuevoEstado == null) {
                mostrarMensajeError("Complete todos los campos.");
                return;
            }

            LocalTime nuevaHora = LocalTime.parse(horaStr);
            LocalDateTime nuevaFechaHora = LocalDateTime.of(nuevaFecha, nuevaHora);

            cita.setIdMedico(medSel.getIdMedico());
            cita.setMedicoNombre(medSel.getNombre() + " " + medSel.getApellido());
            cita.setFechaHora(nuevaFechaHora);
            cita.setEstado(nuevoEstado);

            if (citaDAO.actualizarCita(cita)) {
                mostrarMensajeExito("Cita actualizada correctamente.");
                dialog.close();
                presenter.cargarCitasExistentes();
            } else {
                mostrarMensajeError("Error al actualizar la cita.");
            }
        });

        VBox infoSection = new VBox(5,
            new Label("— Información actual —"),
            lblMedicoActual, lblFechaActual, lblEstadoActual
        );
        infoSection.setPadding(new Insets(0, 0, 10, 0));

        GridPane editGrid = new GridPane();
        editGrid.setHgap(10);
        editGrid.setVgap(8);
        editGrid.add(new Label("Nuevo médico:"), 0, 0);
        editGrid.add(cbMedico, 1, 0);
        editGrid.add(new Label("Nueva fecha:"), 0, 1);
        editGrid.add(dpFecha, 1, 1);
        editGrid.add(new Label("Nuevo horario:"), 0, 2);
        editGrid.add(cbHora, 1, 2);
        editGrid.add(new Label("Nuevo estado:"), 0, 3);
        editGrid.add(cbEstado, 1, 3);

        VBox content = new VBox(12, infoSection, new Label("— Editar —"), editGrid, btnGuardar);
        content.setPadding(new Insets(15));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefWidth(450);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        dialog.showAndWait();
    }

    @Override
    public void mostrarCitasExistentes(List<Cita> citas) {
        citasList.setAll(citas);
    }

    @Override
    public void mostrarHorariosDisponibles(List<LocalTime> bloques) {}

    @Override
    public void mostrarDiasDisponibles(List<Integer> diasSemana) {}

    @Override
    public int getDiaSeleccionado() { return -1; }

    @Override
    public void mostrarMensajeError(String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, mensaje);
            alert.showAndWait();
        });
    }

    @Override
    public void mostrarMensajeExito(String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, mensaje);
            alert.showAndWait();
        });
    }

    @Override
    public void limpiarCampos() {}

    @Override
    public int getIdPacienteSeleccionado() { return -1; }

    @Override
    public int getIdMedicoSeleccionado() { return -1; }

    @Override
    public LocalDate getFechaSeleccionada() { return null; }

    @Override
    public LocalTime getHoraSeleccionada() { return null; }
}
