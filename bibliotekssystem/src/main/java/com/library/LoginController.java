package com.library;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class LoginController {

    @FXML private Button backButton;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Text errorText;

    private UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        backButton.setOnAction(event -> {
            String prev = SceneManager.getPreviousFxml();
            if ("Item-view.fxml".equals(prev)) {
                SceneManager.switchScene("Item-view.fxml", "Bibliotekssystem - Detaljer");
            } else {
                SceneManager.switchScene("Search_Item.fxml", "Bibliotekssystem - Sök media");
            }
        });

        loginButton.setOnAction(event -> {
            String username = usernameField.getText().trim();
            
            if (username.isEmpty()) {
                errorText.setText("Vänligen fyll i ditt namn eller e-post.");
                return;
            }

            LibraryUser user = userDAO.validateLogin(username);

            if (user != null) {
                UserSession.logIn(user);

                if ("ADMIN".equalsIgnoreCase(user.getUserCategory())) {
                    SceneManager.switchScene("Admin-homescreen.fxml", "Bibliotekssystem - Admin Panel");
                } else {
                    SceneManager.switchScene("My-account.fxml", "Mitt Bibliotekskonto");
                }
            } else {
                errorText.setText("Hittade ingen användare med det namnet/eposten.");
            }
        });
    }
}