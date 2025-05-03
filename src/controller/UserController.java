package controller;

import dao.UserDAO;
import exception.CustomException;
import model.User;
import util.Session;

import java.util.List;

public class UserController {
    private final UserDAO userDAO;

    public UserController() {
        this.userDAO = new UserDAO();
    }

    // Method untuk mendapatkan semua user
    public List<User> getAllUsers() throws CustomException {
        try {
            List<User> users = userDAO.getAllUsers();
            if(users.isEmpty()) {
                throw new CustomException("Tidak ada data user");
            }
            return users;
        } catch (Exception e) {
            throw new CustomException("Gagal mengambil data user: " + e.getMessage());
        }
    }

    // Method untuk mencari user berdasarkan username
    public List<User> searchUsers(String keyword) throws CustomException {
        try {
            List<User> users = userDAO.searchUsers(keyword);
            if(users.isEmpty()) {
                throw new CustomException("User tidak ditemukan");
            }
            return users;
        } catch (Exception e) {
            throw new CustomException("Gagal mencari user: " + e.getMessage());
        }
    }

    // Method untuk menambahkan user baru
    public void addUser(User user) throws CustomException {
        try {
            validateUserInput(user);

            if(userDAO.getUserByUsername(user.getUsername()) != null) {
                throw new CustomException("Username sudah digunakan");
            }

            if(!userDAO.addUser(user)) {
                throw new CustomException("Gagal menyimpan user ke database");
            }
        } catch (Exception e) {
            throw new CustomException("Error: " + e.getMessage());
        }
    }

    // Method untuk update user
    public void updateUser(User user) throws CustomException {
        try {
            validateUserInput(user);

            if(!userDAO.updateUser(user)) {
                throw new CustomException("Gagal memperbarui data user");
            }
        } catch (Exception e) {
            throw new CustomException("Error: " + e.getMessage());
        }
    }

    // Method untuk menghapus user
    public void deleteUser(int userId) throws CustomException {
        try {
            if(userId == Session.getCurrentUser().getId()) {
                throw new CustomException("Tidak bisa menghapus user yang sedang aktif");
            }

            if(!userDAO.deleteUser(userId)) {
                throw new CustomException("Gagal menghapus user");
            }
        } catch (Exception e) {
            throw new CustomException("Error: " + e.getMessage());
        }
    }

    // Method untuk mendapatkan user by ID
    public User getUserById(int userId) throws CustomException {
        try {
            User user = userDAO.getUserById(userId);
            if(user == null) {
                throw new CustomException("User tidak ditemukan");
            }
            return user;
        } catch (Exception e) {
            throw new CustomException("Error: " + e.getMessage());
        }
    }

    // Validasi input user
    private void validateUserInput(User user) throws CustomException {
        if(user.getUsername().length() < 5) {
            throw new CustomException("Username minimal 5 karakter");
        }

        if(user.getPassword().length() < 6) {
            throw new CustomException("Password minimal 6 karakter");
        }

        if(!user.getRole().matches("admin|user")) {
            throw new CustomException("Role harus admin atau user");
        }
    }
}