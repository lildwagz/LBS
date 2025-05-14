package view.components;

import controller.BookController;
import exception.CustomException;
import model.Book;
import util.TableHelper;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

public class BookTablePanel extends JPanel {
    private JTable bookTable;
    private JTextField txtSearch;
    private final BookController bookController;

    public BookTablePanel() throws CustomException {
        bookController = new BookController();
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 1. Panel Pencarian
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Cari");
        btnSearch.addActionListener(this::handleSearch);
        searchPanel.add(new JLabel("Cari Buku:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        // 2. Tabel Buku
        bookTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(bookTable);
        TableHelper.configureResponsiveTable(bookTable, this); // Konfigurasi tabel

        // 3. Panel Tombol CRUD
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Tambah");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Hapus");

        btnAdd.addActionListener(this::handleAddBook);
        btnEdit.addActionListener(this::handleEditBook);
        btnDelete.addActionListener(this::handleDeleteBook);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);

        // 4. Tata Letak Utama
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

    }

    private void loadData() throws CustomException {
        try {
            DefaultTableModel model = (DefaultTableModel) bookTable.getModel();
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
            TableHelper.adjustTableColumns(bookTable, getWidth());
        } catch (CustomException e) {
            showErrorDialog(e.getMessage());
        }
    }


    private void handleSearch(ActionEvent e) {
        String keyword = txtSearch.getText().trim();
        // Implement search logic
        try{
            DefaultTableModel model = (DefaultTableModel) bookTable.getModel();
            model.setRowCount(0);
            for (Book book : bookController.searchBooks(keyword)){
                if(book.getStok() > 0){
                    model.addRow(new Object[]{
                            book.getId(),
                            book.getJudul(),
                            book.getPengarang(),
                            book.getStok(),
                            book.getTahunTerbit(),
                    });
                }

            }
        } catch (CustomException ex) {
            System.out.println("Debug - error"+ex.getMessage());
            showErrorDialog(ex.getMessage());
        }
    }

    private void handleAddBook(ActionEvent e) {
        BookFormDialog dialog = new BookFormDialog((JFrame) SwingUtilities.getWindowAncestor(this.getParent()));
        dialog.setVisible(true);

        if (dialog.isSubmitted()) {
            try {
                Book newBook = dialog.getBook();
                // Validasi Manual
                if(newBook.getJudul().isEmpty() || newBook.getPengarang().isEmpty()) {
                    throw new CustomException("Judul dan Pengarang harus diisi!");
                }
                bookController.addBook(newBook);
                loadData(); // Refresh table
                showSuccessDialog("Buku berhasil ditambahkan!");
            } catch (CustomException ex) {
                showErrorDialog("Gagal menambahkan buku: " + ex.getMessage());
            }
        }
    }

    private void handleEditBook(ActionEvent e) {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarningDialog("Silakan pilih buku yang akan diedit");
            return;
        }

        try {
            int bookId = (int) bookTable.getValueAt(selectedRow, 0);
            Book existingBook = bookController.getBookById(bookId);

            BookFormDialog dialog = new BookFormDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    existingBook
            );
            dialog.setVisible(true);

            if (dialog.isSubmitted()) {
                Book updatedBook = dialog.getBook();
                updatedBook.setId(bookId); // Pastikan ID tetap sama
                bookController.updateBook(updatedBook);
                loadData(); // Refresh table
                showSuccessDialog("Buku berhasil diperbarui!");
            }
        } catch (CustomException ex) {
            showErrorDialog("Gagal mengupdate buku: " + ex.getMessage());
        }

    }

    private void handleDeleteBook(ActionEvent e) {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarningDialog("Silakan pilih buku yang akan dihapus");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Apakah Anda yakin ingin menghapus buku ini?\nini akan menghapus semua history peminjaman pada buku ini",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int bookId = (int) bookTable.getValueAt(selectedRow, 0);
                Book existingBook = bookController.getBookById(bookId);

                bookController.deleteBook(existingBook);
                loadData(); // Refresh table
                showSuccessDialog("Buku berhasil dihapus!");
            } catch (CustomException ex) {
                showErrorDialog("Gagal menghapus buku: " + ex.getMessage());
            }
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarningDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }

    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Sukses", JOptionPane.INFORMATION_MESSAGE);
    }
}