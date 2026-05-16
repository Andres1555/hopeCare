package com.esperanza.hopecare.modules.dashboard.dao;

import com.esperanza.hopecare.common.db.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {

    public int obtenerCitasDelDia() {
        String sql = "SELECT COUNT(*) FROM cita WHERE DATE(fecha_hora) = DATE('now')";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public double obtenerIngresosDelMes() {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM factura " +
                     "WHERE strftime('%Y-%m', fecha_emision) = strftime('%Y-%m', 'now') " +
                     "AND estado_pago = 'PAGADO'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    public List<String> obtenerMedicamentosStockBajo() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT nombre_comercial FROM medicamento WHERE stock_actual < stock_minimo";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(rs.getString("nombre_comercial"));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}
