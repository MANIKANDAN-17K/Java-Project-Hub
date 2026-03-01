package gui;

import service.InventoryService;
import model.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ProductPanel extends JPanel {
    
    private InventoryService inventoryService;
    
    // Components
    private JTable productsTable;
    private JTextField nameField;
    private JTextField priceField;
    private JTextField quantityField;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    public ProductPanel(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create table
        productsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(productsTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Product Name:"), gbc);
        
        gbc.gridx = 1;
        nameField = new JTextField(20);
        inputPanel.add(nameField, gbc);
        
        // Price field
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Price (Rs):"), gbc);
        
        gbc.gridx = 1;
        priceField = new JTextField(20);
        inputPanel.add(priceField, gbc);
        
        // Quantity field
        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Quantity:"), gbc);
        
        gbc.gridx = 1;
        quantityField = new JTextField(20);
        inputPanel.add(quantityField, gbc);
        
        add(inputPanel, BorderLayout.NORTH);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        addButton = new JButton("Add Product");
        updateButton = new JButton("Update Product");
        deleteButton = new JButton("Delete Product");
        refreshButton = new JButton("Refresh");
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add button listeners
        addButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());
        refreshButton.addActionListener(e -> loadProducts());
        
        // Load initial data
        loadProducts();
    }
    
    private void loadProducts() {
        String[][] data = inventoryService.getProductTableData();
        String[] columns = {"ID", "Name", "Price", "Qty"};
        productsTable.setModel(new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }
    
    private void addProduct() {
        try {
            String name = nameField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());
            
            boolean success = inventoryService.addProduct(name, price, quantity);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Product added!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadProducts();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed. Check inputs.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid price or quantity format!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateProduct() {
        try {
            int selectedRow = productsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a product to update!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int id = Integer.parseInt(productsTable.getValueAt(selectedRow, 0).toString());
            String name = nameField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());
            
            Product product = new Product(id, name, price, quantity);
            boolean success = inventoryService.updateProduct(product);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadProducts();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed. Check inputs.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid price or quantity format!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteProduct() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int id = Integer.parseInt(productsTable.getValueAt(selectedRow, 0).toString());
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this product?", 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = inventoryService.deleteProduct(id);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, "Deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadProducts();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete product!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid product ID!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearFields() {
        nameField.setText("");
        priceField.setText("");
        quantityField.setText("");
    }
}