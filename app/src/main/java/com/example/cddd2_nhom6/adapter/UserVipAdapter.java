package com.example.cddd2_nhom6.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.databinding.ItemUserBinding;
import com.example.cddd2_nhom6.databinding.ItemUserVipBinding;
import com.example.cddd2_nhom6.model.LichSuThanhToan;
import com.example.cddd2_nhom6.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UserVipAdapter extends RecyclerView.Adapter<UserVipAdapter.UserVipViewHolder> {
    private Context context;
    private List<User> userList;
    private List<LichSuThanhToan> paymentHistoryList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); // Định dạng ngày giờ
    private static UserAdapter.OnRecyclerViewItemClickListener recyclerViewItemClickListener;
    public UserVipAdapter(Context context, List<User> userList, List<LichSuThanhToan> paymentHistoryList) {
        this.context = context;
        this.userList = userList;
        this.paymentHistoryList = paymentHistoryList;
    }
    public void setRecyclerViewItemClickListener(UserAdapter.OnRecyclerViewItemClickListener listener) {
        recyclerViewItemClickListener = listener;
    }
    @NonNull
    @Override
    public UserVipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserVipBinding binding = ItemUserVipBinding.inflate(LayoutInflater.from(context), parent, false);
        return new UserVipViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserVipViewHolder holder, int position) {
        User user = userList.get(position);

        // Hiển thị thông tin User
        holder.binding.tvUserName.setText("Họ và Tên: " + user.getName());
        holder.binding.tvUserId.setText("ID: " + user.getId_user());

        // Lấy thời gian còn lại đến ngày hết hạn
        String remainingTime = getRemainingTime(user.getId_user());
        holder.binding.tvDaysRemaining.setText(remainingTime);
        // Kiểm tra nếu người dùng có avatar, nếu không thì lấy avatar mặc định
        String avatarUrl = user.getAvatar(); // Giả sử bạn đã thêm trường avatar vào model User
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            // Tải ảnh từ URL avatar
            Glide.with(context)
                    .load(avatarUrl)  // Nếu có URL, tải ảnh từ đó
                    .placeholder(R.drawable.profile) // Ảnh mặc định khi chưa có avatar
                    .into(holder.binding.imgAvatar);
        } else {
            // Nếu không có avatar, hiển thị ảnh mặc định
            Glide.with(context)
                    .load(R.drawable.profile)  // Tải ảnh mặc định
                    .into(holder.binding.imgAvatar);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private String getRemainingTime(String idUser) {
        LichSuThanhToan latestPayment = null;

        for (LichSuThanhToan payment : paymentHistoryList) {
            if (payment.getNoiDung().contains(idUser)) {
                try {
                    Date ngayHetHan = dateFormat.parse(payment.getNgayHetHan());

                    if (ngayHetHan != null) {
                        // Kiểm tra và cập nhật giao dịch mới nhất
                        if (latestPayment == null || ngayHetHan.after(dateFormat.parse(latestPayment.getNgayHetHan()))) {
                            latestPayment = payment;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        if (latestPayment != null) {
            try {
                Date ngayHetHan = dateFormat.parse(latestPayment.getNgayHetHan());
                Date ngayHienTai = new Date();

                if (ngayHetHan != null) {
                    long diffInMillis = ngayHetHan.getTime() - ngayHienTai.getTime();

                    if (diffInMillis > 0) {
                        long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);
                        long hours = TimeUnit.MILLISECONDS.toHours(diffInMillis) % 24;

                        return days + " ngày, " + hours + " giờ còn lại";
                    } else {
                        return "Đã hết hạn";
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return "Chưa có giao dịch";
    }


    public static class UserVipViewHolder extends RecyclerView.ViewHolder {
        ItemUserVipBinding binding;
        int position;

        public UserVipViewHolder(@NonNull ItemUserVipBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Thiết lập sự kiện click cho item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewItemClickListener != null) {
                        recyclerViewItemClickListener.onItemClick(view, getAdapterPosition());
                    }
                }
            });
        }
    }
    // Interface để xử lý sự kiện click
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }
}
