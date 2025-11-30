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

    private TransactionDAO transactionDAO = new TransactionDAO();
    private ObservableList<Transaction> transactionList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colCashier.setCellValueFactory(new PropertyValueFactory<>("cashierName"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        loadHistory();
    }

    private void loadHistory() {
        transactionList.setAll(transactionDAO.getAllTransactions());
        historyTable.setItems(transactionList);
    }
}
