package com.esperanza.hopecare.modules.medicamentos_lab.facade;

import com.esperanza.hopecare.modules.medicamentos_lab.dao.*;
import com.esperanza.hopecare.modules.medicamentos_lab.model.*;
import com.esperanza.hopecare.common.utils.RoleValidator;
import com.esperanza.hopecare.common.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Fachada principal para orquestar operaciones de medicamentos y laboratorio.
 * Valida roles: solo 'ADMIN' o 'FARMACIA' pueden usar métodos de farmacia;
 * solo 'ADMIN' o 'LABORATORIO' pueden usar métodos de laboratorio.
 */
public class GestionClinicaFacade {

    private MedicamentoDAO medicamentoDAO;
    private EntregaMedicamentoDAO entregaDAO;
    private SolicitudExamenDAO solicitudDAO;
    private RoleValidator roleValidator;

    public GestionClinicaFacade() {
        this.medicamentoDAO = new MedicamentoDAO();
        this.entregaDAO = new EntregaMedicamentoDAO();
        this.solicitudDAO = new SolicitudExamenDAO();
        this.roleValidator = new RoleValidator();
    }

    /**
     * Procesa la entrega de un medicamento y descuenta del stock.
     * Solo pueden ejecutarlo usuarios con rol 'ADMIN' o 'FARMACIA'.
     * 
     * @param idReceta      Identificador de la receta
     * @param idMedicamento Medicamento a entregar
     * @param cantidad      Cantidad solicitada
     * @param rolUsuario    Rol del usuario que hace la petición
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public boolean procesarEntregaMedicamento(int idReceta, int idMedicamento, int cantidad, String rolUsuario) {
        if (!roleValidator.tieneRol(rolUsuario, "FARMACIA") && !roleValidator.tieneRol(rolUsuario, "ADMIN")) {
            System.err.println("Acceso denegado: rol " + rolUsuario + " no puede procesar entregas.");
            return false;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Verificar stock suficiente
            Medicamento med = medicamentoDAO.obtenerPorId(idMedicamento, conn);
            if (med == null || med.getStockActual() < cantidad) {
                throw new RuntimeException("Stock insuficiente o medicamento no encontrado.");
            }

            // 2. Registrar la entrega
            EntregaMedicamento entrega = new EntregaMedicamento(0, idReceta, idMedicamento, cantidad);
            boolean okEntrega = entregaDAO.insertar(entrega, conn);
            if (!okEntrega) throw new RuntimeException("Error al registrar entrega.");

            // 3. Actualizar stock (restar cantidad)
            int nuevoStock = med.getStockActual() - cantidad;
            boolean okStock = medicamentoDAO.actualizarStock(idMedicamento, nuevoStock, conn);
            if (!okStock) throw new RuntimeException("Error al actualizar stock.");

            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Registra el resultado de un examen de laboratorio.
     * Solo pueden ejecutarlo usuarios con rol 'ADMIN' o 'LABORATORIO'.
     * 
     * @param idSolicitud   Identificador de la solicitud de examen
     * @param resultado     Texto con el resultado
     * @param estado        Nuevo estado (ej. "COMPLETADO")
     * @param rolUsuario    Rol del usuario
     * @return true si se actualizó correctamente
     */
    public boolean registrarResultadoExamen(int idSolicitud, String resultado, String estado, String rolUsuario) {
        if (!roleValidator.tieneRol(rolUsuario, "LABORATORIO") && !roleValidator.tieneRol(rolUsuario, "ADMIN")) {
            System.err.println("Acceso denegado: rol " + rolUsuario + " no puede registrar resultados.");
            return false;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            return solicitudDAO.actualizarResultado(idSolicitud, resultado, estado, conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
