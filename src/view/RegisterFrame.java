package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import controller.AuthController;
import model.User;

public class RegisterFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private final AuthController authController;

    public RegisterFrame() {
        authController = new AuthController();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Library System - Registration");
        setSize(400, 250);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header Panel
        JPanel headerPanel = new JPanel();
        JLabel lblTitle = new JLabel("User Registration");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(lblTitle);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();

        formPanel.add(new JLabel("Username:"));
        formPanel.add(txtUsername);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(txtPassword);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        JButton btnRegister = new JButton("Register");
        JButton btnCancel = new JButton("Cancel");

        btnRegister.addActionListener(this::performRegistration);
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnRegister);
        buttonPanel.add(btnCancel);

        formPanel.add(new JLabel()); // Empty cell
        formPanel.add(buttonPanel);

        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
    }

    private void performRegistration(ActionEvent e) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        // Simple input validation
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username and password cannot be empty!",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Create new user with "user" role by default
            User newUser = new User(username, password, "user");

            if (authController.register(newUser)) {
                JOptionPane.showMessageDialog(this,
                        "Registration successful! Please login with your new account.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new LoginFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Registration failed.",
                        "Registration Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error during registration: " + ex.getMessage(),
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}