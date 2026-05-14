package com.esperanza.hopecare.modules.dashboard.observer.listeners;

import com.esperanza.hopecare.modules.dashboard.observer.events.NuevaCitaEvent;

public interface CitaListener {
    void onCitaCreada(NuevaCitaEvent event);
}
