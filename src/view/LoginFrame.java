package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import controller.AuthController;
import exception.CustomException;
import model.User;
import util.Session;
import util.ValidationHelper;


public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private final AuthController authController;

    public LoginFrame() {
        authController = new AuthController();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Library System Login");
        setSize(400, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header Panel
        JPanel headerPanel = new JPanel();
        JLabel lblTitle = new JLabel("Library Management System");
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
        JButton btnLogin = new JButton("Login");
        JButton btnRegister = new JButton("Register");

        btnLogin.addActionListener(this::performLogin);
        btnRegister.addActionListener(this::showRegistrationForm);

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);

        formPanel.add(new JLabel()); // Empty cell
        formPanel.add(buttonPanel);

        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
    }

    private void performLogin(ActionEvent e) {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        // Input validation
        if (!ValidationHelper.validateLoginInput(username, password)) {
            JOptionPane.showMessageDialog(this,
                    "Username and password are required!",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User authenticatedUser = authController.authenticate(username, password);
            System.out.println("Debug - Trying to pass auth");
            if (authenticatedUser != null) {
                System.out.println("Debug - Logged in succeed");
                Session.setCurrentUser(authenticatedUser);
                System.out.println("Debug - set session user");
                redirectToDashboard();
                System.out.println("Debug - redirect to dashboard");
                dispose();
                System.out.println("Debug - dispose login frame");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password!",
                        "Authentication Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            System.out.println("Debug - Error" + ex);
            JOptionPane.showMessageDialog(this,
                    "Error connecting to database: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void redirectToDashboard() throws CustomException {
        User currentUser = Session.getCurrentUser();
        if (currentUser.getRole().equalsIgnoreCase("admin")) {
            System.out.println("Debug - redirect to admin dashboard");
            new AdminDashboard().setVisible(true);
            System.out.println("Debug - admin dashboard visible");
        } else {
            new UserDashboard(currentUser).setVisible(true);
        }
    }

//    Kode registrasi disni laude ---------------------------->

    private void showRegistrationForm(ActionEvent e) {
        RegisterFrame registerFrame = new RegisterFrame();
        registerFrame.setVisible(true);
    }

}