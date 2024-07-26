package com.embeddedsystem.myapplication.admin;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.embeddedsystem.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Monitoring extends AppCompatActivity {

    // Deklarasi TextView untuk menampilkan status dan data monitoring
    private TextView textStatus, textMaster, textCard, textPanel, textEmTemperature, textPanelTime, textEmTime, textmastername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring2);

        // Inisialisasi TextView dari layout
        textStatus = findViewById(R.id.text_status);
        textMaster = findViewById(R.id.master);
        textCard = findViewById(R.id.text_card);
        textPanel = findViewById(R.id.text_panel);
        textEmTemperature = findViewById(R.id.text_em_temperature);
        textPanelTime = findViewById(R.id.text_paneltime);
        textEmTime = findViewById(R.id.text_emtime);
        textmastername = findViewById(R.id.mastername);

        // RequestQueue untuk mengirimkan request HTTP menggunakan Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // URL endpoint untuk mengambil status pintu
        String url1 = "http://192.168.2.240:36356/door/status";
        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest
                (Request.Method.GET, url1, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Ambil nilai status dari response JSON
                            int status = response.getInt("lock");
                            int card = response.getInt("tap_card");

                            // Set teks status berdasarkan nilai status
                            if (status == 1) {
                                textStatus.setText("LOCKED");
                            } else if (status == 0) {
                                textStatus.setText("UNLOCKED");
                            }

                            // Set teks jumlah kartu yang ditempelkan
                            textCard.setText(String.valueOf(card));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Tampilkan pesan kesalahan jika request gagal
                        textStatus.setText("Gagal mengambil data");
                    }
                });

        // URL endpoint untuk mengambil status master
        String url2 = "http://192.168.2.240:36356/master/status";
        JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest
                (Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Ambil status master dari response JSON
                            String masterStatus = response.getString("status");
                            textMaster.setText(masterStatus);

                            // Ambil nama master admin dari response JSON
                            String masterName = response.getString("name");
                            textmastername.setText(masterName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            textMaster.setText("Gagal mengambil data master");
                            textmastername.setText("Gagal mengambil nama master");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Tampilkan pesan kesalahan jika request gagal
                        textMaster.setText("Gagal mengambil data master");
                        textmastername.setText("Gagal mengambil nama master");
                    }
                });

        // URL endpoint untuk mengambil data suhu panel dan emlock
        String url3 = "http://192.168.2.240:36356/temp/";
        // Data yang akan dipost ke server, dapat diganti dengan nilai aktual suhu
        JSONObject postData = new JSONObject();
        try {
            postData.put("panel_temp", "your_panel_temp_value"); // Ganti dengan nilai suhu panel aktual
            postData.put("emlock_temp", "your_em_temperature_value"); // Ganti dengan nilai suhu emlock aktual
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest3 = new JsonObjectRequest
                (Request.Method.GET, url3, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Ambil nilai suhu panel dan emlock dari response JSON
                            String panelTemp = response.getString("panel_temp");
                            String emLockTemp = response.getString("emlock_temp");

                            // Set teks suhu panel dan emlock
                            textPanel.setText(panelTemp);
                            textEmTemperature.setText(emLockTemp);

                            // Ambil tanggal dan waktu saat ini
                            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss", Locale.ENGLISH);
                            String currentDateAndTime = sdf.format(new Date());

                            // Set tanggal dan waktu saat ini ke TextViews
                            textPanelTime.setText(currentDateAndTime);
                            textEmTime.setText(currentDateAndTime);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Tampilkan pesan kesalahan jika request gagal
                        textPanel.setText("Gagal mengambil data suhu");
                    }
                });

        // Menambahkan request ke RequestQueue
        requestQueue.add(jsonObjectRequest1);
        requestQueue.add(jsonObjectRequest2);
        requestQueue.add(jsonObjectRequest3);
    }
}
