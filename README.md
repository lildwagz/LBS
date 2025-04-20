# Sistem Manajemen Peminjaman Buku Perpustakaan

Sebuah aplikasi desktop untuk mengelola peminjaman buku di perpustakaan menggunakan Java Swing dan MySQL.

## 📚 Fitur yang Tersedia

### 1. Sistem Autentikasi [ Laude ]
- **Login** dengan dua role:
  - **Admin**: Akses penuh ke semua fitur
  - **User**: Hanya bisa meminjam dan melihat riwayat
- **Session Management**: Penyimpanan data user yang login

### 2. Manajemen Buku (Admin Only)
- ✅ Tambah buku baru
- ✏️ Edit informasi buku
- ❌ Hapus buku
- 🔍 Cari buku berdasarkan judul/pengarang (belum ada)
- 📋 Tampilkan daftar buku lengkap [ ammar ] 
- ⚠️ Validasi input (stok tidak boleh negatif)

### 3. Manajemen User (Admin Only)  [ Iqbal] 
- 👥 Tambah user baru (admin/user) (belum ada)
- 📝 Edit data user 
- 🗑️ Hapus user 

### 4. Peminjaman Buku
- 📥 Proses peminjaman (otomatis kurangi stok)
- 📤 Pengembalian buku (update status & stok)
- 📋 Riwayat peminjaman per user
- ⏳ Tampilkan buku yang sedang dipinjam

### 5. Antarmuka Pengguna
- 🖥️ Dashboard admin dengan tabel interaktif
- 📱 Responsive layout dasar
- 💬 Dialog konfirmasi dan error
- 🎨 Tema dasar (belum ada dark mode)

## 🚧 Rencana Pengembangan (Fitur yang Akan Datang)

### 1. Sistem Denda Otomatis
- ⏰ Perhitungan denda keterlambatan
- 💰 Manajemen pembayaran denda
- 📆 Kalender jatuh tempo

### 2. Sistem Reservasi Buku
- 🗓️ Antrian reservasi buku
- 🔔 Notifikasi ketersediaan buku
- ⚠️ Batas waktu pengambilan reservasi

### 3. Laporan Lanjutan
- 📈 Statistik peminjaman (grafik bulanan/tahunan)
- 📉 Analisis buku populer
- 📊 Laporan PDF/Excel

### 4. Keamanan Enhanced
- 🔒 Reset password via email
- 🔑 Validasi kekuatan password
- 📜 Audit log aktivitas sistem

### 5. Fitur Tambahan
- 🔄 Batch import/export data
- 🌓 Dark mode toggle
- 📱 Responsive layout improvement
- 🔍 Pencarian multi-kriteria

  
## 🛠️ Teknologi Digunakan
- **Frontend**: Java Swing
- **Backend**: MySQL
- **Arsitektur**: MVC Pattern
- **Libraries**: 
  - MySQL Connector/J
  - JDBC untuk koneksi database

## 🚀 Panduan Instalasi

### Prasyarat
- Java JDK 8+
- MySQL Server 5.7+
- IDE (Eclipse/IntelliJ)

## 📄 Lisensi
MIT License - Lihat [LICENSE](LICENSE) untuk detail
