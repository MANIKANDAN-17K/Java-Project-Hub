package com;

import database.DBConnection;
import gui.LoginFrame;
import javax.swing.SwingUtilities;

/**
 * Main entry point for the Supermarket Billing System
 */
public class Main {
    
    public static void main(String[] args) {
        // Test database connection
        System.out.println("Connecting to database...");
        
        if (!DBConnection.testConnection()) {
            System.err.println("FATAL: Cannot connect to database.");
            System.err.println("Please check:");
            System.err.println("1. MySQL server is running");
            System.err.println("2. Database 'billing_system' exists (run schema.sql)");
            System.err.println("3. Username and password in DBConnection.java are correct");
            System.exit(1);
        }
        
        System.out.println("Database connected successfully.");
        System.out.println("Starting application...");
        
        // Launch login window
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            }
        });
    }
}