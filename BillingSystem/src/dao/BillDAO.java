package dao;

import model.Bill;
import model.BillItem;
import database.DBConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {
    
    private BillItemDAO billItemDAO;
    
    public BillDAO() {
        this.billItemDAO = new BillItemDAO();
    }
    
    /**
     * Creates a new bill in the database
     * @param bill Bill object with date, totalAmount, and paymentMethod
     * @return Generated bill_id on success, -1 on failure
     */
    public int createBill(Bill bill) {
        String sql = "INSERT INTO bills (date, total_amount, payment_method) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(bill.getDate()));
            pstmt.setDouble(2, bill.getTotalAmount());
            pstmt.setString(3, bill.getPaymentMethod());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error in createBill: " + e.getMessage());
        }
        
        return -1;
    }
    
    /**
     * Creates a new bill using provided connection (for transactions)
     */
    public int createBill(Connection conn, Bill bill) {
        String sql = "INSERT INTO bills (date, total_amount, payment_method) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(bill.getDate()));
            pstmt.setDouble(2, bill.getTotalAmount());
            pstmt.setString(3, bill.getPaymentMethod());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error in createBill (transaction): " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
    
    /**
     * Retrieves a bill by its ID
     */
    public Bill getBillById(int billId) {
        String sql = "SELECT * FROM bills WHERE bill_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, billId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Bill bill = new Bill();
                    bill.setBillId(rs.getInt("bill_id"));
                    bill.setDate(rs.getTimestamp("date").toLocalDateTime());
                    bill.setTotalAmount(rs.getDouble("total_amount"));
                    bill.setPaymentMethod(rs.getString("payment_method"));
                    
                    // Load bill items
                    List<BillItem> items = billItemDAO.getItemsByBillId(billId);
                    for (BillItem item : items) {
                        bill.addItem(item);
                    }
                    
                    return bill;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error in getBillById: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Retrieves all bills - FIXED VERSION
     * Collects all bill data FIRST, then loads items AFTER closing the main ResultSet
     */
    public List<Bill> getAllBills() {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills ORDER BY date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            // STEP 1: Collect all basic bill data FIRST (without loading items yet)
            System.out.println("DEBUG: Starting to load bills...");
            
            while (rs.next()) {
                Bill bill = new Bill();
                bill.setBillId(rs.getInt("bill_id"));
                bill.setDate(rs.getTimestamp("date").toLocalDateTime());
                bill.setTotalAmount(rs.getDouble("total_amount"));
                bill.setPaymentMethod(rs.getString("payment_method"));
                
                bills.add(bill);
                System.out.println("DEBUG: Loaded bill ID: " + bill.getBillId());
            }
            
            System.out.println("DEBUG: Total bills loaded: " + bills.size());
            
        } catch (SQLException e) {
            System.err.println("Error in getAllBills: " + e.getMessage());
            e.printStackTrace();
        }
        
        // STEP 2: Now load items for each bill (AFTER the main ResultSet is closed)
        System.out.println("DEBUG: Now loading items for each bill...");
        for (Bill bill : bills) {
            try {
                List<BillItem> items = billItemDAO.getItemsByBillId(bill.getBillId());
                for (BillItem item : items) {
                    bill.addItem(item);
                }
                System.out.println("DEBUG: Loaded " + items.size() + " items for bill " + bill.getBillId());
            } catch (Exception e) {
                System.err.println("Error loading items for bill " + bill.getBillId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("DEBUG: Finished loading all bills and items");
        return bills;
    }
}