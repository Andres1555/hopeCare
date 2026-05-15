package com.esperanza.hopecare.modules.Auth.services;

import com.esperanza.hopecare.common.db.DatabaseConnection;
import com.esperanza.hopecare.modules.Auth.DAO.AuthDAO;
import com.esperanza.hopecare.modules.Auth.DTO.LoginDTO;
import com.esperanza.hopecare.modules.Auth.DTO.RegistroDTO;
import com.esperanza.hopecare.modules.Auth.model.PersonaModel;

import java.sql.Connection;
import java.sql.SQLException;

public class AuthService {

    private final AuthDAO authDAO;

    public AuthService() {
        this.authDAO = new AuthDAO();
    }

    public LoginDTO login(String usuario, String contrasena) {
        LoginDTO dto = new LoginDTO(usuario, contrasena);

        if (usuario == null || usuario.trim().isEmpty() || contrasena == null || contrasena.isEmpty()) {
            dto.setExitoso(false);
            dto.setMensaje("Usuario y contraseña son requeridos.");
            return dto;
        }

        var model = authDAO.autenticar(usuario, contrasena);

        if (model != null) {
            dto.setExitoso(true);
            dto.setNombreRol(model.getNombreRol());
            dto.setMensaje("Inicio de sesión exitoso.");
        } else {
            dto.setExitoso(false);
            dto.setMensaje("Usuario o contraseña incorrectos.");
        }

        return dto;
    }

    public boolean usuarioExiste(String usuario) {
        return authDAO.usuarioExiste(usuario);
    }

    public boolean emailExiste(String email) {
        return email != null && !email.isEmpty() && authDAO.emailExiste(email);
    }

    public boolean documentoExiste(String documento) {
        return documento != null && !documento.isEmpty() && authDAO.documentoExiste(documento);
    }

    public boolean registroMedicoExiste(String registroMedico) {
        return registroMedico != null && !registroMedico.isEmpty() && authDAO.registroMedicoExiste(registroMedico);
    }

    public String registrar(RegistroDTO dto) {
        if (authDAO.usuarioExiste(dto.getNombreUsuario())) {
            return "El nombre de usuario ya está en uso.";
        }
        if (dto.getDocumento() != null && !dto.getDocumento().isEmpty() && authDAO.documentoExiste(dto.getDocumento())) {
            return "Esta cédula ya está registrada.";
        }
        if (dto.getEmail() != null && !dto.getEmail().isEmpty() && authDAO.emailExiste(dto.getEmail())) {
            return "Este correo electrónico ya está registrado.";
        }
        if ("MEDICO".equals(dto.getRol()) && authDAO.registroMedicoExiste(dto.getRegistroMedico())) {
            return "Este registro médico ya está registrado.";
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            int idUsuario = authDAO.insertarUsuario(conn, dto.getNombreUsuario(),
                    dto.getContrasena(), dto.getRol());

            PersonaModel persona = new PersonaModel();
            persona.setNombre(dto.getNombre());
            persona.setApellido(dto.getApellido());
            persona.setDocumentoIdentidad(dto.getDocumento());
            persona.setFechaNacimiento(dto.getFechaNacimiento() != null ? dto.getFechaNacimiento().toString() : null);
            persona.setTelefono(dto.getTelefono());
            persona.setGenero(dto.getGenero());
            persona.setEmail(dto.getEmail());
            persona.setDireccion(dto.getDireccion());
            persona.setIdUsuario(idUsuario);

            int idPersona = authDAO.insertarPersona(conn, persona);

            if ("MEDICO".equals(dto.getRol())) {
                int idEspecialidad = authDAO.obtenerIdEspecialidad(conn, dto.getEspecialidad());
                authDAO.insertarMedico(conn, idPersona, idEspecialidad, dto.getRegistroMedico());
            } else if ("PACIENTE".equals(dto.getRol())) {
                String historiaClinica = "HC-" + System.currentTimeMillis();
                authDAO.insertarPaciente(conn, idPersona, historiaClinica);
            }

            conn.commit();
            return null;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return "Error al registrar: " + e.getMessage();
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}
