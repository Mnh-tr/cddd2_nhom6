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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.databinding.ActivityHoTroBinding;
import com.example.cddd2_nhom6.model.CloudDinary;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
        binding.btnSubmit.setOnClickListener(view -> submitForm());
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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*"); // Bộ lọc tất cả các định dạng ảnh
        startActivityForResult(Intent.createChooser(intent, "Chọn một ảnh"), PICK_IMAGE);
    }

    // Xử lý kết quả sau khi chọn ảnh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            binding.imgUpload.setImageURI(imageUri);  // Hiển thị ảnh đã chọn
        }
    }

    // Gửi form hỗ trợ
    private void submitForm() {
        String ten = binding.inputTicketCode.getText().toString().trim();
        String moTa = binding.inputDescription.getText().toString().trim();

        // Kiểm tra nếu thông tin không đầy đủ
        if (ten.isEmpty() || moTa.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin và chọn ảnh.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy thời gian hiện tại
        String thoiGian = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Gửi yêu cầu hỗ trợ qua Cloudinary và lưu vào Firebase
        CloudDinary cloudDinary = new CloudDinary(this);
        cloudDinary.uploadHoTroImage(imageUri, idUser, moTa, ten, thoiGian, binding.progressBar, new CloudDinary.AvatarUploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                Toast.makeText(HoTroActivity.this, "Yêu cầu hỗ trợ đã được gửi thành công.", Toast.LENGTH_SHORT).show();
                resetForm();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(HoTroActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Xóa form sau khi gửi thành công
    private void resetForm() {
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
