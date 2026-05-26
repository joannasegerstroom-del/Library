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

            Map<Item, String> results = itemDAO.checkoutCart(user, itemsToCheckout);
            
            resultBox.getChildren().clear();
            
            StringBuilder receiptBuilder = new StringBuilder();
            boolean hasReceipt = false;
            String currentReceiptNumber = "";

            for (Map.Entry<Item, String> entry : results.entrySet()) {
                Item item = entry.getKey();
                String status = entry.getValue();

                Text message = new Text();
                
                if (status.startsWith("SUCCESS")) {
                    String[] parts = status.split("\\|");
                    currentReceiptNumber = parts[1];
                    String barcode = parts[2];
                    String dueDate = parts[3];
                    
                    if (!hasReceipt) {
                        receiptBuilder.append("=====================================\n");
                        receiptBuilder.append("         BIBLIOTEKSKVITTO\n");
                        receiptBuilder.append("=====================================\n");
                        receiptBuilder.append("Datum: ").append(java.time.LocalDate.now()).append("\n");
                        receiptBuilder.append("Låntagare: ").append(user.getName()).append("\n");
                        receiptBuilder.append("Kvittonummer: ").append(currentReceiptNumber).append("\n");
                        receiptBuilder.append("-------------------------------------\n");
                        hasReceipt = true;
                    }
                    
                    receiptBuilder.append("Titel: ").append(item.getTitle()).append("\n");
                    receiptBuilder.append("Identifierare: ").append(barcode).append("\n");
                    receiptBuilder.append("Återlämnas senast: ").append(dueDate).append("\n\n");

                    message.setText(item.getTitle() + " - Lånet lyckades! (Se kvitto)");
                    message.setFill(Color.GREEN);
                    UserSession.getCart().remove(item);
                } else if ("LIMIT_REACHED".equals(status)) {
                    message.setText(item.getTitle() + " - Nekad: Maxgräns för lån uppnådd!");
                    message.setFill(Color.RED);
                } else if ("UNAVAILABLE".equals(status)) {
                    message.setText(item.getTitle() + " - Nekad: Ingen tillgänglig kopia, du kan reservera objektet istället.");
                    message.setFill(Color.RED);
                } else {
                    message.setText(item.getTitle() + " - Hoppsan, ett fel inträffade.");
                    message.setFill(Color.RED);
                }
                
                resultBox.getChildren().add(message);
            }
            
            if (hasReceipt) {
                receiptBuilder.append("-------------------------------------\n");
                receiptBuilder.append("Vänligen spara detta kvitto.\n");
                receiptBuilder.append("Tack för att du lånar hos oss!");
                
                showReceiptDialog(receiptBuilder.toString());
            }
            
            loadCartItems();
        });
    }

    private void showReceiptDialog(String receiptText) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Lånekvitto");
        alert.setHeaderText("Ditt lån har registrerats!");
        
        javafx.scene.control.TextArea textArea = new javafx.scene.control.TextArea(receiptText);
        textArea.setEditable(false);
        textArea.setWrapText(false);
        textArea.setPrefWidth(350);
        textArea.setPrefHeight(350);
        textArea.setStyle("-fx-font-family: 'monospace';"); 
        
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
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