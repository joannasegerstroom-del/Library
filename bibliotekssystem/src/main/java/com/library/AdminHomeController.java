package com.library;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import java.util.List;

public class AdminHomeController {

    @FXML private Button addItemButton;
    @FXML private Button editItemButton;
    @FXML private Button overdueButton;
    @FXML private Button logoutButton;
    @FXML private ListView<String> adminListView;
    @FXML private Button sendEmailsButton;
    @FXML private TextField barcodeField;
    @FXML private Button markLostButton;
    @FXML private Text statusText;

    private UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        addItemButton.setOnAction(event -> SceneManager.switchScene("Admin-add_item.fxml", "Bibliotekssystem - Lägg till objekt"));
        editItemButton.setOnAction(event -> SceneManager.switchScene("Admin-edit.fxml", "Bibliotekssystem - Hantera objekt"));
        logoutButton.setOnAction(event -> SceneManager.switchScene("Search_Item.fxml", "Bibliotekssystem - Sök media"));

        overdueButton.setOnAction(event -> {
            statusText.setText("Här visas alla försenade lån:");
            loadOverdueLoans();
        });

        sendEmailsButton.setOnAction(event -> {
            List<String> overdue = userDAO.getAllOverdueLoans();
            if (overdue.isEmpty()) {
                statusText.setText("Inga försenade lån att skicka mail för.");
                return;
            }

            System.out.println("\n--- BÖRJAR SKICKA DAGLIGA PÅMINNELSEMAIL ---");
            for (String loanInfo : overdue) {
                String email = loanInfo.substring(loanInfo.indexOf("(") + 1, loanInfo.indexOf(")"));
                String title = loanInfo.split("\\|")[1].trim();
                
                System.out.println("Till: " + email);
                System.out.println("Ämne: PÅMINNELSE - Försenat lån!");
                System.out.println("Meddelande: Hej! Vänligen återlämna objektet " + title + " snarast möjligt.");
                System.out.println("-------------------------------------------");
            }
            System.out.println("--- ALLA MAIL SKICKADE ---\n");
            statusText.setText("Mail skickade till " + overdue.size() + " användare!");
        });

        markLostButton.setOnAction(event -> {
            String barcode = barcodeField.getText().trim();
            if (barcode.isEmpty()) {
                statusText.setText("Vänligen fyll i en streckkod först.");
                return;
            }

            boolean success = userDAO.markCopyAsLost(barcode);
            if (success) {
                statusText.setText("Objekt " + barcode + " har markerats som FÖRSVUNNEN.");
                barcodeField.clear();
                loadOverdueLoans();
            } else {
                statusText.setText("Kunde inte hitta streckkod: " + barcode);
            }
        });

        statusText.setText("Välkommen administratör. Välj en åtgärd i menyn.");
        adminListView.getItems().clear();
    }

    private void loadOverdueLoans() {
        adminListView.getItems().clear();
        List<String> overdueList = userDAO.getAllOverdueLoans();
        if (overdueList.isEmpty()) {
            adminListView.getItems().add("Inga försenade lån i systemet just nu. Bra jobbat!");
        } else {
            adminListView.getItems().addAll(overdueList);
        }
    }
}