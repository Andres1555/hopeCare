package com.esperanza.hopecare.modules.medicamentos_lab.dao;

import com.esperanza.hopecare.modules.medicamentos_lab.model.Receta;
import com.esperanza.hopecare.common.db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecetaDAO {

    public Receta obtenerPorId(int idReceta, Connection conn) throws SQLException {
        String sql = "SELECT id_receta, id_consulta, fecha_emision, instrucciones, activa FROM receta WHERE id_receta = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idReceta);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapear(rs);
            }
        }
        return null;
    }

    public List<Receta> listarPorConsulta(int idConsulta) {
        List<Receta> lista = new ArrayList<>();
        String sql = "SELECT id_receta, id_consulta, fecha_emision, instrucciones, activa FROM receta WHERE id_consulta = ?";
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

    public List<Receta> listarActivas() {
        List<Receta> lista = new ArrayList<>();
        String sql = "SELECT id_receta, id_consulta, fecha_emision, instrucciones, activa FROM receta WHERE activa = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public int insertar(Receta receta, Connection conn) throws SQLException {
        String sql = "INSERT INTO receta (id_consulta, instrucciones, activa) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, receta.getIdConsulta());
            ps.setString(2, receta.getInstrucciones());
            ps.setBoolean(3, receta.isActiva());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    receta.setIdReceta(id);
                    return id;
                }
            }
            return -1;
        }
    }

    private Receta mapear(ResultSet rs) throws SQLException {
        Receta r = new Receta();
        r.setIdReceta(rs.getInt("id_receta"));
        r.setIdConsulta(rs.getInt("id_consulta"));
        r.setInstrucciones(rs.getString("instrucciones"));
        r.setActiva(rs.getBoolean("activa"));
        Timestamp ts = rs.getTimestamp("fecha_emision");
        if (ts != null) r.setFechaEmision(ts.toLocalDateTime());
        return r;
    }
}
