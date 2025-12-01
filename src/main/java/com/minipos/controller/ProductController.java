package com.minipos.controller;

import com.minipos.dao.ProductDAO;
import com.minipos.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProductController {

    @FXML
    private TextField searchField;
    @FXML
    private TextField codeField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField stockField;
    @FXML
    private TextField categoryField;

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
    private TableColumn<Product, String> colCategory;

    private ProductDAO productDAO = new ProductDAO();
    private ObservableList<Product> productList = FXCollections.observableArrayList();
    private Product selectedProduct;

    @FXML
    public void initialize() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));

        loadProducts();

        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedProduct = newSelection;
                populateForm(newSelection);
            }
        });
    }

    private void loadProducts() {
        productList.setAll(productDAO.getAllProducts());
        productTable.setItems(productList);
    }

    private void populateForm(Product product) {
        codeField.setText(product.getCode());
        nameField.setText(product.getName());
        priceField.setText(String.valueOf(product.getPrice()));
        stockField.setText(String.valueOf(product.getStock()));
        categoryField.setText(product.getCategory());
    }

    @FXML
    private void handleSave() {
        try {
            String code = codeField.getText();
            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            int stock = Integer.parseInt(stockField.getText());
            String category = categoryField.getText();

            if (selectedProduct == null) {
                // Add new
                Product newProduct = new Product(0, code, name, price, stock, category);
                productDAO.addProduct(newProduct);
            } else {
                // Update existing
                selectedProduct.setCode(code);
                selectedProduct.setName(name);
                selectedProduct.setPrice(price);
                selectedProduct.setStock(stock);
                selectedProduct.setCategory(category);
                productDAO.updateProduct(selectedProduct);
            }

            handleClear();
            loadProducts();
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid input for price or stock.");
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedProduct != null) {
            productDAO.deleteProduct(selectedProduct.getId());
            handleClear();
            loadProducts();
        } else {
            showAlert("Warning", "Please select a product to delete.");
        }
    }

    @FXML
    private void handleClear() {
        selectedProduct = null;
        codeField.clear();
        nameField.clear();
        priceField.clear();
        stockField.clear();
        categoryField.clear();
        productTable.getSelectionModel().clearSelection();
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
    private void handleImportCSV() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Import Products CSV");
        fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("CSV/Text Files", "*.csv", "*.txt"),
                new javafx.stage.FileChooser.ExtensionFilter("All Files", "*.*"));
        java.io.File file = fileChooser.showOpenDialog(productTable.getScene().getWindow());

        if (file != null) {
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
                String line;
                int count = 0;
                while ((line = br.readLine()) != null) {
                    // Simple CSV parsing: code,name,price,stock,category
                    // Assuming no commas in values for now, or basic split
                    String[] parts = line.split(",");
                    if (parts.length >= 5) {
                        try {
                            String code = parts[0].trim();
                            String name = parts[1].trim();
                            double price = Double.parseDouble(parts[2].trim());
                            int stock = Integer.parseInt(parts[3].trim());
                            String category = parts[4].trim();

                            // Skip header if it looks like one
                            if (code.equalsIgnoreCase("code") && name.equalsIgnoreCase("name")) {
                                continue;
                            }

                            Product p = new Product(0, code, name, price, stock, category);
                            productDAO.addProduct(p);
                            count++;
                        } catch (NumberFormatException ignored) {
                            // Skip malformed lines
                        }
                    }
                }
                loadProducts();
                showAlert("Success", "Imported " + count + " products.");
            } catch (java.io.IOException e) {
                showAlert("Error", "Failed to read file: " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
