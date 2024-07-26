package com.embeddedsystem.myapplication.admin;

import com.embeddedsystem.myapplication.R;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.embeddedsystem.myapplication.MainActivity;
import androidx.appcompat.app.AppCompatActivity;

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

public class AddData extends AppCompatActivity {

    private TextInputEditText usernameEditText, nameEditText, idnumberEditText, roleEditText, passwordEditText, txtkonfirmasi;
    private Button simpanButton, scan_data;

    private Spinner jobEditText;
    private RequestQueue requestQueue;
    private String userID;

    String[] jobItems = {"Job", "Staff", "Instructor", "Student", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        // Inisialisasi elemen antarmuka pengguna
        usernameEditText = findViewById(R.id.username);
        nameEditText = findViewById(R.id.name);
        idnumberEditText = findViewById(R.id.id_number);
        jobEditText = findViewById(R.id.job_spinner);
        roleEditText = findViewById(R.id.txt_role);
        passwordEditText = findViewById(R.id.txt_password);
        txtkonfirmasi = findViewById(R.id.txt_konfirmasi);
        simpanButton = findViewById(R.id.btn_save);
        scan_data = findViewById(R.id.btn_scan);

        // Set default role
        roleEditText.setText("admin");

        // Mengatur adapter untuk job spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jobItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobEditText.setAdapter(adapter);

        // Membuat antrian permintaan Volley
        requestQueue = Volley.newRequestQueue(this);

        // Event handler untuk tombol scan data
        scan_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanData();
            }
        });

        // Event handler untuk tombol simpan
        simpanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mengambil nilai dari TextInputEditText
                String username = usernameEditText.getText().toString();
                String name = nameEditText.getText().toString();
                String idnumber = idnumberEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String konfirmasiPassword = txtkonfirmasi.getText().toString();
                String role = roleEditText.getText().toString();
                String job = jobEditText.getSelectedItem().toString();

                // Validasi input
                if (username.isEmpty() || name.isEmpty() || idnumber.isEmpty() || password.isEmpty() || konfirmasiPassword.isEmpty() || role.isEmpty() || job.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Semua data harus diisi", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(konfirmasiPassword)) {
                    Toast.makeText(getApplicationContext(), "Password tidak cocok", Toast.LENGTH_SHORT).show();
                } else {
                    // Jika valid, panggil metode signUp
                    signUp();
                    // Redirect ke MainActivity setelah menyimpan data
                    Intent intent = new Intent(AddData.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        // Metode untuk mengatur panjang maksimum input pada TextInputEditText
        setMaxLength(usernameEditText, 16);
        setMaxLength(nameEditText, 16);
        setMaxLength(idnumberEditText, 8);
        setMaxLength(passwordEditText, 16);
    }

    // Metode untuk menetapkan panjang maksimum input pada TextInputEditText
    private void setMaxLength(TextInputEditText editText, int maxLength) {
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > maxLength) {
                    Toast.makeText(AddData.this, "Maaf, karakter melebihi batas maksimum", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Metode untuk mengirim data pendaftaran pengguna ke server melalui API
    private void signUp() {
        String url = "http://192.168.2.240:36356/user/add_data";
        SharedPreferences sharedPreferences = getSharedPreferences("AUTH", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        JSONObject requestBody = new JSONObject();
        try {
            // Mengisi data yang akan dikirimkan ke server
            requestBody.put("username", usernameEditText.getText().toString());
            requestBody.put("name", nameEditText.getText().toString());
            requestBody.put("id_number", idnumberEditText.getText().toString());
            requestBody.put("job", jobEditText.getSelectedItem().toString());
            requestBody.put("role", roleEditText.getText().toString());
            requestBody.put("password", passwordEditText.getText().toString());
            requestBody.put("card", "0"); // Tidak ada informasi tentang card dalam kode asli
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Membuat permintaan POST dengan Volley
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            Toast.makeText(AddData.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AddData.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(AddData.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Mengirimkan token ke server sebagai bagian dari header Authorization
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        // Menambahkan permintaan ke antrian Volley
        requestQueue.add(request);
    }

    // Metode untuk melakukan pemindaian data
    private void scanData() {
        String scanUrl = "http://192.168.2.240/:36356/add_card/" + userID;

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("id", userID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Membuat permintaan POST untuk pemindaian
        JsonObjectRequest scanRequest = new JsonObjectRequest(Request.Method.POST, scanUrl, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            Toast.makeText(AddData.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddData.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Menambahkan permintaan ke antrian Volley
        requestQueue.add(scanRequest);
    }
}
