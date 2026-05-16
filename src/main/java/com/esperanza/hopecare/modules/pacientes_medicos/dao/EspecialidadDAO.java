package com.esperanza.hopecare.modules.pacientes_medicos.dao;

import com.esperanza.hopecare.common.db.DatabaseConnection;
import com.esperanza.hopecare.modules.pacientes_medicos.model.Especialidad;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EspecialidadDAO {
    public List<Especialidad> listarTodas() {
        List<Especialidad> lista = new ArrayList<>();
        String sql = "SELECT id_especialidad, nombre_especialidad FROM especialidad ORDER BY nombre_especialidad";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Especialidad e = new Especialidad();
                e.setIdEspecialidad(rs.getInt("id_especialidad"));
                e.setNombre(rs.getString("nombre_especialidad"));
                lista.add(e);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return lista;
    }
}
