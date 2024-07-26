package com.embeddedsystem.myapplication.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.Manifest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.embeddedsystem.myapplication.R;




public class Logging extends AppCompatActivity {

    private static final String TAG = "Logging";
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> idnumbers = new ArrayList<>();
    ArrayList<String> jobss = new ArrayList<>();
    ArrayList<String> roles = new ArrayList<>();

    ArrayList<String> userDataList = new ArrayList<>();
    private ListView listViewUsers;
    private Button exportButton;
    private static final int REQUEST_CODE = 1;
    SharedPreferences loginPreferences = getSharedPreferences("AUTH", Context.MODE_PRIVATE);
    String auth = loginPreferences.getString("token","");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logging);

        listViewUsers = findViewById(R.id.listView);
        exportButton = findViewById(R.id.btn_export);

        // Meminta izin penyimpanan jika belum diberikan
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            // Jika izin sudah diberikan, lakukan proses pembuatan file
            fetchDataAndExportToExcel();
        }

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchDataAndExportToExcel();
            }
        });
    }

    private void fetchDataAndExportToExcel() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://192.168.2.240:36356/user/list_user";

        // Request GET ke API
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            ArrayList<String> userDataList = new ArrayList<>();
                            // Parsing JSON response
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject user = response.getJSONObject(i);
                                String identitas = user.getString("_id");
                                String name = user.getString("name");
                                String idnumbers = user.getString("id_number");
                                String jobss = user.getString("job");
                                String role = user.getString("role");

                                // Membuat string data pengguna
                                String userData = "ID: " + identitas + "\nName: " + name + "\nId Number: " + idnumbers + "\nJob: " + jobss + "\nRole: " + role + "\n";

                                // Menambahkan data pengguna ke daftar
                                userDataList.add(userData);
                            }

                            // Membuat adapter untuk ListView
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(Logging.this,
                                    android.R.layout.simple_list_item_1, userDataList);

                            listViewUsers.setAdapter(adapter);
                            
                            exportToExcel(userDataList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Logging.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + auth);
                return headers;
            }
        };
        queue.add(jsonArrayRequest);
    }

    private void exportToExcel(ArrayList<String> userDataList) {
        // Cek kembali izin
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission denied. You can't create a file.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Membuat workbook Excel
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = hssfWorkbook.createSheet("User Data");

        // Header kolom
        HSSFRow headerRow = hssfSheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Nama");
        headerRow.createCell(2).setCellValue("Id Number");
        headerRow.createCell(3).setCellValue("Job");
        headerRow.createCell(4).setCellValue("Role");

        // Data pengguna
        for (int i = 0; i < userDataList.size(); i++) {
            String userData = userDataList.get(i);
            String[] userDataSplit = userData.split("\n");

            HSSFRow dataRow = hssfSheet.createRow(i + 1);
            for (int j = 0; j < userDataSplit.length; j++) {
                String[] userDataParts = userDataSplit[j].split(": ");
                if (userDataParts.length == 2) {
                    dataRow.createCell(j).setCellValue(userDataParts[1]);
                }
            }
        }

        // Simpan workbook ke file Excel
        try {
            File documentsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "Data_" + timeStamp + ".xls";
            File fileOutput = new File(documentsDirectory, fileName);

            FileOutputStream fileOutputStream = new FileOutputStream(fileOutput);
            hssfWorkbook.write(fileOutputStream);
            fileOutputStream.close();
            hssfWorkbook.close();

            Toast.makeText(this, "Excel file created successfully:" + fileName, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to save Excel file", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Jika izin diberikan, lakukan proses pembuatan file
                fetchDataAndExportToExcel();
            } else {
                // Jika izin ditolak, berikan pesan kepada pengguna
                Toast.makeText(this, "Permission denied. You can't create a file.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}