package com.example.cddd2_nhom6.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cddd2_nhom6.databinding.ItemThanhToanBinding;
import com.example.cddd2_nhom6.model.LichSuThanhToan;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class LSThanhToanAdapter extends RecyclerView.Adapter<LSThanhToanAdapter.ViewHolder> {

    private List<LichSuThanhToan> thanhToanList;
    private Context context;
    private static OnRecyclerViewItemClickListener recyclerViewItemClickListener;

    public LSThanhToanAdapter(Context context, List<LichSuThanhToan> thanhToanList) {
        this.context = context;
        this.thanhToanList = thanhToanList;
    }

    // Phương thức để thiết lập listener
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        recyclerViewItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout using View Binding
        ItemThanhToanBinding binding = ItemThanhToanBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Lấy đối tượng LichSuThanhToan hiện tại
        LichSuThanhToan thanhToan = thanhToanList.get(position);

        // Tham chiếu đến bảng Users
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        // Sử dụng id_user từ LichSuThanhToan để truy vấn Users
        usersRef.orderByChild("id_user").equalTo(thanhToan.getIdUser())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Nếu tìm thấy, lấy thông tin từ người dùng đầu tiên khớp
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                String userName = userSnapshot.child("name").getValue(String.class); // Lấy tên
                                holder.binding.tvUserName.setText(userName != null ? userName : "Tên không xác định");
                                break; // Dừng lặp sau khi tìm thấy
                            }
                        } else {
                            holder.binding.tvUserName.setText("Tên không xác định"); // Không tìm thấy user
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        holder.binding.tvUserName.setText("Lỗi kết nối"); // Lỗi Firebase
                        Log.e("Firebase Error", error.getMessage());
                    }
                });

        // Hiển thị các thông tin khác từ LichSuThanhToan
        holder.binding.tvMaUser.setText(thanhToan.getIdUser());
        holder.binding.tvNoiDung.setText(thanhToan.getNoiDung());
        holder.binding.tvNgayThanhToan.setText(thanhToan.getNgayThanhToan());
        holder.binding.tvNgayXacNhan.setText(thanhToan.getNgayXacNhan());
        holder.binding.tvSoTien.setText(String.valueOf(thanhToan.getSoTien()));

        // Lưu vị trí cho Holder
        final int pos = position;
        holder.position = pos;
    }


    @Override
    public int getItemCount() {
        return thanhToanList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // Khai báo binding
        ItemThanhToanBinding binding;
        int position;
        public ViewHolder(ItemThanhToanBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Thiết lập sự kiện click vào item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewItemClickListener != null) {
                        recyclerViewItemClickListener.onItemClick(view, position);
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

