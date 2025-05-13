package controller;

import dao.BookDAO;
import exception.CustomException;
import model.Book;
import java.util.List;

public class BookController {
    private final BookDAO bookDAO;

    public BookController() {
        this.bookDAO = new BookDAO();
    }

    // Method untuk mendapatkan semua buku
    public List<Book> getAllBooks() throws CustomException {
        try {
            return bookDAO.getAllBooks();
        } catch (Exception e) {
            throw new CustomException("Gagal mengambil data buku: " + e.getMessage());
        }
    }

    // Method untuk menambahkan buku
    public void addBook(Book book) throws CustomException {
        try {
            if(bookDAO.isBookExist(book)) {

                throw new CustomException("Buku sudah ada di sistem!\n" +
                        "Judul+Pengarang tidak boleh sama dengan buku yang sudah ada");
            }
            if(!bookDAO.addBook(book)) {
                throw new CustomException("Gagal menambahkan buku");
            }
        } catch (Exception e) {
            throw new CustomException("Error: " + e.getMessage());
        }
    }

    // Method untuk update buku
    public void updateBook(Book book) throws CustomException {
        try {
            if(bookDAO.isBookExist(book)) {
                if(!bookDAO.updateBook(book)) {
                    throw new CustomException("Gagal memperbarui buku");
                }
            }


        } catch (Exception e) {
            throw new CustomException("Error: " + e.getMessage());
        }
    }

    // Method untuk menghapus buku
    public void deleteBook(Book book) throws CustomException {
        try {
            if(bookDAO.isPeminjamanBookExist(book)) {
                throw new CustomException("buku dalam transaksi pemminjaman");

            }
            if(!bookDAO.deleteBook(book.getId())) {
                throw new CustomException("Gagal menambahkan buku");
            }

        } catch (Exception e) {
            throw new CustomException("Error: " + e.getMessage());
        }
    }

    // Method untuk mendapatkan buku berdasarkan ID
    public Book getBookById(int id) throws CustomException {
        try {
            Book book = bookDAO.getBookById(id);
            if(book == null) {
                throw new CustomException("Buku tidak ditemukan");
            }
            return book;
        } catch (Exception e) {
            throw new CustomException("Error: " + e.getMessage());
        }
    }

    // method untuk mencari buku berdasarkan Judul buku
    public List<Book> searchBooks(String keyword) throws CustomException {
        try {
            List<Book> books = bookDAO.searchBooks(keyword);
            if(books.isEmpty()) {
                throw new CustomException("Tidak Ada hasil dengan judul buku "+keyword);
            }
            return books;
        }catch (Exception e) {
            throw new CustomException("Gagal : " + e.getMessage());
        }

    }
}