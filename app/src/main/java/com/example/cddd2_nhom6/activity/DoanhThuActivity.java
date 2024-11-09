package com.example.cddd2_nhom6.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.databinding.ActivityAdminBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.example.cddd2_nhom6.databinding.ActivityDoanhThuBinding;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DoanhThuActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private ActivityDoanhThuBinding binding;

    private HashMap<Integer, Long> monthlyRevenue;
    private long tongDTHomNay = 0;
    private long tongDT7Ngay = 0;
    private long tongDT1Thang = 0;
    private long tongGDHomNay = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoanhThuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("LichSuThanhToan");

        // Khởi tạo HashMap để lưu doanh thu theo tháng
        monthlyRevenue = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            monthlyRevenue.put(i, 0L);
        }
        // Lắng nghe sự thay đổi dữ liệu trong thời gian thực
        loadDuLieuDoanhThu();
        layDuLieuVaHienThiBieuDo();
    }

    private void loadDuLieuDoanhThu() {
        // Lắng nghe sự thay đổi trong toàn bộ "LichSuThanhToan"
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Xóa lại các giá trị để tính lại
                tongDTHomNay = 0;
                tongDT7Ngay = 0;
                tongDT1Thang = 0;
                tongGDHomNay = 0;

                // Duyệt qua tất cả các bản ghi giao dịch
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String ngayXacNhan = childSnapshot.child("ngayXacNhan").getValue(String.class);
                    Long soTien = childSnapshot.child("soTien").getValue(Long.class);

                    if (ngayXacNhan != null && soTien != null) {
                        tinhDoanhThu(ngayXacNhan, soTien);
                    }
                }

                // Cập nhật lại UI sau khi tính toán xong
                capNhapDuLieu();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Xử lý lỗi nếu có
                Log.e("DoanhThuActivity", "Lỗi khi lắng nghe dữ liệu: " + error.getMessage());
            }
        });
    }

    private void tinhDoanhThu(String ngayXacNhan, long soTien) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        try {
            Date transactionDate = sdf.parse(ngayXacNhan);
            long currentTime = System.currentTimeMillis();
            long timeDifference = currentTime - transactionDate.getTime();

            // Doanh thu hôm nay
            if (timeDifference <= TimeUnit.DAYS.toMillis(1)) {
                tongDTHomNay += soTien;
                tongGDHomNay++; // Tổng số giao dịch
            }

            // Doanh thu 7 ngày trước
            if (timeDifference <= TimeUnit.DAYS.toMillis(7)) {
                tongDT7Ngay += soTien;
            }

            // Doanh thu 1 tháng trước
            if (timeDifference <= TimeUnit.DAYS.toMillis(30)) {
                tongDT1Thang += soTien;
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void capNhapDuLieu() {
        // Cập nhật UI thông qua binding (các biến sẽ tự động hiển thị trên giao diện)
        binding.tvTongDoanhThu.setText(String.valueOf(tongDTHomNay));
        binding.tvDoanhThu7Ngay.setText(String.valueOf(tongDT7Ngay));
        binding.tvDoanhThu1Thang.setText(String.valueOf(tongDT1Thang));
        binding.tvSoGiaoDich.setText(String.valueOf(tongGDHomNay));
    }

    private void hienThiBieuDo() {
        ArrayList<Entry> entries = new ArrayList<>();

        // Chuẩn bị dữ liệu từ HashMap để vẽ biểu đồ
        for (int month = 1; month <= 12; month++) {
            entries.add(new Entry(month, monthlyRevenue.get(month)));
        }

        // Tạo LineDataSet và thiết lập cho biểu đồ
        LineDataSet lineDataSet = new LineDataSet(entries, "Doanh Thu Hàng Tháng");
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleColor(Color.RED);
        lineDataSet.setCircleRadius(4f);// Kích thước của điểm tròn
        lineDataSet.setValueTextSize(10f);
        lineDataSet.setValueTextColor(Color.BLACK);

        LineData lineData = new LineData(lineDataSet);
        binding.lineChart.setData(lineData);

        // // Thiết lập cho trục X (hiển thị từ tháng 1 đến tháng 12)
        XAxis xAxis = binding.lineChart.getXAxis();
        xAxis.setGranularity(1f);// Khoảng cách giữa các nhãn trục X
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(12);// Đặt số lượng nhãn trục X là 12
        xAxis.setAxisMinimum(1f);
        xAxis.setAxisMaximum(12f);

        // Thiết lập cho trục Y(dùng để hiển thị doanh thu)
        YAxis leftAxis = binding.lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);

        binding.lineChart.getAxisRight().setEnabled(false);
        binding.lineChart.getDescription().setEnabled(false);
        binding.lineChart.invalidate(); // Refresh biểu đồ
    }
    private void capNhatDoanhThuHangThang(String ngayXacNhan, long soTien) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        try {
            Date transactionDate = sdf.parse(ngayXacNhan);
            SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
            int month = Integer.parseInt(monthFormat.format(transactionDate));

            // Cộng dồn doanh thu cho tháng tương ứng
            monthlyRevenue.put(month, monthlyRevenue.get(month) + soTien);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void layDuLieuVaHienThiBieuDo() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Reset dữ liệu doanh thu hàng tháng
                for (int i = 1; i <= 12; i++) {
                    monthlyRevenue.put(i, 0L);
                }

                // Duyệt qua tất cả các bản ghi giao dịch
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String ngayXacNhan = childSnapshot.child("ngayXacNhan").getValue(String.class);
                    Long soTien = childSnapshot.child("soTien").getValue(Long.class);

                    if (ngayXacNhan != null && soTien != null) {
                        capNhatDoanhThuHangThang(ngayXacNhan, soTien);
                    }
                }

                // Hiển thị biểu đồ
                hienThiBieuDo();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });
    }
}
