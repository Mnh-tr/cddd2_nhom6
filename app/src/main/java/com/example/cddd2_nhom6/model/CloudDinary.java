package com.example.cddd2_nhom6.model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.cddd2_nhom6.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CloudDinary {
    private final Context context;
    private final DatabaseReference usersRef;

    public CloudDinary(Context context) {
        this.context = context.getApplicationContext();
        this.usersRef = FirebaseDatabase.getInstance().getReference("Users");
        khaoBaoCloudinary();
    }

    private void khaoBaoCloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dkjybdmwh");
        config.put("api_key", "636117553374141");
        config.put("api_secret", "FZ-WutItTS0BQoTqxtjztr1ApJk");

        try {
            MediaManager.init(context, config);
        } catch (IllegalStateException e) {
            Log.e("AvatarManager", "Cloudinary đã được khởi tạo trước đó.");
        }
    }

    public void uploadAvatar(Uri imageUri, ProgressBar progressBar, AvatarUploadCallback callback) {
        MediaManager.get().upload(imageUri)
                .option("folder", "user_avatars")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        // Cập nhật tiến trình upload nếu cần
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        progressBar.setVisibility(View.GONE);
                        String imageUrl = (String) resultData.get("secure_url");
                        saveAvatarUrlToUser(imageUrl, callback);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        progressBar.setVisibility(View.GONE);
                        callback.onFailure("Lỗi upload ảnh: " + error.getDescription());
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        // Xử lý khi cần upload lại
                    }
                })
                .dispatch();
    }

    private void saveAvatarUrlToUser(String imageUrl, AvatarUploadCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            usersRef.child(currentUser.getUid()).child("avatar")
                    .setValue(imageUrl)
                    .addOnSuccessListener(aVoid -> callback.onSuccess(imageUrl))
                    .addOnFailureListener(e -> callback.onFailure("Lỗi cập nhật avatar: " + e.getMessage()));
        } else {
            callback.onFailure("Người dùng hiện tại không tồn tại.");
        }
    }
    public void uploadHoTroImage(Uri imageUri, String idNguoiDung, String moTa, String ten, String thoiGian, ProgressBar progressBar, AvatarUploadCallback callback) {
        MediaManager.get().upload(imageUri)
                .option("folder", "user_support_images")  // Lưu ảnh vào thư mục hỗ trợ
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        // Cập nhật tiến trình nếu cần
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        progressBar.setVisibility(View.GONE);
                        String imageUrl = (String) resultData.get("secure_url"); // Lấy URL ảnh từ Cloudinary
                        saveHoTroData(idNguoiDung, imageUrl, moTa, ten, thoiGian, callback); // Lưu dữ liệu vào Firebase
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        progressBar.setVisibility(View.GONE);
                        callback.onFailure("Lỗi upload ảnh: " + error.getDescription());
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        // Xử lý khi cần upload lại
                    }
                })
                .dispatch();
    }

    private void saveHoTroData(String idNguoiDung, String imageUrl, String moTa, String ten, String thoiGian, AvatarUploadCallback callback) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("HoTro");

        // Tạo một ID ngẫu nhiên cho yêu cầu hỗ trợ mới
        String idHoTro = databaseRef.child(idNguoiDung).push().getKey();

        // Tạo dữ liệu cần lưu
        HashMap<String, Object> hoTroData = new HashMap<>();
        hoTroData.put("imageUrl", imageUrl);
        hoTroData.put("moTa", moTa);
        hoTroData.put("ten", ten);
        hoTroData.put("thoiGian", thoiGian);

        // Lưu vào Firebase
        databaseRef.child(idNguoiDung).child(idHoTro).setValue(hoTroData)
                .addOnSuccessListener(aVoid -> callback.onSuccess(imageUrl))
                .addOnFailureListener(e -> callback.onFailure("Lỗi lưu dữ liệu hỗ trợ: " + e.getMessage()));
    }


    public void loadAvatar(ImageView imageView) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            usersRef.child(currentUser.getUid()).child("avatar").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String avatarUrl = dataSnapshot.getValue(String.class);
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Glide.with(context)
                                .load(avatarUrl)
                                .placeholder(R.drawable.profile)
                                .error(R.drawable.profile)
                                .circleCrop()
                                .into(imageView);
                    } else {
                        imageView.setImageResource(R.drawable.profile);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("AvatarManager", "Lỗi load avatar: " + databaseError.getMessage());
                    imageView.setImageResource(R.drawable.profile);
                }
            });
        } else {
            imageView.setImageResource(R.drawable.profile);
        }
    }

    public interface AvatarUploadCallback {
        void onSuccess(String imageUrl);

        void onFailure(String errorMessage);
    }
}


