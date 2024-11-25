package com.example.cddd2_nhom6.activity;

import static com.example.cddd2_nhom6.activity.MainActivity.logUserTimeout;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cddd2_nhom6.adapter.ThongBaoAdapter;
import com.example.cddd2_nhom6.databinding.ActivityThongBaoBinding;
import com.example.cddd2_nhom6.model.ThongBao;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ThongBaoActivity extends AppCompatActivity implements ThongBaoAdapter.OnRecyclerViewItemClickListener{
    private ThongBaoAdapter thongBaoAdapter;
    private ActivityThongBaoBinding binding;
    private List<ThongBao> thongBaoList = new ArrayList<>();
    private String idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = ActivityThongBaoBinding.inflate(getLayoutInflater());
       setContentView(binding.getRoot());

       xulyrecyclerView();
       getThongBaoFromDatabase();
       laythongtinUser();
       binding.btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void xulyrecyclerView() {
        binding.thongbaorecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo Adapter và gán cho RecyclerView
        thongBaoAdapter = new ThongBaoAdapter(ThongBaoActivity.this,thongBaoList,this);
        binding.thongbaorecyclerView.setAdapter(thongBaoAdapter);

    }
    private void getThongBaoFromDatabase() {
        DatabaseReference thongBaoRef = FirebaseDatabase.getInstance().getReference().child("ThongBao");

        thongBaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                thongBaoList.clear(); // Xóa danh sách trước khi thêm mới
                for (DataSnapshot thongBaoSnapshot : snapshot.getChildren()) {
                    ThongBao thongBao = thongBaoSnapshot.getValue(ThongBao.class);
                    if (thongBao != null && thongBao.getId_user().equals(idUser)) { // Kiểm tra userId
                        thongBaoList.add(0,thongBao);
                    }
                }
                // Cập nhật RecyclerView với danh sách thông báo mới
                thongBaoAdapter.notifyDataSetChanged(); // Cập nhật adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ThongBaoActivity.this, "Lỗi khi lấy thông báo: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void laythongtinUser(){
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        idUser = sharedPreferences.getString("id_user", null);

    }
    // Hiển thị dialog thông báo
    public void hienDialogThongBao(ThongBao thongBao) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(thongBao.getTitle());
        builder.setMessage(thongBao.getContent());
        Toast.makeText(ThongBaoActivity.this, "Xem thông báo: " + thongBao.getTitle(), Toast.LENGTH_SHORT).show();
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Đánh dấu thông báo là đã đọc
            capNhatTrangThaiThongBao(thongBao.getIdThongBao());
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void capNhatTrangThaiThongBao(String idThongBao) {
        DatabaseReference ThongBaoRef = FirebaseDatabase.getInstance().getReference("ThongBao")
                .child(idThongBao);

        ThongBaoRef.child("id_TrangThai").setValue(1).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Thông báo", "Người dùng đã đọc thông tin này");
            }
        });
    }
    @Override
    public void onBackPressed() {
        // Tạo Intent để chuyển về MainActivity (trang chủ)
        Intent intent = new Intent(ThongBaoActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Đảm bảo không tạo lại nhiều Activity

        // Bắt đầu MainActivity
        startActivity(intent);
        finish();
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

    @Override
    public void onItemClick(View view, ThongBao thongBao) {
        hienDialogThongBao(thongBao);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Ghi timeout khi ứng dụng thực sự bị xóa khỏi bộ nhớ
        if (isFinishing()) {
            logUserTimeout(idUser);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Kiểm tra nếu ứng dụng không thay đổi cấu hình (xoay màn hình, v.v.)
        if (!isChangingConfigurations()) {
            logUserTimeout(idUser);
        }
    }
}