package com.esperanza.hopecare.facturacion.view;

import com.esperanza.hopecare.modules.facturacion.dao.FacturaDAO;
import com.esperanza.hopecare.modules.facturacion.dto.FacturaDTO;
import com.esperanza.hopecare.modules.facturacion.dto.FacturaResumenDTO;
import com.esperanza.hopecare.modules.facturacion.service.FacturacionService;
import com.esperanza.hopecare.modules.pacientes_medicos.dao.PacienteDAO;
import com.esperanza.hopecare.modules.pacientes_medicos.model.Paciente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class FacturacionController {
    @FXML private TextField txtBuscarPaciente;
    @FXML private Button btnGenerar;
    @FXML private TableView<Paciente> tablaPacientes;
    @FXML private TableColumn<Paciente, Integer> colPacId;
    @FXML private TableColumn<Paciente, String> colPacNombre;
    @FXML private TableColumn<Paciente, String> colPacApellido;
    @FXML private TableColumn<Paciente, String> colPacDocumento;
    @FXML private TableView<FacturaResumenDTO> tablaFacturas;
    @FXML private TableColumn<FacturaResumenDTO, Integer> colId;
    @FXML private TableColumn<FacturaResumenDTO, String> colPaciente;
    @FXML private TableColumn<FacturaResumenDTO, String> colFecha;
    @FXML private TableColumn<FacturaResumenDTO, Double> colSubtotal;
    @FXML private TableColumn<FacturaResumenDTO, Double> colImpuesto;
    @FXML private TableColumn<FacturaResumenDTO, Double> colTotal;
    @FXML private TableColumn<FacturaResumenDTO, String> colEstado;

    private FacturacionService service;
    private FacturaDAO facturaDAO;
    private PacienteDAO pacienteDAO;
    private ObservableList<FacturaResumenDTO> facturasList;
    private FilteredList<Paciente> pacientesFiltrados;

    @FXML
    public void initialize() {
        service = new FacturacionService();
        facturaDAO = new FacturaDAO();
        pacienteDAO = new PacienteDAO();

        configurarTablaPacientes();
        configurarTablaFacturas();
        cargarFacturas();

        btnGenerar.setOnAction(e -> generarFactura());
    }

    private void configurarTablaPacientes() {
        colPacId.setCellValueFactory(new PropertyValueFactory<>("idPaciente"));
        colPacNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPacApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colPacDocumento.setCellValueFactory(new PropertyValueFactory<>("documentoIdentidad"));

        ObservableList<Paciente> pacientes = FXCollections.observableArrayList(pacienteDAO.listarTodos());
        pacientesFiltrados = new FilteredList<>(pacientes, p -> true);
        tablaPacientes.setItems(pacientesFiltrados);

        tablaPacientes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        txtBuscarPaciente.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtro = newVal == null ? "" : newVal.toLowerCase().trim();
            pacientesFiltrados.setPredicate(p -> {
                if (filtro.isEmpty()) return true;
                return p.getNombre().toLowerCase().contains(filtro)
                    || p.getApellido().toLowerCase().contains(filtro)
                    || (p.getDocumentoIdentidad() != null && p.getDocumentoIdentidad().toLowerCase().contains(filtro));
            });
        });
    }

    private void configurarTablaFacturas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idFactura"));
        colPaciente.setCellValueFactory(new PropertyValueFactory<>("pacienteNombre"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaEmision"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colImpuesto.setCellValueFactory(new PropertyValueFactory<>("impuesto"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoPago"));

        colSubtotal.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$%.2f", item));
            }
        });
        colImpuesto.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$%.2f", item));
            }
        });
        colTotal.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$%.2f", item));
            }
        });

        facturasList = FXCollections.observableArrayList();
        tablaFacturas.setItems(facturasList);
    }

    private void cargarFacturas() {
        facturasList.setAll(facturaDAO.listarTodasConPaciente());
    }

    private void generarFactura() {
        Paciente paciente = tablaPacientes.getSelectionModel().getSelectedItem();
        if (paciente == null) {
            mostrarAlerta("Error", "Seleccione un paciente de la tabla.", Alert.AlertType.ERROR);
            return;
        }
        FacturaDTO factura = service.generarFactura(paciente.getIdPaciente());
        if (factura == null) {
            mostrarAlerta("Sin pendientes", "No hay conceptos pendientes para facturar.", Alert.AlertType.INFORMATION);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Factura generada exitosamente\n\n");
        sb.append(String.format("Subtotal: $%.2f\n", factura.getSubtotal()));
        sb.append(String.format("Impuesto (19%%): $%.2f\n", factura.getImpuesto()));
        sb.append(String.format("Total: $%.2f\n\n", factura.getTotal()));
        sb.append("Detalles:\n");
        factura.getDetalles().forEach(d ->
            sb.append(String.format(" - %s: $%.2f\n", d.getConcepto(), d.getMonto()))
        );
        mostrarAlerta("Factura generada", sb.toString(), Alert.AlertType.INFORMATION);
        cargarFacturas();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
