package com.minipos.controller;

import com.minipos.dao.TransactionDAO;
import com.minipos.model.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class HistoryController {

    @FXML private TableView<Transaction> historyTable;
    @FXML private TableColumn<Transaction, Integer> colId;
    @FXML private TableColumn<Transaction, String> colDate;
    @FXML private TableColumn<Transaction, String> colCashier;
    @FXML private TableColumn<Transaction, Double> colTotal;
    @FXML private javafx.scene.control.Button btnDelete;

    private TransactionDAO transactionDAO = new TransactionDAO();
    private ObservableList<Transaction> transactionList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colCashier.setCellValueFactory(new PropertyValueFactory<>("cashierName"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        loadHistory();
        applyRolePermissions();
    }

    private void loadHistory() {
        transactionList.setAll(transactionDAO.getAllTransactions());
        historyTable.setItems(transactionList);
    }

    private void applyRolePermissions() {
        com.minipos.model.User user = com.minipos.App.getCurrentUser();
        String role = user != null ? user.getRole() : "";
        // Only admin can delete
        btnDelete.setVisible("admin".equals(role));
    }

    @FXML
    private void handleDelete() {
        Transaction selected = historyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a transaction to delete.");
            alert.showAndWait();
            return;
        }

        javafx.scene.control.Alert confirm = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete transaction ID " + selected.getId() + "? This will restore product stock.");
        java.util.Optional<javafx.scene.control.ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == javafx.scene.control.ButtonType.OK) {
            boolean ok = transactionDAO.deleteTransaction(selected.getId());
            if (ok) {
                javafx.scene.control.Alert info = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                info.setTitle("Deleted");
                info.setHeaderText(null);
                info.setContentText("Transaction deleted successfully.");
                info.showAndWait();
                loadHistory();
            } else {
                javafx.scene.control.Alert err = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                err.setTitle("Error");
                err.setHeaderText(null);
                err.setContentText("Failed to delete transaction. Check logs.");
                err.showAndWait();
            }
        }
    }
}
