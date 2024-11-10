package com.example.cddd2_nhom6.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.ui.PlayerView;

import com.example.cddd2_nhom6.R;

import java.io.File;

@OptIn(markerClass = androidx.media3.common.util.UnstableApi.class)
public class XemPhimTaixuongActivity extends AppCompatActivity {
    private ExoPlayer player;
    private PlayerView playerView;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_downloaded_movie);
        playerView = findViewById(R.id.playerView);

        //Goi chuc nang nhan 2 lan de thoat
        getOnBackPressedDispatcher().addCallback(this, callback);
        // Lấy tên phim từ Intent
        String movieName = getIntent().getStringExtra("movie_name");

        if (movieName == null) {
            Log.e("PlayDownloadedMovieActivity", "Tên phim bị null");
            Toast.makeText(this, "Không tìm thấy tên phim!", Toast.LENGTH_SHORT).show();
            finish(); // Đóng activity nếu không có tên phim
            return;
        }

        Log.d("PlayDownloadedMovieActivity", "Tên phim: " + movieName);

        // Lấy thư mục phim dựa trên tên phim
        File movieDir = getMovieFile(movieName);

        // Kiểm tra nếu thư mục tồn tại và có chứa file playlist .m3u8
        File m3u8File = new File(movieDir, "playlist.m3u8");
        if (m3u8File.exists()) {
            Log.d("PlayDownloadedMovieActivity", "Đang phát từ file playlist: " + m3u8File.getAbsolutePath());

            // Cấu hình ExoPlayer để phát HLS
            DefaultTrackSelector trackSelector = new DefaultTrackSelector(this);
            player = new ExoPlayer.Builder(this)
                    .setTrackSelector(trackSelector)
                    .build();

            // Liên kết player với PlayerView
            playerView.setPlayer(player);

            // Sử dụng HlsMediaSource để phát tệp HLS
            Uri hlsUri = Uri.fromFile(m3u8File);
            HlsMediaSource hlsMediaSource = new HlsMediaSource.Factory(
                    new DefaultDataSource.Factory(this))
                    .createMediaSource(MediaItem.fromUri(hlsUri));

            // Chuẩn bị và phát
            player.setMediaSource(hlsMediaSource);
            player.prepare();
            player.play();
        } else {
            Log.e("PlayDownloadedMovieActivity", "Không tìm thấy tệp playlist .m3u8");
            Toast.makeText(this, "Không tìm thấy tệp playlist để phát phim!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (player != null) {
            player.play();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }

    // Hàm lấy thư mục chứa phim dựa trên tên phim
    public File getMovieFile(String movieName) {
        File movieDir = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES), "MyMovies/" + movieName);

        if (!movieDir.exists()) {
            boolean created = movieDir.mkdirs();
            if (!created) {
                Log.e("PlayDownloadedMovieActivity", "Không thể tạo thư mục lưu phim");
            }
        }
        return movieDir;
    }
    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    // Thiết lập OnBackPressedDispatcher
    OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (doubleBackToExitPressedOnce) {
                finishAffinity();  // Thoát ứng dụng
                return;
            }
            doubleBackToExitPressedOnce = true;
            Toast.makeText(getApplicationContext(), "Nhấn thoát thêm một lần nữa", Toast.LENGTH_SHORT).show();

            // Reset lại cờ sau 2 giây
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    };
}