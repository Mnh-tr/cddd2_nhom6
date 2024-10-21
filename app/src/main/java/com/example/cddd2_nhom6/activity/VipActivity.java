package com.example.cddd2_nhom6.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.cddd2_nhom6.databinding.ActivityVipBinding;
import com.example.cddd2_nhom6.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VipActivity extends AppCompatActivity {
    private ActivityVipBinding binding;
    private String idUser;
    private  String nameUser;
    private String emailUser;
    private int idLoaiND;
    private DatabaseReference yeuCauRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVipBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        laythongtinUser();
        Toast.makeText(VipActivity.this, "Xin chào " + nameUser, Toast.LENGTH_SHORT).show();
// Kết nối tới Firebase
        yeuCauRef = FirebaseDatabase.getInstance().getReference("YeuCau");

        // Kiểm tra xem idUser có trong bảng YeuCau hay chưa
        kiemTraYeuCau(idUser);
        // Xử lý sự kiện chọn item của Bottom Navigation
        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                if (item.getItemId() == R.id.nav_home) {
                    intent = new Intent(VipActivity.this, MainActivity.class);
                } else if (item.getItemId() == R.id.nav_vip) {
                    return true;
                }
                if (intent != null) {
                    intent.putExtra("selected_item_id", item.getItemId());
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;

            }
        });

        binding.btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VipActivity.this, ThanhToanActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private void laythongtinUser(){
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        idUser = sharedPreferences.getString("id_user", null);
        nameUser = sharedPreferences.getString("name", null);
        emailUser  = sharedPreferences.getString("email", null);
        idLoaiND = sharedPreferences.getInt("id_loaiND", 0);

    }

    // Kiểm tra nếu idUser đã có trong bảng YeuCau
    private void kiemTraYeuCau(String idUser) {
        yeuCauRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean found = false;
                for (DataSnapshot yeuCauSnapshot : dataSnapshot.getChildren()) {
                    String idUserInDB = yeuCauSnapshot.child("idUser").getValue(String.class);
                    Integer idTrangThai = yeuCauSnapshot.child("idTrangThai").getValue(Integer.class);

                    // Kiểm tra nếu idUser trùng khớp
                    if (idUserInDB != null && idUserInDB.equals(idUser)) {
                        found = true;

                        // Kiểm tra trạng thái của yêu cầu
                        if (idTrangThai != null && idTrangThai == 1) {
                            // Nếu idTrangThai là 1, đổi màu và text của các nút
                            binding.btnDangKy.setText("Đang sử dụng");
                            binding.btnDangKy.setEnabled(false); // Vô hiệu hóa nút
                            binding.btnDangKy.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00FB21"))); // Xám

                            binding.btnFree.setText("Sử dụng");
                            binding.btnFree.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff66b2"))); // Xanh lá
                        } else {
                            // Nếu idTrangThai không phải là 1 nhưng idUser đã có trong bảng YeuCau
                            binding.btnDangKy.setText("Đang xử lý");
                            binding.btnDangKy.setEnabled(false); // Vô hiệu hóa nút
                            binding.btnDangKy.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff66b2"))); // Hồng ban đầu
                        }
                        break;
                    }
                }

                if (!found) {
                    // idUser chưa có, nút sẽ hoạt động bình thường
                    binding.btnDangKy.setEnabled(true); // Cho phép nhấn vào nút
                    binding.btnDangKy.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff66b2"))); // Hồng ban đầu
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi khi kết nối Firebase thất bại
                Toast.makeText(VipActivity.this, "Lỗi kết nối tới Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }



}