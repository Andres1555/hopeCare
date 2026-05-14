package com.esperanza.hopecare.modules.medicamentos_lab.dao;

import com.esperanza.hopecare.modules.medicamentos_lab.model.EntregaMedicamento;
import java.sql.*;

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
}
