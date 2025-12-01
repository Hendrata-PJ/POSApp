package com.minipos.model;

public class ProductSales {
    private String name;
    private int quantity;
    private double revenue;

    public ProductSales(String name, int quantity, double revenue) {
        this.name = name;
        this.quantity = quantity;
        this.revenue = revenue;
    }

    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public double getRevenue() { return revenue; }
}
