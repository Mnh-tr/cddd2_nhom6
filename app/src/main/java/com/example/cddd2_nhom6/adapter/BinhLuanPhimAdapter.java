package com.example.cddd2_nhom6.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.databinding.ItemBinhluanphimBinding;
import com.example.cddd2_nhom6.model.BinhLuanPhim;
import com.example.cddd2_nhom6.model.ThoiGianBL;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.List;

public class BinhLuanPhimAdapter extends RecyclerView.Adapter<BinhLuanPhimAdapter.MovieViewHolder> {
    private Activity context;
    private List<BinhLuanPhim> binhLuanPhimList;
    private OnCommentDeleteListener deleteListener;

    public BinhLuanPhimAdapter(Activity context, List<BinhLuanPhim> binhLuanPhims, OnCommentDeleteListener listener) {
        this.context = context;
        this.binhLuanPhimList = binhLuanPhims;
        this.deleteListener = listener;
    }

    public String getCommentUserId(int position) {
        return binhLuanPhimList.get(position).getId_user();
    }

    public String getCommentText(int position) {
        return binhLuanPhimList.get(position).getCommentText();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBinhluanphimBinding binding = ItemBinhluanphimBinding.inflate(context.getLayoutInflater(), parent, false);
        return new MovieViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        // Kiểm tra nếu vị trí hợp lệ
        if (position < 0 || position >= binhLuanPhimList.size()) {
            return;  // Nếu không hợp lệ, thoát ra
        }
        BinhLuanPhim binhLuanPhim = binhLuanPhimList.get(position);
        holder.binding.tvTenNguoiDung.setText(binhLuanPhim.getUserName());
        holder.binding.tvBinhLuan.setText(binhLuanPhim.getCommentText());
        // Sử dụng PrettyTime để định dạng ngày giờ thành kiểu "X phút trước"
        ThoiGianBL thoiGianBL = new ThoiGianBL();
        String formattedDate = thoiGianBL.format(new Date(binhLuanPhim.timestamp));
        holder.binding.tvNgayBinhLuan.setText(formattedDate);
        if (binhLuanPhim.isGif()) {
            // Hiển thị GIF và ẩn văn bản URL
            holder.binding.tvBinhLuan.setVisibility(View.GONE);  // Ẩn text nếu là GIF
            holder.binding.imageViewGif.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(binhLuanPhim.getCommentText())  // Lấy URL GIF từ commentText
                    .into(holder.binding.imageViewGif);
        } else {
            // Hiển thị bình luận văn bản thông thường
            holder.binding.tvBinhLuan.setVisibility(View.VISIBLE);
            holder.binding.imageViewGif.setVisibility(View.GONE);  // Ẩn ImageView nếu không phải GIF
        }
        holder.binding.btnXoaBinhLuan.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.xoaBinhLuan(position); // Gọi listener khi click nút xóa
            }
        });
        fetchAvatarAndBind(binhLuanPhim.getId_user(),holder);
    }

    @Override
    public int getItemCount() {
        return binhLuanPhimList.size();
    }
    public void fetchAvatarAndBind(String id_user, MovieViewHolder holder) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
        userRef.orderByChild("id_user").equalTo(id_user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String avatarUrl = userSnapshot.child("avatar").getValue(String.class); // Lấy URL avatar
                        Log.d("Avatar URL", "URL: " + avatarUrl);

                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            // Sử dụng Glide để load ảnh vào ivAvatar
                            Glide.with(context)
                                    .load(avatarUrl)
                                    .placeholder(R.drawable.profile) // Ảnh placeholder khi chưa tải
                                    .error(R.drawable.profile) // Ảnh fallback nếu lỗi
                                    .into(holder.binding.ivAvatar); // Gán ảnh vào ImageView
                        } else {
                            holder.binding.ivAvatar.setImageResource(R.drawable.profile); // Nếu avatar null
                        }
                    }
                } else {
                    holder.binding.ivAvatar.setImageResource(R.drawable.profile); // Nếu không tìm thấy User
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi Firebase (nếu có)
            }
        });
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        ItemBinhluanphimBinding binding;
        public MovieViewHolder(@NonNull ItemBinhluanphimBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.btnXoaBinhLuan.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    deleteListener.xoaBinhLuan(position);
                }
            });
        }
    }

    public interface OnCommentDeleteListener {
        void xoaBinhLuan(int position);
    }
}

