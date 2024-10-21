package com.example.cddd2_nhom6.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.activity.MainActivity;
import com.example.cddd2_nhom6.activity.VipActivity;
import com.example.cddd2_nhom6.databinding.ActivityProfileBinding;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String idUser;
    private  String nameUser;
    private String emailUser;
    private boolean isUserLoggedIn = false; // Biến để theo dõi trạng thái đăng nhập


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // Initialize Data Binding
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setControl();
        setEven();

    }
    public void setControl(){




    }
    public void setEven(){
        laythongtinUser();
        Toast.makeText(ProfileActivity.this, "Xin chào " + nameUser, Toast.LENGTH_SHORT).show();
        binding.tvTenNguoiDung.setText(nameUser);
        binding.tvEmail.setText(emailUser);

        // Kiểm tra trạng thái đăng nhập
        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (idUser != null) {
            isUserLoggedIn = true; // Người dùng đã đăng nhập
            binding.btnDangNhap.setText("Đăng Xuất"); // Đổi văn bản nút thành "Đăng Xuất"
        } else {
            isUserLoggedIn = false; // Người dùng chưa đăng nhập
            binding.btnDangNhap.setText("Đăng Nhập"); // Đổi văn bản nút thành "Đăng Nhập"
        }

        binding.btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserLoggedIn) {// Nếu người dùng đã đăng nhập, tiến hành đăng xuất

                    FirebaseAuth.getInstance().signOut();
                    SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear(); // Xóa tất cả thông tin
                    editor.apply();

                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(ProfileActivity.this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();
                } else {
                    // Nếu người dùng chưa đăng nhập, chuyển đến màn hình đăng nhập
                    Intent intent = new Intent(ProfileActivity.this, DangNhapActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });


        binding.caiDat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
//        binding.updateInfoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(ProfileActivity.this, EditUserActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
        navigationBottom();



        swipeRefreshLayout = binding.swipeRefreshLayout; // Khởi tạo SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(() -> {

        });
    }



    private void laythongtinUser(){
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        idUser = sharedPreferences.getString("id_user", null);
        nameUser = sharedPreferences.getString("name", null);
        emailUser  = sharedPreferences.getString("email", null);


    }


    private void navigationBottom() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Đặt item mặc định được chọn là màn hình Home
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        // Xử lý sự kiện chọn item của Bottom Navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                if (item.getItemId() == R.id.nav_home) {
                    intent = new Intent(ProfileActivity.this, MainActivity.class);
                } else if (item.getItemId() == R.id.nav_vip) {
                    intent = new Intent(ProfileActivity.this, VipActivity.class);
                } else if(item.getItemId() == R.id.nav_profile) {
                    return  true;
                }
                // Pass the selected item to the new Activity
                if (intent != null) {
                    intent.putExtra("selected_item_id", item.getItemId());
                    startActivity(intent);
                    overridePendingTransition(0, 0);  // No animation for smooth transition
                    return true;
                }
                return false;

            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        // Giữ màn hình sáng khi ứng dụng hoạt động
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Xóa cờ giữ màn hình sáng khi ứng dụng không còn hoạt động
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}