package com.minipos.model;

public class TransactionItem {
    private int id;
    private int transactionId;
    private int productId;
    private int quantity;
    private double priceAtSale;
    private String productName; // For display convenience

    public TransactionItem(int id, int transactionId, int productId, int quantity, double priceAtSale) {
        this.id = id;
        this.transactionId = transactionId;
        this.productId = productId;
        this.quantity = quantity;
        this.priceAtSale = priceAtSale;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPriceAtSale() { return priceAtSale; }
    public void setPriceAtSale(double priceAtSale) { this.priceAtSale = priceAtSale; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
}
