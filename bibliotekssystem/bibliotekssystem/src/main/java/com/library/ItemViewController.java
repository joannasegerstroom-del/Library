package com.library;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.scene.image.ImageView;

public class ItemViewController {

    @FXML private ImageView logoImageView;
    @FXML private ImageView bookCoverImageView;
    @FXML private Text titleText;
    @FXML private Text authorText;
    @FXML private Text isbnText;
    @FXML private TextArea descriptionArea;
    @FXML private Button borrowButton;
    @FXML private Button backButton;

    public void setItem(Item item) {
        titleText.setText("Titel: " + item.getTitle());
        
        if (item instanceof Book) {
            Book book = (Book) item;
            authorText.setText("Författare: " + book.getAuthor());
            isbnText.setText("ISBN: " + book.getIsbn());
            descriptionArea.setText("Klassificering: " + book.getBookClassification() + "\nKategori: " + book.getItemCategory());
        } else if (item instanceof DVD) {
            DVD dvd = (DVD) item;
            authorText.setText("Regissör: " + dvd.getDirector());
            isbnText.setText("Genre: " + dvd.getGenre());
            descriptionArea.setText("Skådespelare: " + dvd.getActor() + "\nKategori: " + dvd.getItemCategory());
        } else if (item instanceof Magazine) {
            authorText.setText("Typ: Tidskrift");
            isbnText.setText("Referenslitteratur");
            descriptionArea.setText("Kategori: " + item.getItemCategory());
        }
    }

    @FXML
    public void initialize() {
        backButton.setOnAction(event -> {
        SceneManager.switchScene("Search_Item.fxml", "Bibliotekssystem - Sök media");
        });
    }
}