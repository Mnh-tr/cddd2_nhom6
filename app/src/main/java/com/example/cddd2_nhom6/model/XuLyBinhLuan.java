package com.example.cddd2_nhom6.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import com.example.cddd2_nhom6.activity.DangNhapActivity;
import com.example.cddd2_nhom6.adapter.BinhLuanPhimAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.ocpsoft.prettytime.PrettyTime;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class XuLyBinhLuan {
    private Context context;
    private DatabaseReference commentsRef;
    private DatabaseReference usersRef;
    private String movieSlug;
    private String currentUserId;
    private String idUser;
    private String nameUser;
    private int idLoaiND;
    private BinhLuanPhimAdapter binhLuanPhimAdapter;
    private List<BinhLuanPhim> binhLuanPhimList;

    public XuLyBinhLuan(Context context, String movieSlug, BinhLuanPhimAdapter adapter, List<BinhLuanPhim> binhLuanList) {
        this.context = context;
        this.movieSlug = movieSlug;
        this.binhLuanPhimAdapter = adapter;
        this.binhLuanPhimList = binhLuanList;
        this.commentsRef = FirebaseDatabase.getInstance().getReference("Comments");
        this.usersRef = FirebaseDatabase.getInstance().getReference("Users");

        laythongtinUser();
        // Retrieve current user info
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }
    }
    public void themBinhLuan(String comment) {
        if (idUser == null) {
            new AlertDialog.Builder(context)
                    .setTitle("Cần đăng nhập")
                    .setMessage("Bạn cần đăng nhập để bình luận. Bạn có muốn đăng nhập ngay?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        context.startActivity(new Intent(context, DangNhapActivity.class));
                    })
                    .setNegativeButton("Không", null)
                    .show();
            return;
        }

        usersRef.child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                String name = nameUser != null ? nameUser : "Người dùng ẩn danh";
                long timestamp = System.currentTimeMillis();
                BinhLuanPhim newComment = new BinhLuanPhim(idUser, movieSlug, comment, timestamp, name, null);

                commentsRef.push().setValue(newComment)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Bình luận đã được lưu!", Toast.LENGTH_SHORT).show();
                            binhLuanPhimAdapter.notifyDataSetChanged();
                        })
                        .addOnFailureListener(e -> Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Lỗi khi lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void xoaBinhLuan(int position) {
        String userId = binhLuanPhimAdapter.getCommentUserId(position);
        String movieSlug = this.movieSlug; // Slug của phim
        String commentText = binhLuanPhimAdapter.getCommentText(position); // Nội dung bình luận

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(context, "Lỗi: Bình luận không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra nếu người dùng hiện tại có quyền xóa bình luận
        if (userId.equals(idUser)) {
            new AlertDialog.Builder(context)
                    .setTitle("Xóa bình luận")
                    .setMessage("Bạn có chắc chắn muốn xóa bình luận này không?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("Comments");

                        // Truy vấn bình luận theo slug của phim
                        commentsRef.orderByChild("slug").equalTo(movieSlug).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String commentId = snapshot.getKey(); // Lấy ID của bình luận
                                    String commentUserId = snapshot.child("userId").getValue(String.class);

                                    // Kiểm tra người dùng có quyền xóa bình luận này không
                                    if (commentUserId != null && commentUserId.equals(userId)) {
                                        // Xóa bình luận khỏi Firebase
                                        commentsRef.child(commentId).removeValue()
                                                .addOnSuccessListener(aVoid -> {
                                                    // Cập nhật giao diện ngay lập tức
                                                    binhLuanPhimList.remove(position);
                                                    binhLuanPhimAdapter.notifyItemRemoved(position);
                                                    Toast.makeText(context, "Bình luận đã được xóa!", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(context, "Lỗi khi xóa bình luận: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(context, "Lỗi khi kiểm tra bình luận", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Không", (dialog, which) -> dialog.dismiss()) // Đóng dialog nếu người dùng hủy
                    .show();
        } else {
            Toast.makeText(context, "Bạn không có quyền xóa bình luận này!", Toast.LENGTH_SHORT).show();
        }
    }
    public void taiBinhLuan(String movieSlug) {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("Comments");

        // Dọn dẹp danh sách bình luận trước khi thêm mới
        binhLuanPhimList.clear();
        binhLuanPhimAdapter.notifyDataSetChanged();

        // Sử dụng ChildEventListener để chỉ xử lý khi có sự thay đổi tại các con
        commentsRef.orderByChild("slug").equalTo(movieSlug).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String userId = snapshot.child("userId").getValue(String.class);
                String commentText = snapshot.child("commentText").getValue(String.class);
                String userName = snapshot.child("userName").getValue(String.class);
                long timestamp = snapshot.child("timestamp").getValue(Long.class);

                if (userId != null && commentText != null) {
                    // Sử dụng PrettyTime để hiển thị thời gian thân thiện
                    PrettyTime prettyTime = new PrettyTime();
                    String formattedDate = prettyTime.format(new Date(timestamp));
                    // Tìm ImageView trong ViewHolder
                    boolean isGif = commentText.contains("https://") && (commentText.endsWith(".gif") || commentText.contains(".gif?"));
                    // Tạo bình luận mới và thêm vào danh sách
                    BinhLuanPhim comment = new BinhLuanPhim(userId, movieSlug, commentText, timestamp, userName, formattedDate);
                    comment.setGif(isGif);
                    binhLuanPhimList.add(0, comment);
                    binhLuanPhimAdapter.notifyItemInserted(0);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Xử lý khi bình luận bị thay đổi (nếu cần thiết)
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Xử lý khi bình luận bị xóa (nếu cần thiết)
                String commentId = snapshot.getKey();
                if (commentId != null) {
                    // Tìm bình luận tương ứng trong danh sách và loại bỏ
                    for (int i = 0; i < binhLuanPhimList.size(); i++) {
                        BinhLuanPhim comment = binhLuanPhimList.get(i);
                        if (comment.getId() != null && comment.getId().equals(commentId)) {
                            binhLuanPhimList.remove(i); // Loại bỏ bình luận khỏi danh sách
                            binhLuanPhimAdapter.notifyItemRemoved(i); // Cập nhật giao diện
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Không cần xử lý ở đây
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Lỗi khi tải bình luận", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void themGifvaoBinhLuan(String gifUrl) {
        if (idUser == null) {
            new android.app.AlertDialog.Builder(context)
                    .setTitle("Cần đăng nhập")
                    .setMessage("Bạn cần đăng nhập để bình luận. Bạn có muốn đăng nhập ngay?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        context.startActivity(new Intent(context, DangNhapActivity.class));
                    })
                    .setNegativeButton("Không", null)
                    .show();
            return;
        }

        // Lấy thông tin người dùng từ Firebase để lưu vào bình luận
        usersRef.child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                // Lấy tên người dùng, nếu không có thì dùng "Người dùng ẩn danh"
                String name = nameUser != null ? nameUser : "Người dùng ẩn danh";
                String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

                // Tạo đối tượng bình luận mới với GIF URL
                BinhLuanPhim newComment = new BinhLuanPhim(idUser, movieSlug, gifUrl, System.currentTimeMillis(), name, formattedDate);

                // Thêm bình luận vào Firebase
                commentsRef.push().setValue(newComment)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Bình luận đã được lưu!", Toast.LENGTH_SHORT).show();
                            binhLuanPhimAdapter.notifyDataSetChanged();  // Cập nhật giao diện
                        })
                        .addOnFailureListener(e -> Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Lỗi khi lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void laythongtinUser() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        idUser = sharedPreferences.getString("id_user", null);
        nameUser = sharedPreferences.getString("name", null);
        idLoaiND = sharedPreferences.getInt("id_loaiND", 0);
    }

}
