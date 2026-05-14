package com.esperanza.hopecare.farmacia.view;

import com.esperanza.hopecare.modules.medicamentos_lab.facade.GestionClinicaFacade;
import com.esperanza.hopecare.modules.medicamentos_lab.dao.MedicamentoDAO;
import com.esperanza.hopecare.modules.medicamentos_lab.model.Medicamento;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

public class FarmaciaController {
    @FXML private ComboBox<String> cbRecetas;
    @FXML private ComboBox<Medicamento> cbMedicamentos;
    @FXML private Spinner<Integer> spCantidad;

    private GestionClinicaFacade facade;
    private MedicamentoDAO medicamentoDAO;

    @FXML
    public void initialize() {
        facade = new GestionClinicaFacade();
        medicamentoDAO = new MedicamentoDAO();
        spCantidad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

        cargarRecetas();
        cargarMedicamentos();

        cbMedicamentos.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Medicamento item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre() + " (Stock: " + item.getStockActual() + ")");
            }
        });
        cbMedicamentos.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Medicamento item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
    }

    private void cargarRecetas() {
        ObservableList<String> recetas = FXCollections.observableArrayList();
        recetas.add("Receta #1 - Consulta 5 (Paciente: Juan)");
        recetas.add("Receta #2 - Consulta 7 (Paciente: María)");
        cbRecetas.setItems(recetas);
    }

    private void cargarMedicamentos() {
        List<Medicamento> lista = medicamentoDAO.listarTodos();
        cbMedicamentos.getItems().setAll(lista);
    }

    @FXML
    private void btnEntregarClick() {
        String recetaSeleccionada = cbRecetas.getValue();
        Medicamento med = cbMedicamentos.getValue();
        int cantidad = spCantidad.getValue();

        if (recetaSeleccionada == null || med == null) {
            mostrarAlerta("Error", "Seleccione receta y medicamento", Alert.AlertType.ERROR);
            return;
        }
        int idReceta = Integer.parseInt(recetaSeleccionada.split("#")[1].split(" ")[0]);
        String rol = "FARMACIA";

        boolean ok = facade.procesarEntregaMedicamento(idReceta, med.getIdMedicamento(), cantidad, rol);
        if (ok) {
            mostrarAlerta("Éxito", "Entrega registrada y stock actualizado", Alert.AlertType.INFORMATION);
            cargarMedicamentos();
        } else {
            mostrarAlerta("Error", "Stock insuficiente o datos inválidos", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
