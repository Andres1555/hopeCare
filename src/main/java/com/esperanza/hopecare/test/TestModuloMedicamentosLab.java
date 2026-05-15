package com.esperanza.hopecare.test;

import com.esperanza.hopecare.common.db.DatabaseConnection;
import com.esperanza.hopecare.modules.medicamentos_lab.dao.*;
import com.esperanza.hopecare.modules.medicamentos_lab.model.*;
import com.esperanza.hopecare.modules.medicamentos_lab.service.*;
import com.esperanza.hopecare.modules.medicamentos_lab.facade.GestionClinicaFacade;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

public class TestModuloMedicamentosLab {

    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("=== TEST MÓDULO MEDICAMENTOS Y LABORATORIO ===\n");

            // 1. Test listarMedicamentos
            testListarMedicamentos();

            // 2. Test listarExamenes
            testListarExamenes();

            // 3. Test listarStockBajo
            testStockBajo();

            // 4. Test insertarReceta + Detalle
            testInsertarReceta(conn);

            // 5. Test solicitarExamen
            testSolicitarExamen(conn);

            // 6. Test listarRecetasActivas
            testListarRecetasActivas();

            // 7. Test listarSolicitudesPendientes
            testListarSolicitudesPendientes();

            // 8. Test procesarEntrega
            testProcesarEntrega();

            // 9. Test registrarResultado
            testRegistrarResultado();

            // 10. Test verificarStock
            testVerificarStock();

            System.out.println("\n=== TODAS LAS PRUEBAS COMPLETADAS ===");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void testListarMedicamentos() {
        System.out.println("\n[1] Listar medicamentos:");
        InventarioService svc = new InventarioService();
        var lista = svc.listarMedicamentos();
        if (lista.isEmpty()) {
            System.out.println("  FALLO: No hay medicamentos");
        } else {
            for (Medicamento m : lista) {
                System.out.println("  OK: " + m.getNombre() + " (stock: " + m.getStockActual() + ", min: " + m.getStockMinimo() + ")");
            }
        }
    }

    static void testListarExamenes() {
        System.out.println("\n[2] Listar exámenes:");
        ExamenService svc = new ExamenService();
        var lista = svc.listarExamenes();
        if (lista.isEmpty()) {
            System.out.println("  FALLO: No hay exámenes");
        } else {
            for (ExamenLaboratorio e : lista) {
                System.out.println("  OK: " + e.getNombreExamen() + " ($" + e.getPrecio() + ")");
            }
        }
    }

    static void testStockBajo() {
        System.out.println("\n[3] Stock bajo:");
        InventarioService svc = new InventarioService();
        var lista = svc.listarStockBajo();
        for (Medicamento m : lista) {
            System.out.println("  OK (stock bajo): " + m.getNombre() + " (" + m.getStockActual() + "/" + m.getStockMinimo() + ")");
        }
        if (lista.isEmpty()) {
            System.out.println("  INFO: Ningún medicamento con stock bajo");
        }
    }

    static void testInsertarReceta(Connection conn) throws SQLException {
        System.out.println("\n[4] Insertar receta + detalle:");
        Receta receta = new Receta(1, "Tomar después de cada comida");
        RecetaDAO rdao = new RecetaDAO();
        int idReceta = rdao.insertar(receta, conn);
        if (idReceta > 0) {
            System.out.println("  OK: Receta #" + idReceta + " creada (consulta " + receta.getIdConsulta() + ")");

            DetalleReceta detalle = new DetalleReceta(idReceta, 1, 10, "1 tableta cada 8h");
            DetalleRecetaDAO ddao = new DetalleRecetaDAO();
            if (ddao.insertar(detalle, conn)) {
                System.out.println("  OK: Detalle receta insertado (med #1, cant 10)");
            } else {
                System.out.println("  FALLO: No se pudo insertar detalle");
            }
        } else {
            System.out.println("  FALLO: No se pudo crear receta");
        }
    }

    static void testSolicitarExamen(Connection conn) throws SQLException {
        System.out.println("\n[5] Solicitar examen:");
        ExamenService svc = new ExamenService();
        boolean ok = svc.solicitarExamen(1, 1);
        if (ok) {
            System.out.println("  OK: Solicitud de Hemograma creada para consulta 1");
        } else {
            System.out.println("  FALLO: No se pudo solicitar examen");
        }
    }

    static void testListarRecetasActivas() {
        System.out.println("\n[6] Listar recetas activas:");
        RecetaDAO dao = new RecetaDAO();
        var lista = dao.listarActivas();
        if (lista.isEmpty()) {
            System.out.println("  FALLO: No hay recetas activas");
        } else {
            for (Receta r : lista) {
                System.out.println("  OK: Receta #" + r.getIdReceta() + " (consulta " + r.getIdConsulta() + ")");
            }
        }
    }

    static void testListarSolicitudesPendientes() {
        System.out.println("\n[7] Listar solicitudes pendientes:");
        SolicitudExamenDAO dao = new SolicitudExamenDAO();
        var lista = dao.listarPendientes();
        if (lista.isEmpty()) {
            System.out.println("  FALLO: No hay solicitudes pendientes");
        } else {
            for (SolicitudExamen s : lista) {
                System.out.println("  OK: Solicitud #" + s.getIdSolicitud() + " (examen #" + s.getIdExamen() + ", estado: " + s.getEstado() + ")");
            }
        }
    }

    static void testProcesarEntrega() {
        System.out.println("\n[8] Procesar entrega (facade):");
        GestionClinicaFacade facade = new GestionClinicaFacade();
        RecetaDAO rdao = new RecetaDAO();
        var recetas = rdao.listarActivas();
        if (recetas.isEmpty()) {
            System.out.println("  SKIP: No hay recetas para probar entrega");
            return;
        }
        int idReceta = recetas.get(0).getIdReceta();
        boolean ok = facade.procesarEntregaMedicamento(idReceta, 1, 2, "FARMACIA");
        if (ok) {
            System.out.println("  OK: Entrega registrada para Receta #" + idReceta + " (med #1, cant 2)");
        } else {
            System.out.println("  FALLO: No se pudo procesar entrega (¿stock insuficiente?)");
        }
    }

    static void testRegistrarResultado() {
        System.out.println("\n[9] Registrar resultado examen (facade):");
        GestionClinicaFacade facade = new GestionClinicaFacade();
        SolicitudExamenDAO sdao = new SolicitudExamenDAO();
        var pendientes = sdao.listarPendientes();
        if (pendientes.isEmpty()) {
            System.out.println("  SKIP: No hay solicitudes pendientes");
            return;
        }
        int idSolicitud = pendientes.get(0).getIdSolicitud();
        boolean ok = facade.registrarResultadoExamen(idSolicitud, "Resultado normal. Glucosa: 85 mg/dL", "COMPLETADO", "LABORATORIO");
        if (ok) {
            System.out.println("  OK: Resultado registrado para Solicitud #" + idSolicitud);
        } else {
            System.out.println("  FALLO: No se pudo registrar resultado");
        }
    }

    static void testVerificarStock() {
        System.out.println("\n[10] Verificar stock:");
        InventarioService svc = new InventarioService();
        boolean hayStock = svc.verificarStock(1, 5);
        System.out.println("  OK: ¿Hay 5 unidades del medicamento #1? " + hayStock);
        boolean sinStock = svc.verificarStock(1, 999);
        System.out.println("  OK: ¿Hay 999 unidades? " + sinStock);
    }
}
