package com.esperanza.hopecare.facturacion.view;

import com.esperanza.hopecare.modules.facturacion.dao.FacturaDAO;
import com.esperanza.hopecare.modules.facturacion.dto.FacturaDTO;
import com.esperanza.hopecare.modules.facturacion.dto.FacturaResumenDTO;
import com.esperanza.hopecare.modules.facturacion.service.FacturacionService;
import com.esperanza.hopecare.modules.pacientes_medicos.dao.PacienteDAO;
import com.esperanza.hopecare.modules.pacientes_medicos.model.Paciente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

public class FacturacionController {
    @FXML private ComboBox<Paciente> cbPacientes;
    @FXML private TextArea txtResultado;
    @FXML private Button btnGenerar;
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

    @FXML
    public void initialize() {
        service = new FacturacionService();
        facturaDAO = new FacturaDAO();
        pacienteDAO = new PacienteDAO();

        configurarTabla();
        cargarPacientes();
        cargarFacturas();

        btnGenerar.setOnAction(e -> generarFactura());
    }

    private void configurarTabla() {
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

    private void cargarPacientes() {
        List<Paciente> pacientes = pacienteDAO.listarTodos();
        cbPacientes.setItems(FXCollections.observableArrayList(pacientes));
        cbPacientes.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Paciente item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre() + " " + item.getApellido());
            }
        });
        cbPacientes.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Paciente item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre() + " " + item.getApellido());
            }
        });
    }

    private void cargarFacturas() {
        facturasList.setAll(facturaDAO.listarTodasConPaciente());
    }

    private void generarFactura() {
        Paciente paciente = cbPacientes.getValue();
        if (paciente == null) {
            mostrarAlerta("Error", "Seleccione un paciente.", Alert.AlertType.ERROR);
            return;
        }
        int idPaciente = paciente.getIdPaciente();
        FacturaDTO factura = service.generarFactura(idPaciente);
        if (factura == null) {
            txtResultado.setText("No hay conceptos pendientes para facturar.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Factura generada exitosamente\n");
        sb.append(String.format("Subtotal: $%.2f\n", factura.getSubtotal()));
        sb.append(String.format("Impuesto: $%.2f\n", factura.getImpuesto()));
        sb.append(String.format("Total: $%.2f\n", factura.getTotal()));
        sb.append("Detalles:\n");
        factura.getDetalles().forEach(d ->
            sb.append(String.format(" - %s: $%.2f\n", d.getConcepto(), d.getMonto()))
        );
        txtResultado.setText(sb.toString());
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
