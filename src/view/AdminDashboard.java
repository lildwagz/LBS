package view;

import exception.CustomException;
import model.User;
import util.Session;
import view.components.BookTablePanel;
import view.components.ReportPanel;
import view.components.PopularBookAnalysisPanel;
import view.components.UserTablePanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AdminDashboard extends JFrame {

    public AdminDashboard() throws CustomException {
        checkAuthorization();
        initComponents();
        setupFrame();

    }


    private void checkAuthorization() {
        User currentUser = Session.getCurrentUser();
        System.out.println("Debug - Check auth admin dashboard");
        if(currentUser == null || !currentUser.getRole().equals("admin")) {
            JOptionPane.showMessageDialog(this, "Akses ditolak!");
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private void initComponents() throws CustomException {
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("Admin Dashboard");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(this::handleLogout);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnLogout, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // TabbedPane untuk Book dan User Management
        JTabbedPane tabbedPane = new JTabbedPane();
        System.out.println("Debug - init TabbedPane");
        tabbedPane.addTab("Kelola Buku", new BookTablePanel());
        tabbedPane.addTab("Kelola User", new UserTablePanel());
        tabbedPane.addTab("Statistik", new ReportPanel());
        tabbedPane.addTab("Analisis Buku", new PopularBookAnalysisPanel());


        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        System.out.println("Debug - add components");
    }

    private void setupFrame() {
        setTitle("Sistem Peminjaman Buku - Admin");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void handleLogout(ActionEvent e) {
        Session.clear();
        dispose();
        new LoginFrame().setVisible(true);
    }


}