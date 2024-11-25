package com.example.cddd2_nhom6.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cddd2_nhom6.adapter.UserVipAdapter;
import com.example.cddd2_nhom6.databinding.ActivityDsvipBinding;
import com.example.cddd2_nhom6.model.LichSuThanhToan;
import com.example.cddd2_nhom6.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DSVipActivity extends AppCompatActivity {
    private ActivityDsvipBinding binding;
    private UserVipAdapter adapter;
    private List<User> userList = new ArrayList<>();
    private List<LichSuThanhToan> paymentHistoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDsvipBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Thiết lập RecyclerView
        binding.rvVipUsers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserVipAdapter(this, userList, paymentHistoryList);
        binding.rvVipUsers.setAdapter(adapter);
// Thiết lập ActionBar và DrawerLayout
        setSupportActionBar(binding.toolbar);
        // Kiểm tra xem ActionBar đã được khởi tạo chưa
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiện biểu tượng trở về

        }
        // Tải dữ liệu từ Firebase
        loadUsers();
        loadPaymentHistory();
    }

    private void loadUsers() {
        FirebaseDatabase.getInstance().getReference("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            User user = userSnapshot.getValue(User.class);
                            if (user != null && user.getId_loaiND() == 1) { // Chỉ thêm nếu id_loaiND là 1
                                userList.add(user);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DSVipActivity.this, "Lỗi khi tải danh sách người dùng!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void loadPaymentHistory() {
        FirebaseDatabase.getInstance().getReference("LichSuThanhToan")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        paymentHistoryList.clear();
                        for (DataSnapshot userPaymentSnapshot : snapshot.getChildren()) {
                            for (DataSnapshot paymentSnapshot : userPaymentSnapshot.getChildren()) {
                                LichSuThanhToan payment = paymentSnapshot.getValue(LichSuThanhToan.class);
                                if (payment != null) {
                                    paymentHistoryList.add(payment);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DSVipActivity.this, "Lỗi khi tải lịch sử thanh toán!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, QLUserActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
