package com.example.cddd2_nhom6.adapter;
import android.app.Activity;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

import com.example.cddd2_nhom6.databinding.ItemPhimBinding;
import com.example.cddd2_nhom6.model.Phim;
public class PhimAdapter extends RecyclerView.Adapter<PhimAdapter.MovieViewHolder> {
    private Activity context;
    private List<Phim> movies;
    private static OnRecyclerViewItemClickListener recyclerViewItemClickListener;

    public PhimAdapter(Activity context, List<Phim> movies) {
        this.context = context;
        this.movies = movies;
    }

    public void setRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        recyclerViewItemClickListener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPhimBinding binding = ItemPhimBinding.inflate(context.getLayoutInflater(), parent, false);
        return new MovieViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Phim movie = movies.get(position);
        holder.binding.movieTitle.setText(movie.getName());
        holder.binding.movieYear.setText(String.valueOf(movie.getYear()));

        // Sử dụng Glide để load hình ảnh
        Glide.with(context)  // Sử dụng context đã được cung cấp
                .load(movie.getPoster_url())
                .into(holder.binding.moviePoster);



        /// Luu Position mới cho Holder
        final int pos = position;
        holder.position = pos;
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        ItemPhimBinding binding;
        int position;
        public MovieViewHolder(@NonNull ItemPhimBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerViewItemClickListener.onItemClick(view, position);
                }
            });
        }
    }

    // Interface để xử lý sự kiện click
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }
}
