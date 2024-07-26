package com.embeddedsystem.myapplication;

// Deklarasi kelas User
public class User {

    // Deklarasi variabel instance (fields) yang digunakan dalam kelas User
    private String _id;
    private String name;
    private String prodi;
    private String role;
    private String nomor_induk;

    // Metode getter untuk mendapatkan nilai _id
    public String get_id() {
        return _id;
    }

    // Metode setter untuk mengatur nilai _id
    public void set_id(String _id) {
        this._id = _id;
    }

    // Metode getter untuk mendapatkan nilai name
    public String getName() {
        return name;
    }

    // Metode setter untuk mengatur nilai name
    public void setName(String name) {
        this.name = name;
    }

    // Metode getter untuk mendapatkan nilai prodi
    public String getProdi() {
        return prodi;
    }

    // Metode setter untuk mengatur nilai prodi
    public void setProdi(String prodi) {
        this.prodi = prodi;
    }

    // Metode getter untuk mendapatkan nilai role
    public String getRole() {
        return role;
    }

    // Metode setter untuk mengatur nilai role
    public void setRole(String role) {
        this.role = role;
    }

    // Metode getter untuk mendapatkan nilai nomor_induk
    public String getNomor_induk() {
        return nomor_induk;
    }

    // Metode setter untuk mengatur nilai nomor_induk
    public void setNomor_induk(String nomor_induk) {
        this.nomor_induk = nomor_induk;
    }
}
