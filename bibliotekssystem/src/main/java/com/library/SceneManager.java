package com.library;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneManager {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static FXMLLoader switchScene(String fxmlFileName, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/com/library/" + fxmlFileName));
            Parent root = loader.load();
            
            if (primaryStage.getScene() == null) {
                primaryStage.setScene(new Scene(root, 600, 400));
            } else {
                primaryStage.getScene().setRoot(root);
            }
            
            primaryStage.setTitle(title);
            primaryStage.show();
            return loader;
        } catch (IOException e) {
            System.err.println("Kunde inte ladda FXML-filen: " + fxmlFileName);
            e.printStackTrace();
            return null;
        }
    }
}