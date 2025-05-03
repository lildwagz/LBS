package util;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class TableHelper {

    public static void configureResponsiveTable(JTable table, Container parent) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Listener untuk resize frame
        parent.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                adjustTableColumns(table, parent.getWidth());
            }
        });

        // Listener untuk resize scrollpane
        if(parent instanceof JScrollPane scrollPane) {
            scrollPane.getViewport().addChangeListener(e -> {
                adjustTableColumns(table, scrollPane.getViewport().getWidth());
            });
        }

        // Initial adjustment
        adjustTableColumns(table, parent.getWidth());
        configureTableStyle(table);
    }

    public static void adjustTableColumns(JTable table, int parentWidth) {
        if (table.getColumnModel().getColumnCount() == 0) return;
        table.setCellSelectionEnabled(false);
        table.setRowSelectionAllowed(true);
        table.setDefaultEditor(Object.class, null);
        int tableType = getTableType(table);
        int[] minWidths;
        double[] percentages;
        int totalMinWidth;

        if (tableType == 0) { // Book Table
            minWidths = new int[]{60, 200, 150, 80}; // ID, Judul, Pengarang, Stok, Tahun
            percentages = new double[]{0.1, 0.45, 0.25, 0.1};
            totalMinWidth = 60 + 200 + 150 + 80 + 80;
        } else { // User Table
            minWidths = new int[]{60, 50, 100}; // ID, Username, Role, Jumlah Peminjaman
            percentages = new double[]{0.1, 0.3, 0.3};
            totalMinWidth = 60 + 50 + 100 ; // Update total min width
        }

        int availableWidth = parentWidth - 30; // Adjust for scrollbar
        if (availableWidth < totalMinWidth) {
            // Use minimum widths if window too small
            for (int i = 0; i < minWidths.length; i++) {
                table.getColumnModel().getColumn(i).setPreferredWidth(minWidths[i]);
            }
        } else {
            // Dynamic width calculation
            for (int i = 0; i < percentages.length; i++) {
                int width = (int) (availableWidth * percentages[i]);
                table.getColumnModel().getColumn(i).setPreferredWidth(
                        Math.max(width, minWidths[i])
                );
            }
        }
    }

    private static int getTableType(JTable table) {
        return table.getColumnCount() == 5 ? 0 : (table.getColumnCount() == 4 ? 1 : -1);
        // Updated to account for User table with 4 columns
    }

    public static void configureTableStyle(JTable table) {
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(225, 240, 255));
        table.setShowGrid(true);
        table.setGridColor(new Color(240, 240, 240));

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
    }
}