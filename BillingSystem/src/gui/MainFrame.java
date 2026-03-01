package gui;

import service.InventoryService;
import service.BillingService;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main Application Frame with role-based access control
 */
public class MainFrame extends JFrame {
    
    private User currentUser;
    private InventoryService inventoryService;
    private BillingService billingService;
    
    /**
     * Constructor that accepts logged-in user
     */
    public MainFrame(User user) {
        this.currentUser = user;
        this.inventoryService = new InventoryService();
        this.billingService = new BillingService();
        
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Supermarket Billing System - " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create menu bar
        createMenuBar();
        
        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Add tabs based on role
        // ADMIN sees: Products, Billing, Reports (3 tabs)
        // CASHIER sees: Billing, Reports (2 tabs)
        
        System.out.println("Current user: " + currentUser.getUsername());
        System.out.println("Current role: " + currentUser.getRole());
        System.out.println("Is admin: " + currentUser.isAdmin());
        
        if (currentUser.isAdmin()) {
            System.out.println("Adding Products tab for admin...");
            tabbedPane.addTab("Products", new ProductPanel(inventoryService));
        }
        tabbedPane.addTab("Products", new ProductPanel(inventoryService));
        // Billing tab (available for all users)
        tabbedPane.addTab("Billing", new BillingPanel(inventoryService, billingService));
        
        // Reports tab (available for all users)
        tabbedPane.addTab("Reports", new ReportPanel(billingService));
        
        add(tabbedPane);
        
        setVisible(true);
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
            }
        });
        
        helpMenu.add(aboutItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            // Close current window
            dispose();
            
            // Open login window
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    LoginFrame loginFrame = new LoginFrame();
                    loginFrame.setVisible(true);
                }
            });
        }
    }
    
    private void showAboutDialog() {
        String message = "Supermarket Billing System\n\n" +
                        "Version: 1.0\n" +
                        "Current User: " + currentUser.getUsername() + "\n" +
                        "Role: " + currentUser.getRole() + "\n\n" +
                        "Features:\n" +
                        "- Product Management (Admin only)\n" +
                        "- Billing & POS\n" +
                        "- Sales Reports\n" +
                        "- User Authentication";
        
        JOptionPane.showMessageDialog(
            this,
            message,
            "About",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}