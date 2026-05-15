package com.esperanza.hopecare.modules.medicamentos_lab.dao;

import com.esperanza.hopecare.modules.medicamentos_lab.model.DetalleReceta;
import com.esperanza.hopecare.common.db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetalleRecetaDAO {

    public List<DetalleReceta> listarPorReceta(int idReceta) {
        List<DetalleReceta> lista = new ArrayList<>();
        String sql = "SELECT id_detalle, id_receta, id_medicamento, cantidad, dosis_indicacion FROM detalle_receta WHERE id_receta = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idReceta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public boolean insertar(DetalleReceta detalle, Connection conn) throws SQLException {
        String sql = "INSERT INTO detalle_receta (id_receta, id_medicamento, cantidad, dosis_indicacion) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, detalle.getIdReceta());
            ps.setInt(2, detalle.getIdMedicamento());
            ps.setInt(3, detalle.getCantidad());
            ps.setString(4, detalle.getDosisIndicacion());
            return ps.executeUpdate() == 1;
        }
    }

    private DetalleReceta mapear(ResultSet rs) throws SQLException {
        DetalleReceta d = new DetalleReceta();
        d.setIdDetalle(rs.getInt("id_detalle"));
        d.setIdReceta(rs.getInt("id_receta"));
        d.setIdMedicamento(rs.getInt("id_medicamento"));
        d.setCantidad(rs.getInt("cantidad"));
        d.setDosisIndicacion(rs.getString("dosis_indicacion"));
        return d;
    }
}
