package com.esperanza.hopecare.farmacia.view;

import com.esperanza.hopecare.modules.medicamentos_lab.facade.GestionClinicaFacade;
import com.esperanza.hopecare.modules.medicamentos_lab.dao.MedicamentoDAO;
import com.esperanza.hopecare.modules.medicamentos_lab.dao.RecetaDAO;
import com.esperanza.hopecare.modules.medicamentos_lab.dao.EntregaMedicamentoDAO;
import com.esperanza.hopecare.modules.medicamentos_lab.model.Medicamento;
import com.esperanza.hopecare.modules.medicamentos_lab.model.Receta;
import com.esperanza.hopecare.modules.medicamentos_lab.model.EntregaMedicamento;
import com.esperanza.hopecare.modules.medicamentos_lab.service.InventarioService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.format.DateTimeFormatter;

public class FarmaciaController {
    @FXML private TextField txtBuscarMedicamento;
    @FXML private Button btnAgregarMedicamento;
    @FXML private Button btnActualizarStock;
    @FXML private TableView<Medicamento> tblInventario;
    @FXML private TableColumn<Medicamento, Integer> colMedicamentoId;
    @FXML private TableColumn<Medicamento, String> colMedicamentoNombre;
    @FXML private TableColumn<Medicamento, Integer> colStockActual;
    @FXML private TableColumn<Medicamento, Integer> colStockMinimo;
    @FXML private TableColumn<Medicamento, String> colEstado;

    @FXML private TableView<Receta> tblRecetas;
    @FXML private TableColumn<Receta, Integer> colRecetaId;
    @FXML private TableColumn<Receta, Integer> colRecetaConsulta;
    @FXML private TableColumn<Receta, String> colRecetaFecha;
    @FXML private TableColumn<Receta, String> colRecetaInstrucciones;

    @FXML private TableView<EntregaMedicamento> tblEntregas;
    @FXML private TableColumn<EntregaMedicamento, Integer> colEntregaId;
    @FXML private TableColumn<EntregaMedicamento, Integer> colEntregaReceta;
    @FXML private TableColumn<EntregaMedicamento, Integer> colEntregaMedicamento;
    @FXML private TableColumn<EntregaMedicamento, Integer> colEntregaCantidad;
    @FXML private TableColumn<EntregaMedicamento, String> colEntregaFecha;
    @FXML private TableColumn<EntregaMedicamento, String> colEntregaFacturado;

    @FXML private TextField txtIdReceta;
    @FXML private TextField txtIdMedicamento;
    @FXML private TextField txtCantidad;
    @FXML private Button btnEntregar;

    private GestionClinicaFacade facade;
    private MedicamentoDAO medicamentoDAO;
    private RecetaDAO recetaDAO;
    private EntregaMedicamentoDAO entregaDAO;
    private InventarioService inventarioService;

    private ObservableList<Medicamento> inventarioData = FXCollections.observableArrayList();
    private ObservableList<Receta> recetasData = FXCollections.observableArrayList();
    private ObservableList<EntregaMedicamento> entregasData = FXCollections.observableArrayList();

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        facade = new GestionClinicaFacade();
        medicamentoDAO = new MedicamentoDAO();
        recetaDAO = new RecetaDAO();
        entregaDAO = new EntregaMedicamentoDAO();
        inventarioService = new InventarioService();

        configurarColumnasInventario();
        configurarColumnasRecetas();
        configurarColumnasEntregas();

        tblInventario.setItems(inventarioData);
        tblRecetas.setItems(recetasData);
        tblEntregas.setItems(entregasData);

        txtBuscarMedicamento.textProperty().addListener((obs, oldVal, newVal) -> {
            filtrarInventario(newVal);
        });

