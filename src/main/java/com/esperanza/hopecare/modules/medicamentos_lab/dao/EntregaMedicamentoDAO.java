package com.esperanza.hopecare.modules.medicamentos_lab.dao;

import com.esperanza.hopecare.modules.medicamentos_lab.model.EntregaMedicamento;
import com.esperanza.hopecare.common.db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntregaMedicamentoDAO {

    public boolean insertar(EntregaMedicamento entrega, Connection conn) throws SQLException {
        String sql = "INSERT INTO entrega_medicamento (id_detalle_receta, cantidad_entregada, entregado_por, facturado) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entrega.getIdDetalleReceta());
            ps.setInt(2, entrega.getCantidadEntregada());
            ps.setInt(3, entrega.getEntregadoPor());
            ps.setBoolean(4, entrega.isFacturado());
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

    public boolean eliminar(int idEntrega, Connection conn) throws SQLException {
        EntregaMedicamento entrega = obtenerPorId(idEntrega, conn);
        if (entrega == null) return false;

        if (entrega.isFacturado()) {
            throw new SQLException("No se puede eliminar una entrega facturada");
        }

        int idDetalleReceta = entrega.getIdDetalleReceta();
        int cantidadEntregada = entrega.getCantidadEntregada();

        String sqlMedicamento = "SELECT m.id_medicamento, m.stock_actual " +
            "FROM detalle_receta dr " +
            "JOIN medicamento m ON dr.id_medicamento = m.id_medicamento " +
            "WHERE dr.id_detalle = ?";
        int idMedicamento = 0;
        int stockActual = 0;
        try (PreparedStatement ps = conn.prepareStatement(sqlMedicamento)) {
            ps.setInt(1, idDetalleReceta);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idMedicamento = rs.getInt("id_medicamento");
                stockActual = rs.getInt("stock_actual");
            }
        }

        if (idMedicamento > 0) {
            String sqlUpdateStock = "UPDATE medicamento SET stock_actual = ? WHERE id_medicamento = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdateStock)) {
                ps.setInt(1, stockActual + cantidadEntregada);
                ps.setInt(2, idMedicamento);
                ps.executeUpdate();
            }
        }

        String sqlDelete = "DELETE FROM entrega_medicamento WHERE id_entrega = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlDelete)) {
            ps.setInt(1, idEntrega);
            return ps.executeUpdate() == 1;
        }
    }

    private EntregaMedicamento obtenerPorId(int idEntrega, Connection conn) throws SQLException {
        String sql = "SELECT id_entrega, id_detalle_receta, cantidad_entregada, entregado_por, facturado, fecha_entrega FROM entrega_medicamento WHERE id_entrega = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEntrega);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapear(rs);
            }
        }
        return null;
    }

    public List<EntregaMedicamento> listarTodas() {
        List<EntregaMedicamento> lista = new ArrayList<>();
        String sql = "SELECT id_entrega, id_detalle_receta, cantidad_entregada, entregado_por, facturado FROM entrega_medicamento ORDER BY fecha_entrega DESC";
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
        String sql = "SELECT id_entrega, id_detalle_receta, cantidad_entregada, entregado_por, facturado FROM entrega_medicamento WHERE id_detalle_receta = ? ORDER BY fecha_entrega DESC";
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
        e.setIdDetalleReceta(rs.getInt("id_detalle_receta"));
        e.setCantidadEntregada(rs.getInt("cantidad_entregada"));
        e.setEntregadoPor(rs.getInt("entregado_por"));
        e.setFacturado(rs.getBoolean("facturado"));
        Timestamp ts = rs.getTimestamp("fecha_entrega");
        if (ts != null) {
            e.setFechaEntrega(ts.toLocalDateTime());
        }
        return e;
    }
}
