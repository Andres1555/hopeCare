package com.esperanza.hopecare.modules.citas_consultas.view;

/**
 * Interfaz que define los métodos que debe implementar cualquier vista
 * para gestionar consultas médicas.
 */
public interface IConsultaView {
    int getIdCitaSeleccionada();
    String getDiagnostico();
    String getSintomas();
    String getTratamiento();
    boolean isFacturado();
    void mostrarError(String mensaje);
    void mostrarExito(String mensaje);
    void limpiarFormulario();
}
