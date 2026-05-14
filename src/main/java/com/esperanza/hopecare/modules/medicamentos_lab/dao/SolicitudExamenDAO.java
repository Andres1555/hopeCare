package com.esperanza.hopecare.modules.medicamentos_lab.dao;

import java.sql.*;

public class SolicitudExamenDAO {
    public boolean actualizarResultado(int idSolicitud, String resultado, String estado, Connection conn) throws SQLException {
        String sql = "UPDATE solicitud_examen SET resultado_texto = ?, estado = ? WHERE id_solicitud = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, resultado);
            ps.setString(2, estado);
            ps.setInt(3, idSolicitud);
            return ps.executeUpdate() == 1;
        }
    }
}
