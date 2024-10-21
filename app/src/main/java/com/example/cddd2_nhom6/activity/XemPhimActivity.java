package com.example.cddd2_nhom6.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.AspectRatioFrameLayout;
import androidx.media3.ui.PlayerView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.adapter.TapPhimAdapter;
import com.example.cddd2_nhom6.api.ApiClient;
import com.example.cddd2_nhom6.api.ApiService;
import com.example.cddd2_nhom6.databinding.ActivityXemPhimBinding;
import com.example.cddd2_nhom6.databinding.CustomPlayerControlsBinding;
import com.example.cddd2_nhom6.model.ChiTietPhim;
import com.example.cddd2_nhom6.model.DSPhimYeuThich;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class XemPhimActivity extends AppCompatActivity {
    private ActivityXemPhimBinding binding; // Khai báo View Binding
    private ExoPlayer exoPlayer; // ExoPlayer để phát video
    private ImageButton btnFullScreen;
    private CustomPlayerControlsBinding controlsBinding;
    private DSPhimYeuThich dsPhimYeuThich;
    private boolean isFullScreen = false;
    private TapPhimAdapter tapPhimAdapter;
    private List<ChiTietPhim.Episode.ServerData> serverDataList = new ArrayList<>();
    private String movieLink;
    private ApiService apiService;
    private String movieSlug;
    private String idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityXemPhimBinding.inflate(getLayoutInflater()); // Khởi tạo View Binding
        setContentView(binding.getRoot()); // Đặt layout cho Activity

        setControl();
        setEvent();

    }
    public void setControl() {
        // Gán View cho các biến

        binding.rcvTapPhim.setLayoutManager(new GridLayoutManager(this, 4)); // Thiết lập RecyclerView
    }

    public void setEvent() {
        binding.playerView.findViewById(R.id.btnFullScreen).setOnClickListener(v -> phongToManHinh());
        layLinkPhim();
        movieSlug = getIntent().getStringExtra("slug");
        apiService = ApiClient.getClient().create(ApiService.class);
        hienThiChiTietPhim();
        dsPhimYeuThich = new DSPhimYeuThich(this, binding, movieSlug);
        // Kiểm tra và cập nhật màu nút trái tim
        dsPhimYeuThich.checkAndToggleFavorite();
        // Thêm sự kiện nhấn cho nút thêm vào danh sách yêu thích
        binding.btnAddToFavorites.setOnClickListener(v -> dsPhimYeuThich.addToFavorites());
    }

    private void layLinkPhim() {
        movieLink = getIntent().getStringExtra("movie_link"); // Đã sửa để lấy lại movieLink
        if (exoPlayer == null) {
            exoPlayer = new ExoPlayer.Builder(this).build();
            PlayerView playerView = binding.playerView; // Đảm bảo đây là PlayerView từ Media3
            playerView.setPlayer(exoPlayer); // Thiết lập player cho PlayerView
            playerView.setKeepScreenOn(true);

            exoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlayerError(PlaybackException error) {
                    Toast.makeText(XemPhimActivity.this, "Phim lỗi vui lòng báo cáo cho admin hoặc xem phim khác: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            // Tạo MediaItem từ đường dẫn video
            MediaItem mediaItem = MediaItem.fromUri(movieLink);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
        }
    }

    private void phatPhim(String episodeLink) {
        if (exoPlayer != null) {
            MediaItem mediaItem = MediaItem.fromUri(episodeLink);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.play();
        }
    }

    private void hienThiChiTietPhim() {
        Call<ChiTietPhim> call = apiService.getChiTietPhim(movieSlug);
        call.enqueue(new Callback<ChiTietPhim>() {
            @Override
            public void onResponse(Call<ChiTietPhim> call, Response<ChiTietPhim> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ChiTietPhim movieDetails = response.body();
                    List<ChiTietPhim.Episode> tapPhim = movieDetails.getEpisodes();
                    String movieTitle = movieDetails.getMovie().getName();
                    String episodeName = getIntent().getStringExtra("episodeCurrent");
                    binding.tvMovieTitle.setText(movieDetails.getMovie().getName());
                    binding.tvMovieTitle.setText(movieTitle + " - " + episodeName);
                    if (tapPhim != null && !tapPhim.isEmpty()) {
                        serverDataList.clear();
                        for (ChiTietPhim.Episode episode : tapPhim) {
                            List<ChiTietPhim.Episode.ServerData> data = episode.getServerData();
                            if (data != null) {
                                serverDataList.addAll(data);
                            }
                        }

                        tapPhimAdapter = new TapPhimAdapter(XemPhimActivity.this, serverDataList);
                        // Cập nhật RecyclerView với danh sách tập phim
                        tapPhimAdapter.setRecyclerViewItemClickListener(new TapPhimAdapter.OnRecyclerViewItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                ChiTietPhim.Episode.ServerData selectedEpisode = serverDataList.get(position);
                                String newMovieLink = selectedEpisode.getLinkM3u8();
                                //Hien thi ten tap phim  dang xem
                                String movieTitle = movieDetails.getMovie().getName();
                                String episodeName = selectedEpisode.getName();
                                binding.tvMovieTitle.setText(movieTitle + " - " + episodeName);

                                phatPhim(newMovieLink);
                            }
                        });

                        binding.rcvTapPhim.setAdapter(tapPhimAdapter);

                        // Play the first episode automatically
                        movieLink = serverDataList.get(0).getLinkM3u8();
                        layLinkPhim(); // Initialize player with the first episode
                    } else {
                        Toast.makeText(XemPhimActivity.this, "Không có tập phim nào", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ChiTietPhim> call, Throwable t) {
                Toast.makeText(XemPhimActivity.this, "Failed to load movie details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void phongToManHinh() {
        if (isFullScreen) {
            // Quay về chế độ portrait
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            // Hiện thanh trạng thái và thanh điều hướng
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

            // Thiết lập chiều cao của PlayerView về 250dp
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.playerView.getLayoutParams();
            params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());
            binding.playerView.setLayoutParams(params);
        } else {
            // Chuyển sang chế độ landscape
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            // Ẩn thanh trạng thái và thanh điều hướng
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

            // Thiết lập chiều cao của PlayerView để chiếm toàn bộ chiều cao màn hình
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.playerView.getLayoutParams();
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            binding.playerView.setLayoutParams(params);
        }

        isFullScreen = !isFullScreen; // Đổi trạng thái fullscreen
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.release();  // Giải phóng ExoPlayer khi Activity bị hủy
            exoPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (exoPlayer != null) {
            exoPlayer.pause(); // Tạm dừng video khi Activity không còn hiển thị
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Giữ màn hình sáng khi ứng dụng hoạt động
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}