package com.esperanza.hopecare.modules.dashboard.observer.listeners;

import com.esperanza.hopecare.modules.dashboard.observer.events.NuevaFacturaEvent;

public interface FacturaListener {
    void onFacturaCreada(NuevaFacturaEvent event);
}
