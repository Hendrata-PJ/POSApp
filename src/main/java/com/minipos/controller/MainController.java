package com.minipos.controller;

import com.minipos.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController {

    @FXML
    private StackPane contentArea;

    @FXML
    private javafx.scene.control.Button btnDashboard;
    @FXML
    private javafx.scene.control.Button btnPOS;
    @FXML
    private javafx.scene.control.Button btnProducts;
    @FXML
    private javafx.scene.control.Button btnHistory;
    @FXML
    private javafx.scene.control.Button btnDarkMode;
    @FXML
    private javafx.scene.control.Button btnLogout;

    private boolean isDarkMode = false;

    @FXML
    public void initialize() {
        applyRolePermissions();
    }

    private void applyRolePermissions() {
        com.minipos.model.User user = com.minipos.App.getCurrentUser();
        String role = user != null ? user.getRole() : "";

        // Default: hide everything then enable based on role
        btnDashboard.setVisible(false);
        btnPOS.setVisible(false);
        btnProducts.setVisible(false);
        btnHistory.setVisible(false);

        switch (role) {
            case "admin":
                // admin sees all
                btnDashboard.setVisible(true);
                btnPOS.setVisible(true);
                btnProducts.setVisible(true);
                btnHistory.setVisible(true);
                // Load dashboard by default
                loadView("dashboard");
                break;
            case "manager":
                // manager sees POS + Products + History + Dashboard
                btnDashboard.setVisible(true);
                btnPOS.setVisible(true);
                btnProducts.setVisible(true);
                btnHistory.setVisible(true);
                loadView("dashboard");
                break;
            case "cashier":
                // cashier sees only POS
                btnPOS.setVisible(true);
                loadView("pos");
                break;
            default:
                // no user or unknown role: show minimal
                btnPOS.setVisible(true);
                loadView("pos");
                break;
        }
    }

    @FXML
    private void showDashboard() {
        // Load the dashboard.fxml into the content area
        loadView("dashboard");
    }

    @FXML
    private void showPOS() {
        loadView("pos");
    }

    @FXML
    private void showProducts() {
        loadView("products");
    }

    @FXML
    private void showHistory() {
        loadView("history");
    }

    @FXML
    private void logout() {
        try {
            App.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        if (isDarkMode) {
            contentArea.getScene().getRoot().getStyleClass().add("dark-mode");
        } else {
            contentArea.getScene().getRoot().getStyleClass().remove("dark-mode");
        }
    }

    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
