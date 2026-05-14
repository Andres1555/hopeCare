package com.esperanza.hopecare.common.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

public class CrearBaseDatos {

    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Leer el script SQL desde resources
            InputStream is = CrearBaseDatos.class.getResourceAsStream("/sisgeho_schema.sql");
            if (is == null) {
                System.err.println("No se encontró el archivo sisgeho_schema.sql en resources");
                return;
            }
            String sqlScript = new BufferedReader(new InputStreamReader(is))
                    .lines().collect(Collectors.joining("\n"));

            // Ejecutar cada sentencia separada por ';'
            for (String sentencia : sqlScript.split(";")) {
                String sql = sentencia.trim();
                if (!sql.isEmpty()) {
                    stmt.execute(sql);
                    System.out.println("Ejecutado: " + sql.substring(0, Math.min(50, sql.length())) + "...");
                }
            }

            System.out.println("\n*** Base de datos Sisgeho creada/actualizada correctamente ***");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
