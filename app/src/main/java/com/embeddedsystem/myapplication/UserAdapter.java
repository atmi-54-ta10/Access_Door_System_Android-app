package com.embeddedsystem.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

// Deklarasi kelas UserAdapter yang merupakan subclass dari ArrayAdapter<User>
public class UserAdapter extends ArrayAdapter<User> {

    private Context context; // Variabel untuk menyimpan konteks aplikasi
    private List<User> users; // Variabel untuk menyimpan daftar pengguna

    // Konstruktor untuk kelas UserAdapter
    public UserAdapter(Context context, List<User> users) {
        super(context, android.R.layout.simple_list_item_1, users);
        this.context = context;
        this.users = users;
    }

    // Metode override dari ArrayAdapter untuk menampilkan item di dalam list view
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        // Jika view belum ada (null), maka inflate layout baru menggunakan LayoutInflater
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        // Mendapatkan referensi TextView di dalam layout item list (android.R.layout.simple_list_item_1)
        TextView textView = view.findViewById(android.R.id.text1);

        // Mengatur teks TextView dengan nama pengguna pada posisi tertentu
        textView.setText(users.get(position).getName());

        return view; // Mengembalikan tampilan item list yang sudah disetup dengan data yang sesuai
    }
}
