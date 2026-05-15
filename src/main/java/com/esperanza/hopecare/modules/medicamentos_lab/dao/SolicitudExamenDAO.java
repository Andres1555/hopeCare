package com.esperanza.hopecare.modules.medicamentos_lab.dao;

import com.esperanza.hopecare.modules.medicamentos_lab.model.SolicitudExamen;
import com.esperanza.hopecare.common.db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SolicitudExamenDAO {

    public SolicitudExamen obtenerPorId(int idSolicitud, Connection conn) throws SQLException {
        String sql = "SELECT id_solicitud, id_consulta, id_examen, fecha_solicitud, estado, resultado_texto, realizado_por, facturado FROM solicitud_examen WHERE id_solicitud = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSolicitud);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapear(rs);
            }
        }
        return null;
    }

    public List<SolicitudExamen> listarPendientes() {
        List<SolicitudExamen> lista = new ArrayList<>();
        String sql = "SELECT id_solicitud, id_consulta, id_examen, fecha_solicitud, estado, resultado_texto, realizado_por, facturado FROM solicitud_examen WHERE estado = 'PENDIENTE'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public List<SolicitudExamen> listarPorConsulta(int idConsulta) {
        List<SolicitudExamen> lista = new ArrayList<>();
        String sql = "SELECT id_solicitud, id_consulta, id_examen, fecha_solicitud, estado, resultado_texto, realizado_por, facturado FROM solicitud_examen WHERE id_consulta = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idConsulta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public boolean insertar(SolicitudExamen solicitud, Connection conn) throws SQLException {
        String sql = "INSERT INTO solicitud_examen (id_consulta, id_examen, estado) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, solicitud.getIdConsulta());
            ps.setInt(2, solicitud.getIdExamen());
            ps.setString(3, solicitud.getEstado());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) solicitud.setIdSolicitud(rs.getInt(1));
            }
            return affected > 0;
        }
    }

    public boolean actualizarResultado(int idSolicitud, String resultado, String estado, Connection conn) throws SQLException {
        String sql = "UPDATE solicitud_examen SET resultado_texto = ?, estado = ? WHERE id_solicitud = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, resultado);
            ps.setString(2, estado);
            ps.setInt(3, idSolicitud);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean marcarFacturado(int idSolicitud, Connection conn) throws SQLException {
        String sql = "UPDATE solicitud_examen SET facturado = 1 WHERE id_solicitud = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSolicitud);
            return ps.executeUpdate() == 1;
        }
    }

    public List<SolicitudExamen> listarTodas() {
        List<SolicitudExamen> lista = new ArrayList<>();
        String sql = "SELECT id_solicitud, id_consulta, id_examen, fecha_solicitud, estado, resultado_texto, realizado_por, facturado FROM solicitud_examen ORDER BY fecha_solicitud DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public List<SolicitudExamen> listarPorEstado(String estado) {
        List<SolicitudExamen> lista = new ArrayList<>();
        String sql = "SELECT id_solicitud, id_consulta, id_examen, fecha_solicitud, estado, resultado_texto, realizado_por, facturado FROM solicitud_examen WHERE estado = ? ORDER BY fecha_solicitud DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    private SolicitudExamen mapear(ResultSet rs) throws SQLException {
        SolicitudExamen s = new SolicitudExamen();
        s.setIdSolicitud(rs.getInt("id_solicitud"));
        s.setIdConsulta(rs.getInt("id_consulta"));
        s.setIdExamen(rs.getInt("id_examen"));
        Timestamp ts = rs.getTimestamp("fecha_solicitud");
        if (ts != null) s.setFechaSolicitud(ts.toLocalDateTime());
        s.setEstado(rs.getString("estado"));
        s.setResultadoTexto(rs.getString("resultado_texto"));
        s.setRealizadoPor(rs.getInt("realizado_por"));
        s.setFacturado(rs.getBoolean("facturado"));
        return s;
    }
}
