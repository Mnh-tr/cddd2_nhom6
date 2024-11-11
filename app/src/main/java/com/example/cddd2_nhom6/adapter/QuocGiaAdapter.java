package com.example.cddd2_nhom6.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.databinding.ItemQuocgiaBinding;
import com.example.cddd2_nhom6.model.QuocGia;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class QuocGiaAdapter extends RecyclerView.Adapter<QuocGiaAdapter.QuocGiaViewHolder> {

    private List<QuocGia> quocGiaList;
    private Activity context;
    private DatabaseReference quocGiaRef;

    public QuocGiaAdapter(List<QuocGia> quocGiaList, Activity context) {
        this.quocGiaList = quocGiaList;
        this.context = context;
        quocGiaRef = FirebaseDatabase.getInstance().getReference("quocGia");
    }

    @NonNull
    @Override
    public QuocGiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemQuocgiaBinding binding = ItemQuocgiaBinding.inflate(LayoutInflater.from(context), parent, false);
        return new QuocGiaViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull QuocGiaViewHolder holder, int position) {
        QuocGia quocGia = quocGiaList.get(position);
        holder.binding.editQuocGia.setText(quocGia.getName());
        holder.binding.tvQuocGiaName.setText(quocGia.getName());

        // Hiển thị logo theo quốc gia
        // Sử dụng Glide để hiển thị hình ảnh
        Glide.with(holder.itemView.getContext())
                .load(quocGia.getImageLink()) // URL hình ảnh
                .error(R.drawable.vietnam) // Hình ảnh hiển thị khi lỗi
                .into(holder.binding.imgQuocGia);

        // Set the position for further use in the ViewHolder
        final int pos = position;
        holder.position = pos;
    }

    @Override
    public int getItemCount() {
        return quocGiaList.size();
    }

    public class QuocGiaViewHolder extends RecyclerView.ViewHolder {
        ItemQuocgiaBinding binding;
        int position;

        public QuocGiaViewHolder(@NonNull ItemQuocgiaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Xử lý nút Sửa
            binding.btnEdit.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Xác nhận")
                        .setMessage("Bạn có chắc chắn muốn sửa thông tin quốc gia này không?")
                        .setPositiveButton("Có", (dialog, which) -> {
                            // Hiển thị form chỉnh sửa
                            binding.editQuocGia.setVisibility(View.VISIBLE);
                            binding.btnSave.setVisibility(View.VISIBLE);
                            binding.editQuocGia.requestFocus();
                        })
                        .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                        .show();
            });

            // Xử lý nút Xóa
            binding.btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Xác nhận")
                        .setMessage("Bạn có chắc chắn muốn xóa quốc gia này không?")
                        .setPositiveButton("Có", (dialog, which) -> {
                            // Truyền ID của quốc gia để xóa
                            String quocGiaId = String.valueOf(quocGiaList.get(position).getId());
                            quocGiaRef.child(quocGiaId).removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Đã xóa quốc gia!", Toast.LENGTH_SHORT).show();
                                        quocGiaList.remove(position);  // Xóa quốc gia khỏi danh sách
                                        notifyItemRemoved(position);  // Cập nhật lại RecyclerView
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Lỗi khi xóa quốc gia!", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                        .show();
            });

            // Xử lý nút Lưu
            binding.btnSave.setOnClickListener(v -> {
                String newName = binding.editQuocGia.getText().toString();
                if (!newName.isEmpty()) {
                    String quocGiaId = String.valueOf(quocGiaList.get(position).getId());
                    quocGiaRef.child(quocGiaId).child("name").setValue(newName)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Đã cập nhật quốc gia!", Toast.LENGTH_SHORT).show();
                                quocGiaList.get(position).setName(newName); // Cập nhật tên quốc gia trong danh sách
                                binding.editQuocGia.setVisibility(View.GONE);
                                binding.btnSave.setVisibility(View.GONE);
                                notifyItemChanged(position);  // Cập nhật lại item trong RecyclerView
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Lỗi khi cập nhật quốc gia!", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(context, "Tên quốc gia không được để trống!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
