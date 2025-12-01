package com.minipos.controller;

import com.minipos.App;
import com.minipos.dao.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;

import java.io.IOException;

public class SignUpController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void initialize() {
        // Initialize role combo box
        roleComboBox.getItems().addAll("cashier", "manager", "admin");
        roleComboBox.setValue("cashier");
    }

    @FXML
    private void handleSignUp() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String role = roleComboBox.getValue();

        // Clear previous messages
        errorLabel.setVisible(false);
        successLabel.setVisible(false);

        // Validation
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("All fields are required");
            errorLabel.setVisible(true);
            return;
        }

        if (username.length() < 3) {
            errorLabel.setText("Username must be at least 3 characters");
            errorLabel.setVisible(true);
            return;
        }

        if (password.length() < 4) {
            errorLabel.setText("Password must be at least 4 characters");
            errorLabel.setVisible(true);
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match");
            errorLabel.setVisible(true);
            return;
        }

        // Try to register user
        if (userDAO.registerUser(username, password, role)) {
            successLabel.setText("Account created successfully! Redirecting to login...");
            successLabel.setVisible(true);
            
            // Redirect to login after 2 seconds
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));
            pause.setOnFinished(event -> {
                try {
                    App.setRoot("login");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            pause.play();
        } else {
            errorLabel.setText("Username already exists. Please choose another.");
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            App.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
