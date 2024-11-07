package com.example.cddd2_nhom6.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.databinding.ActivityAdminBinding;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.example.cddd2_nhom6.databinding.ActivityDoanhThuBinding;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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

        // Lắng nghe sự thay đổi dữ liệu trong thời gian thực
        listenForDataChanges();
    }

    private void listenForDataChanges() {
        // Lắng nghe sự thay đổi trong toàn bộ "LichSuThanhToan"
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Xóa lại các giá trị để tính lại
                totalRevenueToday = 0;
                totalRevenue7Days = 0;
                totalRevenue1Month = 0;
                totalTransactions = 0;

                // Duyệt qua tất cả các bản ghi giao dịch
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String ngayXacNhan = childSnapshot.child("ngayXacNhan").getValue(String.class);
                    Long soTien = childSnapshot.child("soTien").getValue(Long.class);

                    if (ngayXacNhan != null && soTien != null) {
                        calculateRevenue(ngayXacNhan, soTien);
                    }
                }

                // Cập nhật lại UI sau khi tính toán xong
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Xử lý lỗi nếu có
                Log.e("DoanhThuActivity", "Lỗi khi lắng nghe dữ liệu: " + error.getMessage());
            }
        });
    }

    private void calculateRevenue(String ngayXacNhan, long soTien) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        try {
            Date transactionDate = sdf.parse(ngayXacNhan);
            long currentTime = System.currentTimeMillis();
            long timeDifference = currentTime - transactionDate.getTime();

            // Doanh thu hôm nay
            if (timeDifference <= TimeUnit.DAYS.toMillis(1)) {
                totalRevenueToday += soTien;
            }

            // Doanh thu 7 ngày trước
            if (timeDifference <= TimeUnit.DAYS.toMillis(7)) {
                totalRevenue7Days += soTien;
            }

            // Doanh thu 1 tháng trước
            if (timeDifference <= TimeUnit.DAYS.toMillis(30)) {
                totalRevenue1Month += soTien;
            }

            totalTransactions++; // Tổng số giao dịch

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUI() {
        // Cập nhật UI thông qua binding (các biến sẽ tự động hiển thị trên giao diện)
        binding.tvTongDoanhThu.setText(String.valueOf(totalRevenueToday));
        binding.tvDoanhThu7Ngay.setText(String.valueOf(totalRevenue7Days));
        binding.tvDoanhThu1Thang.setText(String.valueOf(totalRevenue1Month));
        binding.tvSoGiaoDich.setText(String.valueOf(totalTransactions));
    }
}
