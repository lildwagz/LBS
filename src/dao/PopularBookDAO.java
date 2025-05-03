package dao;

import model.PopularBookAnalysis;
import util.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PopularBookDAO {
    public List<PopularBookAnalysis> getPopularBooksAnalysis(int tahun, int limit) throws SQLException {
        List<PopularBookAnalysis> books = new ArrayList<>();

        String sql = """
            SELECT 
                b.judul,
                b.pengarang,
                COUNT(p.id) AS total_pinjam,
                AVG(DATEDIFF(p.tgl_kembali, p.tgl_pinjam)) AS avg_durasi,
                MAX(p.tgl_pinjam) AS terakhir_pinjam,
                (COUNT(p.id) * 100.0) / (SELECT COUNT(*) FROM peminjaman WHERE YEAR(tgl_pinjam) = ?) AS persentase
            FROM peminjaman p
            JOIN books b ON p.book_id = b.id
            WHERE YEAR(p.tgl_pinjam) = ?
            GROUP BY b.judul, b.pengarang
            ORDER BY total_pinjam DESC
            LIMIT ?""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tahun);
            stmt.setInt(2, tahun);
            stmt.setInt(3, limit);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                books.add(new PopularBookAnalysis(
                        rs.getString("judul"),
                        rs.getString("pengarang"),
                        rs.getInt("total_pinjam"),
                        rs.getDouble("avg_durasi"),
                        rs.getDate("terakhir_pinjam").toLocalDate(),
                        rs.getDouble("persentase")
                ));
            }
        }
        return books;
    }
}