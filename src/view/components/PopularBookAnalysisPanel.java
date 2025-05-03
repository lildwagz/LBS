package view.components;

import controller.PopularBookController;
import exception.CustomException;
import model.PopularBookAnalysis;
import org.jfree.chart.*;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class PopularBookAnalysisPanel extends JPanel {
    private final PopularBookController controller;
    private JComboBox<Integer> yearCombo;
    private JTable analysisTable;
    private ChartPanel chartPanel;
    private JLabel summaryLabel;

    public PopularBookAnalysisPanel() {
        controller = new PopularBookController();
        initUI();
        loadInitialData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Filter"));

        yearCombo = new JComboBox<>(new Integer[]{2023, 2024, 2025});
        yearCombo.setSelectedItem(2024);
        yearCombo.setPreferredSize(new Dimension(100, 30));

        JButton btnRefresh = new JButton("Muat Ulang Data");
        JButton btnExport = new JButton("Ekspor ke PDF");
        JButton btnHelp = new JButton("Bantuan");

        controlPanel.add(new JLabel("Tahun Analisis:"));
        controlPanel.add(yearCombo);
        controlPanel.add(btnRefresh);
        controlPanel.add(btnExport);
        controlPanel.add(btnHelp);

        // Chart Panel
        chartPanel = new ChartPanel(null);
        chartPanel.setPreferredSize(new Dimension(800, 400));

        // Table Panel
        analysisTable = new JTable();
        JScrollPane tableScroll = new JScrollPane(analysisTable);
        tableScroll.setPreferredSize(new Dimension(800, 300));

        // Summary Panel
        summaryLabel = new JLabel(" ");
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        summaryLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Visualisasi Grafik", chartPanel);
        tabbedPane.addTab("Tabel Detail", tableScroll);

        // Layout
        add(controlPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(summaryLabel, BorderLayout.SOUTH);

        // Event Handlers
        btnRefresh.addActionListener(this::handleRefresh);
        btnExport.addActionListener(this::handleExport);
        btnHelp.addActionListener(this::showHelp);
    }

    private void loadInitialData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws CustomException {
                updateAnalysis();
                return null;
            }
        };
        worker.execute();
    }

    private void updateAnalysis() throws CustomException {
        int tahun = (Integer) yearCombo.getSelectedItem();
        List<PopularBookAnalysis> books = controller.getPopularBooks(tahun, 15);

        updateChart(books,tahun);
        updateTable(books);
        updateSummary(books);
    }

    private void updateChart(List<PopularBookAnalysis> books, int tahun) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (PopularBookAnalysis book : books) {
            dataset.addValue(
                    book.getTotalPeminjaman(),
                    "Jumlah Peminjaman",
                    book.getJudul()
            );
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "10 Buku Paling Populer Tahun " + tahun,
                "Judul Buku",
                "Jumlah Peminjaman",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );

        // Styling
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);

        // Rotate labels
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);

        // Set bar color
        plot.getRenderer().setSeriesPaint(0, new Color(79, 129, 189));

        chartPanel.setChart(chart);
    }

    private void updateTable(List<PopularBookAnalysis> books) {
        AnalysisTableModel model = new AnalysisTableModel(books);
        analysisTable.setModel(model);
        analysisTable.setAutoCreateRowSorter(true);
        analysisTable.getTableHeader().setReorderingAllowed(false);
    }

    private void updateSummary(List<PopularBookAnalysis> books) {
        if(books.isEmpty()) {
            summaryLabel.setText("Tidak ada data peminjaman untuk tahun ini");
            return;
        }

        int total = books.stream().mapToInt(PopularBookAnalysis::getTotalPeminjaman).sum();
        String summary = String.format(
                "Total Peminjaman: %,d | Buku Terpopuler: %s (%,d peminjaman) | Rata-rata Durasi: %.1f hari",
                total,
                books.get(0).getJudul(),
                books.get(0).getTotalPeminjaman(),
                books.stream().mapToDouble(PopularBookAnalysis::getAvgDurasiPinjam).average().orElse(0)
        );
        summaryLabel.setText(summary);
    }

    private void handleRefresh(ActionEvent e) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws CustomException {
                updateAnalysis();
                return null;
            }

            @Override
            protected void done() {
                JOptionPane.showMessageDialog(PopularBookAnalysisPanel.this,
                        "Data berhasil diperbarui",
                        "Refresh Berhasil",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }.execute();
    }

    private void handleExport(ActionEvent e) {
        // Implementasi ekspor PDF
        JOptionPane.showMessageDialog(this,
                "Fitur ekspor sedang dalam pengembangan",
                "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showHelp(ActionEvent e) {
        String helpMessage = """
            <html><b>Panduan Analisis Buku Populer:</b><br>
            1. Pilih tahun dari dropdown<br>
            2. Klik 'Muat Ulang' untuk update data<br>
            3. Gunakan tab untuk beralih antara grafik dan tabel<br>
            4. Klik header tabel untuk sorting<br>
            5. Ekspor data ke PDF untuk laporan""";

        JOptionPane.showMessageDialog(this,
                helpMessage,
                "Panduan Penggunaan",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Custom Table Model
    private static class AnalysisTableModel extends AbstractTableModel {
        private final String[] columns = {
                "Judul", "Pengarang", "Total Pinjam",
                "Rata-rata Durasi (hari)", "Terakhir Dipinjam",
                "Persentase"
        };

        private final List<PopularBookAnalysis> data;

        public AnalysisTableModel(List<PopularBookAnalysis> data) {
            this.data = data;
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public Object getValueAt(int row, int col) {
            PopularBookAnalysis book = data.get(row);
            return switch (col) {
                case 0 -> book.getJudul();
                case 1 -> book.getPengarang();
                case 2 -> String.format("%,d", book.getTotalPeminjaman());
                case 3 -> String.format("%.1f", book.getAvgDurasiPinjam());
                case 4 -> book.getTerakhirDipinjam().toString();
                case 5 -> String.format("%.2f%%", book.getPersentaseTotal());
                default -> null;
            };
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class; // Untuk memudahkan sorting
        }
    }
}