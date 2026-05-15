package com.esperanza.hopecare.modules.medicamentos_lab.dao;

import com.esperanza.hopecare.modules.medicamentos_lab.model.EntregaMedicamento;
import com.esperanza.hopecare.common.db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntregaMedicamentoDAO {

    public boolean insertar(EntregaMedicamento entrega, Connection conn) throws SQLException {
        String sql = "INSERT INTO entrega_medicamento (id_receta, id_medicamento, cantidad) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entrega.getIdReceta());
            ps.setInt(2, entrega.getIdMedicamento());
            ps.setInt(3, entrega.getCantidad());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean marcarFacturado(int idEntrega, Connection conn) throws SQLException {
        String sql = "UPDATE entrega_medicamento SET facturado = 1 WHERE id_entrega = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEntrega);
            return ps.executeUpdate() == 1;
        }
    }

    public List<EntregaMedicamento> listarTodas() {
        List<EntregaMedicamento> lista = new ArrayList<>();
        String sql = "SELECT id_entrega, id_receta, id_medicamento, cantidad, fecha_entrega, facturado FROM entrega_medicamento ORDER BY fecha_entrega DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<EntregaMedicamento> listarPorReceta(int idReceta) {
        List<EntregaMedicamento> lista = new ArrayList<>();
        String sql = "SELECT id_entrega, id_receta, id_medicamento, cantidad, fecha_entrega, facturado FROM entrega_medicamento WHERE id_receta = ? ORDER BY fecha_entrega DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idReceta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private EntregaMedicamento mapear(ResultSet rs) throws SQLException {
        EntregaMedicamento e = new EntregaMedicamento();
        e.setIdEntrega(rs.getInt("id_entrega"));
        e.setIdReceta(rs.getInt("id_receta"));
        e.setIdMedicamento(rs.getInt("id_medicamento"));
        e.setCantidad(rs.getInt("cantidad"));
        Timestamp ts = rs.getTimestamp("fecha_entrega");
        if (ts != null) {
            e.setFechaEntrega(ts.toLocalDateTime());
        }
        e.setFacturado(rs.getBoolean("facturado"));
        return e;
    }
}
