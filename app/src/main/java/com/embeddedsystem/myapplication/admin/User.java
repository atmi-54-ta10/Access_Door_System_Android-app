package com.embeddedsystem.myapplication.admin;

import java.io.Serializable;

// Kelas User merepresentasikan entitas pengguna dengan beberapa atribut
public class User implements Serializable {

    // Variabel instance untuk menyimpan data pengguna
    private String id;         // ID pengguna
    private String name;       // Nama pengguna
    private String job;        // Pekerjaan pengguna
    private String role;       // Peran pengguna
    private String idNumber;   // Nomor identitas pengguna
    private String Card;       // Kartu pengguna

    // Constructor untuk inisialisasi objek User dengan nilai atribut
    public User(String id, String name, String job, String role, String idNumber, String Card) {
        this.id = id;
        this.name = name;
        this.job = job;
        this.role = role;
        this.idNumber = idNumber;
        this.Card = Card;
    }

    // Default constructor kosong
    public User() {}

    // Metode getter dan setter untuk setiap atribut

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getCard() {
        return Card;
    }

    public void setCard(String Card) {
        this.Card = Card;
    }
}
