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
    @FXML private Button loginButton;
    @FXML private Button cartButton;

    private Item currentItem;

    public void setItem(Item item) {
        this.currentItem = item;
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
            isbnText.setText("Referenslitteratur (Får ej lånas hem)");
            descriptionArea.setText("Kategori: " + item.getItemCategory());
        }
    }

    @FXML
    public void initialize() {
        if (UserSession.isLoggedIn()) {
            loginButton.setText("Mina sidor");
            loginButton.setOnAction(event -> SceneManager.switchScene("My-account.fxml", "Mitt Bibliotekskonto"));
        } else {
            loginButton.setText("Logga in");
            loginButton.setOnAction(event -> SceneManager.openLogin("Item-view.fxml"));
        }

        backButton.setOnAction(event -> SceneManager.switchScene("Search_Item.fxml", "Bibliotekssystem - Sök media"));
        if (cartButton != null) {
            cartButton.setOnAction(event -> SceneManager.switchScene("Cart-view.fxml", "Bibliotekssystem - Kassa"));
            cartButton.setText("Kundvagn (" + UserSession.getCart().size() + ")");
        }

        borrowButton.setText("Lägg i kundkorg");
        borrowButton.setOnAction(event -> {
            if (!UserSession.isLoggedIn()) {
                borrowButton.setText("Logga in först!");
                borrowButton.setStyle("-fx-text-fill: red;");
                return;
            }

            if (currentItem instanceof Magazine) {
                borrowButton.setText("Kan ej lånas!");
                borrowButton.setStyle("-fx-text-fill: red;");
                return;
            }

            UserSession.addToCart(currentItem);
            borrowButton.setText("Tillagd i korgen!");
            borrowButton.setStyle("-fx-text-fill: green;");
            borrowButton.setDisable(true);
            
            if (cartButton != null) {
                cartButton.setText("Kundvagn (" + UserSession.getCart().size() + ")");
            }
        });
    }
}