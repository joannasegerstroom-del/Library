package com.library;

import javafx.application.Application;
import javafx.stage.Stage;
import java.sql.Connection;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneManager.setPrimaryStage(primaryStage);
        SceneManager.switchScene("Search_Item.fxml", "Bibliotekssystem - Sök media");
    }

    public static void main(String[] args) {
        System.out.println("Startar systemet...");
        
        Connection conn = DatabaseManager.getConnection();
        if (conn != null) {
            System.out.println("SUCCÉ! Uppkopplingen till databasen fungerar.");
        }

        launch(args); 
    }
}