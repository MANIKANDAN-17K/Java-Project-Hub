
package model;

/**
 * BillItem model class representing a single item in a bill
 * Worker 1 - Model Layer
 */
public class BillItem {
    private int productId;
    private String productName;
    private int quantity;
    private double unitPrice;
    private double subtotal;

    // Constructor - subtotal is calculated automatically
    public BillItem(int productId, String productName, int quantity, double unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = quantity * unitPrice;
    }

    // Getters
    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getSubtotal() {
        return subtotal;
    }

    // Setter for quantity - must recalculate subtotal
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.subtotal = quantity * unitPrice;
    }

    // Convert to table row for GUI display
    public String[] toTableRow() {
        return new String[]{
            productName,
            String.valueOf(quantity),
            String.valueOf(unitPrice),
            String.valueOf(subtotal)
        };
    }
}