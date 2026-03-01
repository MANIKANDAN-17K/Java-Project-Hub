package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection manager using Singleton pattern.
 * Manages a single shared connection to the MySQL database.
 */
public class DBConnection {
    
    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/billing_system";
    private static final String USERNAME = "BillingSystem";
    private static final String PASSWORD = "BillingSystem@123";
    
    // Shared connection instance
    private static Connection connection = null;
    
    /**
     * Private constructor to prevent instantiation
     */
    private DBConnection() {
        // Singleton pattern - prevent instantiation
    }
    
    /**
     * Gets the database connection. Creates a new connection if one doesn't exist
     * or if the existing connection is closed.
     * 
     * @return Connection object to the database
     * @throws SQLException if connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        // Check if connection is null or closed
        if (connection == null || connection.isClosed()) {
            try {
                // Load MySQL JDBC driver (optional for newer versions)
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
            }
            
            // Create new connection
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }
        
        return connection;
    }
    
    /**
     * Closes the database connection safely.
     * Catches any SQLException internally and prints error message.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Tests the database connection.
     * 
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        try {
            // Attempt to get connection
            Connection conn = getConnection();
            
            // If we got here, connection is successful
            return conn != null && !conn.isClosed();
            
        } catch (SQLException e) {
            // Print error message
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}