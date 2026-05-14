package com.esperanza.hopecare;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/esperanza/hopecare/javafx/view/main.fxml"));
        Parent root = loader.load();
        scene = new Scene(root, 900, 600);
        
        // Load CSS
        String css = getClass().getResource("/com/esperanza/hopecare/javafx/css/sisgeho.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        stage.setTitle("Sisgeho - JavaFX");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}