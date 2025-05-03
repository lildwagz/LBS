package view.components;

import model.Book;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class BookFormDialog extends JDialog {
    private JTextField txtTitle;
    private JTextField txtAuthor;
    private JSpinner spnStock;
    private JSpinner spnYear;
    private boolean submitted = false;
    private Book existingBook;

    public BookFormDialog(JFrame parent) {
        this(parent, null);
    }

    public BookFormDialog(JFrame parent, Book book) {
        super(parent, "Book Form", true);
        this.existingBook = book;
        initializeUI();
    }

    private void initializeUI() {
        setSize(400, 300);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtTitle = new JTextField();
        txtAuthor = new JTextField();
        spnStock = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
        spnYear = new JSpinner(new SpinnerNumberModel(2023, 1900, 2100, 1));

        if (existingBook != null) {
            populateFormFields();
        }

        formPanel.add(new JLabel("Title*:"));
        formPanel.add(txtTitle);
        formPanel.add(new JLabel("Author*:"));
        formPanel.add(txtAuthor);
        formPanel.add(new JLabel("Stock*:"));
        formPanel.add(spnStock);
        formPanel.add(new JLabel("Year*:"));
        formPanel.add(spnYear);


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
        txtTitle.setText(existingBook.getJudul());
        txtAuthor.setText(existingBook.getPengarang());
        spnStock.setValue(existingBook.getStok());
        spnYear.setValue(existingBook.getTahunTerbit());
    }

    private void handleSubmit(ActionEvent e) {
        if (validateForm()) {
            submitted = true;
            dispose();
        }
    }

    private boolean validateForm() {
        if (txtTitle.getText().trim().isEmpty()) {
            showValidationError("Title is required");
            return false;
        }

        if (txtAuthor.getText().trim().isEmpty()) {
            showValidationError("Author is required");
            return false;
        }

        if ((int) spnStock.getValue() < 0) {
            showValidationError("Stock cannot be negative");
            return false;
        }

        if ((int) spnYear.getValue() < 1900) {
            showValidationError("Invalid publication year");
            return false;
        }

        return true;
    }

    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Validation Error",
                JOptionPane.WARNING_MESSAGE
        );
    }

    public Book getBook() {
        Book book = new Book();
        if (existingBook != null) {
            book.setId(existingBook.getId());
        }
        book.setJudul(txtTitle.getText().trim());
        book.setPengarang(txtAuthor.getText().trim());
        book.setStok((Integer) spnStock.getValue());
        book.setTahunTerbit((Integer) spnYear.getValue());
        return book;
    }

    public boolean isSubmitted() {
        return submitted;
    }
}