package com.example.cddd2_nhom6.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.activity.ChiTietPhimActivity;
import com.example.cddd2_nhom6.activity.ChiTietPhimFirebaseActivity;
import com.example.cddd2_nhom6.databinding.ItemPhimBinding;
import com.example.cddd2_nhom6.model.Phim;
import com.example.cddd2_nhom6.model.QLPhim;

import java.util.List;

public class PhimAdapter extends RecyclerView.Adapter<PhimAdapter.PhimViewHolder> {

    private Context context;
    private List<QLPhim> movieList;

    public PhimAdapter(Context context, List<QLPhim> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public PhimViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng ViewBinding cho item_movie
        ItemPhimBinding binding = ItemPhimBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PhimViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PhimViewHolder holder, int position) {
        QLPhim movie = movieList.get(position);

        // Đặt dữ liệu vào các thành phần view
        holder.binding.smallImageCorner.setImageResource(R.drawable.logo);
        holder.binding.movieTitle.setText(movie.getName());
        holder.binding.movieYear.setText(String.valueOf(movie.getYear()));

        // Sử dụng Glide để load ảnh từ URL
        Glide.with(context)
                .load(movie.getPoster_url())
                .into(holder.binding.moviePoster);

        // Bắt sự kiện click vào item
        holder.itemView.setOnClickListener(v -> {
            // Lấy slug của phim từ item
            String movieSlug = movie.getId_movie();

            // Tạo Intent để chuyển đến ChiTietPhimActivity
            Intent intent = new Intent(holder.itemView.getContext(), ChiTietPhimFirebaseActivity.class);
            intent.putExtra("slug", movieSlug); // Truyền slug phim cho ChiTietPhimActivity
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class PhimViewHolder extends RecyclerView.ViewHolder {
        private final ItemPhimBinding binding;

        public PhimViewHolder(ItemPhimBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
