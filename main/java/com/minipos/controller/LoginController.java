package com.minipos.controller;

import com.minipos.App;
import com.minipos.dao.UserDAO;
import com.minipos.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        User user = userDAO.validateUser(username, password);

        if (user != null) {
            try {
                // Set current user session
                App.setCurrentUser(user);
                // Navigate to main dashboard (MainController will adapt view based on role)
                App.setRoot("main");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Invalid username or password");
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleLogoutRedirect() {
        // Clear any session and go back to login
        App.setCurrentUser(null);
        try {
            App.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSignUp() {
        try {
            App.setRoot("signup");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
