package com.esperanza.hopecare.modules.facturacion.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SolicitudExamenDAO {
    public List<Object[]> listarNoFacturadasPorPaciente(int idPaciente, Connection conn) throws SQLException {
        String sql = "SELECT s.id_solicitud, e.precio FROM solicitud_examen s JOIN examen_laboratorio e ON s.id_examen = e.id_examen JOIN consulta c ON s.id_consulta = c.id_consulta JOIN cita ci ON c.id_cita = ci.id_cita WHERE ci.id_paciente = ? AND ci.estado = 'ATENDIDA' AND s.estado = 'COMPLETADO' AND s.facturado = 0";
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
        String sql = "UPDATE solicitud_examen SET facturado = 1 WHERE id_solicitud = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idReferencia);
            return ps.executeUpdate() == 1;
        }
    }
}
