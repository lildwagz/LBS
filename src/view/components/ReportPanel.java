package view.components;

import controller.ReportController;

import exception.CustomException;
import model.Report;
import org.jfree.chart.*;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import util.DateUtil;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.time.LocalDate;
import java.util.List;

public class ReportPanel extends JPanel {
    private final ReportController controller;
    private JTable reportTable;
    private ChartPanel barChartPanel;
    private ChartPanel pieChartPanel;
    private DateUtil.DatePicker startDatePicker;
    private DateUtil.DatePicker endDatePicker;
    private JComboBox<String> statusCombo;
    private JComboBox<String> bookFilterCombo;
    private JComboBox<String> userFilterCombo;

    public ReportPanel() throws CustomException {
        controller = new ReportController();
        initUI();
        loadInitialData();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(1000, 600));

        // 1. Panel Filter
        JPanel filterPanel = createFilterPanel();
        add(filterPanel, BorderLayout.NORTH);

        // 2. Konten Utama (Tabel + Grafik)
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setResizeWeight(0.4);
        mainSplitPane.setDividerSize(5);

        // 3. Tabel Laporan
        reportTable = new JTable();
        JScrollPane tableScroll = new JScrollPane(reportTable);
        tableScroll.setPreferredSize(new Dimension(980, 200));
        mainSplitPane.setTopComponent(tableScroll);

        // 4. Panel Grafik
        JSplitPane chartSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        chartSplit.setResizeWeight(0.5);
        chartSplit.setDividerSize(5);

        barChartPanel = createChartPanel(); // Grafik batang
        pieChartPanel = createChartPanel(); // Diagram pie

        chartSplit.setLeftComponent(wrapChartPanel(barChartPanel, "Tren Bulanan"));
        chartSplit.setRightComponent(wrapChartPanel(pieChartPanel, "Distribusi Status"));