        cargarDatos();
    }

    private void configurarColumnasInventario() {
        colMedicamentoId.setCellValueFactory(new PropertyValueFactory<>("idMedicamento"));
        colMedicamentoNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colStockActual.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
        colStockMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));

        colEstado.setCellFactory(column -> new TableCell<Medicamento, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setText(null);
                    setStyle(null);
                } else {
                    Medicamento med = getTableRow().getItem();
                    if (med.getStockActual() <= med.getStockMinimo()) {
                        setText("BAJO");
                        setStyle("-fx-text-fill: #dc2626; -fx-font-weight: 600;");
                    } else {
                        setText("OK");
                        setStyle("-fx-text-fill: #16a34a; -fx-font-weight: 600;");
                    }
                }
            }
        });
    }

    private void configurarColumnasRecetas() {
        colRecetaId.setCellValueFactory(new PropertyValueFactory<>("idReceta"));
        colRecetaConsulta.setCellValueFactory(new PropertyValueFactory<>("idConsulta"));
        colRecetaFecha.setCellFactory(column -> new TableCell<Receta, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    setText(getTableRow().getItem().getFechaEmision() != null ?
                        getTableRow().getItem().getFechaEmision().format(dateFormatter) : "");
                }
            }
        });
        colRecetaInstrucciones.setCellValueFactory(new PropertyValueFactory<>("instrucciones"));
    }

    private void configurarColumnasEntregas() {
        colEntregaId.setCellValueFactory(new PropertyValueFactory<>("idEntrega"));
        colEntregaReceta.setCellValueFactory(new PropertyValueFactory<>("idReceta"));
        colEntregaMedicamento.setCellValueFactory(new PropertyValueFactory<>("idMedicamento"));
        colEntregaCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colEntregaFecha.setCellFactory(column -> new TableCell<EntregaMedicamento, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    setText(getTableRow().getItem().getFechaEntrega() != null ?
                        getTableRow().getItem().getFechaEntrega().format(dateFormatter) : "");
                }
            }
        });
        colEntregaFacturado.setCellFactory(column -> new TableCell<EntregaMedicamento, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    setText(getTableRow().getItem().isFacturado() ? "SÍ" : "NO");
                    setStyle(getTableRow().getItem().isFacturado() ?
                        "-fx-text-fill: #16a34a;" : "-fx-text-fill: #f59e0b;");
                }
            }
        });
    }

    private void cargarDatos() {
        cargarInventario();
        cargarRecetas();
        cargarEntregas();
    }

    private void cargarInventario() {
        inventarioData.clear();
        inventarioData.addAll(medicamentoDAO.listarTodos());
    }

    private void cargarRecetas() {
        recetasData.clear();
        recetasData.addAll(recetaDAO.listarActivas());
    }

    private void cargarEntregas() {
        entregasData.clear();
        entregasData.addAll(entregaDAO.listarTodas());
    }

    private void filtrarInventario(String filtro) {
        inventarioData.clear();
        if (filtro == null || filtro.trim().isEmpty()) {
            inventarioData.addAll(medicamentoDAO.listarTodos());
        } else {
            String lowerFiltro = filtro.toLowerCase();
            medicamentoDAO.listarTodos().stream()
                .filter(m -> m.getNombre().toLowerCase().contains(lowerFiltro))
                .forEach(inventarioData::add);
        }
    }

    @FXML
    private void btnEntregarClick() {
        String strIdReceta = txtIdReceta.getText().trim();
        String strIdMedicamento = txtIdMedicamento.getText().trim();
        String strCantidad = txtCantidad.getText().trim();

        if (strIdReceta.isEmpty() || strIdMedicamento.isEmpty() || strCantidad.isEmpty()) {
            mostrarAlerta("Error", "Complete todos los campos para la entrega", Alert.AlertType.ERROR);
            return;
        }

        try {
            int idReceta = Integer.parseInt(strIdReceta);
            int idMedicamento = Integer.parseInt(strIdMedicamento);
            int cantidad = Integer.parseInt(strCantidad);

            if (cantidad <= 0) {
                mostrarAlerta("Error", "La cantidad debe ser mayor a 0", Alert.AlertType.ERROR);
                return;
            }

            boolean ok = facade.procesarEntregaMedicamento(idReceta, idMedicamento, cantidad, "FARMACIA");
            if (ok) {
                mostrarAlerta("Éxito", "Entrega registrada y stock actualizado", Alert.AlertType.INFORMATION);
                cargarDatos();
                limpiarCamposEntrega();
            } else {
                mostrarAlerta("Error", "Stock insuficiente o datos inválidos", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Los campos deben ser numéricos", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void agregarMedicamentoClick() {
        Dialog<Medicamento> dialog = new Dialog<>();
        dialog.setTitle("Agregar Medicamento");
        dialog.setHeaderText("Ingrese los datos del nuevo medicamento");

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre del medicamento");
        TextField txtStock = new TextField();
        txtStock.setPromptText("Stock inicial");
        TextField txtStockMin = new TextField();
        txtStockMin.setPromptText("Stock mínimo");

        txtStock.setText("0");
        txtStockMin.setText("0");

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setStyle("-fx-padding: 20;");
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Stock Inicial:"), 0, 1);
        grid.add(txtStock, 1, 1);
        grid.add(new Label("Stock Mínimo:"), 0, 2);
        grid.add(txtStockMin, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String nombre = txtNombre.getText().trim();
                String stockStr = txtStock.getText().trim();
                String stockMinStr = txtStockMin.getText().trim();

                if (nombre.isEmpty()) {
                    mostrarAlerta("Error", "El nombre es obligatorio", Alert.AlertType.ERROR);
                    return null;
                }

                try {
                    int stock = Integer.parseInt(stockStr);
                    int stockMin = Integer.parseInt(stockMinStr);
                    return new Medicamento(0, nombre, stock, stockMin);
                } catch (NumberFormatException e) {
                    mostrarAlerta("Error", "Los valores de stock deben ser numéricos", Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(medicamento -> {
            if (inventarioService.agregarMedicamento(medicamento)) {
                mostrarAlerta("Éxito", "Medicamento agregado correctamente", Alert.AlertType.INFORMATION);
                cargarInventario();
            } else {
                mostrarAlerta("Error", "No se pudo agregar el medicamento", Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void actualizarStockClick() {
        Medicamento selected = tblInventario.getSelectionModel().getSelectedItem();
        if (selected == null) {
            mostrarAlerta("Advertencia", "Seleccione un medicamento de la tabla", Alert.AlertType.WARNING);
            return;
        }

        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Actualizar Stock");
        dialog.setHeaderText("Nuevo stock para: " + selected.getNombre());

        TextField txtNuevoStock = new TextField();
        txtNuevoStock.setText(String.valueOf(selected.getStockActual()));

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setStyle("-fx-padding: 20;");
        grid.add(new Label("Stock Actual:"), 0, 0);
        grid.add(new Label(String.valueOf(selected.getStockActual())), 1, 0);
        grid.add(new Label("Nuevo Stock:"), 0, 1);
        grid.add(txtNuevoStock, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    return Integer.parseInt(txtNuevoStock.getText().trim());
                } catch (NumberFormatException e) {
                    mostrarAlerta("Error", "El valor debe ser numérico", Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(nuevoStock -> {
            if (inventarioService.actualizarStock(selected.getIdMedicamento(), nuevoStock)) {
                mostrarAlerta("Éxito", "Stock actualizado correctamente", Alert.AlertType.INFORMATION);
                cargarInventario();
            } else {
                mostrarAlerta("Error", "No se pudo actualizar el stock", Alert.AlertType.ERROR);
            }
        });
    }

    private void limpiarCamposEntrega() {
        txtIdReceta.clear();
        txtIdMedicamento.clear();
        txtCantidad.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}