package model;

public class Book {
    private int id;
    private String judul;
    private String pengarang;
    private int stok;
    private int tahunTerbit;

    // Constructor dengan semua parameter (termasuk ID)
    public Book(int id, String judul, String pengarang, int stok, int tahunTerbit) {
        this.id = id;
        this.judul = judul;
        this.pengarang = pengarang;
        this.stok = stok;
        this.tahunTerbit = tahunTerbit;
    }

    public Book() {

    }


    // Getter dan Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getPengarang() {
        return pengarang;
    }

    public void setPengarang(String pengarang) {
        this.pengarang = pengarang;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public int getTahunTerbit() {
        return tahunTerbit;
    }

    public void setTahunTerbit(int tahunTerbit) {
        this.tahunTerbit = tahunTerbit;
    }

    // toString() untuk representasi objek
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", judul='" + judul + '\'' +
                ", pengarang='" + pengarang + '\'' +
                ", stok=" + stok +
                ", tahunTerbit=" + tahunTerbit +
                '}';
    }
}