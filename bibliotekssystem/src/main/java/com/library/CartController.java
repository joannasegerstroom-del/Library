package com.library;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CartController {

    @FXML private Button backButton;
    @FXML private Button checkoutButton;
    @FXML private Button removeButton;
    @FXML private Button reserveButton;
    @FXML private ListView<Item> cartListView;
    @FXML private VBox resultBox;

    private ItemDAO itemDAO = new ItemDAO();
    private UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        backButton.setOnAction(e -> SceneManager.switchScene("Search_Item.fxml", "Bibliotekssystem - Sök media"));

        cartListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle() + " (" + item.getItemCategory() + ")");
                }
            }
        });

        loadCartItems();

        removeButton.setOnAction(e -> {
            Item selected = cartListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                UserSession.getCart().remove(selected);
                loadCartItems();
            }
        });

        reserveButton.setOnAction(e -> {
            Item selected = cartListView.getSelectionModel().getSelectedItem();
            LibraryUser user = UserSession.getLoggedInUser();

            if (selected != null && user != null) {
                boolean success = userDAO.createReservation(user.getUserID(), selected.getItemID());

                Text message = new Text();
                if (success) {
                    message.setText(selected.getTitle() + " - Reservationen lyckades! Du står nu i kö.");
                    message.setFill(Color.BLUE);
                    
                    UserSession.getCart().remove(selected);
                    loadCartItems();
                } else {
                    message.setText(selected.getTitle() + " - Gick inte att reservera. För hjälp, kontakta biblioteket.");
                    message.setFill(Color.RED);
                }
                
                resultBox.getChildren().add(message);
            }
        });

        checkoutButton.setOnAction(e -> {
            LibraryUser user = UserSession.getLoggedInUser();
            List<Item> itemsToCheckout = new ArrayList<>(UserSession.getCart());

            Map<Item, String> results = itemDAO.checkoutCart(user.getUserID(), itemsToCheckout);
            
            resultBox.getChildren().clear();

            for (Map.Entry<Item, String> entry : results.entrySet()) {
                Item item = entry.getKey();
                String status = entry.getValue();

                Text message = new Text();
                
                if ("SUCCESS".equals(status)) {
                    message.setText(item.getTitle() + " - Lånet lyckades! Gå till Mina Sidor för att hantera dina lån");
                    message.setFill(Color.GREEN);
                    UserSession.getCart().remove(item);
                } else if ("UNAVAILABLE".equals(status)) {
                    message.setText(item.getTitle() + " - Tyvärr, inga lediga exemplar just nu. Du kan reservera objektet.");
                    message.setFill(Color.RED);
                } else {
                    message.setText(item.getTitle() + " - Hoppsan, ett fel inträffade. Vänligen försök igen senare");
                    message.setFill(Color.RED);
                }
                
                resultBox.getChildren().add(message);
            }
            
            loadCartItems();
        });
    }

    private void loadCartItems() {
        cartListView.getItems().clear();
        cartListView.getItems().addAll(UserSession.getCart());

        boolean isEmpty = UserSession.getCart().isEmpty();
        checkoutButton.setDisable(isEmpty);
        removeButton.setDisable(isEmpty);
        reserveButton.setDisable(isEmpty);
    }
}