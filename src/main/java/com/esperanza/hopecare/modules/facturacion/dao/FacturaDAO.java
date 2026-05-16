package com.esperanza.hopecare.modules.facturacion.dao;

import com.esperanza.hopecare.common.db.DatabaseConnection;
import com.esperanza.hopecare.modules.facturacion.dto.FacturaResumenDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FacturaDAO {
    public int insertarFactura(int idPaciente, double subtotal, double impuesto, double total, String estadoPago, Connection conn) throws SQLException {
        String sql = "INSERT INTO factura (id_paciente, fecha_emision, subtotal, impuesto, total, estado_pago) VALUES (?, datetime('now', 'localtime'), ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idPaciente);
            ps.setDouble(2, subtotal);
            ps.setDouble(3, impuesto);
            ps.setDouble(4, total);
            ps.setString(5, estadoPago);
            int affected = ps.executeUpdate();
            if (affected == 0) return -1;
            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    public List<FacturaResumenDTO> listarTodasConPaciente() {
        List<FacturaResumenDTO> lista = new ArrayList<>();
        String sql = "SELECT f.id_factura, f.fecha_emision, f.subtotal, f.impuesto, f.total, f.estado_pago, "
                   + "per.nombre, per.apellido "
                   + "FROM factura f "
                   + "JOIN paciente p ON f.id_paciente = p.id_paciente "
                   + "JOIN persona per ON p.id_persona = per.id_persona "
                   + "ORDER BY f.fecha_emision DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String paciente = rs.getString("nombre") + " " + rs.getString("apellido");
                lista.add(new FacturaResumenDTO(
                    rs.getInt("id_factura"),
                    paciente,
                    rs.getString("fecha_emision"),
                    rs.getDouble("subtotal"),
                    rs.getDouble("impuesto"),
                    rs.getDouble("total"),
                    rs.getString("estado_pago")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Set<Integer> obtenerIdsPacientesConPendientes() {
        Set<Integer> ids = new HashSet<>();
        String sql = "SELECT DISTINCT ci.id_paciente FROM consulta c JOIN cita ci ON c.id_cita = ci.id_cita WHERE ci.estado = 'ATENDIDA' AND c.facturado = 0 "
                   + "UNION "
                   + "SELECT DISTINCT ci.id_paciente FROM solicitud_examen s JOIN consulta c ON s.id_consulta = c.id_consulta JOIN cita ci ON c.id_cita = ci.id_cita WHERE ci.estado = 'ATENDIDA' AND s.estado = 'COMPLETADO' AND s.facturado = 0 "
                   + "UNION "
                   + "SELECT DISTINCT ci.id_paciente FROM entrega_medicamento em JOIN detalle_receta dr ON em.id_detalle_receta = dr.id_detalle JOIN receta r ON dr.id_receta = r.id_receta JOIN consulta c ON r.id_consulta = c.id_consulta JOIN cita ci ON c.id_cita = ci.id_cita WHERE ci.estado = 'ATENDIDA' AND em.facturado = 0";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }
}
