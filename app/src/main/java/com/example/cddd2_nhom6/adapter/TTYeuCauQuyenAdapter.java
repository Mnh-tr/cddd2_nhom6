package com.example.cddd2_nhom6.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.databinding.ItemTtYeucauBinding;
import com.example.cddd2_nhom6.databinding.ItemYeuCauBinding;
import com.example.cddd2_nhom6.model.TTYeuCauUpdateQuyen;
import com.example.cddd2_nhom6.model.User;
import com.example.cddd2_nhom6.model.YeuCau;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TTYeuCauQuyenAdapter extends RecyclerView.Adapter<TTYeuCauQuyenAdapter.TTYeuCauQuyenViewHolder> {
    private Context context;
    private List<TTYeuCauUpdateQuyen> yeuCauList;
    private OnItemClickListener listener;
    private DatabaseReference userRef;

    public TTYeuCauQuyenAdapter(Context context, List<TTYeuCauUpdateQuyen> yeuCauList) {
        this.context = context;
        this.yeuCauList = yeuCauList;
        this.userRef = FirebaseDatabase.getInstance().getReference("Users");
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TTYeuCauQuyenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemTtYeucauBinding binding = ItemTtYeucauBinding.inflate(inflater, parent, false);
        return new TTYeuCauQuyenViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TTYeuCauQuyenViewHolder holder, int position) {
        TTYeuCauUpdateQuyen yeuCau = yeuCauList.get(position);
        holder.bind(yeuCau);
        // Hiển thị các thông tin vào TextView
        //holder.binding.tvUserRequest.setText(yeuCau.getId_userYeuCau());
        holder.binding.tvUpdateDate.setText(yeuCau.getNgayUpdater());
        holder.binding.tvRequestContent.setText(yeuCau.getNoiDung());
        // Lưu vị trí mới cho Holder
        final int pos = position;
        holder.position = pos;
    }

    @Override
    public int getItemCount() {
        return yeuCauList != null ? yeuCauList.size() : 0;
    }

    public class TTYeuCauQuyenViewHolder extends RecyclerView.ViewHolder {

        private final ItemTtYeucauBinding binding;
        int position;

        public TTYeuCauQuyenViewHolder(@NonNull ItemTtYeucauBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(v -> {
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(yeuCauList.get(position));
                }
            });
        }
        public void bind(TTYeuCauUpdateQuyen yeuCau) {
            userRef.orderByChild("id_user").equalTo(yeuCau.getId_userYeuCau()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            User user = userSnapshot.getValue(User.class);
                            if (user != null) {
                                binding.tvUserRequest.setText(user.getName());
                            } else {
                                binding.tvUserRequest.setText("Unknown User");
                            }
                        }
                    } else {
                        binding.tvUserRequest.setText("User Not Found");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    binding.tvUserRequest.setText("Error Loading");
                }
            });
        }
    }
    public interface OnItemClickListener {
        void onItemClick(TTYeuCauUpdateQuyen yeuCauChangeQuyen);
    }
}
