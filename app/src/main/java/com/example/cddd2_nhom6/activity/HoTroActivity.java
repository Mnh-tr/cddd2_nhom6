package com.example.cddd2_nhom6.activity;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.databinding.ActivityHoTroBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class HoTroActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1; // Mã yêu cầu chọn ảnh
    private ActivityHoTroBinding binding;  // View Binding
    private Uri imageUri;  // Để lưu URI của ảnh đã chọn
    private String idUser;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHoTroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Goi chuc nang nhan 2 lan de thoat
        getOnBackPressedDispatcher().addCallback(this, callback);

        // Lắng nghe sự kiện khi người dùng nhấn vào imgUpload để chọn ảnh
        binding.imgUpload.setOnClickListener(view -> openGallery());

        // Lắng nghe sự kiện khi người dùng nhấn vào nút gửi yêu cầu
        binding.btnSubmit.setOnClickListener(view -> guiYeuCau());
        laythongtinUser();
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void laythongtinUser(){
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        idUser = sharedPreferences.getString("id_user", null);

    }

    // Mở thư viện ảnh để chọn
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*"); // Bộ lọc tất cả các định dạng ảnh
        startActivityForResult(Intent.createChooser(intent, "Chọn một ảnh"), PICK_IMAGE);
    }

    // Xử lý kết quả sau khi chọn ảnh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            // Lưu lại quyền truy cập để dùng sau
            getContentResolver().takePersistableUriPermission(uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.ic_notification)  // Hình ảnh khi đang tải
                    .error(R.drawable.ic_notification)      // Hình ảnh khi xảy ra lỗi
                    .into(binding.imgUpload);
        }
    }

    // Gửi form hỗ trợ
    private void guiYeuCau() {
        String ten = binding.inputTicketCode.getText().toString().trim();
        String moTa = binding.inputDescription.getText().toString().trim();

        // Kiểm tra nếu thông tin không đầy đủ
        if (ten.isEmpty() || moTa.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra nếu ảnh chưa được chọn
        if (imageUri == null) {
            Toast.makeText(this, "Vui lòng chọn một ảnh để tiếp tục", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy ID của người dùng đã đăng nhập
        FirebaseUser nguoiDungHienTai = FirebaseAuth.getInstance().getCurrentUser();
        if (nguoiDungHienTai == null) {
            Toast.makeText(this, "Bạn cần đăng nhập để gửi yêu cầu hỗ trợ.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lưu thông tin trực tiếp vào Firebase Realtime Database
        String duongDanAnh = imageUri.toString();
        String thoiGian = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        luuVaoFirebase(ten, moTa, duongDanAnh, idUser, thoiGian);
    }

    // Phương thức lưu thông tin vào Firebase Realtime Database
    private void luuVaoFirebase(String ten, String moTa, String duongDanAnh, String idNguoiDung, String thoiGian) {
        // Tham chiếu đến Firebase Realtime Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("HoTro");
        // Lấy danh sách quốc gia hiện có để tính toán ID tiếp theo
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long newId = 1; // Giá trị ID mặc định nếu đây là yêu cầu đầu tiên
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        long currentId = Long.parseLong(snapshot.getKey());
                        if (currentId >= newId) {
                            newId = currentId + 1; // Tăng ID lên 1
                        }
                    } catch (NumberFormatException e) {
                        Log.e("Lỗi ID", "Không thể chuyển key thành số: " + snapshot.getKey());
                    }
                }

                // Tạo đối tượng để lưu
                HashMap<String, Object> duLieu = new HashMap<>();
                duLieu.put("idHoTro", newId);
                duLieu.put("ten", ten);
                duLieu.put("moTa", moTa);
                duLieu.put("imageUrl", duongDanAnh);
                duLieu.put("thoiGian", thoiGian);
                duLieu.put("idNguoiDung", idNguoiDung);

                // Lưu dữ liệu vào Firebase
                databaseRef.child(String.valueOf(newId)).setValue(duLieu)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(HoTroActivity.this, "Lưu yêu cầu thành công.", Toast.LENGTH_SHORT).show();
                            AlertDialog.Builder builder = new AlertDialog.Builder(HoTroActivity.this);
                            builder.setTitle("Thông báo");
                            builder.setMessage("Chúng tôi đã ghi nhận yêu cầu hỗ trợ của bạn. Chúng tôi sẽ hỗ trợ bạn sớm nhất có thể.");
                            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                            builder.create().show();
                            datLaiForm();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Lỗi Firebase", "Không thể lưu yêu cầu: " + e.getMessage());
                            Toast.makeText(HoTroActivity.this, "Lỗi khi lưu yêu cầu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HoTroActivity.this, "Lỗi khi lấy dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    // Xóa form sau khi gửi thành công
    private void datLaiForm() {
        binding.inputTicketCode.setText("");
        binding.inputDescription.setText("");
        binding.imgUpload.setImageResource(R.drawable.baseline_upload_file_24);  // Đặt lại ảnh mặc định
        imageUri = null;
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
