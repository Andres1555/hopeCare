package com.esperanza.hopecare.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class HopecareApp extends Application {

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

    public static void main(String[] args) {
        launch(args);
    }
}
