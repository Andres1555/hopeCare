package com.esperanza.hopecare.facturacion.view;

import com.esperanza.hopecare.modules.facturacion.dto.FacturaDTO;
import com.esperanza.hopecare.modules.facturacion.service.FacturacionService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FacturacionController {
    @FXML private TextField txtIdPaciente;
    @FXML private TextArea txtResultado;
    @FXML private javafx.scene.control.Button btnGenerar;

    private FacturacionService service;

    @FXML
    public void initialize() {
        service = new FacturacionService();
        btnGenerar.setOnAction(e -> generarFactura());
    }

    private void generarFactura() {
        try {
            int idPaciente = Integer.parseInt(txtIdPaciente.getText().trim());
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
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "ID Paciente inválido", Alert.AlertType.ERROR);
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
