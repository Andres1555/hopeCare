package com.esperanza.hopecare.modules.medicamentos_lab.dao;

import com.esperanza.hopecare.modules.medicamentos_lab.model.Medicamento;
import com.esperanza.hopecare.common.db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicamentoDAO {

    public Medicamento obtenerPorId(int id, Connection conn) throws SQLException {
        String sql = "SELECT id_medicamento, nombre_comercial, principio_activo, presentacion, concentracion, precio_unitario, stock_actual, stock_minimo, requiere_receta FROM medicamento WHERE id_medicamento = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapear(rs);
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

    public boolean insertar(Medicamento medicamento, Connection conn) throws SQLException {
        String sql = "INSERT INTO medicamento (nombre_comercial, principio_activo, presentacion, concentracion, precio_unitario, stock_actual, stock_minimo, requiere_receta) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, medicamento.getNombreComercial());
            ps.setString(2, medicamento.getPrincipioActivo());
            ps.setString(3, medicamento.getPresentacion());
            ps.setString(4, medicamento.getConcentracion());
            ps.setDouble(5, medicamento.getPrecioUnitario());
            ps.setInt(6, medicamento.getStockActual());
            ps.setInt(7, medicamento.getStockMinimo());
            ps.setBoolean(8, medicamento.isRequiereReceta());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) medicamento.setIdMedicamento(rs.getInt(1));
            }
            return affected > 0;
        }
    }

    public List<Medicamento> listarTodos() {
        List<Medicamento> lista = new ArrayList<>();
        String sql = "SELECT id_medicamento, nombre_comercial, principio_activo, presentacion, concentracion, precio_unitario, stock_actual, stock_minimo, requiere_receta FROM medicamento";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public List<Medicamento> listarStockBajo() {
        List<Medicamento> lista = new ArrayList<>();
        String sql = "SELECT id_medicamento, nombre_comercial, principio_activo, presentacion, concentracion, precio_unitario, stock_actual, stock_minimo, requiere_receta FROM medicamento WHERE stock_actual <= stock_minimo";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    private Medicamento mapear(ResultSet rs) throws SQLException {
        Medicamento m = new Medicamento();
        m.setIdMedicamento(rs.getInt("id_medicamento"));
        m.setNombreComercial(rs.getString("nombre_comercial"));
        m.setPrincipioActivo(rs.getString("principio_activo"));
        m.setPresentacion(rs.getString("presentacion"));
        m.setConcentracion(rs.getString("concentracion"));
        m.setPrecioUnitario(rs.getDouble("precio_unitario"));
        m.setStockActual(rs.getInt("stock_actual"));
        m.setStockMinimo(rs.getInt("stock_minimo"));
        m.setRequiereReceta(rs.getBoolean("requiere_receta"));
        return m;
    }
}
