package com.minipos.dao;

import com.minipos.model.Transaction;
import com.minipos.model.TransactionItem;
import com.minipos.util.DatabaseHelper;

import java.sql.*;
import java.util.List;

public class TransactionDAO {

    public void saveTransaction(Transaction transaction) {
        String sqlTrans = "INSERT INTO transactions(date, total, cashier_name) VALUES(?,?,?)";
        String sqlItem = "INSERT INTO transaction_items(transaction_id, product_id, quantity, price_at_sale) VALUES(?,?,?,?)";
        String sqlUpdateStock = "UPDATE products SET stock = stock - ? WHERE id = ?";

        Connection conn = null;
        try {
            conn = DatabaseHelper.connect();
            conn.setAutoCommit(false); // Start transaction

            // Insert Transaction
            int transId = -1;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlTrans, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, transaction.getDate());
                pstmt.setDouble(2, transaction.getTotal());
                pstmt.setString(3, transaction.getCashierName());
                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        transId = rs.getInt(1);
                    }
                }
            }

            if (transId != -1) {
                // Insert Items and Update Stock
                try (PreparedStatement pstmtItem = conn.prepareStatement(sqlItem);
                     PreparedStatement pstmtStock = conn.prepareStatement(sqlUpdateStock)) {
                    
                    for (TransactionItem item : transaction.getItems()) {
                        // Insert Item
                        pstmtItem.setInt(1, transId);
                        pstmtItem.setInt(2, item.getProductId());
                        pstmtItem.setInt(3, item.getQuantity());
                        pstmtItem.setDouble(4, item.getPriceAtSale());
                        pstmtItem.addBatch();

                        // Update Stock
                        pstmtStock.setInt(1, item.getQuantity());
                        pstmtStock.setInt(2, item.getProductId());
                        pstmtStock.addBatch();
                    }
                    pstmtItem.executeBatch();
                    pstmtStock.executeBatch();
                }
            }

            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new java.util.ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY id DESC";

        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                transactions.add(new Transaction(
                        rs.getInt("id"),
                        rs.getString("date"),
                        rs.getDouble("total"),
                        rs.getString("cashier_name")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return transactions;
    }
}
