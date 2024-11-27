package com.example.cddd2_nhom6.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.activity.ChiTietPhimActivity;
import com.example.cddd2_nhom6.databinding.ItemBannerBinding;
import com.example.cddd2_nhom6.model.Phim;

import java.util.List;

public class BannerAdapter extends PagerAdapter {

    private final Activity context;
    private final List<Phim> movies;

    public BannerAdapter(Activity context, List<Phim> movies) {
        this.context = context;
        this.movies = movies;
    }

    public void updateFilms(List<Phim> films) {
        if (films != null) {
            this.movies.clear();
            this.movies.addAll(films);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        // Inflate banner layout for each item
        ItemBannerBinding binding = ItemBannerBinding.inflate(LayoutInflater.from(context), container, false);
        Phim movie = movies.get(position);

        // Set movie name
        binding.tvTenPhim.setText(movie.getName());

        // Load images and set the source logo
        String imageUrl = movie.getPoster_url();
        String thumbUrl = movie.getThumb_urlOphim();
        String source = movie.getSource();

        if (source != null) {
            if (source.equalsIgnoreCase("Ophim")) {
                loadImage(binding, thumbUrl, imageUrl);
                binding.smallImageCorner.setImageResource(R.drawable.ic_logo_ophim);
            } else if (source.equalsIgnoreCase("Kkphim")) {
                loadImage(binding, imageUrl, thumbUrl);
                binding.smallImageCorner.setImageResource(R.drawable.logo_kkphim);
            } else {
                loadImage(binding, imageUrl, thumbUrl);
            }
        } else {
            loadImage(binding, imageUrl, thumbUrl);
        }

        // Handle click event to pass slug
        binding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(context, ChiTietPhimActivity.class);
            intent.putExtra("slug", movie.getSlug());
            context.startActivity(intent);
        });

        container.addView(binding.getRoot());
        return binding.getRoot();
    }

    private void loadImage(ItemBannerBinding binding, String primaryUrl, String fallbackUrl) {
        if (primaryUrl != null && !primaryUrl.isEmpty()) {
            Glide.with(context)
                    .load(primaryUrl)
                    .into(binding.imageViewBanner);
        } else if (fallbackUrl != null && !fallbackUrl.isEmpty()) {
            Glide.with(context)
                    .load(fallbackUrl)
                    .into(binding.imageViewBanner);
        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
