package com.embeddedsystem.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    TextInputEditText username, passwordEdit;
    Button loginButton, loginGuest;
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inisialisasi elemen UI
        username = findViewById(R.id.username);
        passwordEdit = findViewById(R.id.pass);
        loginButton = findViewById(R.id.btn_login);
        loginGuest = findViewById(R.id.btn_guest);

        // Inisialisasi antrian permintaan Volley
        requestQueue = Volley.newRequestQueue(this);
        // Inisialisasi SharedPreferences untuk menyimpan data autentikasi
        sharedPreferences = getSharedPreferences("AUTH", Context.MODE_PRIVATE);

        // Listener untuk tombol login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Panggil metode userLogin() saat tombol login ditekan
                userLogin();
            }
        });

        // Listener untuk tombol login sebagai tamu
        loginGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirect ke aktivitas Guest saat tombol login sebagai tamu ditekan
                Intent i = new Intent(getApplicationContext(), Guest.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void userLogin() {
        // URL endpoint untuk proses login
        String url = "http://192.168.2.240:36356/user/log_in";

        // Buat JSON object dengan username dan password dari input pengguna
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username.getText().toString().trim());
            jsonBody.put("password", passwordEdit.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Buat request POST menggunakan JsonObjectRequest untuk mengirim data login
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Ambil token, nama, dan role dari response JSON
                            String token = response.getString("token");
                            String name = response.getString("name");
                            String role = response.getString("role");

                            // Simpan token, nama, dan role ke SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("token", token);
                            editor.putString("name", name);
                            editor.putString("role", role);
                            editor.apply();

                            // Redirect ke halaman MainActivity setelah login berhasil
                            redirectToMainActivity();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error saat respons error dari server
                        Toast.makeText(Login.this, "LOGIN FAILED", Toast.LENGTH_SHORT).show();
                        Log.e("Login Error", error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Mendapatkan header Authorization dari SharedPreferences untuk setiap request
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + sharedPreferences.getString("token", ""));
                return headers;
            }
        };

        // Menambahkan request ke antrian Volley
        requestQueue.add(jsonObjectRequest);
    }

    // Metode untuk redirect ke MainActivity setelah login berhasil
    private void redirectToMainActivity() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
