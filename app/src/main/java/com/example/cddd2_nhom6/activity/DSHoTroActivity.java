package com.example.cddd2_nhom6.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

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

        //Goi chuc nang nhan 2 lan de thoat
        getOnBackPressedDispatcher().addCallback(this, callback);

        hoTroAdapter = new HoTroAdapter(DSHoTroActivity.this,hoTroList);
        binding.recyclerViewApis.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewApis.setAdapter(hoTroAdapter);

        layDuLieuHoTroTuFirebase();

        setSupportActionBar(binding.toolbar);
        // Kiểm tra xem ActionBar đã được khởi tạo chưa
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Quản lý Hỗ Trợ"); // Đặt tên mới cho Toolbar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiện biểu tượng trở về
        }
    }
    // Hiển thị danh sách quốc gia từ Firebase
    private void layDuLieuHoTroTuFirebase() {
        DatabaseReference hoTroRef = FirebaseDatabase.getInstance().getReference("HoTro");
        hoTroRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hoTroList.clear(); // Xóa danh sách cũ
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    HoTro hoTro = dataSnapshot.getValue(HoTro.class);
                    if (hoTro != null) {
                        hoTroList.add(hoTro);
                    } else {
                        Log.e("HoTro", "Dữ liệu null tại " + dataSnapshot.getKey());
                    }
                }
                // Cập nhật RecyclerView
                hoTroAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DSHoTroActivity.this, "Lỗi khi tải dữ liệu!", Toast.LENGTH_SHORT).show();
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
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, AdminActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  // Giữ màn hình sáng khi hoạt động
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  // Tắt giữ màn hình sáng khi dừng hoạt động
    }
}