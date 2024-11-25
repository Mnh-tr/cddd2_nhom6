package com.example.cddd2_nhom6.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.activity.ChiTietPhimActivity;
import com.example.cddd2_nhom6.databinding.ItemPhimBinding;
import com.example.cddd2_nhom6.model.DSPhim;

import java.util.ArrayList;
import java.util.List;

public class DSPhimTimKiemAdapter extends RecyclerView.Adapter<DSPhimTimKiemAdapter.DSPhimViewHolder>{
    private Context context;  // Thêm biến Activity context
    private List<DSPhim> dsPhims;
    private static OnRecyclerViewItemClickListener recyclerViewItemClickListener;

    public DSPhimTimKiemAdapter(Context context) {
        this.context = context;
        this.dsPhims = new ArrayList<>(); // Khởi tạo danh sách rỗng
    }
    public void updateFilms(List<DSPhim> films) {
        if (films != null) {
            this.dsPhims.clear();
            this.dsPhims.addAll(films);
            notifyDataSetChanged();
        }
    }

    public void addFilms(List<DSPhim> films) {
        if (films != null) {
            int startPosition = this.dsPhims.size();
            this.dsPhims.addAll(films);
            notifyItemRangeInserted(startPosition, films.size());
        }
    }

    // Constructor
    public DSPhimTimKiemAdapter(Activity context, List<DSPhim> dsPhims) {
        this.context = context;  // Khởi tạo biến context
        this.dsPhims = dsPhims;
    }

    // Setter cho listener
    public void setRecyclerViewItemClickListener(OnRecyclerViewItemClickListener recyclerViewItemClickListener) {
        DSPhimTimKiemAdapter.recyclerViewItemClickListener = recyclerViewItemClickListener;
    }

    @NonNull
    @Override
    public DSPhimViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPhimBinding binding = ItemPhimBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DSPhimViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DSPhimViewHolder holder, int position) {
        DSPhim dsPhim = dsPhims.get(position);
        holder.binding.movieTitle.setText(dsPhim.getName());
        holder.binding.movieYear.setText(String.valueOf(dsPhim.getYear()));

        String imageUrl = dsPhim.getPosterUrl();
        String thumbUrl = dsPhim.getPosterUrlOPhim();
        String source = dsPhim.getSource();

        // Load image based on source
        if (source != null) {
            if (source.equalsIgnoreCase("Ophim")) {
                loadImage(holder, thumbUrl, imageUrl);
                holder.binding.smallImageCorner.setImageResource(R.drawable.ic_logo_ophim);
            } else if (source.equalsIgnoreCase("Kkphim")) {
                loadImage(holder, imageUrl, thumbUrl);
                holder.binding.smallImageCorner.setImageResource(R.drawable.logo_kkphim);
            } else {
                // If source is unknown, try to load any available image
                loadImage(holder, imageUrl, thumbUrl);
            }
        } else {
            // If source is null, try to load any available image
            loadImage(holder, imageUrl, thumbUrl);
        }

        // Save the new position for the Holder
        final int pos = position;
        holder.position = pos;
    }



    @Override
    public int getItemCount() {
        return dsPhims.size();
    }

    // ViewHolder class
    public class DSPhimViewHolder extends RecyclerView.ViewHolder {
        ItemPhimBinding binding;
        int position;

        public DSPhimViewHolder(@NonNull ItemPhimBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), ChiTietPhimActivity.class);
                    DSPhim dsPhim = dsPhims.get(position);
                    intent.putExtra("slug", dsPhim.getSlug());
                    view.getContext().startActivity(intent);
                }
            });


        }

    }

    // Interface để xử lý sự kiện click
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }
    private void loadImage(DSPhimViewHolder holder, String primaryUrl, String fallbackUrl) {
        if (primaryUrl != null && !primaryUrl.isEmpty()) {
            Glide.with(context)
                    .load(primaryUrl)
                    .into(holder.binding.moviePoster);
        } else if (fallbackUrl != null && !fallbackUrl.isEmpty()) {
            Glide.with(context)
                    .load(fallbackUrl)
                    .into(holder.binding.moviePoster);
        }
    }
}