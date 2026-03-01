package service;

import model.User;
import dao.UserDAO;

/**
 * Authentication Service for user login and management
 */
public class AuthService {
    
    private UserDAO userDAO;
    private User currentUser;
    
    public AuthService() {
        this.userDAO = new UserDAO();
        this.currentUser = null;
    }
    
    /**
     * Login user with username and password
     * 
     * @param username Username
     * @param password Password
     * @return true if login successful, false otherwise
     */
    public boolean login(String username, String password) {
        // Validate inputs
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        
        // Authenticate
        User user = userDAO.authenticate(username.trim(), password);
        
        if (user != null) {
            this.currentUser = user;
            return true;
        }
        
        return false;
    }
    
    /**
     * Logout current user
     */
    public void logout() {
        this.currentUser = null;
    }
    
    /**
     * Get currently logged in user
     * 
     * @return Current user or null if not logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if user is logged in
     * 
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Check if current user is admin
     * 
     * @return true if admin, false otherwise
     */
    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }
    
    /**
     * Check if current user is cashier
     * 
     * @return true if cashier, false otherwise
     */
    public boolean isCashier() {
        return currentUser != null && currentUser.isCashier();
    }
    
    /**
     * Get current user's role
     * 
     * @return Role string or null if not logged in
     */
    public String getCurrentRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }
    
    /**
     * Change password for current user
     * 
     * @param oldPassword Old password for verification
     * @param newPassword New password
     * @return true if successful, false otherwise
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (currentUser == null) {
            return false;
        }
        
        // Verify old password
        if (!currentUser.getPassword().equals(oldPassword)) {
            return false;
        }
        
        // Validate new password
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return false;
        }
        
        // Update password
        boolean success = userDAO.updatePassword(currentUser.getUsername(), newPassword);
        
        if (success) {
            currentUser.setPassword(newPassword);
        }
        
        return success;
    }
}