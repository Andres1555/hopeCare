package com.esperanza.hopecare.ui.farmacia;

import com.esperanza.hopecare.modules.medicamentos_lab.facade.GestionClinicaFacade;
import com.esperanza.hopecare.modules.medicamentos_lab.dao.MedicamentoDAO;
import com.esperanza.hopecare.modules.medicamentos_lab.model.Medicamento;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FarmaciaPanel extends JPanel {
    private JComboBox<String> cbRecetas;
    private JComboBox<Medicamento> cbMedicamentos;
    private JSpinner spCantidad;
    private JButton btnEntregar;
    private GestionClinicaFacade facade;
    private MedicamentoDAO medicamentoDAO;

    public FarmaciaPanel() {
        facade = new GestionClinicaFacade();
        medicamentoDAO = new MedicamentoDAO();
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        gbc.gridx=0; gbc.gridy=0; add(new JLabel("Receta médica:"), gbc);
        cbRecetas = new JComboBox<>();
        gbc.gridx=1; add(cbRecetas, gbc);

        gbc.gridx=0; gbc.gridy=1; add(new JLabel("Medicamento:"), gbc);
        cbMedicamentos = new JComboBox<>();
        gbc.gridx=1; add(cbMedicamentos, gbc);

        gbc.gridx=0; gbc.gridy=2; add(new JLabel("Cantidad:"), gbc);
        spCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        gbc.gridx=1; add(spCantidad, gbc);

        btnEntregar = new JButton("Registrar entrega");
        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2;
        add(btnEntregar, gbc);

        cargarRecetas();
        cargarMedicamentos();

        btnEntregar.addActionListener(e -> entregar());
    }

    private void cargarRecetas() {
        // Obtener recetas pendientes de entrega (por ejemplo, de la BD)
        // Aquí simulado
        cbRecetas.addItem("Receta #1 - Consulta 5");
        cbRecetas.addItem("Receta #2 - Consulta 7");
    }

    private void cargarMedicamentos() {
        cbMedicamentos.removeAllItems();
        List<Medicamento> medicamentos = medicamentoDAO.listarTodos();
        for (Medicamento m : medicamentos) {
            cbMedicamentos.addItem(m);
        }
    }

    private void entregar() {
        if (cbRecetas.getSelectedIndex() == -1 || cbMedicamentos.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione receta y medicamento");
            return;
        }
        String recetaStr = (String) cbRecetas.getSelectedItem();
        int idReceta = Integer.parseInt(recetaStr.split("#")[1].split(" ")[0]);
        Medicamento med = (Medicamento) cbMedicamentos.getSelectedItem();
        int cantidad = (Integer) spCantidad.getValue();
        String rol = "FARMACIA"; // En un caso real se obtendría del login

        boolean ok = facade.procesarEntregaMedicamento(idReceta, med.getIdMedicamento(), cantidad, rol);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Entrega registrada y stock actualizado");
            // Refrescar lista de medicamentos (stock actualizado)
            cargarMedicamentos();
        } else {
            JOptionPane.showMessageDialog(this, "Error: stock insuficiente o datos inválidos");
        }
    }
}
