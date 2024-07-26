package com.embeddedsystem.myapplication.admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.embeddedsystem.myapplication.MainActivity;
import com.embeddedsystem.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EditGuest extends AppCompatActivity {

    // Deklarasi variabel UI
    private EditText usernameEditText, idNumberEditText, cardEditText;
    private Button updateButton;
    private User user;
    private String userId;
    private Spinner jobEditText;
    private RequestQueue requestQueue;
    private SharedPreferences loginPreferences;
    private String authToken;

    // Array untuk item pekerjaan
    String[] jobItems = {"Job", "Staff", "Instructor", "Student", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_guest);

        // Inisialisasi EditText dan Button
        usernameEditText = findViewById(R.id.username);
        jobEditText = findViewById(R.id.prodi_spinner);
        idNumberEditText = findViewById(R.id.id_number);
        cardEditText = findViewById(R.id.card);
        updateButton = findViewById(R.id.btn_update);

        // Inisialisasi RequestQueue Volley
        requestQueue = Volley.newRequestQueue(this);

        // Setup Adapter untuk Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jobItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobEditText.setAdapter(adapter);

        // Initialize SharedPreferences dan mengambil token
        loginPreferences = getSharedPreferences("AUTH", Context.MODE_PRIVATE);
        authToken = loginPreferences.getString("token", "");

        // Mengambil data dari Intent dan mengatur ke EditText
        userId = getIntent().getStringExtra("user_id");
        usernameEditText.setText(getIntent().getStringExtra("user_name"));
        String userJob = getIntent().getStringExtra("user_job");
        int position = Arrays.asList(jobItems).indexOf(userJob);
        jobEditText.setSelection(position);
        idNumberEditText.setText(getIntent().getStringExtra("user_id_number"));

        // Menambahkan OnClickListener untuk tombol update
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGuestData();
            }
        });

        // Menambahkan OnClickListener untuk tombol delete
        Button deleteButton = findViewById(R.id.btn_hapus);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUserData();
            }
        });
    }

    // Menghapus data pengguna dari server
    private void deleteUserData() {
        String url = "http://192.168.2.240:36356/user/" + userId;

        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle response jika berhasil dihapus
                        Toast.makeText(EditGuest.this, "Delete Success", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error jika terjadi kesalahan
                        Toast.makeText(EditGuest.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                // Mengatur header Authorization dengan token
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    // Menyimpan data tamu yang telah diubah ke server
    private void saveGuestData() {
        String url = "http://192.168.2.240:36356/user/" + userId;

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("name", usernameEditText.getText().toString());
            requestBody.put("job", jobItems[jobEditText.getSelectedItemPosition()]);
            requestBody.put("id_number", idNumberEditText.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            Toast.makeText(EditGuest.this, message, Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(EditGuest.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error jika terjadi kesalahan
                        error.printStackTrace();
                        Toast.makeText(EditGuest.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Mengatur header Authorization dengan token
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    // Mendapatkan header untuk permintaan HTTP
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + authToken);
        return headers;
    }
}
