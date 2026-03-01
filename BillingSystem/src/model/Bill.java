package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Bill model class representing a complete bill with multiple items
 * Worker 1 - Model Layer
 */
public class Bill {
    private int billId;
    private LocalDateTime date;
    private List<BillItem> items;
    private double totalAmount;
    private String paymentMethod;

    // Default constructor - initialize items list and set current date
    public Bill() {
        this.items = new ArrayList<>();
        this.date = LocalDateTime.now();
    }

    // Add an item to the bill
    public void addItem(BillItem item) {
        items.add(item);
    }

    // Calculate total from all item subtotals
    public double calculateTotal() {
        totalAmount = 0.0;
        for (BillItem item : items) {
            totalAmount += item.getSubtotal();
        }
        return totalAmount;
    }

    // Get the list of items
    public List<BillItem> getItems() {
        return items;
    }

    // Check if bill is empty
    public boolean isEmpty() {
        return items.isEmpty();
    }

    // Getters
    public int getBillId() {
        return billId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    // Setters
    public void setBillId(int billId) {
        this.billId = billId;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}