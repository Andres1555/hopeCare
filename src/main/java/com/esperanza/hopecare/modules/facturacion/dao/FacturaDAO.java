package com.esperanza.hopecare.modules.facturacion.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FacturaDAO {
    public int insertarFactura(int idPaciente, double subtotal, double impuesto, double total, String estadoPago, Connection conn) throws SQLException {
        String sql = "INSERT INTO factura (id_paciente, fecha_emision, subtotal, impuesto, total, estado_pago) VALUES (?, datetime('now', 'localtime'), ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idPaciente);
            ps.setDouble(2, subtotal);
            ps.setDouble(3, impuesto);
            ps.setDouble(4, total);
            ps.setString(5, estadoPago);
            int affected = ps.executeUpdate();
            if (affected == 0) return -1;
            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        }
    }
}
