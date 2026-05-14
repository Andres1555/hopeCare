package com.esperanza.hopecare.laboratorio.view;

import com.esperanza.hopecare.modules.medicamentos_lab.facade.GestionClinicaFacade;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class LaboratorioController {
    @FXML private ComboBox<String> cbSolicitudes;
    @FXML private TextArea txtResultado;

    private GestionClinicaFacade facade;

    @FXML
    public void initialize() {
        facade = new GestionClinicaFacade();
        cargarSolicitudes();
    }

    private void cargarSolicitudes() {
        ObservableList<String> items = FXCollections.observableArrayList();
        items.add("Solicitud #1 - Hemograma (Paciente: Juan)");
        items.add("Solicitud #2 - Glucosa (Paciente: María)");
        cbSolicitudes.setItems(items);
    }

    @FXML
    private void btnRegistrarClick() {
        String seleccion = cbSolicitudes.getValue();
        if (seleccion == null || txtResultado.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "Seleccione una solicitud y escriba el resultado", Alert.AlertType.ERROR);
            return;
        }
        int idSolicitud = Integer.parseInt(seleccion.split("#")[1].split(" ")[0]);
        String resultado = txtResultado.getText().trim();
        boolean ok = facade.registrarResultadoExamen(idSolicitud, resultado, "COMPLETADO", "LABORATORIO");
        if (ok) {
            mostrarAlerta("Éxito", "Resultado registrado", Alert.AlertType.INFORMATION);
            cargarSolicitudes();
            txtResultado.clear();
        } else {
            mostrarAlerta("Error", "No se pudo registrar", Alert.AlertType.ERROR);
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
