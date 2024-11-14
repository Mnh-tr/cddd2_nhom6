package com.example.cddd2_nhom6.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.cddd2_nhom6.databinding.ItemBinhluanphimBinding;
import com.example.cddd2_nhom6.model.BinhLuanPhim;
import org.ocpsoft.prettytime.PrettyTime;
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
        return binhLuanPhimList.get(position).getUserId();
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
        PrettyTime prettyTime = new PrettyTime();
        String formattedDate = prettyTime.format(new Date(binhLuanPhim.timestamp));
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
    }

    @Override
    public int getItemCount() {
        return binhLuanPhimList.size();
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

