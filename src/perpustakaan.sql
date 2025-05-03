-- Hapus database
DROP DATABASE perpustakaan;

-- Buat database baru
CREATE DATABASE perpustakaan;

-- Gunakan database
USE perpustakaan;

-- Buat ulang tabel dengan struktur awal
CREATE TABLE `users` (
                         `id` int NOT NULL AUTO_INCREMENT,
                         `username` varchar(50) NOT NULL,
                         `password` varchar(50) NOT NULL,
                         `role` enum('admin','user') NOT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `username` (`username`)
);

CREATE TABLE `books` (
                         `id` int NOT NULL AUTO_INCREMENT,
                         `judul` varchar(100) NOT NULL,
                         `pengarang` varchar(50) NOT NULL,
                         `stok` int NOT NULL,
                         `tahun_terbit` int NOT NULL,
                         PRIMARY KEY (`id`)
);

CREATE TABLE `peminjaman` (
                              `id` int NOT NULL AUTO_INCREMENT,
                              `user_id` int NOT NULL,
                              `book_id` int NOT NULL,
                              `tgl_pinjam` date NOT NULL,
                              `tgl_kembali` date DEFAULT NULL,
                              `status` enum('dipinjam','dikembalikan') NOT NULL,
                              PRIMARY KEY (`id`),
                              KEY `user_id` (`user_id`),
                              KEY `book_id` (`book_id`),
                              CONSTRAINT `peminjaman_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
                              CONSTRAINT `peminjaman_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`)
);