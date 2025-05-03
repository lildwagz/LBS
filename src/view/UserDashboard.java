package view;

import controller.BookController;
import controller.PeminjamanController;
import exception.CustomException;
import model.Book;
import model.Peminjaman;
import model.User;
import util.Session;
import util.TableHelper;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;


public class UserDashboard extends JFrame {
    private JTable booksTable;
    private JTable loansTable;
    private final BookController bookController;
    private final PeminjamanController loanController;

    public UserDashboard(User user) {
        bookController = new BookController();
        loanController = new PeminjamanController();
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setTitle("User Dashboard - Library System");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        JTabbedPane tabbedPane = new JTabbedPane();

        // Books Tab
        JPanel booksPanel = createBooksPanel();
        tabbedPane.addTab("Available Books", booksPanel);

        // Loans Tab
        JPanel loansPanel = createLoansPanel();
        tabbedPane.addTab("My Loans", loansPanel);

        add(tabbedPane, BorderLayout.CENTER);
        add(createHeaderPanel(), BorderLayout.NORTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel welcomeLabel = new JLabel("Welcome, " + Session.getCurrentUser().getUsername());
        JButton btnLogout = new JButton("Logout");

        btnLogout.addActionListener(e -> {
            Session.clear();
            dispose();
            new LoginFrame().setVisible(true);
        });

        headerPanel.add(welcomeLabel);
        headerPanel.add(btnLogout);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return headerPanel;
    }

    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table

        booksTable = new JTable();
        TableHelper.configureResponsiveTable(booksTable, this);

        JScrollPane scrollPane = new JScrollPane(booksTable);

        // Search Panel
        JPanel searchPanel = new JPanel();
        JTextField txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Search");

        btnSearch.addActionListener(e -> searchBooks(txtSearch.getText()));

        searchPanel.add(new JLabel("Search Book:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        JButton btnBorrow = new JButton("Borrow Book");
        btnBorrow.addActionListener(this::handleBorrowBook);

        buttonPanel.add(btnBorrow);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createLoansPanel() {
        // 1. Panel Utama dengan BorderLayout
        JPanel panel = new JPanel(new BorderLayout());

        // 2. Tabel untuk menampilkan riwayat peminjaman
        loansTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(loansTable);

        // 3. Konfigurasi tabel responsif
        TableHelper.configureResponsiveTable(loansTable, this);

        // 4. Panel Tombol Aksi
        JPanel buttonPanel = new JPanel();
        JButton btnReturn = new JButton("Return Book");
        JButton btnRefresh = new JButton("Refresh");

        // 5. Event Handling
        btnReturn.addActionListener(this::handleReturnBook);
        btnRefresh.addActionListener(e -> loadLoansData());

        // 6. Tata Letak Komponen
        buttonPanel.add(btnReturn);
        buttonPanel.add(btnRefresh);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadData() {
        loadBooksData();
        loadLoansData();
    }

    private void loadBooksData() {
        try {
            DefaultTableModel model = (DefaultTableModel) booksTable.getModel();
            model.setColumnIdentifiers(new String[]{"ID", "Title", "Author", "Available", "Year"});
            model.setRowCount(0);

            for (Book book : bookController.getAllBooks()) {
                model.addRow(new Object[]{
                        book.getId(),
                        book.getJudul(),
                        book.getPengarang(),
                        book.getStok(),
                        book.getTahunTerbit(),
                });
            }
            TableHelper.adjustTableColumns(booksTable, getWidth());

        } catch (CustomException e) {
            showErrorDialog(e.getMessage());
        }
    }

    private void loadLoansData() {
        try {
            DefaultTableModel model = (DefaultTableModel) loansTable.getModel();
            model.setColumnIdentifiers(new String[]{"Loan ID", "Book Title", "Loan Date", "Due Date", "Status"});
            model.setRowCount(0);

            for (Peminjaman loan : loanController.getRiwayatPeminjaman()) {
                model.addRow(new Object[]{
                        loan.getId(),
                        loan.getBookTitle(),
                        loan.getTglPinjam(),
                        loan.getTglKembali(),
                        loan.getStatus()
                });
            }
            TableHelper.adjustTableColumns(loansTable, getWidth());

        } catch (CustomException e) {
            showErrorDialog(e.getMessage());
        }
    }
    // method pencarian menggunakan keyword strin
    private void searchBooks(String keyword) {
        try {
            DefaultTableModel model = (DefaultTableModel) booksTable.getModel();
            model.setRowCount(0);

            for (Book book : bookController.searchBooks(keyword)) {
                if(book.getStok() > 0) {
                    model.addRow(new Object[]{
                            book.getId(),
                            book.getJudul(),
                            book.getPengarang(),
                            book.getStok(),
                            book.getTahunTerbit(),
                    });
                }
            }
        } catch (CustomException e) {
            showErrorDialog(e.getMessage());
        }
    }

    private void handleBorrowBook(ActionEvent e) {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarningDialog("Please select a book to borrow");
            return;
        }

        int bookId = (int) booksTable.getValueAt(selectedRow, 0);
        try {
            loanController.pinjamBuku(
                    bookId
            );
            loadData();
            showSuccessDialog("Book borrowed successfully!");
        } catch (CustomException ex) {
            showErrorDialog("Borrow failed: " + ex.getMessage());
        }
    }

    private void handleReturnBook(ActionEvent e) {
        int selectedRow = loansTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarningDialog("Please select a loan to return");
            return;
        }

        int loanId = (int) loansTable.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to return this book?",
                "Confirm Return",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                loanController.kembalikanBuku(loanId);
                loadData();
                showSuccessDialog("Book returned successfully!");
            } catch (CustomException ex) {
                showErrorDialog("Return failed: " + ex.getMessage());
            }
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarningDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }


}