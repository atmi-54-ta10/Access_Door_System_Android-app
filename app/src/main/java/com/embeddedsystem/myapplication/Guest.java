package com.embeddedsystem.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.embeddedsystem.myapplication.guest.About;
import com.embeddedsystem.myapplication.guest.Home;
import com.embeddedsystem.myapplication.guest.Profil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Guest extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);

        // Inisialisasi BottomNavigationView dari layout activity_guest.xml
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Memuat fragment Home saat aktivitas dibuat
        loadFragment(new com.embeddedsystem.myapplication.guest.Home());

        // Menetapkan listener untuk BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            // Memilih fragment berdasarkan item yang dipilih
            if (itemId == R.id.home) {
                selectedFragment = new Home();
            } else if (itemId == R.id.barcode) {
                selectedFragment = new About();
            } else if (itemId == R.id.cart) {
                // Pindah ke aktivitas Login saat item "cart" dipilih
                Intent intent = new Intent(Guest.this, Login.class);
                startActivity(intent);
            }

            // Memuat fragment yang dipilih
            return loadFragment(selectedFragment);
        });

    }

    // Metode untuk memuat fragment
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
