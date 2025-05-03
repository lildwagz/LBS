package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Report {
    // Core Fields
    private int id;
    private int userId;
    private int bookId;
    private String username;
    private String judulBuku;
    private String pengarang;
    private LocalDate tglPinjam;
    private LocalDate tglKembali;
    private Long durasiHari;
    private String status;
    private int jumlahBuku;


    // Additional Info
    private String namaLengkapUser;
    private String roleUser;
    private int tahunTerbit;
    private int stokBukuSaatPinjam;

    // Audit Trail
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Report() {
    }

    public Report(int id, int userId, int bookId, String username,
                            String judulBuku, String pengarang, LocalDate tglPinjam,
                            LocalDate tglKembali, String status, String namaLengkapUser,
                            String roleUser, int tahunTerbit, int stokBukuSaatPinjam) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.username = username;
        this.judulBuku = judulBuku;
        this.pengarang = pengarang;
        this.tglPinjam = tglPinjam;
        this.tglKembali = tglKembali;
        this.status = status;
        this.namaLengkapUser = namaLengkapUser;
        this.roleUser = roleUser;
        this.tahunTerbit = tahunTerbit;
        this.stokBukuSaatPinjam = stokBukuSaatPinjam;
        calculateDuration();
    }

    // Business Logic Methods
    public void calculateDuration() {
        if(tglKembali != null && tglPinjam != null) {
            this.durasiHari = ChronoUnit.DAYS.between(tglPinjam, tglKembali);
        }
    }

    public boolean isOverdue() {
        if(status.equals("dipinjam")) {
            long daysBorrowed = ChronoUnit.DAYS.between(tglPinjam, LocalDate.now());
            return daysBorrowed > 7; // Asumsi masa pinjam 7 hari
        }
        return false;
    }


    // Setter khusus durasi
    public void setDurasiHari(long durasiHari) {
        this.durasiHari = durasiHari;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getJudulBuku() { return judulBuku; }
    public void setJudulBuku(String judulBuku) { this.judulBuku = judulBuku; }

    public String getPengarang() { return pengarang; }
    public void setPengarang(String pengarang) { this.pengarang = pengarang; }

    public LocalDate getTglPinjam() { return tglPinjam; }
    public void setTglPinjam(LocalDate tglPinjam) {
        this.tglPinjam = tglPinjam;
        calculateDuration();
    }

    public LocalDate getTglKembali() { return tglKembali; }
    public void setTglKembali(LocalDate tglKembali) {
        this.tglKembali = tglKembali;
        calculateDuration();
    }

    public Long getDurasiHari() { return durasiHari; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getJumlahBuku() { return jumlahBuku;}
    public void setJumlahBuku(int jumlahBuku) { this.jumlahBuku = jumlahBuku; }

    public String getNamaLengkapUser() { return namaLengkapUser; }
    public void setNamaLengkapUser(String namaLengkapUser) { this.namaLengkapUser = namaLengkapUser; }

    public String getRoleUser() { return roleUser; }
    public void setRoleUser(String roleUser) { this.roleUser = roleUser; }

    public int getTahunTerbit() { return tahunTerbit; }
    public void setTahunTerbit(int tahunTerbit) { this.tahunTerbit = tahunTerbit; }

    public int getStokBukuSaatPinjam() { return stokBukuSaatPinjam; }
    public void setStokBukuSaatPinjam(int stokBukuSaatPinjam) { this.stokBukuSaatPinjam = stokBukuSaatPinjam; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "PeminjamanDetail{" +
                "id=" + id +
                ", userId=" + userId +
                ", bookId=" + bookId +
                ", username='" + username + '\'' +
                ", judulBuku='" + judulBuku + '\'' +
                ", pengarang='" + pengarang + '\'' +
                ", tglPinjam=" + tglPinjam +
                ", tglKembali=" + tglKembali +
                ", durasiHari" + durasiHari +
                ", status='" + status + '\'' +
                ", namaLengkapUser='" + namaLengkapUser + '\'' +
                ", roleUser='" + roleUser + '\'' +
                ", tahunTerbit=" + tahunTerbit +
                ", stokBukuSaatPinjam=" + stokBukuSaatPinjam +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}