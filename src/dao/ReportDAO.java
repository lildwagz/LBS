package dao;

import model.Report;
import util.DBConnection;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ReportDAO {
    private static final String BASE_REPORT_QUERY = """
        SELECT 
            p.id AS peminjaman_id,
            p.tgl_pinjam,
            p.tgl_kembali,
            p.status,
            u.username,
            b.judul AS judul_buku,
            b.pengarang,
            b.tahun_terbit,
            b.stok AS stok_saat_pinjam,
            DATEDIFF(COALESCE(p.tgl_kembali, CURDATE()), p.tgl_pinjam) AS durasi_hari,
            (SELECT COUNT(*) FROM peminjaman p2 WHERE p2.id = p.id) AS jumlah_buku
            
        FROM peminjaman p
        JOIN users u ON p.user_id = u.id
        JOIN books b ON p.book_id = b.id
        """;

    public List<Report> getFilteredReport(LocalDate startDate,
                                                    LocalDate endDate,
                                                    String status,
                                                    String bookTitle,
                                                    String username) {
        List<Report> result = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        StringBuilder query = new StringBuilder(BASE_REPORT_QUERY);
        List<String> conditions = new ArrayList<>();

        addDateCondition(conditions, params, startDate, endDate);
        addStatusCondition(conditions, params, status);
        addBookCondition(conditions, params, bookTitle);
        addUserCondition(conditions, params, username);

        if (!conditions.isEmpty()) {
            query.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        query.append(" ORDER BY p.tgl_pinjam ASC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = buildStatement(conn, query.toString(), params)) {
            System.out.println("Debug - Query: " + query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(mapToPeminjamanDetail(rs));
            }
        } catch (SQLException e) {
            handleSQLException("Error generating filtered report", e);
        }
        return result;
    }

    public Map<String, Integer> calculateMonthlyTrend(List<Report> data) {
        return data.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getTglPinjam().getMonth().toString() + " " + d.getTglPinjam().getYear(),
                        TreeMap::new,
                        Collectors.summingInt(d -> 1)
                ));
    }

    public Map<String, Integer> calculateStatusDistribution(List<Report> data) {
        return data.stream()
                .collect(Collectors.groupingBy(
                        Report::getStatus,
                        Collectors.summingInt(d -> 1)
                ));
    }

    public List<String> getAllBookTitles() {
        List<String> titles = new ArrayList<>();
        String query = "SELECT DISTINCT judul FROM books ORDER BY judul";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                titles.add(rs.getString("judul"));
            }
        } catch (SQLException e) {
            handleSQLException("Error fetching book titles", e);
        }
        return titles;
    }

    public List<String> getAllUsernames() {
        List<String> usernames = new ArrayList<>();
        String query = "SELECT username FROM users WHERE role = 'user' ORDER BY username";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                usernames.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            handleSQLException("Error fetching usernames", e);
        }
        return usernames;
    }

    // Helper Methods
    private void addDateCondition(List<String> conditions, List<Object> params,
                                  LocalDate start, LocalDate end) {
        if (start != null && end != null) {
            conditions.add("p.tgl_pinjam BETWEEN ? AND ?");
            params.add(start);
            params.add(end);
        } else if (start != null) {
            conditions.add("p.tgl_pinjam >= ?");
            params.add(start);
        } else if (end != null) {
            conditions.add("p.tgl_pinjam <= ?");
            params.add(end);
        }
    }

    private void addStatusCondition(List<String> conditions, List<Object> params, String status) {
        if (status != null && !status.isEmpty()) {
            conditions.add("p.status = ?");
            params.add(status);
        }
    }

    private void addBookCondition(List<String> conditions, List<Object> params, String title) {
        if (title != null && !title.isEmpty()) {
            conditions.add("b.judul = ?");
            params.add(title);
        }
    }

    private void addUserCondition(List<String> conditions, List<Object> params, String username) {
        if (username != null && !username.isEmpty()) {
            conditions.add("u.username = ?");
            params.add(username);
        }
    }

    private PreparedStatement buildStatement(Connection conn, String sql,
                                             List<Object> params) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);
        for (int i = 0; i < params.size(); i++) {
            Object param = params.get(i);
            if (param instanceof LocalDate) {
                stmt.setDate(i + 1, Date.valueOf((LocalDate) param));
            } else {
                stmt.setString(i + 1, param.toString());
            }
        }
        return stmt;
    }

    private Report mapToPeminjamanDetail(ResultSet rs) throws SQLException {
        Report detail = new Report();
        detail.setId(rs.getInt("peminjaman_id"));
        detail.setTglPinjam(rs.getDate("tgl_pinjam").toLocalDate());


        Date tglKembali = rs.getDate("tgl_kembali");
        if (tglKembali != null) {
            detail.setTglKembali(tglKembali.toLocalDate());
        }

        detail.setStatus(rs.getString("status"));
        detail.setUsername(rs.getString("username"));
        detail.setJudulBuku(rs.getString("judul_buku"));
        detail.setPengarang(rs.getString("pengarang"));
        detail.setTahunTerbit(rs.getInt("tahun_terbit"));
        detail.setStokBukuSaatPinjam(rs.getInt("stok_saat_pinjam"));
        detail.setDurasiHari(rs.getLong("durasi_hari"));
        detail.setJumlahBuku(rs.getInt("jumlah_buku"));

        return detail;
    }

    private void handleSQLException(String message, SQLException e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
        throw new RuntimeException("Database error: " + e.getMessage());
    }
}