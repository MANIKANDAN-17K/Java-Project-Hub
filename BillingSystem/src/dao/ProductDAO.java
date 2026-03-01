package dao;

import model.Product;
import database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    
    /**
     * Retrieves all products from the database
     * @return List of all products (never null, empty list if no results)
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Product product = new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("quantity")
                );
                products.add(product);
            }
            
        } catch (SQLException e) {
            System.err.println("Error in getAllProducts: " + e.getMessage());
        }
        
        return products;
    }
    
    /**
     * Retrieves a product by its ID
     * @param id Product ID
     * @return Product object or null if not found
     */
    public Product getProductById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                    );
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error in getProductById: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Retrieves a product by its ID using provided connection (for transactions)
     * @param conn Database connection
     * @param id Product ID
     * @return Product object or null if not found
     */
    public Product getProductById(Connection conn, int id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                    );
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error in getProductById (transaction): " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Inserts a new product into the database
     * @param p Product to insert (id will be auto-generated)
     * @return true on success, false on failure
     */
    public boolean insertProduct(Product p) {
        String sql = "INSERT INTO products (name, price, quantity) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, p.getName());
            pstmt.setDouble(2, p.getPrice());
            pstmt.setInt(3, p.getQuantity());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error in insertProduct: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Updates an existing product in the database
     * @param p Product with updated values (must have valid id)
     * @return true on success, false on failure
     */
    public boolean updateProduct(Product p) {
        String sql = "UPDATE products SET name = ?, price = ?, quantity = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, p.getName());
            pstmt.setDouble(2, p.getPrice());
            pstmt.setInt(3, p.getQuantity());
            pstmt.setInt(4, p.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error in updateProduct: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Updates only the stock quantity of a product
     * @param productId Product ID
     * @param newQuantity New quantity value
     * @return true on success, false on failure
     */
    public boolean updateStock(int productId, int newQuantity) {
        String sql = "UPDATE products SET quantity = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, productId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error in updateStock: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Updates only the stock quantity of a product using provided connection (for transactions)
     * @param conn Database connection
     * @param productId Product ID
     * @param newQuantity New quantity value
     * @return true on success, false on failure
     */
    public boolean updateStock(Connection conn, int productId, int newQuantity) {
        String sql = "UPDATE products SET quantity = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, productId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error in updateStock (transaction): " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Deletes a product from the database
     * @param id Product ID to delete
     * @return true on success, false on failure
     */
    public boolean deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error in deleteProduct: " + e.getMessage());
            return false;
        }
    }
}