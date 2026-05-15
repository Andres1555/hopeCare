package com.esperanza.hopecare.consulta.view;

import com.esperanza.hopecare.modules.citas_consultas.model.Cita;
import com.esperanza.hopecare.modules.citas_consultas.dao.CitaDAO;
import com.esperanza.hopecare.modules.citas_consultas.model.Consulta;
import com.esperanza.hopecare.modules.citas_consultas.dao.ConsultaDAO;
import com.esperanza.hopecare.modules.medicamentos_lab.dao.MedicamentoDAO;
import com.esperanza.hopecare.modules.medicamentos_lab.dao.RecetaDAO;
import com.esperanza.hopecare.modules.medicamentos_lab.dao.DetalleRecetaDAO;
import com.esperanza.hopecare.modules.medicamentos_lab.dao.ExamenLaboratorioDAO;
import com.esperanza.hopecare.modules.medicamentos_lab.dao.SolicitudExamenDAO;
import com.esperanza.hopecare.modules.medicamentos_lab.model.Medicamento;
import com.esperanza.hopecare.modules.medicamentos_lab.model.ExamenLaboratorio;
import com.esperanza.hopecare.modules.medicamentos_lab.model.Receta;
import com.esperanza.hopecare.modules.medicamentos_lab.model.DetalleReceta;
import com.esperanza.hopecare.modules.medicamentos_lab.model.SolicitudExamen;
import com.esperanza.hopecare.common.db.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.sql.Connection;
import java.sql.SQLException;
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
    private int idConsultaActual = -1;

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
        List<Cita> citas = citaDAO.obtenerCitasPorEstadoConNombres("PROGRAMADA");
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
        }
    }

    private void seleccionarCita() {
        if (cbCitasPendientes.getSelectionModel().isEmpty()) {
            mostrarAlerta("Error", "Seleccione una cita", Alert.AlertType.ERROR);
            return;
        }
        String selected = cbCitasPendientes.getValue();
        idCitaSeleccionada = Integer.parseInt(selected.split(" - ")[0]);
        idConsultaActual = -1;
        txtSintomas.clear();
        txtDiagnostico.clear();
        txtTratamiento.clear();
        mostrarAlerta("Éxito", "Cita cargada, ingrese los datos de la consulta", Alert.AlertType.INFORMATION);
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
        int idConsulta = consultaDAO.insertarConsultaYActualizarEstado(consulta);
        if (idConsulta > 0) {
            idConsultaActual = idConsulta;
            mostrarAlerta("Éxito", "Consulta registrada correctamente", Alert.AlertType.INFORMATION);
            btnGuardar.setDisable(true);
            cargarCitas();
        } else {
            mostrarAlerta("Error", "No se pudo registrar la consulta", Alert.AlertType.ERROR);
        }
    }

    private void solicitarExamen() {
        if (idConsultaActual <= 0) {
            mostrarAlerta("Error", "Primero guarde la consulta", Alert.AlertType.ERROR);
            return;
        }

        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Solicitar examen");
        dialog.setHeaderText("Seleccione el examen de laboratorio");

        ComboBox<ExamenLaboratorio> cbExamenes = new ComboBox<>();
        cbExamenes.setPrefWidth(350);
        ExamenLaboratorioDAO examenDAO = new ExamenLaboratorioDAO();
        cbExamenes.getItems().setAll(examenDAO.listarTodos());

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

        dialog.showAndWait().ifPresent(idExamen -> {
            SolicitudExamen solicitud = new SolicitudExamen(idConsultaActual, idExamen);
            try (Connection conn = DatabaseConnection.getConnection()) {
                SolicitudExamenDAO solicitudDAO = new SolicitudExamenDAO();
                if (solicitudDAO.insertar(solicitud, conn)) {
                    mostrarAlerta("Éxito", "Examen solicitado correctamente", Alert.AlertType.INFORMATION);
                } else {
                    mostrarAlerta("Error", "No se pudo solicitar el examen", Alert.AlertType.ERROR);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Error", "Error de base de datos", Alert.AlertType.ERROR);
            }
        });
    }

    private void recetarMedicamento() {
        if (idConsultaActual <= 0) {
            mostrarAlerta("Error", "Primero guarde la consulta", Alert.AlertType.ERROR);
            return;
        }

        Dialog<int[]> dialog = new Dialog<>();
        dialog.setTitle("Recetar medicamento");
        dialog.setHeaderText("Seleccione medicamento, cantidad e indicaciones");

        ComboBox<Medicamento> cbMedicamentos = new ComboBox<>();
        cbMedicamentos.setPrefWidth(350);
        MedicamentoDAO medicamentoDAO = new MedicamentoDAO();
        cbMedicamentos.getItems().setAll(medicamentoDAO.listarTodos());

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

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                Medicamento med = cbMedicamentos.getValue();
                if (med != null) {
                    return new int[]{med.getIdMedicamento(), spCantidad.getValue()};
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(data -> {
            int idMedicamento = data[0];
            int cantidad = data[1];
            String dosis = txtDosis.getText().trim();

            Connection conn = null;
            try {
                conn = DatabaseConnection.getConnection();
                conn.setAutoCommit(false);

                Receta receta = new Receta(idConsultaActual, dosis);
                RecetaDAO recetaDAO = new RecetaDAO();
                int idReceta = recetaDAO.insertar(receta, conn);

                if (idReceta > 0) {
                    DetalleReceta detalle = new DetalleReceta(idReceta, idMedicamento, cantidad, dosis);
                    DetalleRecetaDAO detalleDAO = new DetalleRecetaDAO();
                    if (detalleDAO.insertar(detalle, conn)) {
                        conn.commit();
                        mostrarAlerta("Éxito", "Medicamento recetado correctamente", Alert.AlertType.INFORMATION);
                    } else {
                        conn.rollback();
                        mostrarAlerta("Error", "No se pudo registrar el detalle", Alert.AlertType.ERROR);
                    }
                } else {
                    conn.rollback();
                    mostrarAlerta("Error", "No se pudo crear la receta", Alert.AlertType.ERROR);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
                mostrarAlerta("Error", "Error de base de datos", Alert.AlertType.ERROR);
            } finally {
                if (conn != null) {
                    try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
                }
            }
        });
    }

    private void limpiarCampos() {
        txtSintomas.clear();
        txtDiagnostico.clear();
        txtTratamiento.clear();
        idCitaSeleccionada = -1;
        idConsultaActual = -1;
        btnGuardar.setDisable(false);
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
