package com.esperanza.hopecare.modules.citas_consultas.view;
import com.esperanza.hopecare.modules.citas_consultas.model.Cita;
import com.esperanza.hopecare.modules.medicamentos_lab.model.ExamenLaboratorio;
import com.esperanza.hopecare.modules.medicamentos_lab.model.Medicamento;
import java.util.List;

/**
 * Interfaz que define los métodos que debe implementar cualquier vista
 * para gestionar consultas médicas.
 */
public interface IConsultaView {
    int getIdCitaSeleccionada();
    String getDiagnostico();
    String getSintomas();
    String getTratamiento();
    void mostrarCitasPendientes(List<Cita> citas);
    void mostrarError(String mensaje);
    void mostrarExito(String mensaje);
    void limpiarFormulario();
    void limpiarSeleccionCita();
    void actualizarEstadoAcciones(boolean consultaGuardada);
    Integer solicitarExamen(List<ExamenLaboratorio> examenesDisponibles);
    RecetaRequest solicitarReceta(List<Medicamento> medicamentosDisponibles);

    class RecetaRequest {
        private final int idMedicamento;
        private final int cantidad;
        private final String dosisIndicacion;

        public RecetaRequest(int idMedicamento, int cantidad, String dosisIndicacion) {
            this.idMedicamento = idMedicamento;
            this.cantidad = cantidad;
            this.dosisIndicacion = dosisIndicacion;
        }

        public int getIdMedicamento() {
            return idMedicamento;
        }

        public int getCantidad() {
            return cantidad;
        }

        public String getDosisIndicacion() {
            return dosisIndicacion;
        }
    }
}
