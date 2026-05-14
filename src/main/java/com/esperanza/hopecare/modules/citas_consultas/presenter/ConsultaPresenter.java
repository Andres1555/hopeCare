package com.esperanza.hopecare.modules.citas_consultas.presenter;

import com.esperanza.hopecare.modules.citas_consultas.dao.CitaDAO;
import com.esperanza.hopecare.modules.citas_consultas.dao.ConsultaDAO;
import com.esperanza.hopecare.modules.citas_consultas.model.Consulta;
import com.esperanza.hopecare.modules.citas_consultas.view.IConsultaView;

public class ConsultaPresenter {
    private IConsultaView view;
    private ConsultaDAO consultaDAO;
    private CitaDAO citaDAO;

    public ConsultaPresenter(IConsultaView view) {
        this.view = view;
        this.consultaDAO = new ConsultaDAO();
        this.citaDAO = new CitaDAO();
    }

    /**
     * Registra una consulta médica y automáticamente cambia el estado
     * de la cita asociada a 'ATENDIDA'.
     */
    public void registrarConsulta() {
        int idCita = view.getIdCitaSeleccionada();
        String diagnostico = view.getDiagnostico();
        String sintomas = view.getSintomas();
        String tratamiento = view.getTratamiento();
        boolean facturado = view.isFacturado();

        if (idCita <= 0 || diagnostico.isEmpty()) {
            view.mostrarError("Datos incompletos.");
            return;
        }

        Consulta consulta = new Consulta(idCita, diagnostico, sintomas, tratamiento, facturado);
        
        // Ejecutar transacción: insertar consulta y actualizar estado de cita
        boolean exito = consultaDAO.insertarConsultaYActualizarEstado(consulta);
        
        if (exito) {
            view.mostrarExito("Consulta registrada. Cita marcada como ATENDIDA.");
            view.limpiarFormulario();
        } else {
            view.mostrarError("Error al registrar la consulta.");
        }
    }
}
