package com.esperanza.hopecare.modules.medicamentos_lab.dao;

import com.esperanza.hopecare.modules.medicamentos_lab.model.ExamenLaboratorio;
import com.esperanza.hopecare.common.db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExamenLaboratorioDAO {

    public ExamenLaboratorio obtenerPorId(int idExamen, Connection conn) throws SQLException {
        String sql = "SELECT id_examen, nombre, descripcion, precio, tiempo_resultado_horas FROM examen_laboratorio WHERE id_examen = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idExamen);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapear(rs);
            }
        }
        return null;
    }

    public List<ExamenLaboratorio> listarTodos() {
        List<ExamenLaboratorio> lista = new ArrayList<>();
        String sql = "SELECT id_examen, nombre, descripcion, precio, tiempo_resultado_horas FROM examen_laboratorio";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public boolean insertar(ExamenLaboratorio examen, Connection conn) throws SQLException {
        String sql = "INSERT INTO examen_laboratorio (nombre, descripcion, precio, tiempo_resultado_horas) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, examen.getNombreExamen());
            ps.setString(2, examen.getDescripcion());
            ps.setDouble(3, examen.getPrecio());
            ps.setInt(4, examen.getTiempoResultadoHoras());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) examen.setIdExamen(rs.getInt(1));
            }
            return affected > 0;
        }
    }

    private ExamenLaboratorio mapear(ResultSet rs) throws SQLException {
        ExamenLaboratorio e = new ExamenLaboratorio();
        e.setIdExamen(rs.getInt("id_examen"));
        e.setNombreExamen(rs.getString("nombre"));
        e.setDescripcion(rs.getString("descripcion"));
        e.setPrecio(rs.getDouble("precio"));
        e.setTiempoResultadoHoras(rs.getInt("tiempo_resultado_horas"));
        return e;
    }
}
