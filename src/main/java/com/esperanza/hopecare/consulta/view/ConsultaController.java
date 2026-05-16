package com.esperanza.hopecare.consulta.view;

import com.esperanza.hopecare.modules.citas_consultas.model.Cita;
import com.esperanza.hopecare.modules.citas_consultas.presenter.ConsultaPresenter;
import com.esperanza.hopecare.modules.citas_consultas.view.IConsultaView;
import com.esperanza.hopecare.modules.medicamentos_lab.model.ExamenLaboratorio;
import com.esperanza.hopecare.modules.medicamentos_lab.model.Medicamento;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConsultaController implements IConsultaView {
    @FXML private ComboBox<String> cbCitasPendientes;
    @FXML private TextArea txtSintomas, txtDiagnostico, txtTratamiento;
    @FXML private Button btnCargar, btnGuardar, btnSolicitarExamen, btnRecetar;

    private ConsultaPresenter presenter;
    private ObservableList<String> citasList;

    @FXML
    public void initialize() {
        presenter = new ConsultaPresenter(this);
        citasList = FXCollections.observableArrayList();
        cbCitasPendientes.setItems(citasList);

        btnCargar.setOnAction(e -> presenter.seleccionarCita());
        btnGuardar.setOnAction(e -> presenter.registrarConsulta());
        btnSolicitarExamen.setOnAction(e -> presenter.solicitarExamen());
        btnRecetar.setOnAction(e -> presenter.recetarMedicamento());

        presenter.cargarCitasPendientes();
    }

    @Override
    public int getIdCitaSeleccionada() {
        String selected = cbCitasPendientes.getValue();
        if (selected == null || selected.isEmpty()) return -1;
        try {
            return Integer.parseInt(selected.split(" - ")[0]);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public String getDiagnostico() {
        return txtDiagnostico.getText();
    }

    @Override
    public String getSintomas() {
        return txtSintomas.getText();
    }

    @Override
    public String getTratamiento() {
        return txtTratamiento.getText();
    }

    @Override
    public void mostrarCitasPendientes(List<Cita> citas) {
        citasList.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Cita c : citas) {
            String texto = c.getIdCita() + " - Paciente: " + c.getPacienteNombre() +
                           " | Médico: " + c.getMedicoNombre() +
                           " | " + c.getFechaHora().format(formatter);
            citasList.add(texto);
        }
        if (citasList.isEmpty()) {
            cbCitasPendientes.setPromptText("No hay citas pendientes");
        } else {
            cbCitasPendientes.getSelectionModel().selectFirst();
        }
    }

    @Override
    public void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @Override
    public void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @Override
    public void limpiarFormulario() {
        txtSintomas.clear();
        txtDiagnostico.clear();
        txtTratamiento.clear();
    }

    @Override
    public void limpiarSeleccionCita() {
        cbCitasPendientes.getSelectionModel().clearSelection();
    }

    @Override
    public void actualizarEstadoAcciones(boolean consultaGuardada) {
        btnGuardar.setDisable(consultaGuardada);
        btnSolicitarExamen.setDisable(!consultaGuardada);
        btnRecetar.setDisable(!consultaGuardada);
    }

    @Override
    public Integer solicitarExamen(List<ExamenLaboratorio> examenesDisponibles) {
        ComboBox<ExamenLaboratorio> cbExamenes = new ComboBox<>();
        cbExamenes.setPrefWidth(350);
        cbExamenes.getItems().setAll(examenesDisponibles);

        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Solicitar examen");
        dialog.setHeaderText("Seleccione el examen de laboratorio");

        VBox content = new VBox(10, new Label("Examen:"), cbExamenes);
        content.setStyle("-fx-padding: 15;");
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                ExamenLaboratorio seleccion = cbExamenes.getValue();
                return seleccion != null ? seleccion.getIdExamen() : null;
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    @Override
    public RecetaRequest solicitarReceta(List<Medicamento> medicamentosDisponibles) {
        ComboBox<Medicamento> cbMedicamentos = new ComboBox<>();
        cbMedicamentos.setPrefWidth(350);
        cbMedicamentos.getItems().setAll(medicamentosDisponibles);

        Spinner<Integer> spCantidad = new Spinner<>(1, 100, 1);
        spCantidad.setEditable(true);

        TextField txtDosis = new TextField();
        txtDosis.setPromptText("Ej: 1 tableta cada 8 horas");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setStyle("-fx-padding: 15;");
        grid.add(new Label("Medicamento:"), 0, 0);
        grid.add(cbMedicamentos, 1, 0);
        grid.add(new Label("Cantidad:"), 0, 1);
        grid.add(spCantidad, 1, 1);
        grid.add(new Label("Indicaciones:"), 0, 2);
        grid.add(txtDosis, 1, 2);

        Dialog<RecetaRequest> dialog = new Dialog<>();
        dialog.setTitle("Recetar medicamento");
        dialog.setHeaderText("Seleccione medicamento, cantidad e indicaciones");
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                Medicamento med = cbMedicamentos.getValue();
                if (med != null) {
                    return new RecetaRequest(med.getIdMedicamento(), spCantidad.getValue(), txtDosis.getText().trim());
                }
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }
}
