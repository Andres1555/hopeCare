package com.esperanza.hopecare.test;

import com.esperanza.hopecare.common.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TestDatabase {
    public static void main(String[] args) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS test_table (id INTEGER PRIMARY KEY)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
             
            stmt.execute(createTableSQL);
            System.out.println("¡Conexión y creación de tabla de prueba exitosas!");
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
