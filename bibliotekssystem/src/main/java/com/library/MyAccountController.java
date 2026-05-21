package com.library;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class MyAccountController {

    @FXML private Text welcomeText;
    @FXML private Button logoutButton;
    @FXML private Button myLoansButton;
    @FXML private Button pastLoansButton;
    @FXML private Button reservationsButton;
    @FXML private Button searchPageButton;
    @FXML private Text sectionTitleText;
    @FXML private ListView<String> accountListView;
    @FXML private Button returnButton;
    @FXML private Button bellButton;

    private UserDAO userDAO = new UserDAO();
    private ItemDAO itemDAO = new ItemDAO();
    private boolean showingCurrentLoans = true;

    @FXML
    public void initialize() {
        LibraryUser user = UserSession.getLoggedInUser();
        if (user != null) {
            welcomeText.setText("Inloggad som: " + user.getName() + " (" + user.getUserCategory() + ")");
        }

        if (user != null) {
            java.util.List<String> notifications = userDAO.getNotifications(user.getUserID());
            if (!notifications.isEmpty()) {
                bellButton.setText("🔔 Notiser (" + notifications.size() + ")");
                bellButton.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            } else {
                bellButton.setText("🔔 Notiser");
                bellButton.setStyle("");
            }
        }

        bellButton.setOnAction(e -> showNotiser());

        searchPageButton.setOnAction(e -> SceneManager.switchScene("Search_Item.fxml", "Bibliotekssystem - Sök media"));
        myLoansButton.setOnAction(e -> showMinaLan());
        pastLoansButton.setOnAction(e -> showTidigareLan());
        reservationsButton.setOnAction(e -> showReservationer());
        
        logoutButton.setOnAction(e -> {
            UserSession.logOut();
            SceneManager.switchScene("Search_Item.fxml", "Bibliotekssystem - Sök media");
        });

        accountListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else if (item.contains(" | ")) { 
                    // Splitta texten där vi lagt in " | "
                    String[] parts = item.split(" \\| ");
                    String title = parts[0];
                    String rawStatus = parts[1].split(" #")[0];

                    Text leftText = new Text(title);
                    Text rightText = new Text(rawStatus);

                    if (rawStatus.contains("[FÖRSENAD]")) {
                        rightText.setFill(Color.RED);
                    } else if (rawStatus.contains("[AKTIV]")) {
                        rightText.setFill(Color.GREEN);
                    } else if (rawStatus.contains("[RESERVERAD]")) {
                        rightText.setFill(Color.BLUE);
                    }

                    HBox hbox = new HBox();
                    HBox.setHgrow(leftText, Priority.ALWAYS);
                    AnchorPane space = new AnchorPane();
                    HBox.setHgrow(space, Priority.ALWAYS);

                    hbox.getChildren().addAll(leftText, space, rightText);
                    setGraphic(hbox);
                    setText(null);
                } else {
                    setGraphic(null);
                    setText(item);
                }
            }
        });

        returnButton.setOnAction(e -> {
            String selected = accountListView.getSelectionModel().getSelectedItem();
            if (selected != null && showingCurrentLoans) {
                String barcode = selected.substring(selected.lastIndexOf("#") + 1);
                if (userDAO.returnLoan(barcode)) {
                    showMinaLan(); 
                }
            }
        });

        showMinaLan();

        accountListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selected = accountListView.getSelectionModel().getSelectedItem();
                
                if (selected != null && sectionTitleText.getText().contains("Mina köplatser")) {
                    try {

                        String idPart = selected.substring(selected.lastIndexOf("#") + 1);
                        int itemId = Integer.parseInt(idPart);
                        
                        java.util.List<Item> items = itemDAO.searchByKeyword("", "");
                        Item targetItem = null;
                        for (Item i : items) {
                            if (i.getItemID() == itemId) {
                                targetItem = i;
                                break;
                            }
                        }
                        
                        if (targetItem != null) {
                            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("Item-view.fxml"));
                            javafx.scene.Parent root = loader.load();
                            
                            ItemViewController controller = loader.getController();
                            controller.setItem(targetItem);
                            
                            SceneManager.switchScene(root, "Bibliotekssystem - Detaljer");
                        }
                        
                    } catch (Exception e) {
                        System.err.println("Kunde inte öppna objektsidan vid dubbelklick: " + e.getMessage());
                    }
                }
            }
        });

    }

    private void showMinaLan() {
        showingCurrentLoans = true;
        sectionTitleText.setText("Mina aktuella lån");
        returnButton.setVisible(true);
        
        LibraryUser user = UserSession.getLoggedInUser();
        if (user != null) {
            accountListView.getItems().clear();
            accountListView.getItems().addAll(userDAO.getActiveLoans(user.getUserID()));
        }
    }

    private void showTidigareLan() {
        showingCurrentLoans = false;
        sectionTitleText.setText("Tidigare lån (Historik)");
        returnButton.setVisible(false);
        
        LibraryUser user = UserSession.getLoggedInUser();
        if (user != null) {
            accountListView.getItems().clear();
            accountListView.getItems().addAll(userDAO.getPastLoans(user.getUserID()));
        }
    }

    private void showReservationer() {
        showingCurrentLoans = false;
        sectionTitleText.setText("Mina köplatser");
        returnButton.setVisible(false);
        
        LibraryUser user = UserSession.getLoggedInUser();
        if (user != null) {
            accountListView.getItems().clear();
            accountListView.getItems().addAll(userDAO.getReservations(user.getUserID()));
        }
    }

    private void showNotiser() {
        showingCurrentLoans = false;
        sectionTitleText.setText("Mina Notiser");
        returnButton.setVisible(false);
        
        LibraryUser user = UserSession.getLoggedInUser();
        if (user != null) {
            accountListView.getItems().clear();
            java.util.List<String> notisList = userDAO.getNotifications(user.getUserID());
            
            if (notisList.isEmpty()) {
                accountListView.getItems().add("Du har inga nya notiser just nu.");
            } else {
                accountListView.getItems().addAll(notisList);
            }
        }
    }
}