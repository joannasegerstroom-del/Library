package com.library;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import java.util.List;

public class AdminEditController {

    @FXML private Button backButton;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private ListView<Item> resultListView;
    @FXML private TextField titleField;
    @FXML private TextField categoryField;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Text statusText;

    private ItemDAO itemDAO = new ItemDAO();
    private Item selectedItem = null;

    @FXML
    public void initialize() {
        backButton.setOnAction(e -> SceneManager.switchScene("Admin-homescreen.fxml", "Bibliotekssystem - Admin Panel"));
        
        searchButton.setOnAction(e -> performSearch());
        searchField.setOnAction(e -> performSearch());

        resultListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedItem = newSelection;
                titleField.setText(selectedItem.getTitle());
                categoryField.setText(selectedItem.getItemCategory());
                statusText.setText("");
            }
        });

        updateButton.setOnAction(e -> handleUpdate());
        deleteButton.setOnAction(e -> handleDelete());
    }

    private void performSearch() {
        String keyword = searchField.getText();
        List<Item> results = itemDAO.searchByKeyword(keyword, "");
        resultListView.getItems().clear();
        resultListView.getItems().addAll(results);
        statusText.setText("");
    }

    private void handleUpdate() {
        if (selectedItem == null) {
            statusText.setText("Välj ett objekt i listan först.");
            statusText.setFill(Color.RED);
            return;
        }
        
        String newTitle = titleField.getText();
        String newCategory = categoryField.getText();
        
        // Uppdatera databasen
        if (itemDAO.updateItemBasic(selectedItem.getItemID(), newTitle, newCategory)) {
            statusText.setText("Objektet har uppdaterats!");
            statusText.setFill(Color.GREEN);
            performSearch();
        } else {
            statusText.setText("Misslyckades att uppdatera databasen.");
            statusText.setFill(Color.RED);
        }
    }

    private void handleDelete() {
        if (selectedItem == null) {
            statusText.setText("Välj ett objekt i listan först.");
            statusText.setFill(Color.RED);
            return;
        }

        if (itemDAO.deleteItem(selectedItem.getItemID())) {
            statusText.setText("Objektet togs bort!");
            statusText.setFill(Color.GREEN);
            titleField.clear();
            categoryField.clear();
            selectedItem = null;
            performSearch();
        } else {
            statusText.setText("Kunde inte ta bort! Kontrollera om objektet har aktiva kopior/lån.");
            statusText.setFill(Color.RED);
        }
    }
}