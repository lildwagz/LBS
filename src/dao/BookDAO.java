package dao;

import model.Book;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    // Method untuk mendapatkan semua buku
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("judul"),
                        rs.getString("pengarang"),
                        rs.getInt("stok"),
                        rs.getInt("tahun_terbit")

                );
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    // Method untuk mendapatkan buku berdasarkan ID
    public Book getBookById(int id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()) {
                return new Book(
                        rs.getInt("id"),
                        rs.getString("judul"),
                        rs.getString("pengarang"),
                        rs.getInt("stok"),
                        rs.getInt("tahun_terbit")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method untuk menambahkan buku baru
    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (judul, pengarang, stok, tahun_terbit) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, book.getJudul());
            stmt.setString(2, book.getPengarang());
            stmt.setInt(3, book.getStok());
            stmt.setInt(4, book.getTahunTerbit());

            int affectedRows = stmt.executeUpdate();
            if(affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if(generatedKeys.next()) {
                        book.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    // Method untuk update buku
    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET judul=?, pengarang=?, stok=?, tahun_terbit=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, book.getJudul());
            stmt.setString(2, book.getPengarang());
            stmt.setInt(3, book.getStok());
            stmt.setInt(4, book.getTahunTerbit());
            stmt.setInt(5, book.getId());

            System.out.println("Debug - Query: " + stmt);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method untuk menghapus buku
    public boolean deleteBook(int id) {
        String sql = "DELETE FROM books WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    // Method untuk mendapatkan stok buku
    public int getBookStock(int bookId) {
        String sql = "SELECT stok FROM books WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()) {
                return rs.getInt("stok");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Method untuk mengurangi stok
    public boolean decreaseStock(int bookId) {
        String sql = "UPDATE books SET stok = stok - 1 WHERE id = ? AND stok > 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method untuk menambah stok
    public boolean increaseStock(int bookId) {
        String sql = "UPDATE books SET stok = stok + 1 WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean isPeminjamanBookExist(Book book) {
        String sql = "SELECT COUNT(*) count FROM peminjaman  WHERE book_id = ? and status = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, book.getId());
            stmt.setString(2, "dipinjam");
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // method untuk mengecek apakah buku sudah ada di database
    public boolean isBookExist(Book book) {
        String sql = "SELECT COUNT(*) count FROM books WHERE (judul = ? AND pengarang = ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, book.getJudul());
            stmt.setString(2, book.getPengarang());

            ResultSet rs = stmt.executeQuery();
            System.out.println("Debug : "+stmt.toString());
            if(rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public List<Book>  searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT *\n" +
                "FROM books\n" +
                "WHERE judul LIKE ? " +
                "   OR judul LIKE ?" +
                " OR pengarang LIKE ?"+
                "  OR pengarang LIKE ?"+
                "ORDER BY \n" +
                "    CASE \n" +
                "        WHEN judul = ? OR pengarang = ? THEN 1 \n" +
                "        ELSE 2 \n" +
                "    END;";
        try
            (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, keyword );
            stmt.setString(2,"%" + keyword + "%");
            stmt.setString(3,keyword );
            stmt.setString(4,"%"+keyword +"%");
            stmt.setString(5, keyword );
            stmt.setString(6, keyword );

            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("judul"),
                        rs.getString("pengarang"),
                        rs.getInt("stok"),
                        rs.getInt("tahun_terbit")
                );
//                book.setId(rs.getInt("id"));
                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }


}