package com.embeddedsystem.myapplication.admin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.embeddedsystem.myapplication.R;

import java.util.ArrayList;

// Kelas UserAdapter merupakan adapter khusus untuk menampilkan daftar pengguna dalam ListView atau RecyclerView
public class UserAdapter extends ArrayAdapter<User> {

    // Konstruktor untuk inisialisasi adapter dengan data pengguna
    public UserAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
    }

    // Metode getView untuk mengubah data pengguna menjadi tampilan yang sesuai untuk setiap item dalam ListView atau RecyclerView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Mendapatkan objek User pada posisi tertentu
        User user = getItem(position);

        // Memastikan convertView tidak null untuk menghindari inflate berulang
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }

        // Mendapatkan referensi TextView untuk setiap atribut pengguna
        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvJob = convertView.findViewById(R.id.tvJob);
        TextView tvRole = convertView.findViewById(R.id.tvRole);
        TextView tvIdNumber = convertView.findViewById(R.id.tvIdNumber);

        // Menetapkan nilai teks untuk TextView berdasarkan atribut pengguna
        tvName.setText(user.getName());
        tvJob.setText(user.getJob());
        tvRole.setText(user.getRole());
        tvIdNumber.setText(user.getIdNumber());

        // Menambahkan OnClickListener untuk item ListView atau RecyclerView
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Membuat intent untuk navigasi ke EditData atau EditGuest tergantung peran pengguna
                Intent intent;
                if ("guest".equals(user.getRole())) {
                    intent = new Intent(getContext(), EditGuest.class);
                } else {
                    intent = new Intent(getContext(), EditData.class);
                }

                // Menyertakan data pengguna sebagai ekstra dalam intent
                intent.putExtra("user_id", user.getId());
                intent.putExtra("user_name", user.getName());
                intent.putExtra("user_job", user.getJob());
                intent.putExtra("user_role", user.getRole());
                intent.putExtra("user_id_number", user.getIdNumber());

                // Memulai aktivitas dengan intent yang disiapkan
                getContext().startActivity(intent);
            }
        });

        // Mengembalikan tampilan item ListView atau RecyclerView yang sudah diubah
        return convertView;
    }
}
