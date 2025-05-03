package model;

import java.time.LocalDate;

public class PopularBookAnalysis {
    private final String judul;
    private final String pengarang;
    private final int totalPeminjaman;
    private final double avgDurasiPinjam;
    private final LocalDate terakhirDipinjam;
    private final double persentaseTotal;

    public PopularBookAnalysis(String judul, String pengarang, int totalPeminjaman,
                               double avgDurasiPinjam, LocalDate terakhirDipinjam,
                               double persentaseTotal) {
        this.judul = judul;
        this.pengarang = pengarang;
        this.totalPeminjaman = totalPeminjaman;
        this.avgDurasiPinjam = avgDurasiPinjam;
        this.terakhirDipinjam = terakhirDipinjam;
        this.persentaseTotal = persentaseTotal;
    }

    // Getters
    public String getJudul() { return judul; }
    public String getPengarang() { return pengarang; }
    public int getTotalPeminjaman() { return totalPeminjaman; }
    public double getAvgDurasiPinjam() { return avgDurasiPinjam; }
    public LocalDate getTerakhirDipinjam() { return terakhirDipinjam; }
    public double getPersentaseTotal() { return persentaseTotal; }
}