package com.esperanza.hopecare.facturacion.view;

import com.esperanza.hopecare.common.events.DatosFacturablesActualizadosEvent;
import com.esperanza.hopecare.common.events.EventBus;
import com.esperanza.hopecare.modules.facturacion.dao.FacturaDAO;
import com.esperanza.hopecare.modules.facturacion.dto.FacturaDTO;
import com.esperanza.hopecare.modules.facturacion.dto.FacturaResumenDTO;
import com.esperanza.hopecare.modules.facturacion.service.FacturacionService;
import com.esperanza.hopecare.modules.pacientes_medicos.dao.PacienteDAO;
import com.esperanza.hopecare.modules.pacientes_medicos.model.Paciente;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class FacturacionController {
    @FXML private TextField txtBuscarPaciente;
    @FXML private Button btnGenerar;
    @FXML private TableView<Paciente> tablaPacientes;
    @FXML private TableColumn<Paciente, Integer> colPacId;
    @FXML private TableColumn<Paciente, String> colPacNombre;
    @FXML private TableColumn<Paciente, String> colPacApellido;
    @FXML private TableColumn<Paciente, String> colPacDocumento;
    @FXML private TableColumn<Paciente, String> colPacPendiente;
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
    private Set<Integer> idsPacientesPendientes;

    @FXML
    public void initialize() {
        service = new FacturacionService();
        facturaDAO = new FacturaDAO();
        pacienteDAO = new PacienteDAO();

        idsPacientesPendientes = facturaDAO.obtenerIdsPacientesConPendientes();
        configurarTablaPacientes();
        configurarTablaFacturas();
        cargarFacturas();

        EventBus.getInstance().register(DatosFacturablesActualizadosEvent.class, e -> refrescar());

        btnGenerar.setOnAction(e -> generarFactura());
    }

    private void configurarTablaPacientes() {
        colPacId.setCellValueFactory(new PropertyValueFactory<>("idPaciente"));
        colPacNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPacApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colPacDocumento.setCellValueFactory(new PropertyValueFactory<>("documentoIdentidad"));
        colPacPendiente.setCellValueFactory(cellData -> {
            boolean pendiente = idsPacientesPendientes.contains(cellData.getValue().getIdPaciente());
            return new javafx.beans.property.SimpleStringProperty(pendiente ? "SÍ" : "NO");
        });
        colPacPendiente.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("SÍ".equals(item)) {
                        setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #22c55e;");
                    }
                }
            }
        });

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

        colEstado.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("PAGADO".equals(item)) {
                        setStyle("-fx-text-fill: #16a34a; -fx-font-weight: 600;");
                    } else if ("ANULADO".equals(item)) {
                        setStyle("-fx-text-fill: #dc2626; -fx-font-weight: 600;");
                    } else {
                        setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: 600;");
                    }
                }
            }
        });

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

        tablaFacturas.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                FacturaResumenDTO factura = tablaFacturas.getSelectionModel().getSelectedItem();
                if (factura != null) abrirDialogoEditarEstado(factura);
            }
        });
    }

    private void abrirDialogoEditarEstado(FacturaResumenDTO factura) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Cambiar estado de factura #" + factura.getIdFactura());
        dialog.setHeaderText("Paciente: " + factura.getPacienteNombre());

        ComboBox<String> cbEstado = new ComboBox<>();
        cbEstado.getItems().addAll("PENDIENTE", "PAGADO", "ANULADO");
        cbEstado.setValue(factura.getEstadoPago());
        cbEstado.setPrefWidth(250);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));
        grid.add(new Label("Estado de pago:"), 0, 0);
        grid.add(cbEstado, 1, 0);

        Button btnGuardar = new Button("Guardar");
        btnGuardar.setOnAction(e -> {
            String nuevoEstado = cbEstado.getValue();
            if (nuevoEstado == null) return;
            if (facturaDAO.actualizarEstadoPago(factura.getIdFactura(), nuevoEstado)) {
                mostrarAlerta("Éxito", "Estado actualizado a " + nuevoEstado, Alert.AlertType.INFORMATION);
                dialog.close();
                cargarFacturas();
            } else {
                mostrarAlerta("Error", "No se pudo actualizar el estado.", Alert.AlertType.ERROR);
            }
        });

        VBox content = new VBox(12, grid, btnGuardar);
        content.setPadding(new Insets(15));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.showAndWait();
    }

    public void refrescar() {
        idsPacientesPendientes = facturaDAO.obtenerIdsPacientesConPendientes();
        tablaPacientes.refresh();
        cargarFacturas();
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
        FacturaDTO preview = service.previsualizarFactura(paciente.getIdPaciente());
        if (preview == null) {
            mostrarAlerta("Sin pendientes", "No hay conceptos pendientes para facturar.", Alert.AlertType.INFORMATION);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Paciente: ").append(paciente.getNombre()).append(" ").append(paciente.getApellido()).append("\n\n");
        sb.append("Conceptos pendientes:\n");
        preview.getDetalles().forEach(d ->
            sb.append(String.format("  - %s: $%.2f\n", d.getConcepto(), d.getMonto()))
        );
        sb.append(String.format("\nSubtotal: $%.2f\n", preview.getSubtotal()));
        sb.append(String.format("Impuesto (19%%): $%.2f\n", preview.getImpuesto()));
        sb.append(String.format("Total: $%.2f\n", preview.getTotal()));

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Previsualización de factura");
        dialog.setHeaderText("Confirme los conceptos a facturar");

        TextArea area = new TextArea(sb.toString());
        area.setEditable(false);
        area.setPrefHeight(300);
        area.setStyle("-fx-font-family: monospace;");

        VBox content = new VBox(10, area);
        content.setStyle("-fx-padding: 15;");
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        dialog.setResultConverter(btn -> btn);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                FacturaDTO factura = service.generarFactura(paciente.getIdPaciente());
                if (factura == null) {
                    mostrarAlerta("Error", "No se pudo generar la factura.", Alert.AlertType.ERROR);
                    return;
                }
                StringBuilder res = new StringBuilder();
                res.append("Factura generada exitosamente\n\n");
                res.append(String.format("Subtotal: $%.2f\n", factura.getSubtotal()));
                res.append(String.format("Impuesto (19%%): $%.2f\n", factura.getImpuesto()));
                res.append(String.format("Total: $%.2f\n\n", factura.getTotal()));
                res.append("Detalles:\n");
                factura.getDetalles().forEach(d ->
                    res.append(String.format(" - %s: $%.2f\n", d.getConcepto(), d.getMonto()))
                );
                mostrarAlerta("Factura generada", res.toString(), Alert.AlertType.INFORMATION);
                idsPacientesPendientes = facturaDAO.obtenerIdsPacientesConPendientes();
                tablaPacientes.refresh();
                cargarFacturas();
            }
        });
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
