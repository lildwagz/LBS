package model;

import java.util.Date;

public class Peminjaman {
    private int id;
    private int userId;
    private int bookId;
    private Date tglPinjam;
    private Date tglKembali;
    private String status;



    // Constructor
    public Peminjaman(int userId, int bookId, Date tglPinjam, String status) {
        this.userId = userId;
        this.bookId = bookId;
        this.tglPinjam = tglPinjam;
        this.status = status;


    }



    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public Date getTglPinjam() { return tglPinjam; }
    public void setTglPinjam(Date tglPinjam) { this.tglPinjam = tglPinjam; }
    public Date getTglKembali() { return tglKembali; }
    public void setTglKembali(Date tglKembali) { this.tglKembali = tglKembali; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }



    // Tambahan field dan method di model Peminjaman
    private String bookTitle;
    private String userName;

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }



}