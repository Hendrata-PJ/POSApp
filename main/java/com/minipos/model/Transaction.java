package com.minipos.model;

import java.util.List;

public class Transaction {
    private int id;
    private String date;
    private double total;
    private String cashierName;
    private double discount;
    private List<TransactionItem> items;

    public Transaction(int id, String date, double total, String cashierName) {
        this(id, date, total, 0.0, cashierName);
    }

    public Transaction(int id, String date, double total, double discount, String cashierName) {
        this.id = id;
        this.date = date;
        this.total = total;
        this.discount = discount;
        this.cashierName = cashierName;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public String getCashierName() { return cashierName; }
    public void setCashierName(String cashierName) { this.cashierName = cashierName; }

    public List<TransactionItem> getItems() { return items; }
    public void setItems(List<TransactionItem> items) { this.items = items; }
}
