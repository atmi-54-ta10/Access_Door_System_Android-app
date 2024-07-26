package com.embeddedsystem.myapplication.guest;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.embeddedsystem.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Home extends Fragment {

    boolean isLocked = false;
    TextView textClose, textDescription, masterStatus, textnamemaster;
    ImageView doorImageView;
    Button btOpen, btClose, presence;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home2, container, false);

        // Mendapatkan instance dari Calendar untuk mendapatkan waktu sekarang
        Calendar calendar = Calendar.getInstance();

        // Format hari dalam seminggu (misal: Senin, Selasa, dst.)
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        String dayOfWeek = dayFormat.format(calendar.getTime());

        // Format tanggal (misal: 30 Juni 2024)
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
        String currentDate = dateFormat.format(calendar.getTime());

        // Menghubungkan elemen-elemen dalam layout dengan variabel yang ada di kode
        textDescription = rootView.findViewById(R.id.text_description);
        textClose = rootView.findViewById(R.id.text_close);
        btOpen = rootView.findViewById(R.id.bt_open);
        doorImageView = rootView.findViewById(R.id.door_image);
        masterStatus = rootView.findViewById(R.id.master);
        textnamemaster = rootView.findViewById(R.id.text_namemaster);

        // Menetapkan teks pada TextView textDescription
        textDescription.setText(dayOfWeek + ", " + currentDate);

        // Mengatur teks dan gambar berdasarkan status pintu (terkunci atau terbuka)
        if (isLocked) {
            textClose.setText("Door Status: Locked");
            doorImageView.setImageResource(R.drawable.lock_door_hitam);
        } else {
            textClose.setText("Door Status: Open");
            doorImageView.setImageResource(R.drawable.unlock_door_hitam);
        }

        // Menambahkan listener untuk tombol btOpen
        btOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGuestDoor(); // Memanggil metode untuk membuka pintu tamu
            }
        });

        // Memanggil metode untuk memperbarui status pintu dan status master
        updateDoorStatus();
        updateMasterStatus();

        return rootView;
    }

    // Metode untuk mengirim permintaan untuk membuka pintu tamu
    private void openGuestDoor() {
        String url = "http://192.168.2.240:36356/door/opens";

        // Membuat objek JSONObject untuk menyimpan data yang akan dikirim
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("platform", "Android");
            requestData.put("job", "Others");
            requestData.put("name", "Guest");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Membuat request menggunakan Volley untuk mengirim data JSON
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Mendapatkan pesan dari respons JSON dan menampilkannya
                            String message = response.getString("message");
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                            // Memanggil metode untuk memperbarui status pintu dan status master
                            updateDoorStatus();
                            updateMasterStatus();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Menambahkan request ke antrian request Volley
        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
    }

    // Metode untuk memperbarui status pintu
    private void updateDoorStatus() {
        // URL untuk memeriksa status pintu
        String url = "http://192.168.2.240:36356/door/status";

        // Membuat request menggunakan Volley untuk mendapatkan respons JSON
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Mendapatkan status kunci pintu dari respons JSON
                            int lockStatus = response.getInt("lock");

                            // Memperbarui tampilan berdasarkan status pintu
                            if (lockStatus == 1) {
                                // Jika terkunci
                                textClose.setText("Door Status: Locked");
                                doorImageView.setImageResource(R.drawable.lock_door_hitam);
                            } else {
                                // Jika terbuka
                                textClose.setText("Door Status: Unlocked");
                                doorImageView.setImageResource(R.drawable.unlock_door_hitam);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Menambahkan request ke antrian request Volley
        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
    }

    // Metode untuk memperbarui status master
    private void updateMasterStatus() {
        String url = "http://192.168.2.240:36356/master/status";

        // Membuat request menggunakan Volley untuk mendapatkan respons JSON
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Mendapatkan status master dari respons JSON
                            String lockStatus = response.getString("status");
                            masterStatus.setText(lockStatus);

                            // Mendapatkan nama master admin dari respons JSON
                            String masterName = response.getString("name");
                            textnamemaster.setText(masterName);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Menambahkan request ke antrian request Volley
        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
    }
}
