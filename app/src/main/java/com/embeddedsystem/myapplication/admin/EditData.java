package com.embeddedsystem.myapplication.admin;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;



import androidx.appcompat.app.AppCompatActivity;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.embeddedsystem.myapplication.MainActivity;
import com.embeddedsystem.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditData extends AppCompatActivity {
    // UI Elements
    private EditText usernameEditText, idNumberEditText, cardEditText;
    private Spinner jobEditText;
    private Button updateButton, scanButton, deleteButton;

    // Variables
    private String userId;
    private String authToken;
    private RequestQueue requestQueue;
    private String[] jobItems = {"Job", "Staff", "Instructor", "Student", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);

        // Initialize SharedPreferences and retrieve token
        SharedPreferences loginPreferences = getSharedPreferences("AUTH", Context.MODE_PRIVATE);
        authToken = loginPreferences.getString("token", "");

        // Initialize RequestQueue for Volley
        requestQueue = Volley.newRequestQueue(this);

        // Initialize UI Elements
        usernameEditText = findViewById(R.id.username);
        jobEditText = findViewById(R.id.prodi_spinner);
        idNumberEditText = findViewById(R.id.id_number);
        cardEditText = findViewById(R.id.card);
        updateButton = findViewById(R.id.btn_update);
        scanButton = findViewById(R.id.btn_scanedit);
        deleteButton = findViewById(R.id.btn_hapus);

        // Setup Spinner Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jobItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobEditText.setAdapter(adapter);

        // Retrieve data from Intent and populate EditText and Spinner
        userId = getIntent().getStringExtra("user_id");
        usernameEditText.setText(getIntent().getStringExtra("user_name"));
        String userJob = getIntent().getStringExtra("user_job");
        int position = getIndex(jobItems, userJob);
        jobEditText.setSelection(position);
        idNumberEditText.setText(getIntent().getStringExtra("user_id_number"));

        // Set OnClickListener for Update Button
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    saveAdminData();
                }
            }
        });

        // Set OnClickListener for Delete Button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Optionally, show confirmation dialog
                deleteUserData();
            }
        });

        // Set OnClickListener for Scan Button
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform RFID scan
                scanData();
            }
        });
    }

    // Method to validate user inputs
    private boolean validateInputs() {
        if (usernameEditText.getText().toString().isEmpty()) {
            usernameEditText.setError("Username cannot be empty");
            return false;
        }
        if (idNumberEditText.getText().toString().isEmpty()) {
            idNumberEditText.setError("ID number cannot be empty");
            return false;
        }
        return true;
    }

    // Method to save updated admin data to server
    private void saveAdminData() {
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
                            Toast.makeText(EditData.this, message, Toast.LENGTH_SHORT).show();
                            // Navigate back to MainActivity
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(EditData.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(EditData.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    // Method to delete user data from server
    private void deleteUserData() {
        String url = "http://192.168.2.240:36356/user/" + userId;

        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(EditData.this, "Delete Success", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EditData.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    // Method to perform RFID scan
    private void scanData() {
        String scanUrl = "http://192.168.2.240:36356/user/add_card/" + userId;


        JsonObjectRequest scanRequest = new JsonObjectRequest(Request.Method.POST, scanUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            Toast.makeText(EditData.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EditData.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };

        requestQueue.add(scanRequest);
    }

    // Method to get index of an item in an array
    private int getIndex(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) {
                return i;
            }
        }
        return 0;
    }
}