package com.esperanza.hopecare.modules.pacientes_medicos.dao;

import com.esperanza.hopecare.modules.pacientes_medicos.model.Paciente;
import com.esperanza.hopecare.common.db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO {

    public List<Paciente> listarTodos() {
        List<Paciente> lista = new ArrayList<>();
        String sql = "SELECT p.id_paciente, p.id_persona, p.historia_clinica, "
                   + "per.nombre, per.apellido, per.documento_identidad "
                   + "FROM paciente p "
                   + "JOIN persona per ON p.id_persona = per.id_persona";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Paciente p = new Paciente();
                p.setIdPaciente(rs.getInt("id_paciente"));
                p.setIdPersona(rs.getInt("id_persona"));
                p.setHistoriaClinica(rs.getString("historia_clinica"));
                p.setNombre(rs.getString("nombre"));
                p.setApellido(rs.getString("apellido"));
                p.setDocumentoIdentidad(rs.getString("documento_identidad"));
                lista.add(p);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}
