package com.esperanza.hopecare.common.utils;

public class CalculadoraImpuestos {
    private static final double TASA_IVA = 0.19;

    public static double calcularIVA(double subtotal) {
        return subtotal * TASA_IVA;
    }
}
