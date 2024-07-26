package com.embeddedsystem.myapplication.admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.embeddedsystem.myapplication.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingAdmin extends AppCompatActivity {

    private TextInputEditText nameEditText, idNumberEditText;
    private Spinner jobEditText;
    private Button saveButton;
    private RequestQueue requestQueue;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] jobItems = {"Job", "Student", "Instructor", "Staff", "Other"};

        setContentView(R.layout.activity_setting_admin);

        // Inisialisasi komponen UI
        nameEditText = findViewById(R.id.name_txt);
        idNumberEditText = findViewById(R.id.id_number);
        jobEditText = findViewById(R.id.job_spinner);
        saveButton = findViewById(R.id.btn_save);

        // Inisialisasi RequestQueue Volley
        requestQueue = Volley.newRequestQueue(this);

        // Ambil token autentikasi dari SharedPreferences
        SharedPreferences loginPreferences = getSharedPreferences("AUTH", Context.MODE_PRIVATE);
        authToken = loginPreferences.getString("token", "");

        // Setup Spinner dengan adapter untuk pekerjaan
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jobItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobEditText.setAdapter(adapter);

        // Ambil data pengguna saat activity dibuat
        getData();

        // Set onClickListener untuk tombol simpan
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEditRequest();
            }
        });
    }

    // Mengambil data pengguna dari server
    private void getData() {
        String url = "http://192.168.2.240:36356/user/";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Ambil data pengguna dari respons JSON
                            String name = response.getString("name");
                            String idNumber = response.getString("id_number");
                            String job = response.getString("job");

                            // Tampilkan data pengguna di UI
                            nameEditText.setText(name);
                            idNumberEditText.setText(idNumber);
                            jobEditText.setSelection(((ArrayAdapter) jobEditText.getAdapter()).getPosition(job));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SettingAdmin.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(SettingAdmin.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Mengirim token autentikasi sebagai header Authorization
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    // Mengirim permintaan untuk menyimpan perubahan data pengguna
    private void sendEditRequest() {
        String url = "http://192.168.2.240:36356/user/";

        // Membuat JSON untuk mengirim data yang akan diubah
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("name", nameEditText.getText().toString());
            requestBody.put("id_number", idNumberEditText.getText().toString());
            requestBody.put("job", jobEditText.getSelectedItem().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(SettingAdmin.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        // Permintaan PUT ke server untuk menyimpan perubahan
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Tampilkan pesan konfirmasi dari respons JSON
                            String message = response.getString("message");
                            Toast.makeText(SettingAdmin.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SettingAdmin.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(SettingAdmin.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Mengirim token autentikasi sebagai header Authorization
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };

        requestQueue.add(request);
    }
}
