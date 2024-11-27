package com.example.cddd2_nhom6.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.adapter.HoTroAdapter;
import com.example.cddd2_nhom6.databinding.ActivityDsHoTroBinding;
import com.example.cddd2_nhom6.model.HoTro;
import com.example.cddd2_nhom6.model.QuocGia;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DSHoTroActivity extends AppCompatActivity {
    private ActivityDsHoTroBinding binding;
    private HoTroAdapter hoTroAdapter;
    private List<HoTro> hoTroList = new ArrayList<>();
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDsHoTroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Loại bỏ dòng findViewById không cần thiết khi đã dùng View Binding

        // Khởi tạo RecyclerView
        hoTroAdapter = new HoTroAdapter(this, hoTroList);
        binding.recyclerViewApis.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewApis.setAdapter(hoTroAdapter);

        // Gọi chức năng nhấn 2 lần để thoát
        getOnBackPressedDispatcher().addCallback(this, callback);

        // Load dữ liệu từ Firebase
        loadHoTroFromFirebase();

        // Cài đặt Toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Quản lý Hỗ Trợ");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // Hiển thị danh sách hỗ trợ từ Firebase
    private void loadHoTroFromFirebase() {
        DatabaseReference hoTroRef = FirebaseDatabase.getInstance().getReference("HoTro");

        hoTroRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hoTroList.clear();

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot hoTroItemSnapshot : userSnapshot.getChildren()) {
                        String name = hoTroItemSnapshot.child("ten").getValue(String.class);
                        String description = hoTroItemSnapshot.child("moTa").getValue(String.class);
                        String time = hoTroItemSnapshot.child("thoiGian").getValue(String.class);
                        String imageUrl = hoTroItemSnapshot.child("imageUrl").getValue(String.class);
                        String userId = userSnapshot.getKey();

                        // Tạo đối tượng HoTro với imageUrl
                        HoTro hoTro = new HoTro(name, description, userId, time, imageUrl);

                        hoTroList.add(hoTro);
                    }
                }

                Collections.sort(hoTroList, (o1, o2) -> o2.getTime().compareTo(o1.getTime()));
                hoTroAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DSHoTroActivity.this,
                        "Lỗi: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Thiết lập OnBackPressedDispatcher
    OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (doubleBackToExitPressedOnce) {
                finishAffinity();  // Thoát ứng dụng
                return;
            }
            doubleBackToExitPressedOnce = true;
            Toast.makeText(getApplicationContext(), "Nhấn thoát thêm một lần nữa", Toast.LENGTH_SHORT).show();

            // Reset lại cờ sau 2 giây
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, AdminActivity.class);
            startActivity(intent);
            finish(); // Đóng activity hiện tại
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}