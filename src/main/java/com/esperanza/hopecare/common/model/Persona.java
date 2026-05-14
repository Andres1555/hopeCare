/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.esperanza.hopecare.common.model;

/**
 *
 * @author Jenfer
 */

public class Persona {
    protected int idPersona;
    protected String documentoIdentidad;

    public Persona() {}

    public Persona(String documentoIdentidad) {
        this.documentoIdentidad = documentoIdentidad;
    }

    public int getIdPersona() { return idPersona; }
    public void setIdPersona(int idPersona) { this.idPersona = idPersona; }

    public String getDocumentoIdentidad() { return documentoIdentidad; }
    public void setDocumentoIdentidad(String documentoIdentidad) { this.documentoIdentidad = documentoIdentidad; }
}
