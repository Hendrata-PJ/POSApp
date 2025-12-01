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

    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        // Load default view (Dashboard is already in FXML, but we might want to load it dynamically later)
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

