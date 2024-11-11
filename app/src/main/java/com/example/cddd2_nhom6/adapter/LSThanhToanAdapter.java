package com.example.cddd2_nhom6.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cddd2_nhom6.databinding.ItemThanhToanBinding;
import com.example.cddd2_nhom6.model.LichSuThanhToan;

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
        LichSuThanhToan thanhToan = thanhToanList.get(position);

        // Gán dữ liệu vào các TextView thông qua binding
        //holder.binding.tvUserName.setText(thanhToan.getUserName());
        holder.binding.tvMaUser.setText(thanhToan.getIdUser());
        holder.binding.tvNoiDung.setText(thanhToan.getNoiDung());
        holder.binding.tvNgayThanhToan.setText(thanhToan.getNgayThanhToan());
        holder.binding.tvNgayXacNhan.setText(thanhToan.getNgayXacNhan());
        //holder.binding.tvSoTien.setText(thanhToan.getSoTien());

        /// Luu Position mới cho Holder
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

