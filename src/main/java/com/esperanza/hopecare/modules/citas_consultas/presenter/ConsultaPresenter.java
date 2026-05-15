package com.esperanza.hopecare.modules.citas_consultas.presenter;
import com.esperanza.hopecare.common.db.DatabaseConnection;

import com.esperanza.hopecare.modules.citas_consultas.dao.CitaDAO;
import com.esperanza.hopecare.modules.citas_consultas.dao.ConsultaDAO;
import com.esperanza.hopecare.modules.citas_consultas.model.Cita;
import com.esperanza.hopecare.modules.citas_consultas.model.Consulta;
import com.esperanza.hopecare.modules.citas_consultas.view.IConsultaView;
import com.esperanza.hopecare.modules.medicamentos_lab.dao.DetalleRecetaDAO;
import com.esperanza.hopecare.modules.medicamentos_lab.dao.ExamenLaboratorioDAO;
import com.esperanza.hopecare.modules.medicamentos_lab.dao.MedicamentoDAO;
import com.esperanza.hopecare.modules.medicamentos_lab.dao.RecetaDAO;
import com.esperanza.hopecare.modules.medicamentos_lab.dao.SolicitudExamenDAO;
import com.esperanza.hopecare.modules.medicamentos_lab.model.DetalleReceta;
import com.esperanza.hopecare.modules.medicamentos_lab.model.ExamenLaboratorio;
import com.esperanza.hopecare.modules.medicamentos_lab.model.Medicamento;
import com.esperanza.hopecare.modules.medicamentos_lab.model.Receta;
import com.esperanza.hopecare.modules.medicamentos_lab.model.SolicitudExamen;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ConsultaPresenter {
    private final IConsultaView view;
    private final ConsultaDAO consultaDAO;
    private final CitaDAO citaDAO;
    private final ExamenLaboratorioDAO examenLaboratorioDAO;
    private final SolicitudExamenDAO solicitudExamenDAO;
    private final MedicamentoDAO medicamentoDAO;
    private final RecetaDAO recetaDAO;
    private final DetalleRecetaDAO detalleRecetaDAO;
    private int idConsultaActual = -1;

    public ConsultaPresenter(IConsultaView view) {
        this.view = view;
        this.consultaDAO = new ConsultaDAO();
        this.citaDAO = new CitaDAO();
        this.examenLaboratorioDAO = new ExamenLaboratorioDAO();
        this.solicitudExamenDAO = new SolicitudExamenDAO();
        this.medicamentoDAO = new MedicamentoDAO();
        this.recetaDAO = new RecetaDAO();
        this.detalleRecetaDAO = new DetalleRecetaDAO();
    }

    public void cargarCitasPendientes() {
        List<Cita> citas = citaDAO.obtenerCitasPorEstadoConNombres("PROGRAMADA");
        view.mostrarCitasPendientes(citas);
        view.actualizarEstadoAcciones(false);
    }

    public void seleccionarCita() {
        int idCita = view.getIdCitaSeleccionada();
        if (idCita <= 0) {
            view.mostrarError("Seleccione una cita.");
            return;
        }
        idConsultaActual = -1;
        view.limpiarFormulario();
        view.actualizarEstadoAcciones(false);
        view.mostrarExito("Cita cargada, puede registrar la consulta.");
    }

    public void registrarConsulta() {
        int idCita = view.getIdCitaSeleccionada();
        String diagnostico = limpiarTexto(view.getDiagnostico());
        String sintomas = limpiarTexto(view.getSintomas());
        String tratamiento = limpiarTexto(view.getTratamiento());

        if (idCita <= 0) {
            view.mostrarError("Seleccione una cita primero.");
            return;
        }
        if (sintomas.isEmpty() || diagnostico.isEmpty()) {
            view.mostrarError("Síntomas y diagnóstico son obligatorios.");
            return;
        }
        Consulta consulta = new Consulta(idCita, diagnostico, sintomas, tratamiento, false);
        int idConsulta = consultaDAO.insertarConsultaYActualizarEstado(consulta);
        if (idConsulta > 0) {
            idConsultaActual = idConsulta;
            view.actualizarEstadoAcciones(true);
            view.mostrarExito("Consulta registrada correctamente.");
            cargarCitasPendientes();
        } else {
            view.mostrarError("Error al registrar la consulta.");
        }
    }

    public void solicitarExamen() {
        if (idConsultaActual <= 0) {
            view.mostrarError("Primero guarde la consulta.");
            return;
        }

        List<ExamenLaboratorio> examenes = examenLaboratorioDAO.listarTodos();
        if (examenes.isEmpty()) {
            view.mostrarError("No hay exámenes disponibles.");
            return;
        }

        Integer idExamen = view.solicitarExamen(examenes);
        if (idExamen == null || idExamen <= 0) {
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            SolicitudExamen solicitud = new SolicitudExamen(idConsultaActual, idExamen);
            if (solicitudExamenDAO.insertar(solicitud, conn)) {
                view.mostrarExito("Examen solicitado correctamente.");
            } else {
                view.mostrarError("No se pudo solicitar el examen.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            view.mostrarError("Error de base de datos al solicitar examen.");
        }
    }

    public void recetarMedicamento() {
        if (idConsultaActual <= 0) {
            view.mostrarError("Primero guarde la consulta.");
            return;
        }

        List<Medicamento> medicamentos = medicamentoDAO.listarTodos();
        if (medicamentos.isEmpty()) {
            view.mostrarError("No hay medicamentos disponibles.");
            return;
        }

        IConsultaView.RecetaRequest recetaRequest = view.solicitarReceta(medicamentos);
        if (recetaRequest == null) {
            return;
        }
        if (recetaRequest.getIdMedicamento() <= 0 || recetaRequest.getCantidad() <= 0) {
            view.mostrarError("Datos de receta inválidos.");
            return;
        }

        String dosis = limpiarTexto(recetaRequest.getDosisIndicacion());
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            Receta receta = new Receta(idConsultaActual, dosis);
            int idReceta = recetaDAO.insertar(receta, conn);
            if (idReceta <= 0) {
                conn.rollback();
                view.mostrarError("No se pudo crear la receta.");
                return;
            }

            DetalleReceta detalle = new DetalleReceta(
                idReceta,
                recetaRequest.getIdMedicamento(),
                recetaRequest.getCantidad(),
                dosis
            );
            boolean detalleGuardado = detalleRecetaDAO.insertar(detalle, conn);
            if (!detalleGuardado) {
                conn.rollback();
                view.mostrarError("No se pudo registrar el detalle de receta.");
                return;
            }

            conn.commit();
            view.mostrarExito("Medicamento recetado correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            view.mostrarError("Error de base de datos al recetar medicamento.");
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String limpiarTexto(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
