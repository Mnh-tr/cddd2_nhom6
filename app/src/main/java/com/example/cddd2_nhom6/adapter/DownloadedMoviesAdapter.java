package com.example.cddd2_nhom6.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.model.MovieItem;


import java.util.List;

public class DownloadedMoviesAdapter extends RecyclerView.Adapter<DownloadedMoviesAdapter.ViewHolder> {
    private List<MovieItem> movies;
    private OnMovieClickListener listener;

    public DownloadedMoviesAdapter(List<MovieItem> movies, OnMovieClickListener listener) {
        this.movies = movies;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_downloaded_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MovieItem movie = movies.get(position);
        holder.bind(movie, listener);

        // Set up delete button
        holder.btnDelete.setOnClickListener(v -> {
            // Gọi phương thức xóa phim
            listener.onDeleteMovie(movie, position);
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Khai báo các view trong layout item
        ImageView moviePoster;
        TextView movieName;
        ImageView btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            moviePoster = itemView.findViewById(R.id.moviePoster);
            movieName = itemView.findViewById(R.id.movieTitle);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(MovieItem movie, OnMovieClickListener listener) {
            movieName.setText(movie.getName());
            // Load poster using Glide or any image loading library
            Glide.with(itemView.getContext()).load(movie.getPosterPath()).into(moviePoster);
            itemView.setOnClickListener(v -> listener.onMovieClick(movie));
        }
    }

    public interface OnMovieClickListener {
        void onMovieClick(MovieItem movie);
        void onDeleteMovie(MovieItem movie, int position); // Thêm phương thức xóa
    }
}





