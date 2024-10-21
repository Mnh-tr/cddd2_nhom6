package com.example.cddd2_nhom6.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cddd2_nhom6.model.ChiTietPhim;
import com.example.xemphim.databinding.ItemLichsuBinding;
import com.example.xemphim.model.MovieDetail;

import java.util.List;

public class FavoriteMoviesAdapter extends RecyclerView.Adapter<FavoriteMoviesAdapter.ViewHolder> {
    private Activity context;
    private List<ChiTietPhim.MovieItem> movies;
    private static OnRecyclerViewItemClickListener recyclerViewItemClickListener;

    public FavoriteMoviesAdapter(Activity context, List<ChiTietPhim.MovieItem> favoriteMovies) {
        this.context = context;
        this.movies = favoriteMovies;
    }

    public void setRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.recyclerViewItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Item binding = ItemLichsuBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChiTietPhim.MovieItem movie = movies.get(position);
        holder.binding.movieTitle.setText(movie.getName());

        // Sử dụng Glide để load hình ảnh
        Glide.with(context)
                .load(movie.getPosterUrl())
                .into(holder.binding.moviePoster);

        final int pos = position;
        holder.position = pos; // Lưu Position cho Holder
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemLichsuBinding binding;
        int position;

        public ViewHolder(@NonNull ItemLichsuBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

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

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }
}
