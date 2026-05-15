module com.esperanza.hopecare {
    requires javafx.controls;
    requires javafx.fxml;
    
    requires java.desktop;
    
    requires java.sql;

    opens com.esperanza.hopecare to javafx.fxml;
    opens com.esperanza.hopecare.main to javafx.fxml;
    opens com.esperanza.hopecare.registro.view to javafx.fxml;
    opens com.esperanza.hopecare.citas.view to javafx.fxml;
    opens com.esperanza.hopecare.consulta.view to javafx.fxml;
    opens com.esperanza.hopecare.farmacia.view to javafx.fxml;
    opens com.esperanza.hopecare.laboratorio.view to javafx.fxml;
    opens com.esperanza.hopecare.facturacion.view to javafx.fxml;
    opens com.esperanza.hopecare.dashboard.view to javafx.fxml;

    opens com.esperanza.hopecare.modules.medicamentos_lab.model to javafx.base;
    opens com.esperanza.hopecare.modules.citas_consultas.model to javafx.base;
    opens com.esperanza.hopecare.modules.pacientes_medicos.model to javafx.base;
    opens com.esperanza.hopecare.modules.dashboard.model to javafx.base;

    exports com.esperanza.hopecare;
    exports com.esperanza.hopecare.main;
}
