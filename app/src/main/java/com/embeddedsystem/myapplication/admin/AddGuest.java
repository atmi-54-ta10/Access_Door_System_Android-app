package com.embeddedsystem.myapplication.admin;

import android.content.Context;
import android.content.Intent;
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
import com.embeddedsystem.myapplication.MainActivity;
import com.embeddedsystem.myapplication.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddGuest extends AppCompatActivity {

    private TextInputEditText usernameEditText, idnumberEditText, roleEditText, cardEditText;
    private Button simpanButton;
    private Spinner jobEditText;
    private RequestQueue requestQueue;
    private String url = "http://192.168.2.240:36356/user/add_data"; // URL server API

    String[] jobItems = {"Job", "Staff", "Instructor", "Student", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_guest);

        // Inisialisasi elemen antarmuka pengguna
        usernameEditText = findViewById(R.id.username);
        jobEditText = findViewById(R.id.prodi_spinner);
        idnumberEditText = findViewById(R.id.no_induk);
        roleEditText = findViewById(R.id.txt_role);
        cardEditText = findViewById(R.id.txt_card);
        simpanButton = findViewById(R.id.btn_simpan);

        // Set nilai default untuk role
        roleEditText.setText("guest");

        // Membuat antrian permintaan Volley
        requestQueue = Volley.newRequestQueue(this);

        // Mengatur adapter untuk spinner pekerjaan
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jobItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobEditText.setAdapter(adapter);

        // Event handler untuk tombol simpan
        simpanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mengambil nilai dari TextInputEditText
                String username = usernameEditText.getText().toString();
                String noInduk = idnumberEditText.getText().toString();
                String role = roleEditText.getText().toString();
                String card = cardEditText.getText().toString();
                String job = jobEditText.getSelectedItem().toString();

                // Validasi input
                if (username.isEmpty() || noInduk.isEmpty() || role.isEmpty() || card.isEmpty() || job.equals("Job")) {
                    Toast.makeText(getApplicationContext(), "Harap isi semua kolom", Toast.LENGTH_SHORT).show();
                } else {
                    // Jika valid, panggil metode signUp
                    signUp();
                    // Redirect ke MainActivity setelah menyimpan data
                    Intent i = new Intent(AddGuest.this, MainActivity.class);
                    startActivity(i);
                }
            }
        });
    }

    // Metode untuk mengirim data pendaftaran ke server melalui API
    private void signUp() {
        SharedPreferences sharedPreferences = getSharedPreferences("AUTH", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        JSONObject requestBody = new JSONObject();
        try {
            // Mengisi data yang akan dikirimkan ke server
            requestBody.put("name", usernameEditText.getText().toString());
            requestBody.put("job", jobEditText.getSelectedItem().toString());
            requestBody.put("id_number", idnumberEditText.getText().toString());
            requestBody.put("role", roleEditText.getText().toString());
            requestBody.put("card", cardEditText.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Membuat permintaan POST dengan Volley
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Menangani respon dari server
                            String message = response.getString("message");
                            Toast.makeText(AddGuest.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AddGuest.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Menangani kesalahan jika permintaan gagal
                        error.printStackTrace();
                        Toast.makeText(AddGuest.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
}
