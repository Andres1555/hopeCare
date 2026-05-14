package com.esperanza.hopecare.modules.facturacion.service;

import com.esperanza.hopecare.modules.facturacion.dto.DetalleFacturaDTO;
import com.esperanza.hopecare.modules.facturacion.dto.FacturaDTO;
import com.esperanza.hopecare.modules.facturacion.dao.*;
import com.esperanza.hopecare.common.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.esperanza.hopecare.common.events.EventBus;
import com.esperanza.hopecare.common.events.NuevaFacturaEvent;

/**
 * Servicio de facturación que orquesta la creación de facturas a partir de
 * conceptos pendientes (consultas no facturadas, exámenes, medicamentos entregados).
 */
public class FacturacionService {

    private static final double TASA_IMPUESTO = 0.19; // 19% IVA

    private ConsultaDAO consultaDAO;
    private SolicitudExamenDAO solicitudExamenDAO;
    private EntregaMedicamentoDAO entregaDAO;
    private FacturaDAO facturaDAO;
    private DetalleFacturaDAO detalleFacturaDAO;

    public FacturacionService() {
        this.consultaDAO = new ConsultaDAO();
        this.solicitudExamenDAO = new SolicitudExamenDAO();
        this.entregaDAO = new EntregaMedicamentoDAO();
        this.facturaDAO = new FacturaDAO();
        this.detalleFacturaDAO = new DetalleFacturaDAO();
    }

    /**
     * Genera una factura para un paciente a partir de todos los conceptos pendientes:
     * - Consultas médicas con facturado = 0
     * - Solicitudes de examen con facturado = 0 (se asume campo en solicitud_examen)
     * - Entregas de medicamentos con facturado = 0 (se asume campo en entrega_medicamento)
     *
     * @param idPaciente Identificador del paciente
     * @return FacturaDTO con los detalles generados, o null si no hay pendientes o error
     */
    public FacturaDTO generarFactura(int idPaciente) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Obtener todos los conceptos pendientes con sus montos
            List<DetalleFacturaDTO> detallesPendientes = new ArrayList<>();

            // Consultas no facturadas
            List<Object[]> consultas = consultaDAO.listarNoFacturadasPorPaciente(idPaciente, conn);
            for (Object[] c : consultas) {
                int idConsulta = (int) c[0];
                double monto = (double) c[1];   // precio de la consulta
                String concepto = "Consulta médica #" + idConsulta;
                detallesPendientes.add(new DetalleFacturaDTO(concepto, idConsulta, "CONSULTA", monto));
            }

            // Exámenes de laboratorio no facturados
            List<Object[]> examenes = solicitudExamenDAO.listarNoFacturadasPorPaciente(idPaciente, conn);
            for (Object[] e : examenes) {
                int idExamen = (int) e[0];
                double monto = (double) e[1];
                String concepto = "Examen de laboratorio #" + idExamen;
                detallesPendientes.add(new DetalleFacturaDTO(concepto, idExamen, "EXAMEN", monto));
            }

            // Medicamentos entregados no facturados
            List<Object[]> medicamentos = entregaDAO.listarNoFacturadosPorPaciente(idPaciente, conn);
            for (Object[] m : medicamentos) {
                int idEntrega = (int) m[0];
                double monto = (double) m[1];
                String concepto = "Medicamento entregado #" + idEntrega;
                detallesPendientes.add(new DetalleFacturaDTO(concepto, idEntrega, "MEDICAMENTO", monto));
            }

            if (detallesPendientes.isEmpty()) {
                conn.rollback();
                return null; // No hay nada que facturar
            }

            // 2. Calcular subtotal, impuesto y total
            double subtotal = detallesPendientes.stream().mapToDouble(DetalleFacturaDTO::getMonto).sum();
            double impuesto = subtotal * TASA_IMPUESTO;
            double total = subtotal + impuesto;

            // 3. Insertar cabecera de factura
            String estadoPago = "PENDIENTE";
            int idFactura = facturaDAO.insertarFactura(idPaciente, subtotal, impuesto, total, estadoPago, conn);
            if (idFactura == -1) throw new SQLException("No se pudo insertar la factura");

            // 4. Insertar cada detalle y actualizar el estado facturado de los conceptos origen
            for (DetalleFacturaDTO detalle : detallesPendientes) {
                boolean okDetalle = detalleFacturaDAO.insertarDetalle(idFactura, detalle, conn);
                if (!okDetalle) throw new SQLException("Error al insertar detalle: " + detalle.getConcepto());

                // Marcar el concepto original como facturado
                boolean okActualizar = marcarConceptoFacturado(detalle, conn);
                if (!okActualizar) throw new SQLException("Error al actualizar facturado para: " + detalle.getConcepto());
            }

            conn.commit();
            EventBus.getInstance().post(new NuevaFacturaEvent(idFactura, total));

            // 5. Retornar el DTO inmutable
            return new FacturaDTO(idPaciente, subtotal, impuesto, total, estadoPago, detallesPendientes);

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return null;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Marcar el concepto original (consulta, examen o medicamento) como facturado (=1).
     */
    private boolean marcarConceptoFacturado(DetalleFacturaDTO detalle, Connection conn) throws SQLException {
        String tipo = detalle.getTipoReferencia();
        int idReferencia = detalle.getIdReferencia();
        switch (tipo) {
            case "CONSULTA":
                return consultaDAO.marcarFacturado(idReferencia, conn);
            case "EXAMEN":
                return solicitudExamenDAO.marcarFacturado(idReferencia, conn);
            case "MEDICAMENTO":
                return entregaDAO.marcarFacturado(idReferencia, conn);
            default:
                return false;
        }
    }
}
