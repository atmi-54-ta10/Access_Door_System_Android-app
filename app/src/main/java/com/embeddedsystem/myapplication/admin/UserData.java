package com.embeddedsystem.myapplication.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import android.content.SharedPreferences;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.embeddedsystem.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import com.embeddedsystem.myapplication.admin.User;

public class UserData extends AppCompatActivity {

    private Button tambahData;
    private ListView listViewUsers;
    private UserAdapter userAdapter;
    private ArrayList<User> usersList;
    private static final String TAG = "UserData"; // Tag untuk logcat

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);

        // Inisialisasi komponen tampilan
        listViewUsers = findViewById(R.id.listViewUsers);
        usersList = new ArrayList<>();
        userAdapter = new UserAdapter(this, usersList);
        listViewUsers.setAdapter(userAdapter);

        // Mengatur OnClickListener untuk item ListView
        listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User selectedUser = usersList.get(position);
                // Menentukan intent berdasarkan peran pengguna yang dipilih
                if (selectedUser.getRole().equalsIgnoreCase("guest")) {
                    Intent intent = new Intent(UserData.this, EditGuest.class);
                    intent.putExtra("user", selectedUser);
                    startActivity(intent);
                } else if (selectedUser.getRole().equalsIgnoreCase("admin")) {
                    Intent intent = new Intent(UserData.this, EditData.class);
                    intent.putExtra("user", selectedUser);
                    startActivity(intent);
                }
            }
        });

        // Mendapatkan token otentikasi dari SharedPreferences
        SharedPreferences loginPreferences = getSharedPreferences("AUTH", Context.MODE_PRIVATE);
        String auth = loginPreferences.getString("token", "");
        String role = loginPreferences.getString("role", "");

        // Memulai AsyncTask untuk mengambil data pengguna dari server
        new FetchUsersTask(auth).execute("http://192.168.2.240:36356/user/list_user");

        // Mengatur OnClickListener untuk tambahData Button
        tambahData = findViewById(R.id.btn_tambah);
        tambahData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Membuat dialog AlertDialog untuk memilih tipe pengguna baru
                AlertDialog.Builder builder = new AlertDialog.Builder(UserData.this);
                builder.setTitle("Choose User Type");
                builder.setMessage("");
                // Menampilkan opsi "Admin" jika peran pengguna saat ini adalah "master"
                if (role.equalsIgnoreCase("master")) {
                    builder.setPositiveButton("Admin", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(getApplicationContext(), AddData.class);
                            startActivity(intent);
                        }
                    });
                }
                // Menambahkan opsi "Guest" pada AlertDialog
                builder.setNegativeButton("Guest", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getApplicationContext(), AddGuest.class);
                        startActivity(intent);
                    }
                });

                // Membuat dan menampilkan dialog AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    // AsyncTask untuk mengambil data pengguna dari server secara asynchronous (alur program tidak harus berhenti dan menunggu hingga satu tugas selesai sebelum melanjutkan ke tugas berikutnya)
    private class FetchUsersTask extends AsyncTask<String, Void, String> {
        private String auth;

        public FetchUsersTask(String auth) {
            this.auth = auth;
        }

        @Override
        protected String doInBackground(String... strings) {
            String response = "";
            try {
                // Membuat koneksi HTTP untuk mengambil data pengguna dari server
                URL url = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + auth);

                // Membaca data JSON dari respons server
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                reader.close();
                response = stringBuilder.toString();
                Log.d(TAG, "Response from server: " + response);
            } catch (Exception e) {
                Log.e(TAG, "Error fetching data", e);
            }
            return response;
        }

        // Metode yang dipanggil setelah AsyncTask selesai
        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "Result: " + result);
            // Menampilkan pesan kesalahan jika respons kosong atau null
            if (result == null || result.isEmpty()) {
                Toast.makeText(UserData.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // Menguraikan array JSON dari respons server untuk mendapatkan daftar pengguna
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    // Membuat objek User baru dari data JSON
                    User user = new User();
                    if (jsonObject.has("_id")) {
                        user.setId(jsonObject.getString("_id"));
                    }
                    if (jsonObject.has("name")) {
                        user.setName(jsonObject.getString("name"));
                    }
                    if (jsonObject.has("job")) {
                        user.setJob(jsonObject.getString("job"));
                    } else {
                        user.setJob("N/A"); // Nilai default jika atribut "job" tidak ada dalam JSON
                    }
                    if (jsonObject.has("role")) {
                        user.setRole(jsonObject.getString("role"));
                    }
                    if (jsonObject.has("id_number")) {
                        user.setIdNumber(jsonObject.getString("id_number"));
                    }

                    // Menambahkan objek User ke dalam daftar usersList
                    usersList.add(user);
                }
                // Memberitahu adapter bahwa data telah berubah
                userAdapter.notifyDataSetChanged();
                Log.d(TAG, "Users list updated. Size: " + usersList.size());
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing JSON", e);
            }
        }
    }
}
