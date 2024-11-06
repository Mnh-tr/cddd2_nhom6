package com.example.cddd2_nhom6.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.adapter.UserAdapter;
import com.example.cddd2_nhom6.databinding.ActivityQluserBinding;
import com.example.cddd2_nhom6.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class QLUserActivity extends AppCompatActivity {
    private ActivityQluserBinding binding;
    private UserAdapter userAdapter;
    private List<User> userList;
    private DatabaseReference yeuCauRef;
    private DatabaseReference usersRef;
    private DatabaseReference loaiNguoiDungRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQluserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo Firebase reference
        yeuCauRef = FirebaseDatabase.getInstance().getReference("YeuCau");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        loaiNguoiDungRef = FirebaseDatabase.getInstance().getReference("LoaiND");
        // Cài đặt RecyclerView
        hienThiRecyclerView();
        laySoLuongYeuCauHomNay();
        demSoLuongUserVip();
        loadDuLieu();

        // Thiết lập sự kiện click cho từng item
        userAdapter.setRecyclerViewItemClickListener(new UserAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                User clickedUser = userList.get(position);
                Toast.makeText(QLUserActivity.this, "Bạn đã nhấn vào: " + clickedUser.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnYeuCau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QLUserActivity.this, QLYeuCauActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void hienThiRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this);
        binding.recyclerView.setAdapter(userAdapter);
    }
    private void laySoLuongYeuCauHomNay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String ngayHienTai = sdf.format(new Date());

        Query query = yeuCauRef.orderByChild("paymentDate").startAt(ngayHienTai).endAt(ngayHienTai + "\uf8ff");


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int soLuong = (int) dataSnapshot.getChildrenCount();

                Log.d("laySoLuongYeuCauHomNay", "Số lượng yêu cầu hôm nay: " + soLuong);
                binding.tvSLUserDangKy.setText(String.valueOf(soLuong));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QLUserActivity.this, "Lỗi khi lấy dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void demSoLuongUserVip() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int countVipUsers = 0;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Long idLoaiND = userSnapshot.child("id_loaiND").getValue(Long.class);
                    if (idLoaiND != null && idLoaiND == 1) {
                        countVipUsers++;
                    }
                }
                binding.tvSLGoiVipDangKy.setText(String.valueOf(countVipUsers));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QLUserActivity.this, "Lỗi khi lấy dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDuLieu() {
        // Xóa dữ liệu cũ trong danh sách trước khi load mới
        userList.clear();

        loaiNguoiDungRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot loaiDataSnapshot) {
                HashMap<Long, String> dsUser = new HashMap<>();
                for (DataSnapshot loaiSnapshot : loaiDataSnapshot.getChildren()) {
                    Long id = loaiSnapshot.child("id").getValue(Long.class);
                    String type = loaiSnapshot.child("type").getValue(String.class);
                    dsUser.put(id, type);
                }

                usersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userList.clear();

                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String id_user = userSnapshot.child("id_user").getValue(String.class);
                            String name = userSnapshot.child("name").getValue(String.class);
                            String status = userSnapshot.child("status").getValue(String.class);
                            Long idLoaiND = userSnapshot.child("id_loaiND").getValue(Long.class);
                            String goi = dsUser.getOrDefault(idLoaiND, "Thường");

                            if (status == null) status = "offline";
                            userList.add(new User(id_user, name, status, goi));
                        }
                        // Thông báo cho adapter rằng dữ liệu đã thay đổi
                        userAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(QLUserActivity.this, "Lỗi khi lấy dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QLUserActivity.this, "Lỗi khi lấy dữ liệu loại người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}