package com.example.cddd2_nhom6.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.adapter.UserVipAdapter;
import com.example.cddd2_nhom6.databinding.ActivityDsvipBinding;
import com.example.cddd2_nhom6.model.LichSuThanhToan;
import com.example.cddd2_nhom6.model.User;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DSVipActivity extends AppCompatActivity {
    private ActivityDsvipBinding binding;
    private UserVipAdapter adapter;
    private List<User> userList = new ArrayList<>();
    private List<LichSuThanhToan> paymentHistoryList = new ArrayList<>();
    private SimpleDateFormat dateFormat;
    private Chip a = null;
    private int selectedChipId = R.id.chipAll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDsvipBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

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


        // Thiết lập sự kiện Chip
        setupChipListeners();

        // Hiển thị danh sách tất cả người dùng VIP khi vào màn hình
        updateSelectedButton(binding.chipAll);
        hienThiTatCaUsersVip();
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

    private void hienThiTatCaUsersVip() {
        loadUsers();
        loadPaymentHistory();
        binding.chipAll.setChecked(true);
        adapter.capNhatDanhSach(userList); // Hiển thị toàn bộ danh sách người dùng VIP
    }
    private void setupChipListeners() {
        binding.chipAll.setOnClickListener(v -> {
            updateSelectedButton(binding.chipAll);
            hienThiTatCaUsersVip();
        });

        binding.chipExpireSoon.setOnClickListener(v -> {
            updateSelectedButton(binding.chipExpireSoon);
            capNhatNguoiDungSapHetHan();
        });

        binding.chipNewUsers.setOnClickListener(v -> {
            updateSelectedButton(binding.chipNewUsers);
            capNhatNguoiDungMoiDangKy();
        });
    }

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

        selectedChipId = newButton.getId();
    }
    private void capNhatNguoiDungSapHetHan() {
        DatabaseReference lichSuThanhToanRef = FirebaseDatabase.getInstance().getReference("LichSuThanhToan");
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        Date sevenDaysLater = calendar.getTime();

        List<User> filteredUsers = new ArrayList<>();  // Danh sách người dùng hợp lệ


        lichSuThanhToanRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userIdSnapshot : dataSnapshot.getChildren()) {
                    String id_user = userIdSnapshot.getKey(); // id_user cha
                    filteredUsers.clear();// Đảm bảo làm sạch danh sách trước khi thêm người dùng mới
                    for (DataSnapshot transactionSnapshot : userIdSnapshot.getChildren()) {
                        String ngayHetHanStr = transactionSnapshot.child("ngayHetHan").getValue(String.class);

                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                            Date ngayHetHan = sdf.parse(ngayHetHanStr);

                            if (ngayHetHan != null && ngayHetHan.after(today) && ngayHetHan.before(sevenDaysLater)) {
                                usersRef.orderByChild("id_user").equalTo(id_user).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot usersSnapshot) {
                                        for (DataSnapshot userSnapshot : usersSnapshot.getChildren()) {
                                            User user = userSnapshot.getValue(User.class);
                                            if (user != null) {
                                                filteredUsers.add(user);  // Thêm user vào danh sách
                                            }
                                        }
                                        adapter.capNhatDanhSach(filteredUsers);  // Cập nhật adapter
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("FirebaseError", error.getMessage());
                                    }
                                });
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                onFirebaseDataChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }





    private void capNhatNguoiDungMoiDangKy() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        DatabaseReference lichSuThanhToanRef = FirebaseDatabase.getInstance().getReference("LichSuThanhToan");

        Calendar calendar = Calendar.getInstance();
        long todayMillis = calendar.getTimeInMillis();  // Ngày hôm nay (millisecond)

        List<User> filteredUsers = new ArrayList<>(); // Danh sách người dùng hợp lệ
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                // Xóa dữ liệu cũ trong danh sách mỗi khi bắt đầu lọc lại
                filteredUsers.clear();  // Đảm bảo danh sách được làm sạch trước khi thêm người dùng mới

                for (DataSnapshot userChild : userSnapshot.getChildren()) {
                    User user = userChild.getValue(User.class);

                    if (user != null) {
                        // Kiểm tra ngày hết hạn của user trong bảng LichSuThanhToan
                        lichSuThanhToanRef.child(user.getId_user()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot paymentSnapshot) {
                                if (paymentSnapshot.exists()) { // Nếu có lịch sử thanh toán
                                    for (DataSnapshot paymentChild : paymentSnapshot.getChildren()) {
                                        // Lấy ngày hết hạn từ lịch sử thanh toán (dưới dạng chuỗi)
                                        String ngayHetHanStr = paymentChild.child("ngayHetHan").getValue(String.class);

                                        if (ngayHetHanStr != null) {
                                            try {
                                                // Chuyển đổi ngày hết hạn từ String sang Date
                                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                                                Date ngayHetHan = sdf.parse(ngayHetHanStr);
                                                if (ngayHetHan != null) {
                                                    long expirationMillis = ngayHetHan.getTime();  // Ngày hết hạn tính theo millisecond

                                                    // Kiểm tra xem ngày hết hạn - ngày hôm nay > 25 ngày
                                                    long diffInMillis = expirationMillis - todayMillis;
                                                    long diffInDays = diffInMillis / (24 * 60 * 60 * 1000); // Chuyển đổi thành số ngày

                                                    if (diffInDays > 25) {  // Kiểm tra điều kiện ngày hết hạn
                                                        filteredUsers.add(user);  // Thêm user vào danh sách hợp lệ
                                                    }
                                                }
                                            } catch (ParseException e) {
                                                Log.e("DateError", "Lỗi khi chuyển đổi ngày: " + e.getMessage());
                                            }
                                        }
                                    }
                                }
                                // Cập nhật danh sách người dùng đã lọc sau khi xử lý tất cả
                                adapter.capNhatDanhSach(filteredUsers);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("FirebaseError", error.getMessage());
                            }
                        });
                    }
                }
                onFirebaseDataChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }



    private void onFirebaseDataChanged() {
        if (selectedChipId == R.id.chipAll) {
            hienThiTatCaUsersVip();
        } else if (selectedChipId == R.id.chipExpireSoon) {
            capNhatNguoiDungSapHetHan();
        } else if (selectedChipId == R.id.chipNewUsers) {
            capNhatNguoiDungMoiDangKy();
        }
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
