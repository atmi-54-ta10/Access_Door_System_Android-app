package com.embeddedsystem.myapplication.admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Home extends Fragment {

    // Status awal pintu, default tidak terkunci
    private boolean isLocked = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout untuk tampilan fragment_home
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inflate layout untuk dialog kontrol
        View dialogView = getLayoutInflater().inflate(R.layout.activity_controll, null);

        // Mendapatkan data login dari SharedPreferences
        SharedPreferences loginPreferences = requireActivity().getSharedPreferences("AUTH", Context.MODE_PRIVATE);
        String name = loginPreferences.getString("name", "");
        String auth = loginPreferences.getString("token", "");
        String role = loginPreferences.getString("role", "");

        // Mendapatkan status pintu dari SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("DoorStatus", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("status", isLocked ? "Locked" : "Unlocked");
        editor.apply();

        // Inisialisasi komponen UI dari dialog kontrol
        Button unlockButton = dialogView.findViewById(R.id.btn_unlock);
        Button lockButton = dialogView.findViewById(R.id.btn_lock);
        Button openButton = dialogView.findViewById(R.id.btn_open);
        TextView textStatus = dialogView.findViewById(R.id.text_status);
        TextView text_master = dialogView.findViewById(R.id.text_available);
        TextView text_namamaster = dialogView.findViewById(R.id.text_namamaster);

        // Set nama master admin ke TextView
        text_namamaster.setText(name);

        // Inisialisasi komponen UI dari fragment_home
        ImageView Menu = view.findViewById(R.id.dash);
        ImageView User = view.findViewById(R.id.user_data);
        ImageView Mon = view.findViewById(R.id.monitor);
        ImageView riwayat = view.findViewById(R.id.bt_history);
        TextView status = view.findViewById(R.id.text_status);
        TextView sett = view.findViewById(R.id.text_setting);
        TextView lab_name = view.findViewById(R.id.text_lab);

        // Mengambil data dari API untuk mengupdate UI
        fetchDataFromApi(lab_name, text_master, textStatus);
        status.setText(name);

        // Menampilkan atau menyembunyikan menu berdasarkan peran pengguna
        if (role != null) {
            if (role.equals("admin")) {
                Menu.setVisibility(View.VISIBLE);
                sett.setVisibility(View.VISIBLE);
            } else if (role.equals("master")) {
                Menu.setVisibility(View.VISIBLE);
                sett.setVisibility(View.VISIBLE);
            } else {
                Menu.setVisibility(View.GONE);
                sett.setVisibility(View.GONE);
                view.findViewById(R.id.bt_controll).setVisibility(View.VISIBLE);
            }
        } else {
            status.setText("Unknown Role");
            Menu.setVisibility(View.GONE);
            view.findViewById(R.id.bt_controll).setVisibility(View.VISIBLE);
        }

        try {
            // Menyiapkan aksi saat tombol kontrol diklik
            ImageView controll = view.findViewById(R.id.bt_controll);
            TextView textDescription = view.findViewById(R.id.text_description);

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
            String dayOfWeek = dayFormat.format(calendar.getTime());

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
            String currentDate = dateFormat.format(calendar.getTime());

            textDescription.setText(dayOfWeek + ", " + currentDate);

            controll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fetchDataFromApi(lab_name, text_master, textStatus);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    // Menampilkan dialog kontrol berdasarkan peran pengguna
                    if (role != null) {
                        if (role.equals("admin")) {
                            // Meng-handle peran admin jika diperlukan
                        } else if (role.equals("master")) {
                            // Meng-handle peran master jika diperlukan
                        } else {
                            status.setText("Other Roles");
                            view.findViewById(R.id.bt_controll).setVisibility(View.VISIBLE);
                        }
                    } else {
                        status.setText("Unknown Role");
                        view.findViewById(R.id.bt_controll).setVisibility(View.VISIBLE);
                    }

                    builder.setView(dialogView);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("platform", "Android");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Aksi ketika tombol lock diklik
                    lockButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String url = "http://192.168.2.240:36356/door/lock";
                            sendHttpRequest(url, jsonBody, lab_name, text_master, textStatus);
                        }
                    });

                    // Aksi ketika tombol unlock diklik
                    unlockButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String url = "http://192.168.2.240:36356/door/unlock";
                            sendHttpRequest(url, jsonBody, lab_name, text_master, textStatus);
                        }
                    });

                    // Aksi ketika tombol open diklik
                    openButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String url = "http://192.168.2.240:36356/door/open";
                            sendHttpRequest(url, jsonBody, lab_name, text_master, textStatus);
                        }
                    });
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "An error occurred when trying to access a UI element", Toast.LENGTH_SHORT).show();
        }

        // Aksi saat tombol Menu diklik
        Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (role != null) {
                    Intent intent;
                    if (role.equals("admin")) {
                        intent = new Intent(getContext(), SettingAdmin.class);
                    } else if (role.equals("master")) {
                        intent = new Intent(getContext(), Dashboard.class);
                    } else {
                        intent = new Intent(getContext(), MainActivity.class);
                    }
                    startActivity(intent);  // Memulai activity sesuai dengan intent
                }
            }
        });

        // Aksi saat tombol User diklik
        User.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), UserData.class);
                startActivity(i);
            }
        });

        // Aksi saat tombol Monitoring diklik
        Mon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), Monitoring.class);
                startActivity(i);
            }
        });

        // Aksi saat tombol Riwayat diklik
        riwayat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), History.class);
                startActivity(i);
            }
        });

        return view;  // Mengembalikan tampilan yang sudah diinflate
    }

    // Method untuk mengambil data dari API dan mengupdate UI
    private void fetchDataFromApi(final TextView lab_name, final TextView text_master, final TextView text_door) {
        String url = "http://192.168.2.240:36356/master/status";
        String url_door = "http://192.168.2.240:36356/door/status";

        // Request data master status dari API
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String lab = response.getString("lab");
                            String stat_master = response.getString("status");
                            lab_name.setText(lab);
                            text_master.setText("Master Availability : " + stat_master);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        lab_name.setText("Error: " + error.getMessage());
                    }
                });

        // Request status pintu dari API
        JsonObjectRequest jsonObjectRequest_door = new JsonObjectRequest(Request.Method.GET, url_door, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String doorLockStatus = response.getString("lock");
                            text_door.setText(doorLockStatus.equals("1") ? "Door Status : Locked" : "Door Status : Unlocked");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        lab_name.setText("Error: " + error.getMessage());
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(jsonObjectRequest);
        requestQueue.add(jsonObjectRequest_door);
    }

    // Method untuk mengirim HTTP request ke API dengan method PUT
    private void sendHttpRequest(String url, JSONObject jsonBody, final TextView lab_name, final TextView text_master, final TextView text_door) {
        SharedPreferences loginPreferences = requireActivity().getSharedPreferences("AUTH", Context.MODE_PRIVATE);
        String auth = loginPreferences.getString("token", "");

        RequestQueue queue = Volley.newRequestQueue(getContext());
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + auth);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getContext(), response.toString(), Toast.LENGTH_SHORT).show();
                        fetchDataFromApi(lab_name, text_master, text_door);  // Update status after response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }
}
