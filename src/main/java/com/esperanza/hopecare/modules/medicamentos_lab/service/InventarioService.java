package com.esperanza.hopecare.modules.medicamentos_lab.service;

import com.esperanza.hopecare.modules.medicamentos_lab.dao.MedicamentoDAO;
import com.esperanza.hopecare.modules.medicamentos_lab.model.Medicamento;
import com.esperanza.hopecare.common.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class InventarioService {

    private MedicamentoDAO medicamentoDAO;

    public InventarioService() {
        this.medicamentoDAO = new MedicamentoDAO();
    }

    public List<Medicamento> listarMedicamentos() {
        return medicamentoDAO.listarTodos();
    }

    public List<Medicamento> listarStockBajo() {
        return medicamentoDAO.listarStockBajo();
    }

    public boolean verificarStock(int idMedicamento, int cantidad) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            Medicamento med = medicamentoDAO.obtenerPorId(idMedicamento, conn);
            return med != null && med.getStockActual() >= cantidad;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean agregarMedicamento(Medicamento medicamento) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return medicamentoDAO.insertar(medicamento, conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarStock(int idMedicamento, int nuevoStock) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return medicamentoDAO.actualizarStock(idMedicamento, nuevoStock, conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
