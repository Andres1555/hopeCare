package com.esperanza.hopecare.modules.citas_consultas.dao;

import com.esperanza.hopecare.modules.citas_consultas.model.Cita;
import com.esperanza.hopecare.common.db.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CitaDAO {
    public List<Cita> obtenerCitasPorMedicoYFecha(int idMedico, LocalDate fecha) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT id_cita, id_paciente, id_medico, fecha_hora, estado " +
                     "FROM cita WHERE id_medico = ? AND DATE(fecha_hora) = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idMedico);
            pstmt.setString(2, fecha.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Cita c = new Cita();
                c.setIdCita(rs.getInt("id_cita"));
                c.setIdPaciente(rs.getInt("id_paciente"));
                c.setIdMedico(rs.getInt("id_medico"));
                c.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
                c.setEstado(rs.getString("estado"));
                citas.add(c);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return citas;
    }
    
    public boolean insertarCita(Cita cita) {
        String sql = "INSERT INTO cita (id_paciente, id_medico, fecha_hora, estado) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, cita.getIdPaciente());
            pstmt.setInt(2, cita.getIdMedico());
            pstmt.setTimestamp(3, Timestamp.valueOf(cita.getFechaHora()));
            pstmt.setString(4, cita.getEstado());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cita.setIdCita(generatedKeys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean actualizarEstado(int idCita, String nuevoEstado) {
        String sql = "UPDATE cita SET estado = ? WHERE id_cita = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nuevoEstado);
            pstmt.setInt(2, idCita);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Cita> obtenerCitasPorEstado(String estado) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT id_cita, id_paciente, id_medico, fecha_hora, estado " +
                     "FROM cita WHERE estado = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, estado);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Cita c = new Cita();
                c.setIdCita(rs.getInt("id_cita"));
                c.setIdPaciente(rs.getInt("id_paciente"));
                c.setIdMedico(rs.getInt("id_medico"));
                c.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
                c.setEstado(rs.getString("estado"));
                citas.add(c);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return citas;
    }
}
