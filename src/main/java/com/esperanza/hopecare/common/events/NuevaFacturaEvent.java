package com.esperanza.hopecare.common.events;

public class NuevaFacturaEvent {
    private final int idFactura;
    private final double total;

    public NuevaFacturaEvent(int idFactura, double total) {
        this.idFactura = idFactura;
        this.total = total;
    }

    public int getIdFactura() { return idFactura; }
    public double getTotal() { return total; }
}
