package controller;

import dao.BookDAO;
import dao.PeminjamanDAO;
import exception.CustomException;
import model.Peminjaman;
import util.Session;
import util.DateHelper;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PeminjamanController {
    private final PeminjamanDAO peminjamanDAO;
    private final BookDAO bookDAO;

    public PeminjamanController() {
        peminjamanDAO = new PeminjamanDAO();
        bookDAO = new BookDAO();
    }

    // Proses peminjaman buku
    public void pinjamBuku(int bookId) throws CustomException {
        // Validasi stok
        if(bookDAO.getBookStock(bookId) <= 0) {
            throw new CustomException("Stok buku habis!");
        }
        Peminjaman peminjaman = new Peminjaman(
                Session.getCurrentUser().getId(),
                bookId,
                DateHelper.getCurrentDate(),
                "dipinjam"
        );
        if(!peminjamanDAO.addPeminjaman(peminjaman)) {
            throw new CustomException("Gagal memproses peminjaman");
        }
        // Kurangi stok buku
        if(!bookDAO.decreaseStock(bookId)) {
            throw new CustomException("Gagal update stok buku");
        }
    }

    // Proses pengembalian buku
    public void kembalikanBuku(int peminjamanId) throws CustomException {

        Peminjaman peminjaman = peminjamanDAO.getPeminjamanById(peminjamanId);
        try {
            if(!peminjamanDAO.isBookDipinjam(peminjaman.getUserId(),peminjaman.getBookId())) {
                throw new CustomException("Pilihlah Buku!\n" +
                        "Yang Belum Dikembalikan");

            }
            if(!peminjamanDAO.kembalikanBuku(peminjamanId)) {
                throw new CustomException("Gagal mengembalikan buku");
            }
            // Tambah stok buku
            if(!bookDAO.increaseStock(peminjaman.getBookId())) {
                throw new CustomException("Gagal update stok buku");
            }
        } catch (CustomException e) {
            throw new CustomException("Error: " + e.getMessage());

        }



    }

    // Get riwayat peminjaman user
    public List<Peminjaman> getRiwayatPeminjaman() throws CustomException {
        int userId = Session.getCurrentUser().getId();
        List<Peminjaman> riwayat = peminjamanDAO.getPeminjamanByUser(userId);

        if(riwayat.isEmpty()) {
            throw new CustomException("Belum ada riwayat peminjaman");
        }
        return riwayat;
    }

    // Validasi masa pinjam
    public boolean validateLoanPeriod(Date tglPinjam) {
        long diff = DateHelper.getCurrentDate().getTime() - tglPinjam.getTime();
        long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return days <= 14; // Maksimal 14 hari
    }
}