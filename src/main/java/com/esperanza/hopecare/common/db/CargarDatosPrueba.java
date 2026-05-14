package com.esperanza.hopecare.common.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CargarDatosPrueba {

    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Insertar roles
                insertarRol(conn, "ADMIN");
                insertarRol(conn, "RECEPCION");
                insertarRol(conn, "FARMACIA");
                insertarRol(conn, "LABORATORIO");

                // Insertar especialidades
                insertarEspecialidad(conn, "Medicina General");
                insertarEspecialidad(conn, "Pediatría");
                insertarEspecialidad(conn, "Traumatología");

                // Insertar medicamentos
                insertarMedicamento(conn, "Paracetamol 500mg", 100, 20);
                insertarMedicamento(conn, "Ibuprofeno 400mg", 50, 15);
                insertarMedicamento(conn, "Amoxicilina 500mg", 30, 10);

                // Insertar exámenes de laboratorio
                insertarExamenLab(conn, "Hemograma completo", 25000.0);
                insertarExamenLab(conn, "Glucosa", 8000.0);
                insertarExamenLab(conn, "Colesterol total", 12000.0);

                // Insertar pacientes (requiere persona primero)
                int idPersonaPaciente = insertarPersona(conn, "12345678");
                insertarPaciente(conn, idPersonaPaciente, "HC001");

                // Insertar médicos (con especialidad, persona)
                int idPersonaMedico = insertarPersona(conn, "87654321");
                insertarMedico(conn, idPersonaMedico, 1, "RM12345"); // especialidad 1 = Medicina General

                // Insertar horario de atención para el médico
                insertarHorario(conn, 1, 1, "08:00", "12:00", 30); // lunes
                insertarHorario(conn, 1, 2, "08:00", "12:00", 30); // martes

                conn.commit();
                System.out.println("Datos de prueba insertados correctamente.");

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertarRol(Connection conn, String nombre) throws SQLException {
        String sql = "INSERT OR IGNORE INTO rol (nombre) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.executeUpdate();
        }
    }

    private static void insertarEspecialidad(Connection conn, String nombre) throws SQLException {
        String sql = "INSERT OR IGNORE INTO especialidad (nombre) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.executeUpdate();
        }
    }

    private static void insertarMedicamento(Connection conn, String nombre, int stockActual, int stockMinimo) throws SQLException {
        String sql = "INSERT OR IGNORE INTO medicamento (nombre, stock_actual, stock_minimo) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, stockActual);
            ps.setInt(3, stockMinimo);
            ps.executeUpdate();
        }
    }

    private static void insertarExamenLab(Connection conn, String nombre, double precio) throws SQLException {
        String sql = "INSERT OR IGNORE INTO examen_laboratorio (nombre, precio) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setDouble(2, precio);
            ps.executeUpdate();
        }
    }

    private static int insertarPersona(Connection conn, String documento) throws SQLException {
        String sql = "INSERT OR IGNORE INTO persona (documento_identidad) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, documento);
            ps.executeUpdate();
            var rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            // Si ya existía, obtener el id
            String select = "SELECT id_persona FROM persona WHERE documento_identidad = ?";
            try (PreparedStatement ps2 = conn.prepareStatement(select)) {
                ps2.setString(1, documento);
                var rs2 = ps2.executeQuery();
                if (rs2.next()) return rs2.getInt(1);
            }
            throw new SQLException("No se pudo obtener id_persona");
        }
    }

    private static void insertarPaciente(Connection conn, int idPersona, String historiaClinica) throws SQLException {
        String sql = "INSERT OR IGNORE INTO paciente (id_persona, historia_clinica) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPersona);
            ps.setString(2, historiaClinica);
            ps.executeUpdate();
        }
    }

    private static void insertarMedico(Connection conn, int idPersona, int idEspecialidad, String registroMedico) throws SQLException {
        String sql = "INSERT OR IGNORE INTO medico (id_persona, id_especialidad, registro_medico) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPersona);
            ps.setInt(2, idEspecialidad);
            ps.setString(3, registroMedico);
            ps.executeUpdate();
        }
    }

    private static void insertarHorario(Connection conn, int idMedico, int diaSemana, String horaInicio, String horaFin, int intervalo) throws SQLException {
        String sql = "INSERT OR IGNORE INTO horario_atencion (id_medico, dia_semana, hora_inicio, hora_fin, intervalo_minutos) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            ps.setInt(2, diaSemana);
            ps.setString(3, horaInicio);
            ps.setString(4, horaFin);
            ps.setInt(5, intervalo);
            ps.executeUpdate();
        }
    }
}
