package com.minipos.controller;

import com.minipos.App;
import com.minipos.dao.ProductDAO;
import com.minipos.dao.TransactionDAO;
import com.minipos.model.Product;
import com.minipos.model.Transaction;
import com.minipos.model.TransactionItem;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class PosController {

    @FXML
    private TextField searchField;
    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, String> colCode;
    @FXML
    private TableColumn<Product, String> colName;
    @FXML
    private TableColumn<Product, Double> colPrice;
    @FXML
    private TableColumn<Product, Integer> colStock;

    @FXML
    private TableView<TransactionItem> cartTable;
    @FXML
    private TableColumn<TransactionItem, String> colCartName;
    @FXML
    private TableColumn<TransactionItem, Integer> colCartQty;
    @FXML
    private TableColumn<TransactionItem, Double> colCartPrice;
    @FXML
    private TableColumn<TransactionItem, Double> colCartTotal;

    @FXML
    private Label totalLabel;
    @FXML
    private TextField discountField;

    private ProductDAO productDAO = new ProductDAO();
    private TransactionDAO transactionDAO = new TransactionDAO();
    private ObservableList<Product> productList = FXCollections.observableArrayList();
    private ObservableList<TransactionItem> cartList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Product Table Setup
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        // Cart Table Setup
        colCartName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colCartQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCartPrice.setCellValueFactory(new PropertyValueFactory<>("priceAtSale"));
        colCartTotal.setCellValueFactory(cellData -> new SimpleDoubleProperty(
                cellData.getValue().getQuantity() * cellData.getValue().getPriceAtSale()).asObject());

        loadProducts();
        cartTable.setItems(cartList);
    }

    private void loadProducts() {
        productList.setAll(productDAO.getAllProducts());
        productTable.setItems(productList);
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        if (query == null || query.isEmpty()) {
            loadProducts();
        } else {
            productList.setAll(productDAO.searchProducts(query));
            productTable.setItems(productList);
        }
    }

    @FXML
    private void addToCart() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (selected.getStock() <= 0) {
                showAlert("Error", "Out of stock!");
                return;
            }

            // Check if already in cart
            for (TransactionItem item : cartList) {
                if (item.getProductId() == selected.getId()) {
                    if (item.getQuantity() < selected.getStock()) {
                        item.setQuantity(item.getQuantity() + 1);
                        cartTable.refresh();
                        updateTotal();
                    } else {
                        showAlert("Error", "Not enough stock!");
                    }
                    return;
                }
            }

            // Add new item
            TransactionItem newItem = new TransactionItem(0, 0, selected.getId(), 1, selected.getPrice());
            newItem.setProductName(selected.getName());
            cartList.add(newItem);
            updateTotal();
        }
    }

    @FXML
    private void removeFromCart() {
        TransactionItem selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cartList.remove(selected);
            updateTotal();
        }
    }

    private void updateTotal() {
        double total = 0;
        for (TransactionItem item : cartList) {
            total += item.getQuantity() * item.getPriceAtSale();
        }
        // Apply discount if any (percent)
        double discountPercent = 0.0;
        try {
            String txt = discountField.getText();
            if (txt != null && !txt.isEmpty()) {
                discountPercent = Double.parseDouble(txt);
            }
        } catch (NumberFormatException ex) {
            discountPercent = 0.0;
        }

        double finalTotal = total;
        if (discountPercent != 0.0) {
            finalTotal = total * (1 - (discountPercent / 100.0));
        }

        totalLabel.setText(String.format("Rp %.2f", finalTotal));
    }

    @FXML
    private void handleCheckout() {
        if (cartList.isEmpty()) {
            showAlert("Error", "Cart is empty!");
            return;
        }

        double total = 0;
        for (TransactionItem item : cartList) {
            total += item.getQuantity() * item.getPriceAtSale();
        }

        // Create Transaction — use logged-in user as cashier if available
        String cashierName = "System";
        if (App.getCurrentUser() != null && App.getCurrentUser().getUsername() != null) {
            cashierName = App.getCurrentUser().getUsername();
        }

        double discountPercent = 0.0;
        try {
            String txt = discountField.getText();
            if (txt != null && !txt.isEmpty()) {
                discountPercent = Double.parseDouble(txt);
            }
        } catch (NumberFormatException ex) {
            discountPercent = 0.0;
        }

        double finalTotal = total;
        if (discountPercent != 0.0) {
            finalTotal = total * (1 - (discountPercent / 100.0));
        }

        Transaction transaction = new Transaction(0,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                finalTotal, discountPercent, cashierName);

        transaction.setItems(new ArrayList<>(cartList));

        // Save to DB
        transactionDAO.saveTransaction(transaction);

        showAlert("Success", "Transaction completed!");
        cartList.clear();
        updateTotal();
        loadProducts(); // Refresh stock
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
