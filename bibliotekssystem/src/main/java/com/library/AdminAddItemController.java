package com.library;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class AdminAddItemController {

    @FXML private TextField titleField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField categoryField;
    @FXML private TextField authorField;
    @FXML private TextField isbnField;
    @FXML private TextField classificationField;
    @FXML private TextField directorField;
    @FXML private TextField genreField;
    @FXML private TextField actorField;
    @FXML private Button saveButton;
    @FXML private Button backButton;
    @FXML private Button logoutButton;
    @FXML private Button editItemButton;
    @FXML private Text statusText;

    private ItemDAO itemDAO = new ItemDAO();

    @FXML
    public void initialize() {
        if (typeComboBox != null) {
            typeComboBox.getItems().addAll("Bok", "Film", "Tidskrift");
            typeComboBox.setValue("Bok");
            typeComboBox.setOnAction(e -> handleTypeChange());
            handleTypeChange();
        }

        backButton.setOnAction(event -> {
            SceneManager.switchScene("Admin-homescreen.fxml", "Bibliotekssystem - Admin Panel");
        });

        if (logoutButton != null) {
            logoutButton.setOnAction(event -> {
                UserSession.logOut(); // Rensa sessionen när man loggar ut!
                SceneManager.switchScene("Search_Item.fxml", "Bibliotekssystem - Sök media");
            });
        }

        if (editItemButton != null) {
            editItemButton.setOnAction(e -> SceneManager.switchScene("Admin-edit.fxml", "Bibliotekssystem - Hantera objekt"));
        }

        saveButton.setOnAction(event -> handleSave());
    }

    private void handleTypeChange() {
        String selectedType = typeComboBox.getValue();
        boolean isBook = "Bok".equals(selectedType);
        boolean isDVD = "Film".equals(selectedType);

        authorField.setDisable(!isBook);
        isbnField.setDisable(!isBook);
        classificationField.setDisable(!isBook);

        directorField.setDisable(!isDVD);
        genreField.setDisable(!isDVD);
        actorField.setDisable(!isDVD);
    }

    private void handleSave() {
        String title = titleField.getText().trim();
        String type = typeComboBox.getValue();
        String category = categoryField.getText().trim();

        if (title.isEmpty() || category.isEmpty()) {
            statusText.setText("Titel och kategori måste fyllas i.");
            return;
        }

        boolean success = false;

        if ("Bok".equals(type)) {
            String author = authorField.getText().trim();
            String isbn = isbnField.getText().trim();
            String classification = classificationField.getText().trim();
            success = itemDAO.addBook(title, category, author, isbn, classification);
        } else if ("Film".equals(type)) {
            String director = directorField.getText().trim();
            String genre = genreField.getText().trim();
            String actor = actorField.getText().trim();
            success = itemDAO.addDVD(title, category, director, genre, actor);
        } else if ("Tidskrift".equals(type)) {
            success = itemDAO.addMagazine(title, category);
        }

        if (success) {
            statusText.setText("Media har lagts till framgångsrikt!");
            clearFields();
        } else {
            statusText.setText("Ett fel uppstod när media skulle sparas.");
        }
    }

    private void clearFields() {
        titleField.clear();
        categoryField.clear();
        authorField.clear();
        isbnField.clear();
        classificationField.clear();
        directorField.clear();
        genreField.clear();
        actorField.clear();
    }
}