package com.embeddedsystem.myapplication.guest;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.embeddedsystem.myapplication.R;

/**
 * Fragment untuk menampilkan informasi tentang aplikasi.
 */
public class About extends Fragment {

    // Parameter yang dapat digunakan untuk menginisialisasi fragment
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Variabel untuk menyimpan nilai parameter
    private String mParam1;
    private String mParam2;

    // Konstruktor kosong (diperlukan oleh Fragment)
    public About() {
        // Required empty public constructor
    }

    /**
     * Metode factory untuk membuat instance baru dari fragment ini
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return Instance fragment About dengan parameter yang diberikan.
     */
    public static About newInstance(String param1, String param2) {
        About fragment = new About();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Memeriksa apakah ada argumen yang diberikan saat membuat instance fragment
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Menginisialisasi tata letak untuk fragment ini
        return inflater.inflate(R.layout.fragment_about2, container, false);
    }
}
