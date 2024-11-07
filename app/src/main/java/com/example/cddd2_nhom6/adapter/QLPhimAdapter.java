package com.example.cddd2_nhom6.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.databinding.ItemQlPhimBinding;
import com.example.cddd2_nhom6.model.Goi;
import com.example.cddd2_nhom6.model.KieuPhim;
import com.example.cddd2_nhom6.model.Phim;

import java.util.ArrayList;
import java.util.List;

public class QLPhimAdapter extends RecyclerView.Adapter<QLPhimAdapter.QLPhimViewHolder> {

    private Context context;
    private List<Phim> phimList;
    private List<KieuPhim> kieuPhimList;
    private List<Goi> goiList;
    private List<Phim> selectedMovies = new ArrayList<>(); // Danh sách phim được chọn
    private boolean multiSelectMode = false;
    private OnMovieSelectListener onMovieSelectListener;
    private static OnRecyclerViewItemClickListener recyclerViewItemClickListener;

    // Interface để thông báo về trạng thái lựa chọn phim
    public interface OnMovieSelectListener {
        void onMovieSelected(int selectedCount); // Gọi khi số lượng phim được chọn thay đổi
    }

    public void setRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        recyclerViewItemClickListener = listener;
    }

    // Constructor
    public QLPhimAdapter(Context context, List<Phim> phimList, List<KieuPhim> kieuPhimList, List<Goi> goiList, OnMovieSelectListener onMovieSelectListener) {
        this.context = context;
        this.phimList = phimList;
        this.kieuPhimList = kieuPhimList;
        this.goiList = goiList;
        this.onMovieSelectListener = onMovieSelectListener;
    }

    @NonNull
    @Override
    public QLPhimViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemQlPhimBinding binding = ItemQlPhimBinding.inflate(LayoutInflater.from(context), parent, false);
        return new QLPhimViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull QLPhimViewHolder holder, int position) {
        Phim currentPhim = phimList.get(position);

        // Lưu vị trí mới cho Holder
        final int pos = position;
        holder.position = pos;

        // Bind dữ liệu
        holder.binding.tenphim.setText(currentPhim.getName());
        holder.binding.nam.setText(currentPhim.getYear());

        //String time = getKieuPhimNameById(String.valueOf(currentPhim.getTime()));
        String a = "Phim lẻ: ";
        holder.binding.thoiluong.setText( a + currentPhim.getTime()+"p");

        String goiType = getGoiTypeById(Integer.parseInt(currentPhim.getGoi()));
        holder.binding.goiPhim.setText(goiType);

        holder.binding.theloai.setText(currentPhim.getTheLoai());
        holder.binding.ngaytao.setText(currentPhim.getNgayThemPhim());

        // Load hình ảnh
        Glide.with(context)
                .load(currentPhim.getPoster_url())
                .into(holder.binding.imgmovie);

        // Đổi màu nền nếu item được chọn
        if (selectedMovies.contains(currentPhim)) {
            holder.binding.itemView.setBackgroundColor(Color.RED); // Màu nền khi được chọn
        } else {
            holder.binding.itemView.setBackgroundResource(R.drawable.movie_item_background); // Màu nền mặc định
        }

    }

    public void updateMovieList(List<Phim> newList) {
        phimList.clear();
        phimList.addAll(newList);
        notifyDataSetChanged();
    }



    // Hàm chọn/bỏ chọn phim
    private void toggleSelection(Phim movie) {
        if (selectedMovies.contains(movie)) {
            selectedMovies.remove(movie);
        } else {
            selectedMovies.add(movie);
        }

        // Tắt chế độ multiSelectMode nếu không còn mục nào được chọn
        if (selectedMovies.isEmpty()) {
            multiSelectMode = false;
        }

        notifyDataSetChanged();
        onMovieSelectListener.onMovieSelected(selectedMovies.size()); // Thông báo số lượng phim được chọn
    }


    public List<Phim> getSelectedMovies() {
        return selectedMovies;
    }

    @Override
    public int getItemCount() {
        return phimList.size();
    }

    public class QLPhimViewHolder extends RecyclerView.ViewHolder {
        private final ItemQlPhimBinding binding;
        int position;

        public QLPhimViewHolder(@NonNull ItemQlPhimBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Nhấn giữ để chọn phim
            itemView.setOnLongClickListener(view -> {
                multiSelectMode = true;
                Phim currentPhim = phimList.get(position); // Lấy phim hiện tại
                toggleSelection(currentPhim);
                return true;
            });

            // Nhấn để chọn/bỏ chọn phim
            itemView.setOnClickListener(view -> {
                if (multiSelectMode) {
                    Phim currentPhim = phimList.get(position); // Lấy phim hiện tại
                    toggleSelection(currentPhim);
                } else {
                    recyclerViewItemClickListener.onItemClick(view, position);
                }
            });
        }
    }



    // Hàm lấy loại gói
    private String getGoiTypeById(int id) {
        for (Goi goi : goiList) {
            if (goi.getId() == id) {
                return goi.getType();
            }
        }
        return "Không xác định"; // Nếu không tìm thấy
    }

    // Interface để xử lý sự kiện click
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }
}