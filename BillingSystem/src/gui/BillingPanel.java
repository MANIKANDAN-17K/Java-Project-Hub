package gui;

import service.InventoryService;
import service.BillingService;
import model.Bill;
import model.BillItem;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class BillingPanel extends JPanel {
    
    private InventoryService inventoryService;
    private BillingService billingService;
    private Bill currentBill;
    
    // Components
    private JTextField productIdField;
    private JTextField quantityField;
    private JButton addToCartButton;
    private JTable cartTable;
    private JLabel totalLabel;
    private JComboBox<String> paymentCombo;
    private JButton checkoutButton;
    private JButton newBillButton;
    
    public BillingPanel(InventoryService inventoryService, BillingService billingService) {
        this.inventoryService = inventoryService;
        this.billingService = billingService;
        this.currentBill = billingService.createNewBill();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create top panel for adding items
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Add Items to Cart"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Product ID field
        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(new JLabel("Product ID:"), gbc);
        
        gbc.gridx = 1;
        productIdField = new JTextField(15);
        topPanel.add(productIdField, gbc);
        
        // Quantity field
        gbc.gridx = 2;
        topPanel.add(new JLabel("Quantity:"), gbc);
        
        gbc.gridx = 3;
        quantityField = new JTextField(10);
        topPanel.add(quantityField, gbc);
        
        // Add to cart button
        gbc.gridx = 4;
        addToCartButton = new JButton("Add to Cart");
        topPanel.add(addToCartButton, gbc);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Create center panel for cart table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));
        
        cartTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(cartTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Create bottom panel for checkout
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        
        // Total label
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalLabel = new JLabel("Total: Rs. 0.0");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalPanel.add(totalLabel);
        bottomPanel.add(totalPanel, BorderLayout.NORTH);
        
        // Payment and checkout panel
        JPanel checkoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        checkoutPanel.add(new JLabel("Payment Method:"));
        paymentCombo = new JComboBox<>(new String[]{"CASH", "CARD", "UPI"});
        checkoutPanel.add(paymentCombo);
        
        checkoutButton = new JButton("Checkout");
        checkoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        checkoutButton.setBackground(new Color(34, 139, 34));
        checkoutButton.setForeground(Color.WHITE);
        checkoutPanel.add(checkoutButton);
        
        newBillButton = new JButton("New Bill");
        newBillButton.setFont(new Font("Arial", Font.BOLD, 14));
        checkoutPanel.add(newBillButton);
        
        bottomPanel.add(checkoutPanel, BorderLayout.CENTER);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Add button listeners
        addToCartButton.addActionListener(e -> addItemToCart());
        checkoutButton.addActionListener(e -> checkout());
        newBillButton.addActionListener(e -> newBill());
        
        // Initialize cart table
        refreshCartTable();
    }
    
    private void addItemToCart() {
        try {
            int productId = Integer.parseInt(productIdField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());
            
            String result = billingService.addItemToBill(currentBill, productId, quantity);
            
            if (result.equals("SUCCESS")) {
                refreshCartTable();
                double total = billingService.calculateBillTotal(currentBill);
                totalLabel.setText("Total: Rs. " + String.format("%.2f", total));
                
                // Clear input fields
                productIdField.setText("");
                quantityField.setText("");
                
                JOptionPane.showMessageDialog(this, "Item added to cart!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, result, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid product ID or quantity format!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void checkout() {
        if (currentBill.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String paymentMethod = (String) paymentCombo.getSelectedItem();
        boolean success = billingService.finalizeBill(currentBill, paymentMethod);
        
        if (success) {
            showReceipt(currentBill);
            
            // Reset for new bill
            currentBill = billingService.createNewBill();
            refreshCartTable();
            totalLabel.setText("Total: Rs. 0.0");
            
            JOptionPane.showMessageDialog(this, "Checkout completed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Checkout failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void newBill() {
        if (!currentBill.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Current cart will be cleared. Continue?", 
                "Confirm", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        currentBill = billingService.createNewBill();
        refreshCartTable();
        totalLabel.setText("Total: Rs. 0.0");
        productIdField.setText("");
        quantityField.setText("");
    }
    
    private void refreshCartTable() {
        String[][] data = billingService.getBillItemsTableData(currentBill);
        String[] columns = {"Product", "Qty", "Unit Price", "Subtotal"};
        cartTable.setModel(new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }
    
    private void showReceipt(Bill bill) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("=======================================\n");
        receipt.append("       SUPERMARKET RECEIPT\n");
        receipt.append("=======================================\n\n");
        
        receipt.append("Bill ID: ").append(bill.getBillId()).append("\n");
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        receipt.append("Date: ").append(bill.getDate().format(formatter)).append("\n");
        receipt.append("Payment: ").append(bill.getPaymentMethod()).append("\n\n");
        
        receipt.append("---------------------------------------\n");
        receipt.append(String.format("%-20s %5s %8s %10s\n", "Product", "Qty", "Price", "Subtotal"));
        receipt.append("---------------------------------------\n");
        
        for (BillItem item : bill.getItems()) {
            receipt.append(String.format("%-20s %5d %8.2f %10.2f\n", 
                item.getProductName(), 
                item.getQuantity(), 
                item.getUnitPrice(), 
                item.getSubtotal()));
        }
        
        receipt.append("---------------------------------------\n");
        receipt.append(String.format("%-34s %10.2f\n", "TOTAL:", bill.getTotalAmount()));
        receipt.append("=======================================\n");
        receipt.append("\n       Thank you for shopping!\n");
        receipt.append("=======================================\n");
        
        JTextArea textArea = new JTextArea(receipt.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(450, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Receipt", JOptionPane.INFORMATION_MESSAGE);
    }
}