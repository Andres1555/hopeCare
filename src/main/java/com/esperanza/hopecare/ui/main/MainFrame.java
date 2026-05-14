package com.esperanza.hopecare.ui.main;

import com.esperanza.hopecare.ui.registro.RegistroMedicoPanel;
import com.esperanza.hopecare.ui.citas.CitasPanel;
import com.esperanza.hopecare.ui.facturacion.FacturacionPanel;
import com.esperanza.hopecare.ui.dashboard.DashboardPanel;
import com.esperanza.hopecare.ui.consulta.ConsultaPanel;
import com.esperanza.hopecare.ui.farmacia.FarmaciaPanel;
import com.esperanza.hopecare.ui.laboratorio.LaboratorioPanel;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Sisgeho - Sistema de Gestión Hospitalaria");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Registro de Médicos", new RegistroMedicoPanel());
        tabbedPane.addTab("Agendar Cita", new CitasPanel());
        tabbedPane.addTab("Facturación", new FacturacionPanel());
        tabbedPane.addTab("Dashboard", new DashboardPanel());
        tabbedPane.addTab("Consulta Médica", new ConsultaPanel());
        tabbedPane.addTab("Farmacia", new FarmaciaPanel());
        tabbedPane.addTab("Laboratorio", new LaboratorioPanel());
        
        add(tabbedPane);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
