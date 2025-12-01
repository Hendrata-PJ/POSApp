package com.minipos.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:minipos.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void initializeDatabase() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            // Users table
            String sqlUsers = "CREATE TABLE IF NOT EXISTS users (" +
                    "username TEXT PRIMARY KEY," +
                    "password TEXT NOT NULL," +
                    "role TEXT NOT NULL" +
                    ");";
            stmt.execute(sqlUsers);

            // Products table
            String sqlProducts = "CREATE TABLE IF NOT EXISTS products (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "code TEXT UNIQUE NOT NULL," +
                    "name TEXT NOT NULL," +
                    "price REAL NOT NULL," +
                    "stock INTEGER NOT NULL," +
                    "category TEXT" +
                    ");";
            stmt.execute(sqlProducts);

            // Transactions table
            String sqlTransactions = "CREATE TABLE IF NOT EXISTS transactions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "date TEXT NOT NULL," +
                    "total REAL NOT NULL," +
                    "discount REAL DEFAULT 0," +
                    "cashier_name TEXT" +
                    ");";
            stmt.execute(sqlTransactions);

            // Transaction Items table
            String sqlTransactionItems = "CREATE TABLE IF NOT EXISTS transaction_items (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "transaction_id INTEGER NOT NULL," +
                    "product_id INTEGER NOT NULL," +
                    "quantity INTEGER NOT NULL," +
                    "price_at_sale REAL NOT NULL," +
                    "FOREIGN KEY(transaction_id) REFERENCES transactions(id)," +
                    "FOREIGN KEY(product_id) REFERENCES products(id)" +
                    ");";
            stmt.execute(sqlTransactionItems);
            
            // Seed default admin user if not exists
            // In a real app, passwords should be hashed. For this demo, simple text.
            String checkAdmin = "SELECT username FROM users WHERE username = 'admin'";
            if (!stmt.executeQuery(checkAdmin).next()) {
                String insertAdmin = "INSERT INTO users(username, password, role) VALUES('admin', 'admin123', 'admin')";
                stmt.execute(insertAdmin);
            }

            // Ensure transactions table has discount column for older DBs
            try (java.sql.ResultSet cols = stmt.executeQuery("PRAGMA table_info(transactions);")) {
                boolean hasDiscount = false;
                while (cols.next()) {
                    String name = cols.getString("name");
                    if ("discount".equalsIgnoreCase(name)) {
                        hasDiscount = true;
                        break;
                    }
                }
                if (!hasDiscount) {
                    stmt.execute("ALTER TABLE transactions ADD COLUMN discount REAL DEFAULT 0;");
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
