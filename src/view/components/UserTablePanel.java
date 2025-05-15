package view.components;

import controller.UserController;
import exception.CustomException;
import model.Book;
import model.User;
import util.TableHelper;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

public class UserTablePanel extends JPanel {
    private JTable table;
    private JTextField txtSearch;
    private final UserController userController;

    public UserTablePanel() throws CustomException {
        userController = new UserController();
        initUI();
        loadData();
    }
    // deklarasikan UI
    private void initUI() {
        setLayout(new BorderLayout());

        // 1. Panel Pencarian User
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Cari");
        btnSearch.addActionListener(this::handleSearch);
        searchPanel.add(new JLabel("Cari User:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        // 2. Tabel User
        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        TableHelper.configureResponsiveTable(table, this); // Konfigurasi tabel

        // 3. Panel Tombol CRUD
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Tambah");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Hapus");

        btnAdd.addActionListener(this::handleAddUser);
        btnEdit.addActionListener(this::handleEditUser);
        btnDelete.addActionListener(this::handleDeleteUser);

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
           DefaultTableModel model = (DefaultTableModel) table.getModel();
           model.setColumnIdentifiers(new String[]{"ID", "Username", "Role"});
           model.setRowCount(0);
           for(User user : userController.getAllUsers()) {
               model.addRow(new Object[]{
                   user.getId(),
                   user.getUsername(),
                   user.getRole()


               });
           }
           TableHelper.adjustTableColumns(table, getWidth());
       }catch (CustomException e) {
           showErrorDialog(e.getMessage());

       }
    }
//-------------------- Block kode untuk yudistira fitur pencarian -------------------//
    private void handleSearch(ActionEvent e) {
        String keyword = txtSearch.getText().trim();
        // Implement search logic

    }
// ------------------------>block kode untuk iqbal fitur nambah user ---------------->
    private void handleAddUser(ActionEvent e)
    {
        UserFormDialog dialog = new UserFormDialog((JFrame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);

        if (dialog.isSubmitted()) {
            try {
                User newUser = dialog.getUser();
                // Validasi Manual
                if(newUser.getUsername().isEmpty() || newUser.getPassword().isEmpty()) {
                    throw new CustomException("Username harus di isi!");
                }
                userController.addUser(newUser);
                loadData(); // Refresh table
                showSuccessDialog("User berhasil ditambahkan!");
            } catch (CustomException ex) {
                showErrorDialog("Gagal menambahkan User: " + ex.getMessage());
            }
        }

    }

    // ------------------------>block kode untuk ammar fitur nambah user ---------------->

    private void handleEditUser(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            showWarningDialog("Silakan pilih user yang akan diedit");
            return;
        }

        try {
            int userId = (int) table.getValueAt(selectedRow, 0);
            User existingUser = userController.getUserById(userId);

            UserFormDialog dialog = new UserFormDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    existingUser
            );
            dialog.setVisible(true);

            if (dialog.isSubmitted()) {
                User updatedUser = dialog.getUser();
                updatedUser.setId(userId); // Pastikan ID tetap sama
                userController.updateUser(updatedUser);
                loadData(); // Refresh table
                showSuccessDialog("User berhasil diperbarui!");
            }
        } catch (CustomException ex) {
            showErrorDialog("Gagal mengupdate User: " + ex.getMessage());
        }

    }


    private void handleDeleteUser(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if(selectedRow < 0) {
            // Delete confirmation and logic
            showWarningDialog("Silakan pilih Pengguna yang akan dihapus");
            return;

        }
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Apakah Anda yakin ingin menghapus user ini?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int userId = (int) table.getValueAt(selectedRow, 0);
                userController.deleteUser(userId);
                loadData(); // Refresh table
                showSuccessDialog("Pengguna berhasil dihapus!");
            } catch (CustomException ex) {
                showErrorDialog("Gagal menghapus pengguna: " + ex.getMessage());
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