package service;

import model.Product;
import model.Bill;
import model.BillItem;
import dao.ProductDAO;
import dao.BillDAO;
import dao.BillItemDAO;
import database.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BillingService {
    private ProductDAO productDAO;
    private BillDAO billDAO;
    private BillItemDAO billItemDAO;
    
    public BillingService() {
        this.productDAO = new ProductDAO();
        this.billDAO = new BillDAO();
        this.billItemDAO = new BillItemDAO();
    }
    
    /**
     * Create a new empty bill
     * @return New Bill object
     */
    public Bill createNewBill() {
        return new Bill();
    }
    
    /**
     * Add an item to the current bill
     * Validates product exists and stock is available
     * @param bill Current bill
     * @param productId Product ID to add
     * @param quantity Quantity to add
     * @return "SUCCESS" or error message
     */
    public String addItemToBill(Bill bill, int productId, int quantity) {
        // Get product by productId
        Product product = productDAO.getProductById(productId);
        
        // If null return "Product not found"
        if (product == null) {
            return "Product not found";
        }
        
        // If product.getQuantity() < quantity return insufficient stock message
        if (product.getQuantity() < quantity) {
            return "Insufficient stock. Available: " + product.getQuantity();
        }
        
        // Create new BillItem
        BillItem item = new BillItem(
            product.getId(),
            product.getName(),
            quantity,
            product.getPrice()
        );
        
        // Add item to bill
        bill.addItem(item);
        
        // Return "SUCCESS"
        return "SUCCESS";
    }
    
    /**
     * Calculate the total amount for a bill
     * @param bill Bill to calculate
     * @return Total amount
     */
    public double calculateBillTotal(Bill bill) {
        return bill.calculateTotal();
    }
    
    /**
     * Finalize bill - complete database transaction
     * This method performs a full ACID transaction:
     * 1. Create bill record
     * 2. Add all bill items
     * 3. Update product stock
     * 4. Commit or rollback on failure
     * @param bill Bill to finalize
     * @param paymentMethod Payment method (CASH/CARD/UPI)
     * @return true if successful, false otherwise
     */
    public boolean finalizeBill(Bill bill, String paymentMethod) {
        Connection con = null;
        try {
            // Get connection and start transaction
            con = DBConnection.getConnection();
            con.setAutoCommit(false);
            
            // Set payment method and calculate total
            bill.setPaymentMethod(paymentMethod);
            bill.calculateTotal();
            
            // Step 1: Create bill in database using direct SQL
            String billSql = "INSERT INTO bills (date, total_amount, payment_method) VALUES (?, ?, ?)";
            java.sql.PreparedStatement billStmt = con.prepareStatement(billSql, java.sql.Statement.RETURN_GENERATED_KEYS);
            billStmt.setTimestamp(1, java.sql.Timestamp.valueOf(bill.getDate()));
            billStmt.setDouble(2, bill.getTotalAmount());
            billStmt.setString(3, bill.getPaymentMethod());
            
            int rowsAffected = billStmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Bill creation failed, no rows affected");
            }
            
            // Get generated bill ID
            int billId = -1;
            java.sql.ResultSet generatedKeys = billStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                billId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Bill creation failed, no ID obtained");
            }
            generatedKeys.close();
            billStmt.close();
            
            // Step 2: Add all bill items
            String itemSql = "INSERT INTO bill_items (bill_id, product_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";
            java.sql.PreparedStatement itemStmt = con.prepareStatement(itemSql);
            
            for (BillItem item : bill.getItems()) {
                itemStmt.setInt(1, billId);
                itemStmt.setInt(2, item.getProductId());
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.setDouble(4, item.getUnitPrice());
                itemStmt.setDouble(5, item.getSubtotal());
                itemStmt.executeUpdate();
            }
            itemStmt.close();
            
            // Step 3: Update stock for each product
            String stockSql = "UPDATE products SET quantity = ? WHERE id = ?";
            java.sql.PreparedStatement stockStmt = con.prepareStatement(stockSql);
            
            for (BillItem item : bill.getItems()) {
                // Get current product quantity
                String selectSql = "SELECT quantity FROM products WHERE id = ?";
                java.sql.PreparedStatement selectStmt = con.prepareStatement(selectSql);
                selectStmt.setInt(1, item.getProductId());
                java.sql.ResultSet rs = selectStmt.executeQuery();
                
                if (rs.next()) {
                    int currentQty = rs.getInt("quantity");
                    int newQty = currentQty - item.getQuantity();
                    
                    stockStmt.setInt(1, newQty);
                    stockStmt.setInt(2, item.getProductId());
                    stockStmt.executeUpdate();
                }
                rs.close();
                selectStmt.close();
            }
            stockStmt.close();
            
            // Commit transaction
            con.commit();
            
            // Set bill ID in the bill object
            bill.setBillId(billId);
            
            return true;
            
        } catch (SQLException e) {
            // Rollback on any error
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ex) {
                // Ignore rollback errors
            }
            
            System.err.println("finalizeBill error: " + e.getMessage());
            e.printStackTrace();
            return false;
            
        } finally {
            // Restore auto-commit mode
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                }
            } catch (SQLException e) {
                // Ignore
            }
        }
    }
    
    /**
     * Get bill items formatted for JTable display
     * @param bill Bill containing items
     * @return 2D String array with bill item data
     */
    public String[][] getBillItemsTableData(Bill bill) {
        List<BillItem> items = bill.getItems();
        String[][] data = new String[items.size()][];
        
        for (int i = 0; i < items.size(); i++) {
            data[i] = items.get(i).toTableRow();
        }
        
        return data;
    }
    
    /**
     * Get all bills from database
     * @return List of all bills
     */
    public List<Bill> getAllBills() {
        return billDAO.getAllBills();
    }
}