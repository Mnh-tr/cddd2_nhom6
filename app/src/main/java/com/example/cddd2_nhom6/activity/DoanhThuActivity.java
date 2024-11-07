package com.example.cddd2_nhom6.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.databinding.ActivityAdminBinding;
import com.google.firebase.database.DatabaseReference;
import com.example.cddd2_nhom6.databinding.ActivityDoanhThuBinding;
import com.google.firebase.database.FirebaseDatabase;

public class DoanhThuActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private ActivityDoanhThuBinding binding;

    private long totalRevenueToday = 0;
    private long totalRevenue7Days = 0;
    private long totalRevenue1Month = 0;
    private long totalTransactions = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoanhThuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("LichSuThanhToan");

    }

}