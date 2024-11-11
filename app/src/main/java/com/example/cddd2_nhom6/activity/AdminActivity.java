package com.example.cddd2_nhom6.activity;

import static java.lang.Long.parseLong;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.databinding.ActivityAdminBinding;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.chip.Chip;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AdminActivity extends AppCompatActivity {

    private ActivityAdminBinding binding;
    private DatabaseReference dataUser;
    private DatabaseReference dataTruyCap;
    private DatabaseReference dataThanhToan;
    private long startOfDay;
    private long endOfDay;
    private Calendar calendar = Calendar.getInstance();
    private Chip a = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataUser = FirebaseDatabase.getInstance().getReference("Users");
        dataTruyCap = FirebaseDatabase.getInstance().getReference("TruyCap");
        dataThanhToan = FirebaseDatabase.getInstance().getReference("LichSuThanhToan");

        // Đặt cho tất cả
        updateSelectedButton(binding.btnAll);
        hienThiTatCaThongTin();

        xulyXemThongTin();
        xulybuttonMenu();


    }



    private void xulyXemThongTin() {
        // Lắng nghe sự kiện khi nhấn nút "Hôm nay

        binding.btnAll.setOnClickListener(view -> {
            updateSelectedButton(binding.btnAll);
            hienThiTatCaThongTin();
        });

        binding.btnHomNay.setOnClickListener(view -> {

            updateSelectedButton(binding.btnHomNay);
            layThongTinDangKy();
            layThongTInDoanhThu();
            layThongTinTruyCapHomNay();
            // Gọi hàm lấy thông tin truy cập hôm nay
        });
// Lắng nghe sự kiện khi nhấn nút "3 ngày qua"
        binding.btn3ngay.setOnClickListener(view -> {

            updateSelectedButton(binding.btn3ngay);
            layThongTinDKTrongKhoangThoiGian(3);
            laythongtinDoanhThuTrongKhoang(3);
            layThongTinTruyCapTrongKhoangThoiGian(3);
            // Gọi hàm lấy thông tin truy cập 7 ngày qua
        });
        // Lắng nghe sự kiện khi nhấn nút "7 ngày qua"
        binding.btn7Ngay.setOnClickListener(view -> {

            updateSelectedButton(binding.btn7Ngay);
            layThongTinDKTrongKhoangThoiGian(7);
            laythongtinDoanhThuTrongKhoang(7);
            layThongTinTruyCapTrongKhoangThoiGian(7);
            // Gọi hàm lấy thông tin truy cập 7 ngày qua
        });
        // Lắng nghe sự kiện khi nhấn nút "1 tháng qua"
        binding.btnThang.setOnClickListener(view -> {

            updateSelectedButton(binding.btnThang);
            layThongTinDKTrongKhoangThoiGian(30);
            laythongtinDoanhThuTrongKhoang(30);
            layThongTinTruyCapTrongKhoangThoiGian(30);
            // Cập nhật số lượng truy cập 1 tháng qua
        });
        // Lắng nghe sự kiện khi nhấn nút "1 năm qua"
        binding.btnNam.setOnClickListener(view -> {

            updateSelectedButton(binding.btnNam);
            layThongTinDKTrongKhoangThoiGian(365);
            laythongtinDoanhThuTrongKhoang(365);
            layThongTinTruyCapTrongKhoangThoiGian(365);
            // Cập nhật số lượng truy cập 1 tháng qua
        });
    }

    private void hienThiTatCaThongTin() {

        dataUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int slDangKy = 0;
                for (DataSnapshot data : snapshot.getChildren()){
                      slDangKy++;
                }
                binding.tvLuotDangKyAmount.setText(""+ slDangKy);
                xulyBieuDo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dataThanhToan.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double doanhthu = 0;
                int goi = 0;
                for (DataSnapshot data : snapshot.getChildren()){
                    Long soTien = data.child("soTien").getValue(Long.class);
                    doanhthu += soTien;
                    goi++;
                }
                // Định dạng và hiển thị số lượng gói VIP
                DecimalFormat decimalFormat = new DecimalFormat("#,###");
                String formattedDoanhThu = decimalFormat.format(doanhthu);
                binding.tvDoanhThuAmount.setText(formattedDoanhThu + " đ");
                binding.tvGoiVIPAmount.setText("" + goi);

                xulyBieuDo();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dataTruyCap.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int soluongTruycap = 0;
                for (DataSnapshot data : snapshot.getChildren()){
                        soluongTruycap++;
                }

                binding.tvTruyCapAmount.setText("" + soluongTruycap);

                xulyBieuDo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    // Phương thức để cập nhật màu của các nút
    // Phương thức để cập nhật màu của các nút Chip
    private void updateSelectedButton(Chip newButton) {
        // Nếu nút đã được chọn khác nút hiện tại, đổi màu
        if (a != null) {
            a.setChipBackgroundColorResource(R.color.colorDefault); // Màu nền mặc định
            a.setTextColor(getResources().getColor(R.color.defaultTextColor)); // Màu chữ mặc định
        }

        // Cập nhật nút hiện tại và đổi màu
        a = newButton;
        a.setChipBackgroundColorResource(R.color.colorSelected); // Màu nền đã chọn
        a.setTextColor(getResources().getColor(R.color.selectedTextColor)); // Màu chữ đã chọn
    }


    private void layThongTinDangKy() {
        LayThoigianNgayHomNay();

        dataUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int userTodayCount = 0;

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Long createdAt = userSnapshot.child("created_at").getValue(Long.class);

                    // Kiểm tra thời gian và tính toán
                    if (createdAt != null && createdAt >= startOfDay && createdAt <= endOfDay) {
                        userTodayCount++; // Tăng số lượng người dùng đăng ký hôm nay
                    }
                }


                binding.tvLuotDangKyAmount.setText("" + userTodayCount);
                // Gọi hàm xulyBieuDo() sau khi đã cập nhật dữ liệu
                xulyBieuDo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi khi đọc dữ liệu: " + error.getMessage());
            }
        });
    }
    private void layThongTinDKTrongKhoangThoiGian(int soNgay) {
        calendar.setTimeInMillis(System.currentTimeMillis()); // Đặt lại về hiện tại
        long startTime = LayThoigianCachDay(soNgay);
        long endTime = System.currentTimeMillis();

        dataUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int userCount = 0;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Long createdAt = userSnapshot.child("created_at").getValue(Long.class);
                    Long idLoaiND = userSnapshot.child("id_loaiND").getValue(Long.class);

                    if (createdAt != null && createdAt >= startTime && createdAt <= endTime) {
                        userCount++;
                    }
                }
                binding.tvLuotDangKyAmount.setText("" + userCount);
                // Gọi hàm xulyBieuDo() sau khi đã cập nhật dữ liệu
                xulyBieuDo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi khi đọc dữ liệu: " + error.getMessage());
            }
        });
    }

    private void layThongTInDoanhThu() {
        LayThoigianNgayHomNay(); // 23:59:59 hôm nay

        dataThanhToan.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double doanhthu = 0;
                int goi = 0;
                for (DataSnapshot data : snapshot.getChildren()){
                    //Long trangThai = data.child("idTrangThai").getValue(Long.class);
                    String ngayxacnhan = data.child("ngayXacNhan").getValue(String.class);
                    Long soTien = data.child("soTien").getValue(Long.class);
                    if (ngayxacnhan != null ) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            Date date = sdf.parse(ngayxacnhan); // Chuyển đổi chuỗi thành Date
                            long timeInMillis = date.getTime(); // Lấy thời gian mili giây

                            // Kiểm tra xem timeInMillis có nằm trong khoảng startOfDay và endOfDay không
                            if (timeInMillis >= startOfDay && timeInMillis <= endOfDay) {
                                doanhthu += soTien;
                                goi++;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace(); // In lỗi nếu không thể phân tích cú pháp
                        }
                    }

                }
                // Định dạng và hiển thị số lượng gói VIP
                DecimalFormat decimalFormat = new DecimalFormat("#,###");
                String formattedDoanhThu = decimalFormat.format(doanhthu);
                binding.tvDoanhThuAmount.setText(formattedDoanhThu + " đ");
                binding.tvGoiVIPAmount.setText("" + goi);
                // Gọi hàm xulyBieuDo() sau khi đã cập nhật dữ liệu
                xulyBieuDo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi khi đọc dữ liệu: " + error.getMessage());
            }
        });
    }
    // doanh thu
    private void laythongtinDoanhThuTrongKhoang(int soNgay) {

        long startTime = LayThoigianCachDay(soNgay);
        long endTime = System.currentTimeMillis();

        dataThanhToan.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double doanhthu = 0;
                int goi = 0;
                for (DataSnapshot data : snapshot.getChildren()){
                    //Long trangThai = data.child("idTrangThai").getValue(Long.class);
                    String ngayxacnhan = data.child("ngayXacNhan").getValue(String.class);
                    Long soTien = data.child("soTien").getValue(Long.class);
                    if (ngayxacnhan != null) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            Date date = sdf.parse(ngayxacnhan); // Chuyển đổi chuỗi thành Date
                            long timeInMillis = date.getTime(); // Lấy thời gian mili giây

                            // Kiểm tra xem timeInMillis có nằm trong khoảng startOfDay và endOfDay không
                            if (timeInMillis >= startTime && timeInMillis <= endTime) {
                                doanhthu += soTien;
                                goi++;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace(); // In lỗi nếu không thể phân tích cú pháp
                        }
                    }

                }
                // Định dạng và hiển thị số lượng gói VIP
                DecimalFormat decimalFormat = new DecimalFormat("#,###");
                String formattedDoanhThu = decimalFormat.format(doanhthu);
                binding.tvDoanhThuAmount.setText(formattedDoanhThu + " đ");
                binding.tvGoiVIPAmount.setText("" + goi);
                // Gọi hàm xulyBieuDo() sau khi đã cập nhật dữ liệu
                xulyBieuDo();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    // Truy Caap
    private void layThongTinTruyCapHomNay() {
        LayThoigianNgayHomNay(); // 23:59:59 hôm nay

        dataTruyCap.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot accessSnapshot : snapshot.getChildren()) {
                    String timestamp = accessSnapshot.child("thoigiantruycap").getValue(String.class);
                    if (timestamp != null) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            Date date = sdf.parse(timestamp); // Chuyển đổi chuỗi thành Date
                            long timeInMillis = date.getTime(); // Lấy thời gian mili giây

                            // Kiểm tra xem timeInMillis có nằm trong khoảng startOfDay và endOfDay không
                            if (timeInMillis >= startOfDay && timeInMillis <= endOfDay) {
                                count++;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace(); // In lỗi nếu không thể phân tích cú pháp
                        }
                    }
                }

                binding.tvTruyCapAmount.setText("" + count);
                // Gọi hàm xulyBieuDo() sau khi đã cập nhật dữ liệu
                xulyBieuDo();
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi khi đọc dữ liệu: " + error.getMessage());
            }
        });
    }
    private void layThongTinTruyCapTrongKhoangThoiGian(int soNgay) {

        long startTime = LayThoigianCachDay(soNgay);
        long endTime = System.currentTimeMillis();


        dataTruyCap.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot accessSnapshot : snapshot.getChildren()) {
                    String timestamp = accessSnapshot.child("thoigiantruycap").getValue(String.class);

                    if (timestamp != null) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            Date date = sdf.parse(timestamp); // Chuyển đổi chuỗi thành Date
                            long timeInMillis = date.getTime(); // Lấy thời gian mili giây

                            // Kiểm tra xem timeInMillis có nằm trong khoảng startOfDay và endOfDay không
                            if (timeInMillis >= startTime && timeInMillis <= endTime) {
                                count++;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace(); // In lỗi nếu không thể phân tích cú pháp
                        }
                    }
                }

                binding.tvTruyCapAmount.setText("" + count);
                // Gọi hàm xulyBieuDo() sau khi đã cập nhật dữ liệu
                xulyBieuDo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi khi đọc dữ liệu: " + error.getMessage());
            }
        });
    }
    // Lay thoi gian ngay hom nay
    private void LayThoigianNgayHomNay() {
        calendar = Calendar.getInstance();  // Đặt lại calendar về thời gian hiện tại
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        startOfDay = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        endOfDay = calendar.getTimeInMillis();
    }

    private long LayThoigianCachDay(int soNgay) {
        calendar = Calendar.getInstance();  // Đặt lại calendar về thời gian hiện tại
        calendar.add(Calendar.DAY_OF_YEAR, -soNgay);
        return calendar.getTimeInMillis();
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
        barEntries.add(new BarEntry(0, truyCap));
        barEntries.add(new BarEntry(1, luotDangKy));
        barEntries.add(new BarEntry(2, goiVIP));
        barEntries.add(new BarEntry(3, doanhThu));

        BarDataSet barDataSet = new BarDataSet(barEntries, "Thống kê");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.8f); // Đặt độ rộng của cột nhỏ lại để tạo khoảng cách

        // Định dạng giá trị hiển thị trên các cột
        barData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                DecimalFormat decimalFormat = new DecimalFormat("#,###");
                return decimalFormat.format(value);
            }
        });
        barData.setValueTextSize(12f); // Kích thước chữ cho giá trị

        // Cập nhật BarChart
        binding.barChart.setData(barData);
        binding.barChart.setFitBars(true); // Đảm bảo các cột vừa với biểu đồ
        binding.barChart.invalidate(); // Làm mới biểu đồ


        // Bỏ tiêu đề của biểu đồ
        binding.barChart.getDescription().setEnabled(false);


        // Tùy chỉnh Legend (chú thích) cho biểu đồ
        Legend legend = binding.barChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setWordWrapEnabled(true);
        legend.setYOffset(10f);
        legend.setXOffset(0f);
        legend.setTextSize(12f);

        // Đặt màu sắc và nhãn cho mỗi dữ liệu (thay thế cho tên chung "Thống kê")
        ArrayList<LegendEntry> legendEntries = new ArrayList<>();
        legendEntries.add(new LegendEntry("Truy cập", Legend.LegendForm.SQUARE, 10f, 2f, null, ColorTemplate.MATERIAL_COLORS[0]));
        legendEntries.add(new LegendEntry("Lượt đăng ký", Legend.LegendForm.SQUARE, 10f, 2f, null, ColorTemplate.MATERIAL_COLORS[1]));
        legendEntries.add(new LegendEntry("Gói VIP", Legend.LegendForm.SQUARE, 10f, 2f, null, ColorTemplate.MATERIAL_COLORS[2]));
        legendEntries.add(new LegendEntry("Doanh thu", Legend.LegendForm.SQUARE, 10f, 2f, null, ColorTemplate.MATERIAL_COLORS[3]));
        legend.setCustom(legendEntries);

        // Tùy chỉnh trục X để hiển thị nhãn cho từng cột
        XAxis xAxis = binding.barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Truy cập", "Đăng ký", "Gói VIP", "Doanh thu"}));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        // Ẩn lưới trên trục Y
        binding.barChart.getAxisLeft().setDrawGridLines(false);
        binding.barChart.getAxisRight().setDrawGridLines(false);
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
        // Xử lý khi nhấn vào ImageView để mở menu
        binding.ivButtonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    binding.drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        // Xử lý sự kiện khi người dùng chọn các mục trong NavigationView
        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Intent a = null;
                if (item.getItemId() == R.id.itemQLPhim) {
                     a = new Intent(AdminActivity.this, QLPhimActivity.class);
//                    return handled = true;
                } else if (item.getItemId() == R.id.itemQLDoanhThu) {
                    // Mở giao diện Quản Lý Doanh Thu
                     a = new Intent(AdminActivity.this, DoanhThuActivity.class);
                } else if (item.getItemId() == R.id.itemQLUser) {
                    // Mở giao diện Quản Lý User
                     a = new Intent(AdminActivity.this, QLUserActivity.class);
                } else if (item.getItemId() == R.id.itemQLTheLoai) {
                    // Mở giao diện Quản Lý Thể Loại
                     a = new Intent(AdminActivity.this, QLTheLoaiActivity.class);
                } else if (item.getItemId() == R.id.itemQLQuocGia) {
                    // Mở giao diện Quản Lý Quốc Gia
//                    Intent a = new Intent(AdminActivity.this, QL.class);
                } else if (item.getItemId() == R.id.itemQLThongBao) {
                    // Mở giao diện Quản Lý Thông Báo
                     a = new Intent(AdminActivity.this, QLThongBaoActivity.class);
                } else if (item.getItemId() == R.id.itemHoTro) {
                    // Hiển thị thông tin hỗ trợ
                     a = new Intent(AdminActivity.this, QLHoTroActivity.class);
                } else if (item.getItemId() == R.id.itemQLApi) {
                    // Mở giao diện Quản Lý Api
                     a = new Intent(AdminActivity.this, QuanLyAPI.class);
                }
                else if (item.getItemId() == R.id.itemthat) {
                    // Mở giao diện Quản Lý Api
                     a = new Intent(AdminActivity.this, CaNhanActivity.class);
                }
                // Đóng ngăn kéo nếu đã xử lý sự kiện
                if (a != null) {
                    //binding.drawerLayout.closeDrawer(GravityCompat.START);
                    startActivity(a);
                    return true;
                }
                return false;
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
                popupView.findViewById(R.id.btn_hotro).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdminActivity.this, DSHoTroActivity.class);
                        startActivity(intent);
                        popupWindow.dismiss();
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

                popupView.findViewById(R.id.btn_ql_api).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdminActivity.this, QuanLyAPI.class);
                        startActivity(intent);
                        popupWindow.dismiss();  // Đóng PopupWindow sau khi nhấn
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
                popupView.findViewById(R.id.btn_ql_user).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdminActivity.this, QLUserActivity.class);
                        startActivity(intent);
                        popupWindow.dismiss();  // Đóng PopupWindow sau khi nhấn
                    }
                });
                popupView.findViewById(R.id.btn_ql_theloai).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(AdminActivity.this, QLTheLoaiActivity.class);
                        startActivity(intent);
                        popupWindow.dismiss();  // Đóng PopupWindow sau khi nhấn
                    }
                });
                popupView.findViewById(R.id.btn_ql_quocgia).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Chuyển sang trang Quản lý Quốc gia
                        Intent intent = new Intent(AdminActivity.this, QLQuocGiaActivity.class);
                        startActivity(intent);
                        popupWindow.dismiss();  // Đóng PopupWindow sau khi nhấn
                    }
                });

            }
        });
    }

}