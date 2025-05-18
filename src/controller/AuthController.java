package controller;

import dao.UserDAO;
import exception.CustomException;
import model.User;

public class AuthController {
    private final UserDAO userDAO;

    public AuthController() {
        userDAO = new UserDAO();
    }

    public User authenticate(String username, String password) throws CustomException {
        try {
            User user = userDAO.getUserByUsername(username);
            System.out.println("Debug - User from DB: " + user);
            if (user != null && user.getPassword().equals(password)) {
                System.out.println("Debug - User login attempt");
                return user;
            }
            return null;
        } catch (Exception e) {
            throw new CustomException("Authentication failed: " + e.getMessage());
        }
    }

    public boolean register(User user) throws CustomException {
        // Check if username already exists
        if (userDAO.getUserByUsername(user.getUsername()) != null) {
            throw new CustomException("Username already exists");
        }

        // Add new user
        return userDAO.addUser(user);
    }
}