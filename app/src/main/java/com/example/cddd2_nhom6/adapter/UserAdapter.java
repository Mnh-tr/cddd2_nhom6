package com.example.cddd2_nhom6.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.databinding.ItemUserBinding;
import com.example.cddd2_nhom6.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;
    private Context context; // Giả sử bạn có một lớp User để đại diện cho người dùng
    private static OnRecyclerViewItemClickListener  recyclerViewItemClickListener;
    public UserAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }
    public void setRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        recyclerViewItemClickListener = listener;
    }
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(LayoutInflater.from(context), parent, false);
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        Log.d("UserAdapter", "Binding user: " + user.getName());
        holder.binding.tvUserName.setText(user.getName());
        holder.binding.tvMaUser.setText(user.getId_user());
        holder.binding.tvUserStatus.setText(user.getStatus());
        holder.binding.tvGoi.setText(user.getGoi());
        Log.d("UserAdapter", "Binding user: " + user.getGoi());
        // Đổi màu dựa trên trạng thái (online/offline)
        if (user.getStatus().equals("online")) {
            holder.binding.tvUserStatus.setTextColor(ContextCompat.getColor(context, R.color.green));  // Màu xanh lá cho online
        } else {
            holder.binding.tvUserStatus.setTextColor(ContextCompat.getColor(context, R.color.red));  // Màu đỏ cho offline
        }

        // Đổi màu dựa trên loại người dùng
        switch (user.getGoi()) {
            case "Thường":
                holder.binding.tvGoi.setTextColor(ContextCompat.getColor(context, R.color.gray));  // Màu xám cho người dùng thường
                break;
            case "VIP":
                holder.binding.tvGoi.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));  // Màu vàng cho VIP
                break;
            case "Admin":
                holder.binding.tvGoi.setTextColor(ContextCompat.getColor(context, R.color.red));  // Màu xanh dương cho admin
                break;
            case "Quản Lý":
                holder.binding.tvGoi.setTextColor(ContextCompat.getColor(context, R.color.green));  // Màu tím cho quản lý
                break;
            default:
                holder.binding.tvGoi.setTextColor(ContextCompat.getColor(context, R.color.black));  // Màu đen mặc định
                break;
        }
        /// Luu Position mới cho Holder
        final int pos = position;
        holder.position = pos;
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ItemUserBinding binding;
        int position;
        public UserViewHolder(@NonNull ItemUserBinding binding) {
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

    // Phương thức để cập nhật dữ liệu và thông báo RecyclerView
    public void updateData(List<User> filteredList) {
        this.userList = filteredList;
        notifyDataSetChanged(); // Thông báo cho RecyclerView cập nhật dữ liệu
    }
    // Interface để xử lý sự kiện click
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }
}

