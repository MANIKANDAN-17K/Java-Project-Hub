package model;

/**
 * User model class representing a system user (Admin or Cashier)
 */
public class User {
    
    private int id;
    private String username;
    private String password;
    private String role; // "ADMIN" or "CASHIER"
    
    /**
     * Default constructor
     */
    public User() {
    }
    
    /**
     * Constructor without ID (for new users)
     */
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
    
    /**
     * Constructor with all fields
     */
    public User(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        this.password = password;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        if (role == null || (!role.equals("ADMIN") && !role.equals("CASHIER"))) {
            throw new IllegalArgumentException("Role must be ADMIN or CASHIER");
        }
        this.role = role;
    }
    
    /**
     * Check if user is admin
     */
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
    
    /**
     * Check if user is cashier
     */
    public boolean isCashier() {
        return "CASHIER".equals(role);
    }
    
    @Override
    public String toString() {
        return username + " (" + role + ")";
    }
}