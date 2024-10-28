package com.example.cddd2_nhom6.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.databinding.ActivityAdminBinding;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class AdminActivity extends AppCompatActivity {

    private ActivityAdminBinding binding;
    private DatabaseReference dataUser;
    private DatabaseReference dataTruyCap;
    private DatabaseReference dataThanhToan;
    private long startOfDay;
    private long endOfDay;
    private Calendar calendar = Calendar.getInstance();

    private Button selectedButton = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataUser = FirebaseDatabase.getInstance().getReference("Users");
        dataTruyCap = FirebaseDatabase.getInstance().getReference("TruyCap");
        dataThanhToan = FirebaseDatabase.getInstance().getReference("ThanhToan");


        xulyBieuDo();
        xulybuttonMenu();

    }
    private void xulyBieuDo() {
        // Lấy giá trị từ các TextView
        String doanhThuText = binding.tvDoanhThuAmount.getText().toString();
        String truyCapText = binding.tvTruyCapAmount.getText().toString();
        String luotDangKyText = binding.tvLuotDangKyAmount.getText().toString();
        String goiVIPText = binding.tvGoiVIPAmount.getText().toString();

        // Chuyển đổi giá trị từ TextView thành float
        float doanhThu = parseValue(doanhThuText);
        float truyCap = parseValue(truyCapText);
        float luotDangKy = parseValue(luotDangKyText);
        float goiVIP = parseValue(goiVIPText);

        // Tạo dữ liệu cho BarChart
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, doanhThu));
        barEntries.add(new BarEntry(1, truyCap));
        barEntries.add(new BarEntry(2, luotDangKy));
        barEntries.add(new BarEntry(3, goiVIP));

        BarDataSet barDataSet = new BarDataSet(barEntries, "Thống kê");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f); // Độ rộng của cột


        // Cập nhật BarChart
        binding.barChart.setData(barData);
        binding.barChart.setFitBars(true); // Đảm bảo các cột vừa với biểu đồ
        binding.barChart.invalidate(); // Làm mới biểu đồ

        // Tùy chỉnh Legend (chú thích) cho biểu đồ
        Legend legend = binding.barChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM); // Đặt legend ở dưới
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER); // Canh giữa
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL); // Hiển thị theo chiều ngang
        legend.setDrawInside(false); // Để bên ngoài biểu đồ
        legend.setWordWrapEnabled(true); // Tự động xuống dòng nếu cần
        legend.setYOffset(10f); // Khoảng cách giữa legend và biểu đồ
        legend.setXOffset(0f); // Khoảng cách bên trái/phải của legend
        legend.setTextSize(12f); // Kích thước chữ của legend

        // Đặt màu sắc và nhãn cho mỗi dữ liệu (thay thế cho tên chung "Thống kê")
        ArrayList<LegendEntry> legendEntries = new ArrayList<>();
        legendEntries.add(new LegendEntry("Doanh thu", Legend.LegendForm.SQUARE, 10f, 2f, null, ColorTemplate.MATERIAL_COLORS[0]));
        legendEntries.add(new LegendEntry("Truy cập", Legend.LegendForm.SQUARE, 10f, 2f, null, ColorTemplate.MATERIAL_COLORS[1]));
        legendEntries.add(new LegendEntry("Lượt đăng ký", Legend.LegendForm.SQUARE, 10f, 2f, null, ColorTemplate.MATERIAL_COLORS[2]));
        legendEntries.add(new LegendEntry("Gói VIP", Legend.LegendForm.SQUARE, 10f, 2f, null, ColorTemplate.MATERIAL_COLORS[3]));

        legend.setCustom(legendEntries); // Đặt các ghi chú tùy chỉnh vào legend

    }
    // Phương thức để chuyển đổi giá trị từ chuỗi thành float
    private float parseValue(String value) {
        value = value.replaceAll("[^\\d.]", ""); // Xóa các ký tự không phải số
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    //xu ly button menu
    //xu ly button menu
    private void xulybuttonMenu() {
        binding.ivButtonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the menu layout
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.menu_layout_admin, null);

                // Create the PopupWindow
                PopupWindow popupWindow = new PopupWindow(popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        true);

                // Show the PopupWindow at the desired location
                popupWindow.showAsDropDown(binding.ivButtonMenu, 0, 0);

                // Handle Doanh thu button click
                popupView.findViewById(R.id.btn_doanh_thu).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Chuyển sang trang Doanh thu
//                        Intent intent = new Intent(AdminActivity.this, QLPhimActivity.class);
//                        startActivity(intent);
//                        popupWindow.dismiss();  // Đóng PopupWindow sau khi nhấn
                    }
                });

                popupView.findViewById(R.id.btn_ThongBao).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdminActivity.this, DSThongBaoActivity.class);
                        startActivity(intent);
                        popupWindow.dismiss();  // Đóng PopupWindow sau khi nhấn
                    }
                });


            }
        });
    }
}