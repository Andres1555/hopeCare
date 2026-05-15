package com.esperanza.hopecare.modules.citas_consultas.dao;

import com.esperanza.hopecare.modules.citas_consultas.model.Consulta;
import com.esperanza.hopecare.common.db.DatabaseConnection;
import java.sql.*;

public class ConsultaDAO {

    public int insertarConsultaYActualizarEstado(Consulta consulta) {
        String sqlInsert = "INSERT INTO consulta (id_cita, diagnostico, sintomas, tratamiento, facturado) VALUES (?, ?, ?, ?, ?)";
        String sqlUpdate = "UPDATE cita SET estado = 'ATENDIDA' WHERE id_cita = ?";
        Connection conn = null;
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtUpdate = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            pstmtInsert = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            pstmtInsert.setInt(1, consulta.getIdCita());
            pstmtInsert.setString(2, consulta.getDiagnostico());
            pstmtInsert.setString(3, consulta.getSintomas());
            pstmtInsert.setString(4, consulta.getTratamiento());
            pstmtInsert.setBoolean(5, consulta.isFacturado());
            int affectedInsert = pstmtInsert.executeUpdate();

            pstmtUpdate = conn.prepareStatement(sqlUpdate);
            pstmtUpdate.setInt(1, consulta.getIdCita());
            int affectedUpdate = pstmtUpdate.executeUpdate();

            if (affectedInsert == 1 && affectedUpdate == 1) {
                ResultSet rs = pstmtInsert.getGeneratedKeys();
                int idConsulta = -1;
                if (rs.next()) idConsulta = rs.getInt(1);
                consulta.setIdConsulta(idConsulta);
                conn.commit();
                return idConsulta;
            } else {
                conn.rollback();
                return -1;
            }
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return -1;
        } finally {
            try {
                if (pstmtInsert != null) pstmtInsert.close();
                if (pstmtUpdate != null) pstmtUpdate.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
