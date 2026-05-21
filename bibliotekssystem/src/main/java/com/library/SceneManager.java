package com.library;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneManager {

    private static Stage primaryStage;
    private static String previousFxml = "Search_Item.fxml";

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static String getPreviousFxml() {
        return previousFxml;
    }

    public static FXMLLoader switchScene(String fxmlFileName, String title) {
        try {
            if (primaryStage.getScene() != null && !fxmlFileName.equals("Login.fxml") && !fxmlFileName.equals("My-account.fxml") && !fxmlFileName.equals("Admin-homescreen.fxml")) {
            }

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

    public static void switchScene(Parent root, String title) {
        if (primaryStage != null) {
            if (primaryStage.getScene() == null) {
                primaryStage.setScene(new Scene(root, 600, 400));
            } else {
                primaryStage.getScene().setRoot(root);
            }
            primaryStage.setTitle(title);
            primaryStage.show();
        } else {
            System.err.println("Fel: primaryStage är null i SceneManager!");
        }
    }

    public static void openLogin(String currentFxmlName) {
        previousFxml = currentFxmlName;
        switchScene("Login.fxml", "Bibliotekssystem - Logga in");
    }
}