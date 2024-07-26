package com.embeddedsystem.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.embeddedsystem.myapplication.admin.About;
import com.embeddedsystem.myapplication.admin.Home;
import com.embeddedsystem.myapplication.admin.Profil;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi BottomNavigationView dari layout XML
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Memuat fragment Home sebagai fragment awal yang ditampilkan
        loadFragment(new Home());

        // Menetapkan listener untuk item yang dipilih pada BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            // Memilih fragment yang akan ditampilkan berdasarkan item yang dipilih
            if (itemId == R.id.home) {
                selectedFragment = new Home();
            } else if (itemId == R.id.barcode) {
                selectedFragment = new About();
            } else if (itemId == R.id.cart) {
                // Mengarahkan ke halaman Login jika item Cart dipilih
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            }

            // Memuat fragment yang dipilih
            return loadFragment(selectedFragment);
        });
    }

    // Metode untuk memuat fragment yang diberikan ke dalam container fragment
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

    @Override
    public void onBackPressed() {
        // Memeriksa apakah ada fragment yang dapat di-pop dari tumpukan kembali
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            // Jika tidak ada fragment di tumpukan kembali, menjalankan default onBackPressed
            super.onBackPressed();
        }
    }
}
