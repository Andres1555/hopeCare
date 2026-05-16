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
                insertarRol(conn, "ADMIN", "Administrador del sistema");
                insertarRol(conn, "RECEPCION", "Personal de recepción");
                insertarRol(conn, "FARMACIA", "Personal de farmacia");
                insertarRol(conn, "LABORATORIO", "Personal de laboratorio");

                insertarEspecialidad(conn, "Medicina General", "Especialidad en medicina general");
                insertarEspecialidad(conn, "Pediatría", "Especialidad en pediatría");
                insertarEspecialidad(conn, "Traumatología", "Especialidad en traumatología");
                insertarEspecialidad(conn, "Cardiología", "Especialidad en cardiología");
                insertarEspecialidad(conn, "Dermatología", "Especialidad en dermatología");

                insertarMedicamento(conn, "Paracetamol", "Paracetamol", "Tabletas", "500mg", 100.0, 100, 20, true);
                insertarMedicamento(conn, "Ibuprofeno", "Ibuprofeno", "Tabletas", "400mg", 50.0, 50, 15, true);
                insertarMedicamento(conn, "Amoxicilina", "Amoxicilina", "Cápsulas", "500mg", 30.0, 30, 10, true);
                insertarMedicamento(conn, "Losartán", "Losartán Potásico", "Tabletas", "50mg", 40.0, 40, 5, true);
                insertarMedicamento(conn, "Omeprazol", "Omeprazol", "Cápsulas", "20mg", 60.0, 60, 10, true);

                insertarExamenLab(conn, "Hemograma completo", "Análisis de sangre completo", 25000.0, 4, null);
                insertarExamenLab(conn, "Glucosa", "Medición de glucosa en sangre", 8000.0, 2, null);
                insertarExamenLab(conn, "Colesterol total", "Perfil lipídico", 12000.0, 3, null);
                insertarExamenLab(conn, "Radiografía de tórax", "Rayos X de tórax", 35000.0, 24, null);
                insertarExamenLab(conn, "Electrocardiograma", "ECG en reposo", 20000.0, 6, null);

                // --- USUARIOS DE PRUEBA ---
                int u1 = insertarUsuario(conn, "admin", "admin123", 1);
                int u2 = insertarUsuario(conn, "recepcion", "recepcion123", 2);
                int u3 = insertarUsuario(conn, "farmacia", "farmacia123", 3);
                int u4 = insertarUsuario(conn, "laboratorio", "laboratorio123", 4);

                // --- PACIENTES ---
                int pp1 = insertarPersona(conn, "PACIENTE", "Juan", "Pérez", "12345678", "1980-01-15", "123456789", "juan.perez@email.com", "Calle 123 #45-67", "M", u1);
                insertarPaciente(conn, pp1, "HC001", "Ninguna", "O+", "María Pérez - 987654321");
                int pp2 = insertarPersona(conn, "PACIENTE", "María", "González", "23456789", "1985-05-20", "234567890", "maria.gonzalez@email.com", "Calle 456 #78-90", "F", u2);
                insertarPaciente(conn, pp2, "HC002", "Penicilina", "A+", "Roberto González - 876543210");
                int pp3 = insertarPersona(conn, "PACIENTE", "Carlos", "López", "34567890", "1990-08-10", "345678901", "carlos.lopez@email.com", "Carrera 789 #12-34", "M", u3);
                insertarPaciente(conn, pp3, "HC003", "Ninguna", "B+", "Laura López - 765432109");
                int pp4 = insertarPersona(conn, "PACIENTE", "Laura", "Fernández", "45678901", "1975-12-03", "456789012", "laura.fernandez@email.com", "Avenida 123 #45-67", "F", u4);
                insertarPaciente(conn, pp4, "HC004", "Aspirina", "AB+", "Carlos Fernández - 654321098");
                int pp5 = insertarPersona(conn, "PACIENTE", "Roberto", "Díaz", "56789012", "1988-03-25", "567890123", "roberto.diaz@email.com", "Diagonal 456 #78-90", "M", u1);
                insertarPaciente(conn, pp5, "HC005", "Ninguna", "O-", "Laura Díaz - 543210987");

                // --- MÉDICOS ---
                int pm1 = insertarPersona(conn, "MEDICO", "Ana", "Martínez", "87654321", "1970-07-15", "678901234", "ana.martinez@email.com", "Calle 789 #12-34", "F", u1);
                insertarMedico(conn, pm1, 1, "RM12345");
                int pm2 = insertarPersona(conn, "MEDICO", "Pedro", "Ramírez", "98765432", "1972-11-22", "789012345", "pedro.ramirez@email.com", "Carrera 123 #45-67", "M", u2);
                insertarMedico(conn, pm2, 2, "RM12346");
                int pm3 = insertarPersona(conn, "MEDICO", "Sofía", "Torres", "11111111", "1980-04-30", "890123456", "sofia.torres@email.com", "Avenida 456 #78-90", "F", u3);
                insertarMedico(conn, pm3, 4, "RM12347");

                // --- HORARIOS ---
                // Dr. Ana Martínez (id=1) — Medicina General, L-V mañana
                insertarHorario(conn, 1, 1, "08:00", "12:00", 30, true);
                insertarHorario(conn, 1, 2, "08:00", "12:00", 30, true);
                insertarHorario(conn, 1, 3, "08:00", "12:00", 30, true);
                insertarHorario(conn, 1, 4, "08:00", "12:00", 30, true);
                insertarHorario(conn, 1, 5, "08:00", "12:00", 30, true);
                // Dr. Pedro Ramírez (id=2) — Pediatría, L-J tarde
                insertarHorario(conn, 2, 1, "14:00", "18:00", 30, true);
                insertarHorario(conn, 2, 2, "14:00", "18:00", 30, true);
                insertarHorario(conn, 2, 3, "14:00", "18:00", 30, true);
                insertarHorario(conn, 2, 4, "14:00", "18:00", 30, true);
                // Dra. Sofía Torres (id=3) — Cardiología, L-V mañana
                insertarHorario(conn, 3, 1, "09:00", "13:00", 30, true);
                insertarHorario(conn, 3, 2, "09:00", "13:00", 30, true);
                insertarHorario(conn, 3, 3, "09:00", "13:00", 30, true);
                insertarHorario(conn, 3, 4, "09:00", "13:00", 30, true);
                insertarHorario(conn, 3, 5, "09:00", "13:00", 30, true);

                // --- CITAS PROGRAMADAS ---
                LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
                LocalDateTime dayAfter = LocalDateTime.now().plusDays(2);
                LocalDateTime now = LocalDateTime.now();

                insertarCita(conn, 1, 1, tomorrow.withHour(9).withMinute(0), "PROGRAMADA", "Consulta de rutina", 1, now);
                insertarCita(conn, 2, 1, tomorrow.withHour(9).withMinute(30), "PROGRAMADA", "Revisión general", 1, now);
                insertarCita(conn, 3, 1, tomorrow.withHour(10).withMinute(0), "PROGRAMADA", "Chequeo anual", 1, now);
                insertarCita(conn, 4, 2, tomorrow.withHour(14).withMinute(0), "PROGRAMADA", "Consulta pediátrica", 2, now);
                insertarCita(conn, 5, 2, tomorrow.withHour(14).withMinute(30), "PROGRAMADA", "Seguimiento", 2, now);
                insertarCita(conn, 1, 3, tomorrow.withHour(9).withMinute(0), "PROGRAMADA", "Consulta cardiológica", 3, now);
                insertarCita(conn, 2, 3, tomorrow.withHour(10).withMinute(0), "PROGRAMADA", "Control", 3, now);
                insertarCita(conn, 3, 2, dayAfter.withHour(14).withMinute(0), "PROGRAMADA", "Consulta pediátrica", 2, now);
                insertarCita(conn, 4, 2, dayAfter.withHour(15).withMinute(0), "PROGRAMADA", "Seguimiento", 2, now);
                insertarCita(conn, 5, 3, dayAfter.withHour(9).withMinute(30), "PROGRAMADA", "Consulta cardiológica", 3, now);

                // --- CONSULTAS (con precio para facturación) ---
                insertarConsulta(conn, 1, "Paciente presenta síntomas de gripe", "Fiebre, tos, dolor de garganta", "Reposo y paracetamol", "", now, false, 50000.0);
                insertarConsulta(conn, 3, "Control anual normal", "Ninguno", "Continuar con hábitos saludables", "", now, false, 80000.0);
                insertarConsulta(conn, 5, "Seguimiento pediátrico", "Revisión de crecimiento", "Desarrollo normal", "", now, false, 60000.0);

                // --- RECETA Y ENTREGA DE MEDICAMENTO ---
                int receta1 = insertarReceta(conn, 1, "Tomar cada 8 horas", now);
                int detalleReceta1 = insertarDetalleReceta(conn, receta1, 1, 2, "1 tableta cada 8 horas");
                insertarEntregaMedicamento(conn, detalleReceta1, 2, 3, now, false);

                // --- SOLICITUD DE EXAMEN ---
                insertarSolicitudExamen(conn, 2, 1, "PENDIENTE", null, null, false);

                // --- FACTURA DE PRUEBA (ya emitida) ---
                int facturaTest = insertarFacturaPrueba(conn, 1, 130000.0, 24700.0, 154700.0, "PAGADO");
                insertarDetalleFacturaPrueba(conn, facturaTest, "Consulta médica #1", 1, "CONSULTA", 50000.0);
                insertarDetalleFacturaPrueba(conn, facturaTest, "Examen de laboratorio #1", 1, "EXAMEN", 25000.0);
                insertarDetalleFacturaPrueba(conn, facturaTest, "Medicamento entregado #1", 1, "MEDICAMENTO", 55000.0);
                marcarFacturadoConsulta(conn, 1);
                marcarFacturadoSolicitud(conn, 1);
                marcarFacturadoEntrega(conn, 1);

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

    private static void insertarRol(Connection conn, String nombre, String descripcion) throws SQLException {
        String sql = "INSERT OR IGNORE INTO rol (nombre_rol, descripcion) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.executeUpdate();
        }
    }

    private static void insertarEspecialidad(Connection conn, String nombre, String descripcion) throws SQLException {
        String sql = "INSERT OR IGNORE INTO especialidad (nombre_especialidad, descripcion) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.executeUpdate();
        }
    }

    private static int insertarUsuario(Connection conn, String nombreUsuario, String password, int idRol) throws SQLException {
        String sql = "INSERT OR IGNORE INTO usuario (nombre_usuario, contrasena_hash, id_rol, activo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, password); // En producción se debe hacer hash
            ps.setInt(3, idRol);
            ps.setInt(4, 1);
            ps.executeUpdate();
            var rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            // Si no se generó un nuevo ID, buscar el existente
            String select = "SELECT id_usuario FROM usuario WHERE nombre_usuario = ?";
            try (PreparedStatement ps2 = conn.prepareStatement(select)) {
                ps2.setString(1, nombreUsuario);
                var rs2 = ps2.executeQuery();
                if (rs2.next()) return rs2.getInt(1);
            }
            throw new SQLException("No se pudo obtener id_usuario");
        }
    }

    private static void insertarMedicamento(Connection conn, String nombreComercial, String principioActivo, String presentacion, String concentracion, double precioUnitario, int stockActual, int stockMinimo, boolean requiereReceta) throws SQLException {
        String sql = "INSERT OR IGNORE INTO medicamento (nombre_comercial, principio_activo, presentacion, concentracion, precio_unitario, stock_actual, stock_minimo, requiere_receta) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreComercial);
            ps.setString(2, principioActivo);
            ps.setString(3, presentacion);
            ps.setString(4, concentracion);
            ps.setDouble(5, precioUnitario);
            ps.setInt(6, stockActual);
            ps.setInt(7, stockMinimo);
            ps.setBoolean(8, requiereReceta);
            ps.executeUpdate();
        }
    }

    private static void insertarExamenLab(Connection conn, String nombre, String descripcion, double precio, int tiempoHoras, byte[] resultadoArchivo) throws SQLException {
        String sql = "INSERT OR IGNORE INTO examen_laboratorio (nombre_examen, descripcion, precio, tiempo_resultado_horas, resultado_archivo) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setDouble(3, precio);
            ps.setInt(4, tiempoHoras);
            ps.setBytes(5, resultadoArchivo);
            ps.executeUpdate();
        }
    }

    private static int insertarPersona(Connection conn, String tipo, String nombre, String apellido, String documento, 
                                  String fechaNacimiento, String telefono, String email, String direccion, String genero, int idUsuario) throws SQLException {
        String sql = "INSERT INTO persona (tipo_persona, nombre, apellido, documento_identidad, fecha_nacimiento, telefono, email, direccion, genero, id_usuario) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tipo);
            ps.setString(2, nombre);
            ps.setString(3, apellido);
            ps.setString(4, documento);
            ps.setString(5, fechaNacimiento);
            ps.setString(6, telefono);
            ps.setString(7, email);
            ps.setString(8, direccion);
            ps.setString(9, genero);
            ps.setInt(10, idUsuario);
            ps.executeUpdate();
            var rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            throw new SQLException("No se pudo obtener id_persona");
        }
    }

    private static void insertarPaciente(Connection conn, int idPersona, String historiaClinica, String alergias, String grupoSanguineo, String contactoEmergencia) throws SQLException {
        String sql = "INSERT INTO paciente (id_persona, historia_clinica, alergias, grupo_sanguineo, contacto_emergencia) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPersona);
            ps.setString(2, historiaClinica);
            ps.setString(3, alergias);
            ps.setString(4, grupoSanguineo);
            ps.setString(5, contactoEmergencia);
            ps.executeUpdate();
        }
    }

    private static void insertarMedico(Connection conn, int idPersona, int idEspecialidad, String registroMedico) throws SQLException {
        String sql = "INSERT INTO medico (id_persona, id_especialidad, registro_medico, activo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPersona);
            ps.setInt(2, idEspecialidad);
            ps.setString(3, registroMedico);
            ps.setInt(4, 1); // activo
            ps.executeUpdate();
        }
    }

    private static void insertarHorario(Connection conn, int idMedico, int diaSemana, String horaInicio, String horaFin, int intervalo, boolean activo) throws SQLException {
        String sql = "INSERT INTO horario_atencion (id_medico, dia_semana, hora_inicio, hora_fin, intervalo_minutos, activo) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            ps.setInt(2, diaSemana);
            ps.setString(3, horaInicio);
            ps.setString(4, horaFin);
            ps.setInt(5, intervalo);
            ps.setBoolean(6, activo);
            ps.executeUpdate();
        }
    }

    private static void insertarCita(Connection conn, int idPaciente, int idMedico, LocalDateTime fechaHora, String estado, String motivo, int creadaPor, LocalDateTime fechaCreacion) throws SQLException {
        String sql = "INSERT INTO cita (id_paciente, id_medico, fecha_hora, estado, motivo, creada_por, fecha_creacion) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            ps.setInt(2, idMedico);
            ps.setString(3, fechaHora.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            ps.setString(4, estado);
            ps.setString(5, motivo);
            ps.setInt(6, creadaPor);
            ps.setString(7, fechaCreacion.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            ps.executeUpdate();
        }
    }

    private static void insertarConsulta(Connection conn, int idCita, String diagnostico, String sintomas, String tratamiento, String notasMedicas, LocalDateTime fechaConsulta, boolean facturado, double precio) throws SQLException {
        String sql = "INSERT INTO consulta (id_cita, diagnostico, sintomas, tratamiento, notas_medicas, fecha_consulta, facturado, precio) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCita);
            ps.setString(2, diagnostico);
            ps.setString(3, sintomas);
            ps.setString(4, tratamiento);
            ps.setString(5, notasMedicas);
            ps.setString(6, fechaConsulta.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            ps.setBoolean(7, facturado);
            ps.setDouble(8, precio);
            ps.executeUpdate();
        }
    }

    private static int insertarReceta(Connection conn, int idConsulta, String instrucciones, LocalDateTime fechaEmision) throws SQLException {
        String sql = "INSERT INTO receta (id_consulta, fecha_emision, instrucciones, activa) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idConsulta);
            ps.setString(2, fechaEmision.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            ps.setString(3, instrucciones);
            ps.setInt(4, 1);
            ps.executeUpdate();
            var rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            throw new SQLException("No se pudo obtener id_receta");
        }
    }

    private static int insertarDetalleReceta(Connection conn, int idReceta, int idMedicamento, int cantidad, String dosisIndicacion) throws SQLException {
        String sql = "INSERT INTO detalle_receta (id_receta, id_medicamento, cantidad, dosis_indicacion) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idReceta);
            ps.setInt(2, idMedicamento);
            ps.setInt(3, cantidad);
            ps.setString(4, dosisIndicacion);
            ps.executeUpdate();
            var rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            throw new SQLException("No se pudo obtener id_detalle");
        }
    }

    private static void insertarEntregaMedicamento(Connection conn, int idDetalleReceta, int cantidadEntregada, int entregadoPor, LocalDateTime fechaEntrega, boolean facturado) throws SQLException {
        String sql = "INSERT INTO entrega_medicamento (id_detalle_receta, cantidad_entregada, fecha_entrega, entregado_por, facturado) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDetalleReceta);
            ps.setInt(2, cantidadEntregada);
            ps.setString(3, fechaEntrega.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            ps.setInt(4, entregadoPor);
            ps.setBoolean(5, facturado);
            ps.executeUpdate();
        }
    }

    private static void insertarSolicitudExamen(Connection conn, int idConsulta, int idExamen, String estado, String resultadoTexto, byte[] resultadoArchivo, boolean facturado) throws SQLException {
        String sql = "INSERT INTO solicitud_examen (id_consulta, id_examen, estado, resultado_texto, resultado_archivo, facturado) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idConsulta);
            ps.setInt(2, idExamen);
            ps.setString(3, estado);
            ps.setString(4, resultadoTexto);
            ps.setBytes(5, resultadoArchivo);
            ps.setBoolean(6, facturado);
            ps.executeUpdate();
        }
    }

    private static int insertarFacturaPrueba(Connection conn, int idPaciente, double subtotal, double impuesto, double total, String estadoPago) throws SQLException {
        String sql = "INSERT INTO factura (id_paciente, fecha_emision, subtotal, impuesto, total, estado_pago) VALUES (?, datetime('now', 'localtime'), ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idPaciente);
            ps.setDouble(2, subtotal);
            ps.setDouble(3, impuesto);
            ps.setDouble(4, total);
            ps.setString(5, estadoPago);
            ps.executeUpdate();
            var rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            throw new SQLException("No se pudo obtener id_factura");
        }
    }

    private static void insertarDetalleFacturaPrueba(Connection conn, int idFactura, String concepto, int idReferencia, String tipoReferencia, double monto) throws SQLException {
        String sql = "INSERT INTO detalle_factura (id_factura, concepto, id_referencia, tipo_referencia, monto) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idFactura);
            ps.setString(2, concepto);
            ps.setInt(3, idReferencia);
            ps.setString(4, tipoReferencia);
            ps.setDouble(5, monto);
            ps.executeUpdate();
        }
    }

    private static void marcarFacturadoConsulta(Connection conn, int idConsulta) throws SQLException {
        String sql = "UPDATE consulta SET facturado = 1 WHERE id_consulta = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idConsulta);
            ps.executeUpdate();
        }
    }

    private static void marcarFacturadoSolicitud(Connection conn, int idSolicitud) throws SQLException {
        String sql = "UPDATE solicitud_examen SET facturado = 1 WHERE id_solicitud = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSolicitud);
            ps.executeUpdate();
        }
    }

    private static void marcarFacturadoEntrega(Connection conn, int idEntrega) throws SQLException {
        String sql = "UPDATE entrega_medicamento SET facturado = 1 WHERE id_entrega = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEntrega);
            ps.executeUpdate();
        }
    }
}
