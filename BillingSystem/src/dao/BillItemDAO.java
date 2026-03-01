package dao;

import model.BillItem;
import database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillItemDAO {
    
    /**
     * Adds a single bill item to the database
     * @param billId Bill ID that this item belongs to
     * @param item BillItem to add
     * @return true on success, false on failure
     */
    public boolean addBillItem(int billId, BillItem item) {
        String sql = "INSERT INTO bill_items (bill_id, product_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, billId);
            pstmt.setInt(2, item.getProductId());
            pstmt.setInt(3, item.getQuantity());
            pstmt.setDouble(4, item.getUnitPrice());
            pstmt.setDouble(5, item.getSubtotal());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error in addBillItem: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Adds a single bill item to the database using provided connection (for transactions)
     * @param conn Database connection
     * @param billId Bill ID that this item belongs to
     * @param item BillItem to add
     * @return true on success, false on failure
     */
    public boolean addBillItem(Connection conn, int billId, BillItem item) {
        String sql = "INSERT INTO bill_items (bill_id, product_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, billId);
            pstmt.setInt(2, item.getProductId());
            pstmt.setInt(3, item.getQuantity());
            pstmt.setDouble(4, item.getUnitPrice());
            pstmt.setDouble(5, item.getSubtotal());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error in addBillItem (transaction): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Adds multiple bill items to the database
     * @param billId Bill ID that these items belong to
     * @param items List of BillItems to add
     * @return true if all items added successfully, false if any fails
     */
    public boolean addAllBillItems(int billId, List<BillItem> items) {
        for (BillItem item : items) {
            if (!addBillItem(billId, item)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Adds multiple bill items to the database using provided connection (for transactions)
     * @param conn Database connection
     * @param billId Bill ID that these items belong to
     * @param items List of BillItems to add
     * @return true if all items added successfully, false if any fails
     */
    public boolean addAllBillItems(Connection conn, int billId, List<BillItem> items) {
        for (BillItem item : items) {
            if (!addBillItem(conn, billId, item)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Retrieves all items for a specific bill using JOIN to get product names
     * @param billId Bill ID
     * @return List of BillItems (never null, empty list if no results)
     */
    public List<BillItem> getItemsByBillId(int billId) {
        List<BillItem> items = new ArrayList<>();
        
        // *** FIX: Use JOIN to get product name in one query ***
        String sql = "SELECT bi.product_id, p.name AS product_name, bi.quantity, bi.unit_price " +
                     "FROM bill_items bi " +
                     "INNER JOIN products p ON bi.product_id = p.id " +
                     "WHERE bi.bill_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, billId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int productId = rs.getInt("product_id");
                    String productName = rs.getString("product_name");
                    int quantity = rs.getInt("quantity");
                    double unitPrice = rs.getDouble("unit_price");
                    
                    BillItem item = new BillItem(productId, productName, quantity, unitPrice);
                    items.add(item);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error in getItemsByBillId: " + e.getMessage());
            e.printStackTrace();
        }
        
        return items;
    }
}