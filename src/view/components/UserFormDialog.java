package view.components;

import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class UserFormDialog extends JDialog {
    private JTextField txtUsername;
    private JPasswordField txtPassword; // Ganti ke JPasswordField
    private JComboBox<String> comboRole; // Ganti ke JComboBox
    private boolean submitted = false;
    private User existingUser;

    // Constructor
    public UserFormDialog(JFrame parent) {
        this(parent, null);
    }

    public UserFormDialog(JFrame parent, User user) {
        super(parent, "User Form", true);
        this.existingUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setSize(400, 200); // Sesuaikan ukuran
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5)); // Sesuaikan grid
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        comboRole = new JComboBox<>(new String[]{"admin", "user"}); // Dropdown role

        if (existingUser != null) {
            populateFormFields();
        }

        formPanel.add(new JLabel("Username*:"));
        formPanel.add(txtUsername);
        formPanel.add(new JLabel("Password*:"));
        formPanel.add(txtPassword);
        formPanel.add(new JLabel("Role*:"));
        formPanel.add(comboRole); // Gunakan combobox

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSubmit = new JButton("Submit");
        JButton btnCancel = new JButton("Cancel");

        btnSubmit.addActionListener(this::handleSubmit);
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSubmit);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void populateFormFields() {
        txtUsername.setText(existingUser.getUsername());
        // Password sengaja tidak diisi untuk keamanan
        comboRole.setSelectedItem(existingUser.getRole());
    }

    private void handleSubmit(ActionEvent e) {
        if (validateForm()) {
            submitted = true;
            dispose();
        }
    }

    private boolean validateForm() {
        if (txtUsername.getText().trim().isEmpty()) {
            showValidationError("Username harus diisi");
            return false;
        }

        if (new String(txtPassword.getPassword()).trim().isEmpty()) {
            showValidationError("Password harus diisi");
            return false;
        }

        if (comboRole.getSelectedItem() == null) {
            showValidationError("Role harus dipilih");
            return false;
        }

        return true;
    }

    // Ganti nama method dari getBook() ke getUser()
    public User getUser() {
        User user = new User();
        if (existingUser != null) {
            user.setId(existingUser.getId());
        }
        user.setUsername(txtUsername.getText().trim());
        user.setPassword(new String(txtPassword.getPassword()).trim()); // Ambil password
        user.setRole(comboRole.getSelectedItem().toString());
        return user;
    }

    // Tetap sama
    public boolean isSubmitted() {
        return submitted;
    }

    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Validasi Error",
                JOptionPane.WARNING_MESSAGE
        );
    }
}