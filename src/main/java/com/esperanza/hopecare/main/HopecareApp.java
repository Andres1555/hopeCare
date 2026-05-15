package com.esperanza.hopecare.main;

import com.esperanza.hopecare.common.db.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.stream.Collectors;

public class HopecareApp extends Application {

    @Override
    public void init() {
        inicializarBaseDatos();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esperanza/hopecare/main/main.fxml"));
        BorderPane root = loader.load();

        Scene scene = new Scene(root, 1280, 720);
        scene.setFill(Color.web("#f5f7fa"));
        scene.getStylesheets().add(getClass().getResource("/com/esperanza/hopecare/main/hopecare.css").toExternalForm());

        primaryStage.setTitle("HopeCare - Sistema de Gestión Hospitalaria");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void inicializarBaseDatos() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (!tablasExisten(conn)) {
                System.out.println("Base de datos vacía. Creando esquema...");
                ejecutarScriptSQL(conn, "/sisgeho_schema.sql");
                System.out.println("Esquema creado. Insertando datos de prueba...");
                com.esperanza.hopecare.common.db.CargarDatosPrueba.main(new String[]{});
            } else if (baseDatosVacia(conn)) {
                System.out.println("Base de datos sin datos. Insertando datos de prueba...");
                com.esperanza.hopecare.common.db.CargarDatosPrueba.main(new String[]{});
            } else {
                System.out.println("Base de datos lista.");
            }
        } catch (Exception e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean tablasExisten(Connection conn) throws Exception {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='persona'")) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private boolean baseDatosVacia(Connection conn) throws Exception {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT count(*) FROM persona")) {
            return rs.next() && rs.getInt(1) == 0;
        }
    }

    private void ejecutarScriptSQL(Connection conn, String resourcePath) throws Exception {
        InputStream is = getClass().getResourceAsStream(resourcePath);
        if (is == null) {
            throw new RuntimeException("No se encontró el archivo " + resourcePath);
        }
        String sqlScript = new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining("\n"));
        try (Statement stmt = conn.createStatement()) {
            for (String sentencia : sqlScript.split(";")) {
                String sql = sentencia.trim();
                if (!sql.isEmpty()) {
                    stmt.execute(sql);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
