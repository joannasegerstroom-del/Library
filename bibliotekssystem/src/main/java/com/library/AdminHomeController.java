package com.library;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AdminHomeController {

    @FXML private Button addItemButton; 
    @FXML private Button editItemButton; 
    @FXML private Button logoutButton;

    @FXML
    public void initialize() {
        
        if (addItemButton != null) {
            addItemButton.setOnAction(event -> {
                SceneManager.switchScene("Admin-add_item.fxml", "Bibliotekssystem - Lägg till objekt");
            });
        }

        if (editItemButton != null) {
            editItemButton.setOnAction(event -> {
                SceneManager.switchScene("Admin-edit.fxml", "Bibliotekssystem - Hantera objekt");
            });
        }

        if (logoutButton != null) {
            logoutButton.setOnAction(event -> {
                SceneManager.switchScene("Search_Item.fxml", "Bibliotekssystem - Sök media");
            });
        }
    }
}