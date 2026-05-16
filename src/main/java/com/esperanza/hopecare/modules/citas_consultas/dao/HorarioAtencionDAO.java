package com.esperanza.hopecare.modules.citas_consultas.dao;

import com.esperanza.hopecare.modules.citas_consultas.model.HorarioAtencion;
import com.esperanza.hopecare.common.db.DatabaseConnection;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HorarioAtencionDAO {
    public HorarioAtencion obtenerHorarioPorMedicoYDia(int idMedico, int diaSemana) {
        String sql = "SELECT id_medico, dia_semana, hora_inicio, hora_fin, intervalo_minutos, activo " +
                     "FROM horario_atencion WHERE id_medico = ? AND dia_semana = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idMedico);
            pstmt.setInt(2, diaSemana);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                HorarioAtencion h = new HorarioAtencion();
                h.setIdMedico(rs.getInt("id_medico"));
                h.setDiaSemana(rs.getInt("dia_semana"));
                h.setHoraInicio(LocalTime.parse(rs.getString("hora_inicio")));
                h.setHoraFin(LocalTime.parse(rs.getString("hora_fin")));
                h.setIntervaloMinutos(rs.getInt("intervalo_minutos"));
                h.setActivo(rs.getBoolean("activo"));
                return h;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<HorarioAtencion> obtenerHorariosPorMedico(int idMedico) {
        List<HorarioAtencion> lista = new ArrayList<>();
        String sql = "SELECT id_medico, dia_semana, hora_inicio, hora_fin, intervalo_minutos, activo " +
                     "FROM horario_atencion WHERE id_medico = ? ORDER BY dia_semana";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idMedico);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                HorarioAtencion h = new HorarioAtencion();
                h.setIdMedico(rs.getInt("id_medico"));
                h.setDiaSemana(rs.getInt("dia_semana"));
                h.setHoraInicio(LocalTime.parse(rs.getString("hora_inicio")));
                h.setHoraFin(LocalTime.parse(rs.getString("hora_fin")));
                h.setIntervaloMinutos(rs.getInt("intervalo_minutos"));
                h.setActivo(rs.getBoolean("activo"));
                lista.add(h);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}
