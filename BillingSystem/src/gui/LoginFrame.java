package gui;

import service.AuthService;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Login Frame for user authentication
 */
public class LoginFrame extends JFrame {
    
    private AuthService authService;
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;
    private JLabel statusLabel;
    
    public LoginFrame() {
        this.authService = new AuthService();
        
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Supermarket Billing System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(new Color(240, 240, 240));
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(240, 240, 240));
        JLabel titleLabel = new JLabel("SUPERMARKET BILLING SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));
        titlePanel.add(titleLabel);
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username label and field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(usernameField, gbc);
        
        // Password label and field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(passwordField, gbc);
        
        // Status label
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(Color.RED);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(statusLabel, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));
        
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(0, 153, 76));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(100, 35));
        
        exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 14));
        exitButton.setBackground(new Color(204, 51, 51));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setPreferredSize(new Dimension(100, 35));
        
        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);
        
        // Info panel (default credentials)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(240, 240, 240));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        JLabel infoLabel1 = new JLabel("Default Login Credentials:");
        infoLabel1.setFont(new Font("Arial", Font.PLAIN, 11));
        infoLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel infoLabel2 = new JLabel("Admin: admin / admin123");
        infoLabel2.setFont(new Font("Arial", Font.PLAIN, 11));
        infoLabel2.setForeground(new Color(100, 100, 100));
        infoLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel infoLabel3 = new JLabel("Cashier: cashier / cashier123");
        infoLabel3.setFont(new Font("Arial", Font.PLAIN, 11));
        infoLabel3.setForeground(new Color(100, 100, 100));
        infoLabel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        infoPanel.add(infoLabel1);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(infoLabel2);
        infoPanel.add(infoLabel3);
        
        // Add all panels to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Create a wrapper to add info panel at bottom
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(mainPanel, BorderLayout.CENTER);
        wrapperPanel.add(infoPanel, BorderLayout.SOUTH);
        
        add(wrapperPanel);
        
        // Add action listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        // Allow Enter key to login
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        // Clear status
        statusLabel.setText(" ");
        
        // Validate inputs
        if (username.isEmpty()) {
            statusLabel.setText("Please enter username");
            usernameField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            statusLabel.setText("Please enter password");
            passwordField.requestFocus();
            return;
        }
        
        // Disable button during login
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");
        
        // Attempt login
        if (authService.login(username, password)) {
            // Login successful
            User currentUser = authService.getCurrentUser();
            
            JOptionPane.showMessageDialog(
                this,
                "Welcome, " + currentUser.getUsername() + "!\nRole: " + currentUser.getRole(),
                "Login Successful",
                JOptionPane.INFORMATION_MESSAGE
            );
            
            // Open main application
            openMainApplication(currentUser);
            
            // Close login window
            dispose();
            
        } else {
            // Login failed
            statusLabel.setText("Invalid username or password");
            passwordField.setText("");
            loginButton.setEnabled(true);
            loginButton.setText("Login");
            passwordField.requestFocus();
        }
    }
    
    private void openMainApplication(User user) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame(user);
            }
        });
    }
}