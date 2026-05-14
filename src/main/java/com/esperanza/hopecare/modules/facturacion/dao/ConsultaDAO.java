package com.esperanza.hopecare.modules.facturacion.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class ConsultaDAO {
    public List<Object[]> listarNoFacturadasPorPaciente(int idPaciente, Connection conn) {
        // Stub
        return new ArrayList<>();
    }

    public boolean marcarFacturado(int idReferencia, Connection conn) {
        // Stub
        return true;
    }
}
