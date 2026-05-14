package com.esperanza.hopecare.modules.medicamentos_lab.dao;

import com.esperanza.hopecare.modules.medicamentos_lab.model.Medicamento;
import com.esperanza.hopecare.common.db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicamentoDAO {
    public Medicamento obtenerPorId(int id, Connection conn) throws SQLException {
        String sql = "SELECT id_medicamento, nombre, stock_actual FROM medicamento WHERE id_medicamento = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Medicamento(rs.getInt("id_medicamento"), rs.getString("nombre"), rs.getInt("stock_actual"));
            }
        }
        return null;
    }

    public boolean actualizarStock(int idMedicamento, int nuevoStock, Connection conn) throws SQLException {
        String sql = "UPDATE medicamento SET stock_actual = ? WHERE id_medicamento = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nuevoStock);
            ps.setInt(2, idMedicamento);
            return ps.executeUpdate() == 1;
        }
    }

    public List<Medicamento> listarTodos() {
        List<Medicamento> lista = new ArrayList<>();
        String sql = "SELECT id_medicamento, nombre, stock_actual FROM medicamento";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Medicamento(rs.getInt("id_medicamento"), rs.getString("nombre"), rs.getInt("stock_actual")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}
