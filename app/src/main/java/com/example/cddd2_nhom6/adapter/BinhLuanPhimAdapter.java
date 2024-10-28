package com.example.cddd2_nhom6.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cddd2_nhom6.databinding.ItemBinhluanphimBinding;
import com.example.cddd2_nhom6.model.BinhLuanPhim;
import com.example.cddd2_nhom6.databinding.ItemBinhluanphimBinding;
import com.example.cddd2_nhom6.model.BinhLuanPhim;

import java.text.DateFormat;
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
    public void removeComment(int position) {
        binhLuanPhimList.remove(position);
        notifyItemRemoved(position);
    }
    public String getCommentUserId(int position) {
        return binhLuanPhimList.get(position).getUserId();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBinhluanphimBinding binding = ItemBinhluanphimBinding.inflate(context.getLayoutInflater(), parent, false);
        return new MovieViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        BinhLuanPhim binhLuanPhim = binhLuanPhimList.get(position);
        holder.binding.tvTenNguoiDung.setText(binhLuanPhim.getUserName());
        holder.binding.tvBinhLuan.setText(binhLuanPhim.getCommentText());
        // Định dạng thời gian tùy theo yêu cầu
        holder.binding.tvNgayBinhLuan.setText(DateFormat.getDateTimeInstance().format(binhLuanPhim.timestamp));
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
            // Gọi hàm xóa bình luận khi nút được nhấn
            binding.btnXoaBinhLuan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition(); // Lấy vị trí hiện tại của ViewHolder
                    if (position != RecyclerView.NO_POSITION) { // Kiểm tra vị trí hợp lệ
                        deleteListener.xoaBinhLuan(position); // Gọi hàm xóa bình luận
                    }
                }
            });
        }
    }
    // Khai báo interface
    public interface OnCommentDeleteListener {
        void xoaBinhLuan(int position);
    }

}
