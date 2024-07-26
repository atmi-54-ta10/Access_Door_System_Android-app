package com.embeddedsystem.myapplication.admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

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

public class Dashboard extends AppCompatActivity {

    // Deklarasi variabel untuk elemen UI dan request queue
    private TextInputEditText usernameEditText, nameEditText, labEditText, sectionEditText;
    private Button in, out, changePasswordButton;
    private Spinner jobEditText, statusEditText;
    private RequestQueue requestQueue;
    private String authToken;

    private Button saveButton;

    String[] jobItems = {"Job", "Student", "Instructor", "Staff", "Other"};
    String[] statusItems = {"Status","Unavailable", "Available"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Inisialisasi elemen UI
        usernameEditText = findViewById(R.id.username);
        labEditText = findViewById(R.id.txt_lab);
        nameEditText = findViewById(R.id.name);
        jobEditText = findViewById(R.id.prodi_spinner);
        statusEditText = findViewById(R.id.status_spinner);
        sectionEditText = findViewById(R.id.txt_section);
        saveButton = findViewById(R.id.btn_simpan);
        changePasswordButton = findViewById(R.id.btn_chpw);

        in = findViewById(R.id.btn_cardin);
        out = findViewById(R.id.btn_cardout);

        // Inisialisasi adapter untuk spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jobItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobEditText.setAdapter(adapter);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusEditText.setAdapter(adapter2);

        // Inisialisasi request queue
        requestQueue = Volley.newRequestQueue(this);

        // Mengambil token dari SharedPreferences
        SharedPreferences loginPreferences = getSharedPreferences("AUTH", Context.MODE_PRIVATE);
        authToken = loginPreferences.getString("token", "");

        // Set OnClickListener untuk tombol in dan out
        in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCardInRequest();
            }
        });

        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCardOutRequest();
            }
        });

        // Membatasi panjang input pada EditText
        usernameEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        nameEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});

        // Menambahkan text change listeners pada EditText
        addTextChangeListeners();

        // Mengambil status data dari server
        getStatusData();

        // Set OnClickListener untuk tombol save
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEditRequest();
            }
        });

        // Set OnClickListener untuk tombol changePassword
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangePasswordDialog();
            }
        });
    }

    private void addTextChangeListeners() {
        // Menambahkan TextWatcher pada usernameEditText dan nameEditText untuk validasi panjang teks
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 16) {
                    Toast.makeText(Dashboard.this, "PASSWORD DOES NOT MATCH", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 16) {
                    Toast.makeText(Dashboard.this, "PASSWORD DOES NOT MATCH", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void getStatusData() {
        // Mengambil data status dari server
        String url = "http://192.168.2.240:36356/master/";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String username = response.getString("username");
                            String name = response.getString("name");
                            String lab = response.getString("lab");
                            String job = response.getString("job");
                            String section = response.getString("section");
                            String status = response.getString("status");

                            // Mengisi EditText dengan data yang diterima
                            usernameEditText.setText(username);
                            nameEditText.setText(name);
                            sectionEditText.setText(section);
                            labEditText.setText(lab);
                            jobEditText.setSelection(((ArrayAdapter) jobEditText.getAdapter()).getPosition(job));
                            statusEditText.setSelection(((ArrayAdapter) statusEditText.getAdapter()).getPosition(status));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Dashboard.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(Dashboard.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Mengatur header untuk otorisasi
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };

        // Menambahkan request ke request queue
        requestQueue.add(request);
    }

    private void sendCardInRequest() {
        // Mengirim request card in ke server
        String url = "http://192.168.2.240:36356/master/card_in";

        JSONObject requestBody = new JSONObject();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            Toast.makeText(Dashboard.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Dashboard.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(Dashboard.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Mengatur header untuk otorisasi
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };

        // Menambahkan request ke request queue
        requestQueue.add(request);
    }

    private void sendCardOutRequest() {
        // Mengirim request card out ke server
        String url = "http://192.168.2.240:36356/master/card_out";

        JSONObject requestBody = new JSONObject();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            Toast.makeText(Dashboard.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Dashboard.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(Dashboard.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Mengatur header untuk otorisasi
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };

        // Menambahkan request ke request queue
        requestQueue.add(request);
    }

    private void sendEditRequest() {
        // Mengirim data yang telah diedit ke server
        String url = "http://192.168.2.240:36356/master/edit";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", usernameEditText.getText().toString());
            requestBody.put("name", nameEditText.getText().toString());
            requestBody.put("lab", labEditText.getText().toString());
            requestBody.put("job", jobEditText.getSelectedItem().toString());
            requestBody.put("section", sectionEditText.getText().toString());
            requestBody.put("status", statusEditText.getSelectedItem().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(Dashboard.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            Toast.makeText(Dashboard.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Dashboard.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(Dashboard.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Mengatur header untuk otorisasi
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };

        // Menambahkan request ke request queue
        requestQueue.add(request);
    }

    private void showChangePasswordDialog() {
        // Menampilkan dialog untuk mengubah password
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_chpw, null);
        builder.setView(dialogView);

        TextInputEditText oldPasswordEditText = dialogView.findViewById(R.id.pw_txt);
        TextInputEditText newPasswordEditText = dialogView.findViewById(R.id.new_pw_txt);
        Button confirmChangePasswordButton = dialogView.findViewById(R.id.btn_save);

        AlertDialog alertDialog = builder.create();

        // Set OnClickListener untuk tombol confirmChangePassword
        confirmChangePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = oldPasswordEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();

                if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                    Toast.makeText(Dashboard.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    sendChangePasswordRequest(oldPassword, newPassword);
                    alertDialog.dismiss();
                }
            }
        });

        alertDialog.show();
    }

    private void sendChangePasswordRequest(String oldPassword, String newPassword) {
        // Mengirim request untuk mengubah password
        String url = "http://192.168.2.240:36356/master/pw";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("password", oldPassword);
            requestBody.put("new_password", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(Dashboard.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            Toast.makeText(Dashboard.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Dashboard.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(Dashboard.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Mengatur header untuk otorisasi
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };

        // Menambahkan request ke request queue
        requestQueue.add(request);
    }

}
