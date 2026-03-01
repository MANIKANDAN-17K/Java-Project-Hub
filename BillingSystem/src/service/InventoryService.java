
package service;

import model.Product;
import dao.ProductDAO;
import java.util.List;

public class InventoryService {
    private ProductDAO productDAO;
    
    public InventoryService() {
        this.productDAO = new ProductDAO();
    }
    
    /**
     * Get all products from database
     * @return List of all products
     */
    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }
    
    /**
     * Find a product by its ID
     * @param id Product ID
     * @return Product object or null if not found
     */
    public Product findProductById(int id) {
        return productDAO.getProductById(id);
    }
    
    /**
     * Add a new product to inventory
     * Validates all inputs before calling DAO
     * @param name Product name
     * @param price Product price
     * @param quantity Product quantity
     * @return true if successful, false otherwise
     */
    public boolean addProduct(String name, double price, int quantity) {
        // Validate name not null/empty
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        // Validate price <= 0
        if (price <= 0) {
            return false;
        }
        
        // Validate quantity < 0
        if (quantity < 0) {
            return false;
        }
        
        // Call DAO to insert product
        return productDAO.insertProduct(new Product(name.trim(), price, quantity));
    }
    
    /**
     * Update an existing product
     * Validates all inputs before calling DAO
     * @param p Product object with updated values
     * @return true if successful, false otherwise
     */
    public boolean updateProduct(Product p) {
        // Validate same rules as addProduct
        if (p.getName() == null || p.getName().trim().isEmpty()) {
            return false;
        }
        
        if (p.getPrice() <= 0) {
            return false;
        }
        
        if (p.getQuantity() < 0) {
            return false;
        }
        
        // Call DAO to update product
        return productDAO.updateProduct(p);
    }
    
    /**
     * Delete a product by ID
     * @param id Product ID
     * @return true if successful, false otherwise
     */
    public boolean deleteProduct(int id) {
        return productDAO.deleteProduct(id);
    }
    
    /**
     * Check if sufficient stock is available for a product
     * @param productId Product ID
     * @param requestedQty Requested quantity
     * @return true if stock available, false otherwise
     */
    public boolean isStockAvailable(int productId, int requestedQty) {
        Product product = productDAO.getProductById(productId);
        
        // Return false if product is null
        if (product == null) {
            return false;
        }
        
        // Check if available quantity >= requested quantity
        return product.getQuantity() >= requestedQty;
    }
    
    /**
     * Get product data formatted for JTable display
     * @return 2D String array with product data
     */
    public String[][] getProductTableData() {
        List<Product> products = getAllProducts();
        String[][] data = new String[products.size()][];
        
        for (int i = 0; i < products.size(); i++) {
            data[i] = products.get(i).toTableRow();
        }
        
        return data;
    }
}