        mainSplitPane.setBottomComponent(chartSplit);
        add(mainSplitPane, BorderLayout.CENTER);
    }
    private JPanel wrapChartPanel(ChartPanel chartPanel, String title) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createTitledBorder(title));
        wrapper.add(chartPanel, BorderLayout.CENTER);
        return wrapper;
    }

    private ChartPanel createChartPanel() {
        // Inisialisasi dengan chart kosong
        JFreeChart initialChart = createEmptyChart("Data sedang dimuat...");
        ChartPanel panel = new ChartPanel(initialChart) {
            // Override untuk handle null chart
            @Override
            public JFreeChart getChart() {
                JFreeChart chart = super.getChart();
                return chart != null ? chart : createEmptyChart("Chart tidak tersedia");
            }

        };

        panel.setPreferredSize(new Dimension(480, 300));
        panel.setMinimumSize(new Dimension(200, 200));
        panel.setMouseWheelEnabled(true);
        panel.setDomainZoomable(true);
        panel.setRangeZoomable(true);
        // Nonaktifkan semua fitur zoom
        panel.setMouseWheelEnabled(false);


        // Atur default renderer untuk mencegah NPE
        panel.getChart().getPlot().setNoDataMessage("Tunggu sebentar...");

        return panel;
    }
    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new GridLayout(0, 4, 5, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter"));

        startDatePicker = DateUtil.createDatePicker();
        endDatePicker = DateUtil.createDatePicker();
        statusCombo = new JComboBox<>(new String[]{"Semua", "Dipinjam", "Dikembalikan"});
        bookFilterCombo = new JComboBox<>();
        userFilterCombo = new JComboBox<>();

        JButton btnFilter = new JButton("Filter");
        JButton btnReset = new JButton("Reset");
        JButton btnExport = new JButton("Export");

        filterPanel.add(new JLabel("Mulai:"));
        filterPanel.add(startDatePicker.getComponent());
        filterPanel.add(new JLabel("Selesai:"));
        filterPanel.add(endDatePicker.getComponent());
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusCombo);
        filterPanel.add(new JLabel("Buku:"));
        filterPanel.add(bookFilterCombo);
        filterPanel.add(new JLabel("Pengguna:"));
        filterPanel.add(userFilterCombo);
        filterPanel.add(btnFilter);
        filterPanel.add(btnReset);
        filterPanel.add(btnExport);

        btnFilter.addActionListener(this::handleFilter);
        btnReset.addActionListener(this::handleReset);
        btnExport.addActionListener(this::handleExport);

        return filterPanel;
    }

    private void loadInitialData() throws CustomException {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws CustomException {
                loadBooks();
                System.out.println("Debug - loadInitialDataBooks");
                loadUsers();
                System.out.println("Debug - loadInitialDataUser");
                return null;
            }

            @Override
            protected void done() {
                try {
                    refreshReport();
                } catch (CustomException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        worker.execute();
    }

    private void loadBooks() throws CustomException {
        bookFilterCombo.removeAllItems();
        bookFilterCombo.addItem("Semua Buku");
        controller.getAllBookTitles().forEach(bookFilterCombo::addItem);
    }

    private void loadUsers() throws CustomException {
        userFilterCombo.removeAllItems();
        userFilterCombo.addItem("Semua Pengguna");
        controller.getAllUsernames().forEach(userFilterCombo::addItem);
    }

    private void refreshReport() throws CustomException {
        try {
            LocalDate start = startDatePicker.getDate();
            LocalDate end = endDatePicker.getDate();

            if (!DateUtil.isValidRange(start, end)) {
                JOptionPane.showMessageDialog(this, "Range tanggal tidak valid!");
                return;
            }

            String status = statusCombo.getSelectedIndex() > 0
                    ? statusCombo.getSelectedItem().toString().toLowerCase()
                    : null;

            String book = bookFilterCombo.getSelectedIndex() > 0
                    ? bookFilterCombo.getSelectedItem().toString()
                    : null;

            String user = userFilterCombo.getSelectedIndex() > 0
                    ? userFilterCombo.getSelectedItem().toString()
                    : null;

            List<Report> data = controller.getFilteredReport(start, end, status, book, user);
            updateTable(data);
            updateCharts(data);
        }catch (CustomException e) {
            SwingUtilities.invokeLater(() ->
                    showErrorDialog("Database Error", e.getMessage())
            );
        }

    }

    private void updateTable(List<Report> data) {
        reportTable.setModel(new ReportTableModel(data));
        reportTable.setAutoCreateRowSorter(true);
    }

    private void updateCharts(List<Report> data) throws CustomException {
        // Pastikan chart selalu ada sebelum diakses
        if(barChartPanel.getChart() == null) {
            barChartPanel.setChart(createEmptyChart(""));
        }

        if(pieChartPanel.getChart() == null) {
            pieChartPanel.setChart(createEmptyChart(""));
        }
        // Handle empty data
        if(data.isEmpty()) {
            barChartPanel.setChart(createEmptyChart("Tidak ada data untuk tren bulanan"));
            pieChartPanel.setChart(createEmptyChart("Tidak ada data untuk distribusi status"));
            return;
        }

        // Bar Chart
        DefaultCategoryDataset barData = new DefaultCategoryDataset();
        controller.getMonthlyTrend(data).forEach((month, count) ->
                barData.addValue(count, "Peminjaman", month)
        );

        JFreeChart barChart = ChartFactory.createBarChart(
                null, "Bulan", "Jumlah", barData,
                PlotOrientation.VERTICAL, false, true, false
        );
        styleBarChart(barChart);
        barChartPanel.setChart(barChart);

        // Pie Chart
        DefaultPieDataset pieData = new DefaultPieDataset();
        controller.getStatusDistribution(data).forEach(pieData::setValue);
        // Hitung total buku
        int totalBuku = data.stream()
                .mapToInt(Report::getJumlahBuku)
                .sum();

        // Buat judul dengan total
        String pieTitle = "Distribusi Status (Total: " + totalBuku + " Buku)";

        JFreeChart pieChart = ChartFactory.createPieChart(
                pieTitle,
                pieData,
                true,
                true,
                false
        );
        stylePieChart(pieChart);
        // Tambahkan total di legend
        pieChart.addSubtitle(new TextTitle("Total Peminjaman: " + totalBuku+" Buku",
                new Font("SansSerif", Font.BOLD, 14)));
        pieChartPanel.setChart(pieChart);
    }

    private JFreeChart createEmptyChart(String message) {
        JFreeChart chart = ChartFactory.createPieChart(
                null,
                new DefaultPieDataset(),
                false,
                false,
                false
        );
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setNoDataMessage(message);
        plot.setBackgroundPaint(Color.WHITE);
        return chart;
    }

    private void styleBarChart(JFreeChart chart) {
        if(chart == null) return;
        CategoryPlot plot = chart.getCategoryPlot();
        if(plot == null) return;
        plot.setBackgroundPaint(Color.WHITE);


        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10));

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(79, 129, 189));
    }

    private void stylePieChart(JFreeChart chart) {
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} ({1})"));
        plot.setSimpleLabels(true);
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.setLegendLabelGenerator(new StandardPieSectionLabelGenerator("{0} - {1}"));
        plot.setCircular(true);
        plot.setBackgroundPaint(Color.WHITE);

    }

    private void handleFilter(ActionEvent e) {
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws CustomException { refreshReport(); return null; }
        }.execute();
    }

    private void handleReset(ActionEvent e)  {
        try {
            startDatePicker.clearDate();
            endDatePicker.clearDate();
            statusCombo.setSelectedIndex(0);
            bookFilterCombo.setSelectedIndex(0);
            userFilterCombo.setSelectedIndex(0);
            refreshReport();
        }catch (CustomException ex) {
            showErrorDialog("Database Error", ex.getMessage());
        }

    }

    private void handleExport(ActionEvent e) {
        JOptionPane.showMessageDialog(this, "Fitur ekspor dalam pengembangan!");
    }

    private static class ReportTableModel extends AbstractTableModel {
        private final String[] COLUMNS = {"ID", "Buku", "Pengguna", "Tgl Pinjam", "Tgl Kembali", "Durasi", "Status"};
        private final List<Report> data;

        public ReportTableModel(List<Report> data) {
            this.data = data;
        }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return COLUMNS.length; }
        @Override public String getColumnName(int col) { return COLUMNS[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            Report item = data.get(row);
            return switch (col) {
                case 0 -> item.getId();
                case 1 -> item.getJudulBuku();
                case 2 -> item.getUsername();
                case 3 -> DateUtil.formatForDisplay(item.getTglPinjam());
                case 4 -> item.getTglKembali() != null ?
                        DateUtil.formatForDisplay(item.getTglKembali()) : "-";
                case 5 -> item.getDurasiHari() + " hari";
                case 6 -> item.getStatus();
                default -> null;
            };
        }
    }

    private void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }
}