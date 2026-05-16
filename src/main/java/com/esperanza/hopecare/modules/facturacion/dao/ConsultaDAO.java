package com.esperanza.hopecare.modules.facturacion.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConsultaDAO {
    public List<Object[]> listarNoFacturadasPorPaciente(int idPaciente, Connection conn) throws SQLException {
        String sql = "SELECT c.id_consulta, c.precio FROM consulta c JOIN cita ci ON c.id_cita = ci.id_cita WHERE ci.id_paciente = ? AND ci.estado = 'ATENDIDA' AND c.facturado = 0";
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
        String sql = "UPDATE consulta SET facturado = 1 WHERE id_consulta = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idReferencia);
            return ps.executeUpdate() == 1;
        }
    }
}
