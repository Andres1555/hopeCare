package com.esperanza.hopecare.modules.dashboard.observer.events;

import java.util.EventObject;

public class NuevaFacturaEvent extends EventObject {
    private final int idFactura;
    private final double total;

    public NuevaFacturaEvent(Object source, int idFactura, double total) {
        super(source);
        this.idFactura = idFactura;
        this.total = total;
    }

    public int getIdFactura() { return idFactura; }
    public double getTotal() { return total; }
}
