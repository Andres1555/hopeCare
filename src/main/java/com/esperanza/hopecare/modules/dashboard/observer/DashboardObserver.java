package com.esperanza.hopecare.modules.dashboard.observer;

import com.esperanza.hopecare.common.events.EventBus;
import com.esperanza.hopecare.common.events.NuevaCitaEvent;
import com.esperanza.hopecare.common.events.NuevaFacturaEvent;
import com.esperanza.hopecare.modules.dashboard.dao.DashboardDAO;
import com.esperanza.hopecare.modules.dashboard.ui.DashboardView;
import javax.swing.SwingWorker;
import java.util.List;

public class DashboardObserver {
    private DashboardView view;
    private DashboardDAO dao;

    public DashboardObserver(DashboardView view) {
        this.view = view;
        this.dao = new DashboardDAO();
        
        // Suscribirse a eventos
        EventBus.getInstance().register(NuevaCitaEvent.class, this::onNuevaCita);
        EventBus.getInstance().register(NuevaFacturaEvent.class, this::onNuevaFactura);
    }
    
    private void onNuevaCita(NuevaCitaEvent event) {
        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() {
                return dao.obtenerCitasDelDia();
            }
            @Override
            protected void done() {
                try {
                    int citasHoy = get();
                    view.actualizarCitasDelDia(citasHoy);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
    
    private void onNuevaFactura(NuevaFacturaEvent event) {
        new SwingWorker<Void, Void>() {
            private double ingresosMes;
            private List<String> meds;

            @Override
            protected Void doInBackground() {
                ingresosMes = dao.obtenerIngresosDelMes();
                meds = dao.obtenerMedicamentosStockBajo();
                return null;
            }
            @Override
            protected void done() {
                view.actualizarIngresosMensuales(ingresosMes);
                view.actualizarMedicamentosBajoStock(meds);
            }
        }.execute();
    }
}
