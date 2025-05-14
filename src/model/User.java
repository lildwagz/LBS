package model;

public class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private int JumlahPeminjaman;


    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;

    }

<<<<<<< HEAD
    public User () {
=======
    public User() {
>>>>>>> 8fee44f9b4fb109949b17b73c43853a23d062c8e

    }


    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

}