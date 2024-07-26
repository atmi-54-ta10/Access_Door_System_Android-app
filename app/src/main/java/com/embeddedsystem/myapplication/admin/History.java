package com.embeddedsystem.myapplication.admin;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.embeddedsystem.myapplication.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class History extends AppCompatActivity {

    private Button btnReport;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private Button btnTempLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Inisialisasi tombol
        btnReport = findViewById(R.id.btn_report);
        btnTempLog = findViewById(R.id.temp_log);

        // Mengatur OnClickListener untuk tombol btnReport
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReportFormatDialog();
            }
        });

        // Mengatur OnClickListener untuk tombol btnTempLog
        btnTempLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTempLogFormatDialog();
            }
        });
    }

    // Memeriksa izin dan memulai proses download file Excel jika izin diberikan
    private void checkPermissionAndDownloadExcel() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Izin belum diberikan, meminta izin kepada pengguna
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            // Izin sudah diberikan, langsung download file Excel
            downloadExcel();
        }
    }

    // Proses download file Excel dari URL yang ditentukan
    private void downloadExcel() {
        // Replace this URL with your actual URL
        String excelUrl = "http://192.168.2.240:36356/action/excel";
        new DownloadExcelTask().execute(excelUrl);
    }

    // AsyncTask untuk melakukan download file Excel secara asynchronous
    private class DownloadExcelTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... urls) {
            String excelUrl = urls[0];
            try {
                URL url = new URL(excelUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = urlConnection.getInputStream();
                    // Di sini bisa membaca InputStream dan menyimpannya ke file
                    // Setelah file tersimpan, bisa membukanya menggunakan library pihak ketiga atau intent
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                Log.e("DownloadExcelTask", "Error downloading Excel file: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Tindakan setelah proses download selesai, seperti menampilkan pesan
            Toast.makeText(History.this, "Excel file downloaded successfully", Toast.LENGTH_SHORT).show();
        }
    }


    // Menampilkan dialog pilihan format laporan
    private void showReportFormatDialog() {
        String excel_url = "http://192.168.2.240:36356/action/excel";
        String pdf_url = "http://192.168.2.240:36356/action/pdf";
        String csv_url = "http://192.168.2.240:36356/action/csv";

        final CharSequence[] formats = {"Excel", "CSV", "PDF"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Export Data Report");
        builder.setItems(formats, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedFormat = formats[which].toString();
                Toast.makeText(History.this, "Selected Report Format: " + selectedFormat, Toast.LENGTH_SHORT).show();
                // Memeriksa jika format yang dipilih adalah Excel, kemudian membuka URL
                if (selectedFormat.equals("Excel")) {
                    openURL(excel_url);
                }
                else if (selectedFormat.equals("PDF")){
                    openURL(pdf_url);
                }
                else if (selectedFormat.equals("CSV")){
                    openURL(csv_url);
                }
            }
        });
        builder.show();
    }

    // Membuka URL menggunakan intent untuk menampilkan dalam browser atau aplikasi eksternal
    private void openURL(String URL) {
        // Ganti URL ini dengan URL yang sesuai
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(URL));

        // Memeriksa jika ada aktivitas yang bisa menangani intent ini
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Memeriksa jika ada aktivitas yang bisa menangani intent ini
            Toast.makeText(this, "No app found to open URL", Toast.LENGTH_SHORT).show();
        }
    }


    // Menampilkan dialog pilihan format log suhu
    private void showTempLogFormatDialog() {
        String excel_url = "http://192.168.2.240:36356/temp/excel";
        String pdf_url = "http://192.168.2.240:36356/temp/pdf";
        String csv_url = "http://192.168.2.240:36356/temp/csv";
        final CharSequence[] formats = {"Excel", "CSV", "PDF"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Export Data Temperature Log");
        builder.setItems(formats, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedFormat = formats[which].toString();
                Toast.makeText(History.this, "Selected Temperature Log Format: " + selectedFormat, Toast.LENGTH_SHORT).show();
                if (selectedFormat.equals("Excel")) {
                    openURL(excel_url);
                }
                else if (selectedFormat.equals("PDF")){
                    openURL(pdf_url);
                }
                else if (selectedFormat.equals("CSV")){
                    openURL(csv_url);
                }
            }
        });
        builder.show();
    }

    // Memproses hasil dari permintaan izin pengguna untuk penyimpanan eksternal
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin diberikan
                downloadExcel();
            } else {
                // Izin ditolak
                Toast.makeText(this, "Permission denied, cannot download Excel file", Toast.LENGTH_SHORT).show();
            }
        }
    }
}