package com.minipos.controller;

import com.minipos.dao.TransactionDAO;
import com.minipos.model.ProductSales;
import com.minipos.model.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardController {

    @FXML private Label dailyRevenueLabel;
    @FXML private Label transactionsCountLabel;
    @FXML private Label avgTicketLabel;
    @FXML private Label lowStockLabel;

    @FXML private TableView<ProductSales> topProductsTable;
    @FXML private TableColumn<ProductSales, String> colProdName;
    @FXML private TableColumn<ProductSales, Integer> colProdQty;
    @FXML private TableColumn<ProductSales, Double> colProdRevenue;

    @FXML private TableView<Transaction> recentTransTable;
    @FXML private TableColumn<Transaction, Integer> colTransId;
    @FXML private TableColumn<Transaction, String> colTransDate;
    @FXML private TableColumn<Transaction, Double> colTransTotal;

    private TransactionDAO transactionDAO = new TransactionDAO();

    @FXML
    public void initialize() {
        // Setup table columns
        colProdName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colProdQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colProdRevenue.setCellValueFactory(new PropertyValueFactory<>("revenue"));

        colTransId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTransDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTransTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        loadMetrics();
    }

    @FXML
    public void loadMetrics() {
        String todayPrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        double revenue = transactionDAO.getDailyRevenue(todayPrefix);
        int txCount = transactionDAO.getTransactionsCountToday(todayPrefix);

        double avgTicket = txCount > 0 ? (revenue / txCount) : 0.0;

        dailyRevenueLabel.setText(String.format("Rp %.2f", revenue));
        transactionsCountLabel.setText(String.valueOf(txCount));
        avgTicketLabel.setText(String.format("Rp %.2f", avgTicket));

        // Top products
        List<ProductSales> top = transactionDAO.getTopProducts(todayPrefix, 10);
        ObservableList<ProductSales> topList = FXCollections.observableArrayList(top);
        topProductsTable.setItems(topList);

        // Recent transactions
        List<Transaction> recent = transactionDAO.getRecentTransactions(10);
        ObservableList<Transaction> recentList = FXCollections.observableArrayList(recent);
        recentTransTable.setItems(recentList);
    }
}
