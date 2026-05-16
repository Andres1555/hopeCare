package com.esperanza.hopecare.modules.facturacion.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EntregaMedicamentoDAO {
    public List<Object[]> listarNoFacturadosPorPaciente(int idPaciente, Connection conn) throws SQLException {
        String sql = "SELECT em.id_entrega, m.precio_unitario * em.cantidad_entregada FROM entrega_medicamento em JOIN detalle_receta dr ON em.id_detalle_receta = dr.id_detalle JOIN medicamento m ON dr.id_medicamento = m.id_medicamento JOIN receta r ON dr.id_receta = r.id_receta JOIN consulta c ON r.id_consulta = c.id_consulta JOIN cita ci ON c.id_cita = ci.id_cita WHERE ci.id_paciente = ? AND ci.estado = 'ATENDIDA' AND em.facturado = 0";
        List<Object[]> resultados = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                resultados.add(new Object[]{rs.getInt(1), rs.getDouble(2)});
            }
        }
        return resultados;
    }

    public boolean marcarFacturado(int idReferencia, Connection conn) throws SQLException {
        String sql = "UPDATE entrega_medicamento SET facturado = 1 WHERE id_entrega = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idReferencia);
            return ps.executeUpdate() == 1;
        }
    }
}
