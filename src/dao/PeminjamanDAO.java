package dao;

import model.Peminjaman;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PeminjamanDAO {

    // Method untuk menambahkan data peminjaman baru
    public boolean addPeminjaman(Peminjaman peminjaman) {
        String sql = "INSERT INTO peminjaman (user_id, book_id, tgl_pinjam, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, peminjaman.getUserId());
            stmt.setInt(2, peminjaman.getBookId());
            stmt.setDate(3, new java.sql.Date(peminjaman.getTglPinjam().getTime()));
            stmt.setString(4, peminjaman.getStatus());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        peminjaman.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method untuk mendapatkan semua data peminjaman
    public List<Peminjaman> getAllPeminjaman() {
        List<Peminjaman> list = new ArrayList<>();
        String sql = "SELECT p.*, b.judul AS book_title, u.username AS user_name "
                + "FROM peminjaman p "
                + "JOIN books b ON p.book_id = b.id "
                + "JOIN users u ON p.user_id = u.id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapPeminjamanWithDetails(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Method untuk mendapatkan data peminjaman berdasarkan ID
    public Peminjaman getPeminjamanById(int id) {
        String sql = "SELECT p.*, b.judul AS book_title, u.username AS user_name "
                + "FROM peminjaman p "
                + "JOIN books b ON p.book_id = b.id "
                + "JOIN users u ON p.user_id = u.id "
                + "WHERE p.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapPeminjamanWithDetails(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method untuk mengembalikan buku
    public boolean kembalikanBuku(int peminjamanId) {
        String sql = "UPDATE peminjaman SET tgl_kembali = CURDATE(), status = 'dikembalikan' WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, peminjamanId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //method untuk melihat jumlahPeminjaman per user
//    public List<Peminjaman> getPeminjamanByStatus(int id) throws SQLException {
//        List<Peminjaman> list = new ArrayList<>();
//        String sql = "select count(*) jumlah from peminjaman where user_id= ?;";
//        try(Connection conn = DBConnection.getConnection();
//            PreparedStatement stmt = conn.prepareStatement(sql)){
//                stmt.setInt(1,id);
//                ResultSet rs = stmt.executeQuery();
//                while (rs.next()) {
//                    list.add(mapPeminjamanWithDetails(rs));
//                }
//            }
//        )
//    }


    // Method untuk mendapatkan riwayat peminjaman user
    public List<Peminjaman> getPeminjamanByUser(int userId) {
        List<Peminjaman> list = new ArrayList<>();
        String sql = "SELECT p.*, b.judul AS book_title "
                + "FROM peminjaman p "
                + "JOIN books b ON p.book_id = b.id "
                + "WHERE p.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapPeminjamanWithDetails(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Method untuk mendapatkan peminjaman aktif
    public List<Peminjaman> getPeminjamanAktifByUser(int userId) {
        List<Peminjaman> list = new ArrayList<>();
        String sql = "SELECT p.*, b.judul AS book_title "
                + "FROM peminjaman p "
                + "JOIN books b ON p.book_id = b.id "
                + "WHERE p.user_id = ? AND status = 'dipinjam'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapPeminjamanWithDetails(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    // Method untuk mengecek status buku berdasarkan buku dan id user
    public boolean isBookDipinjam(int user_id,int bookId) {
        String sql = "SELECT count(*) count " +
                "FROM peminjaman p " +
                "JOIN books b ON p.book_id = b.id " +
                "WHERE p.user_id = ? and p.book_id = ?  and status = 'dipinjam'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, user_id);
            stmt.setInt(2, bookId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper method untuk mapping ResultSet ke objek Peminjaman
    private Peminjaman mapPeminjamanWithDetails(ResultSet rs) throws SQLException {
        Peminjaman peminjaman = new Peminjaman(
                rs.getInt("user_id"),
                rs.getInt("book_id"),
                rs.getDate("tgl_pinjam"),
                rs.getString("status")
        );

        peminjaman.setId(rs.getInt("id"));
        peminjaman.setTglKembali(rs.getDate("tgl_kembali"));
        peminjaman.setBookTitle(rs.getString("book_title"));

        try {  // Handle jika kolom user_name tidak ada di beberapa query
            peminjaman.setUserName(rs.getString("user_name"));
        } catch (SQLException ignored) {}

        return peminjaman;
    }



}