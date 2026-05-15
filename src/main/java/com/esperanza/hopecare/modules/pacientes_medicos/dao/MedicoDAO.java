/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.esperanza.hopecare.modules.pacientes_medicos.dao;

/**
 *
 * @author Jenfer
 */

import com.esperanza.hopecare.modules.pacientes_medicos.model.Medico;
import com.esperanza.hopecare.common.db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicoDAO {

    public List<Medico> listarTodos() {
        List<Medico> lista = new ArrayList<>();
        String sql = "SELECT m.id_medico, m.id_persona, m.id_especialidad, m.registro_medico, "
                   + "p.nombre, p.apellido, p.documento_identidad, e.nombre AS nombre_especialidad "
                   + "FROM medico m "
                   + "JOIN persona p ON m.id_persona = p.id_persona "
                   + "JOIN especialidad e ON m.id_especialidad = e.id_especialidad "
                   + "WHERE m.activo = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Medico m = new Medico();
                m.setIdMedico(rs.getInt("id_medico"));
                m.setIdPersona(rs.getInt("id_persona"));
                m.setIdEspecialidad(rs.getInt("id_especialidad"));
                m.setRegistroMedico(rs.getString("registro_medico"));
                m.setNombre(rs.getString("nombre"));
                m.setApellido(rs.getString("apellido"));
                m.setDocumentoIdentidad(rs.getString("documento_identidad"));
                m.setNombreEspecialidad(rs.getString("nombre_especialidad"));
                lista.add(m);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    /**
     * Inserta un médico de forma transaccional:
     * 1. Guarda en tabla persona (documento_identidad)
     * 2. Obtiene id_persona generado (last_insert_rowid)
     * 3. Guarda en tabla medico (id_persona, id_especialidad, registro_medico)
     * Si falla, ejecuta ROLLBACK.
     *
     * @param medico Objeto Medico con datos completos
     * @return true si la inserción fue exitosa, false en caso contrario
     */
    public boolean insertarMedico(Medico medico) {
        String sqlPersona = "INSERT INTO persona (documento_identidad) VALUES (?)";
        String sqlMedico = "INSERT INTO medico (id_persona, id_especialidad, registro_medico) VALUES (?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmtPersona = null;
        PreparedStatement pstmtMedico = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);  // Inicio de transacción
            
            // 1. Insertar en persona
            pstmtPersona = conn.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS);
            pstmtPersona.setString(1, medico.getDocumentoIdentidad());
            int affectedRows = pstmtPersona.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al insertar en persona, ninguna fila afectada.");
            }
            
            // 2. Obtener el id_persona generado
            ResultSet generatedKeys = pstmtPersona.getGeneratedKeys();
            int idPersona;
            if (generatedKeys.next()) {
                idPersona = generatedKeys.getInt(1);
                medico.setIdPersona(idPersona);
            } else {
                throw new SQLException("No se pudo obtener el id_persona generado.");
            }
            
            // 3. Insertar en medico
            pstmtMedico = conn.prepareStatement(sqlMedico);
            pstmtMedico.setInt(1, idPersona);
            pstmtMedico.setInt(2, medico.getIdEspecialidad());
            pstmtMedico.setString(3, medico.getRegistroMedico());
            affectedRows = pstmtMedico.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al insertar en medico, ninguna fila afectada.");
            }
            
            // Éxito: confirmar transacción
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            // Error: deshacer cambios
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            // Cerrar recursos y restaurar auto-commit
            try {
                if (pstmtPersona != null) pstmtPersona.close();
                if (pstmtMedico != null) pstmtMedico.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}