package com.esperanza.hopecare.common.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CargarDatosPrueba {

    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                insertarRol(conn, "ADMIN");
                insertarRol(conn, "RECEPCION");
                insertarRol(conn, "FARMACIA");
                insertarRol(conn, "LABORATORIO");

                insertarEspecialidad(conn, "Medicina General");
                insertarEspecialidad(conn, "Pediatría");
                insertarEspecialidad(conn, "Traumatología");
                insertarEspecialidad(conn, "Cardiología");
                insertarEspecialidad(conn, "Dermatología");

                insertarMedicamento(conn, "Paracetamol 500mg", 100, 20);
                insertarMedicamento(conn, "Ibuprofeno 400mg", 50, 15);
                insertarMedicamento(conn, "Amoxicilina 500mg", 30, 10);
                insertarMedicamento(conn, "Losartán 50mg", 40, 5);
                insertarMedicamento(conn, "Omeprazol 20mg", 60, 10);

                insertarExamenLab(conn, "Hemograma completo", "Análisis de sangre completo", 25000.0, 4);
                insertarExamenLab(conn, "Glucosa", "Medición de glucosa en sangre", 8000.0, 2);
                insertarExamenLab(conn, "Colesterol total", "Perfil lipídico", 12000.0, 3);
                insertarExamenLab(conn, "Radiografía de tórax", "Rayos X de tórax", 35000.0, 24);
                insertarExamenLab(conn, "Electrocardiograma", "ECG en reposo", 20000.0, 6);

                // --- PACIENTES ---
                int pp1 = insertarPersona(conn, "PACIENTE", "Juan", "Pérez", "12345678");
                insertarPaciente(conn, pp1, "HC001");
                int pp2 = insertarPersona(conn, "PACIENTE", "María", "González", "23456789");
                insertarPaciente(conn, pp2, "HC002");
                int pp3 = insertarPersona(conn, "PACIENTE", "Carlos", "López", "34567890");
                insertarPaciente(conn, pp3, "HC003");
                int pp4 = insertarPersona(conn, "PACIENTE", "Laura", "Fernández", "45678901");
                insertarPaciente(conn, pp4, "HC004");
                int pp5 = insertarPersona(conn, "PACIENTE", "Roberto", "Díaz", "56789012");
                insertarPaciente(conn, pp5, "HC005");

                // --- MÉDICOS ---
                int pm1 = insertarPersona(conn, "MEDICO", "Ana", "Martínez", "87654321");
                insertarMedico(conn, pm1, 1, "RM12345");
                int pm2 = insertarPersona(conn, "MEDICO", "Pedro", "Ramírez", "98765432");
                insertarMedico(conn, pm2, 2, "RM12346");
                int pm3 = insertarPersona(conn, "MEDICO", "Sofía", "Torres", "11111111");
                insertarMedico(conn, pm3, 4, "RM12347");

                // --- HORARIOS ---
                // Dr. Ana Martínez (id=1) — Medicina General, L-V mañana
                insertarHorario(conn, 1, 1, "08:00", "12:00", 30);
                insertarHorario(conn, 1, 2, "08:00", "12:00", 30);
                insertarHorario(conn, 1, 3, "08:00", "12:00", 30);
                insertarHorario(conn, 1, 4, "08:00", "12:00", 30);
                insertarHorario(conn, 1, 5, "08:00", "12:00", 30);
                // Dr. Pedro Ramírez (id=2) — Pediatría, L-J tarde
                insertarHorario(conn, 2, 1, "14:00", "18:00", 30);
                insertarHorario(conn, 2, 2, "14:00", "18:00", 30);
                insertarHorario(conn, 2, 3, "14:00", "18:00", 30);
                insertarHorario(conn, 2, 4, "14:00", "18:00", 30);
                // Dra. Sofía Torres (id=3) — Cardiología, L-V mañana
                insertarHorario(conn, 3, 1, "09:00", "13:00", 30);
                insertarHorario(conn, 3, 2, "09:00", "13:00", 30);
                insertarHorario(conn, 3, 3, "09:00", "13:00", 30);
                insertarHorario(conn, 3, 4, "09:00", "13:00", 30);
                insertarHorario(conn, 3, 5, "09:00", "13:00", 30);

                // --- CITAS PROGRAMADAS ---
                LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
                LocalDateTime dayAfter = LocalDateTime.now().plusDays(2);

                insertarCita(conn, 1, 1, tomorrow.withHour(9).withMinute(0), "PROGRAMADA");
                insertarCita(conn, 2, 1, tomorrow.withHour(9).withMinute(30), "PROGRAMADA");
                insertarCita(conn, 3, 1, tomorrow.withHour(10).withMinute(0), "PROGRAMADA");
                insertarCita(conn, 4, 2, tomorrow.withHour(14).withMinute(0), "PROGRAMADA");
                insertarCita(conn, 5, 2, tomorrow.withHour(14).withMinute(30), "PROGRAMADA");
                insertarCita(conn, 1, 3, tomorrow.withHour(9).withMinute(0), "PROGRAMADA");
                insertarCita(conn, 2, 3, tomorrow.withHour(10).withMinute(0), "PROGRAMADA");
                insertarCita(conn, 3, 2, dayAfter.withHour(14).withMinute(0), "PROGRAMADA");
                insertarCita(conn, 4, 2, dayAfter.withHour(15).withMinute(0), "PROGRAMADA");
                insertarCita(conn, 5, 3, dayAfter.withHour(9).withMinute(30), "PROGRAMADA");

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

    private static void insertarExamenLab(Connection conn, String nombre, String descripcion, double precio, int tiempoHoras) throws SQLException {
        String sql = "INSERT OR IGNORE INTO examen_laboratorio (nombre, descripcion, precio, tiempo_resultado_horas) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setDouble(3, precio);
            ps.setInt(4, tiempoHoras);
            ps.executeUpdate();
        }
    }

    private static int insertarPersona(Connection conn, String tipo, String nombre, String apellido, String documento) throws SQLException {
        String sql = "INSERT INTO persona (tipo_persona, nombre, apellido, documento_identidad) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tipo);
            ps.setString(2, nombre);
            ps.setString(3, apellido);
            ps.setString(4, documento);
            ps.executeUpdate();
            var rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            throw new SQLException("No se pudo obtener id_persona");
        }
    }

    private static void insertarPaciente(Connection conn, int idPersona, String historiaClinica) throws SQLException {
        String sql = "INSERT INTO paciente (id_persona, historia_clinica) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPersona);
            ps.setString(2, historiaClinica);
            ps.executeUpdate();
        }
    }

    private static void insertarMedico(Connection conn, int idPersona, int idEspecialidad, String registroMedico) throws SQLException {
        String sql = "INSERT INTO medico (id_persona, id_especialidad, registro_medico) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPersona);
            ps.setInt(2, idEspecialidad);
            ps.setString(3, registroMedico);
            ps.executeUpdate();
        }
    }

    private static void insertarHorario(Connection conn, int idMedico, int diaSemana, String horaInicio, String horaFin, int intervalo) throws SQLException {
        String sql = "INSERT INTO horario_atencion (id_medico, dia_semana, hora_inicio, hora_fin, intervalo_minutos) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            ps.setInt(2, diaSemana);
            ps.setString(3, horaInicio);
            ps.setString(4, horaFin);
            ps.setInt(5, intervalo);
            ps.executeUpdate();
        }
    }

    private static void insertarCita(Connection conn, int idPaciente, int idMedico, LocalDateTime fechaHora, String estado) throws SQLException {
        String sql = "INSERT INTO cita (id_paciente, id_medico, fecha_hora, estado) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            ps.setInt(2, idMedico);
            ps.setString(3, fechaHora.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            ps.setString(4, estado);
            ps.executeUpdate();
        }
    }
}
