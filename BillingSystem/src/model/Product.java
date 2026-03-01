package model;

/**
 * Product model class representing a product in the inventory
 * Worker 1 - Model Layer
 */
public class Product {
    private int id;
    private String name;
    private double price;
    private int quantity;

    // Default constructor
    public Product() {
    }

    // Constructor without id (for new products)
    public Product(String name, double price, int quantity) {
        setName(name);
        setPrice(price);
        setQuantity(quantity);
    }

    // Constructor with all fields (for existing products from database)
    public Product(int id, String name, double price, int quantity) {
        this.id = id;
        setName(name);
        setPrice(price);
        // Allow negative quantities from database (data correction scenario)
        // but clamp to 0 to prevent display issues
        this.quantity = Math.max(0, quantity);
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setters with validation
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
    }

    public void setPrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        this.price = price;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }

    // Convert to table row for GUI display
    public String[] toTableRow() {
        return new String[]{
            String.valueOf(id),
            name,
            String.valueOf(price),
            String.valueOf(quantity)
        };
    }

    // String representation for display
    @Override
    public String toString() {
        return id + " - " + name + " (Rs." + price + ")";
    }
}