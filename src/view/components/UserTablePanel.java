package view.components;

import controller.UserController;
import exception.CustomException;
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
    private void handleAddUser(ActionEvent e) {
        // Open add user dialog
    }

    // ------------------------>block kode untuk ammar fitur nambah user ---------------->

    private void handleEditUser(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if(selectedRow < 0) {
            return;
        }
        int userId = (int) table.getValueAt(selectedRow, 0);


    }

// ---------------------------------------->> block kode untuk gatan
    private void handleDeleteUser(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if(selectedRow >= 0) {
            int userId = (int) table.getValueAt(selectedRow, 0);
            // Delete confirmation and logic


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