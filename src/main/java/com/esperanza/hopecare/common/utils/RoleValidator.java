package com.esperanza.hopecare.common.utils;

public class RoleValidator {
    public boolean tieneRol(String rolUsuario, String rolRequerido) {
        return rolUsuario != null && rolUsuario.equalsIgnoreCase(rolRequerido);
    }
}
