package com.esperanza.hopecare.modules.medicamentos_lab.model;

import java.time.LocalDateTime;

public class SolicitudExamen {
    private int idSolicitud;
    private int idConsulta;
    private int idExamen;
    private LocalDateTime fechaSolicitud;
    private String estado;
    private String resultadoTexto;
    private byte[] resultadoArchivo;
    private int realizadoPor;
    private boolean facturado;

    public SolicitudExamen() {}

    public SolicitudExamen(int idSolicitud, int idConsulta, int idExamen, LocalDateTime fechaSolicitud,
                           String estado, String resultadoTexto, byte[] resultadoArchivo, int realizadoPor, boolean facturado) {
        this.idSolicitud = idSolicitud;
        this.idConsulta = idConsulta;
        this.idExamen = idExamen;
        this.fechaSolicitud = fechaSolicitud;
        this.estado = estado;
        this.resultadoTexto = resultadoTexto;
        this.resultadoArchivo = resultadoArchivo;
        this.realizadoPor = realizadoPor;
        this.facturado = facturado;
    }

    public SolicitudExamen(int idConsulta, int idExamen) {
        this.idConsulta = idConsulta;
        this.idExamen = idExamen;
        this.fechaSolicitud = LocalDateTime.now();
        this.estado = "PENDIENTE";
        this.facturado = false;
    }

    public int getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(int idSolicitud) { this.idSolicitud = idSolicitud; }
    public int getIdConsulta() { return idConsulta; }
    public void setIdConsulta(int idConsulta) { this.idConsulta = idConsulta; }
    public int getIdExamen() { return idExamen; }
    public void setIdExamen(int idExamen) { this.idExamen = idExamen; }
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getResultadoTexto() { return resultadoTexto; }
    public void setResultadoTexto(String resultadoTexto) { this.resultadoTexto = resultadoTexto; }
    public byte[] getResultadoArchivo() { return resultadoArchivo; }
    public void setResultadoArchivo(byte[] resultadoArchivo) { this.resultadoArchivo = resultadoArchivo; }
    public int getRealizadoPor() { return realizadoPor; }
    public void setRealizadoPor(int realizadoPor) { this.realizadoPor = realizadoPor; }
    public boolean isFacturado() { return facturado; }
    public void setFacturado(boolean facturado) { this.facturado = facturado; }
}
