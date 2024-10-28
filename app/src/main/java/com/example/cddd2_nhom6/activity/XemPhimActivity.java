package com.example.cddd2_nhom6.activity;

import android.content.SharedPreferences;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.adapter.TapPhimAdapter;
import com.example.cddd2_nhom6.api.ApiClient;
import com.example.cddd2_nhom6.api.ApiService;
import com.example.cddd2_nhom6.databinding.ActivityXemPhimBinding;
import com.example.cddd2_nhom6.model.ChiTietPhim;
import com.example.cddd2_nhom6.model.DSPhimYeuThich;
import com.example.cddd2_nhom6.model.DanhGiaPhim;
import com.example.cddd2_nhom6.model.LichSuPhim;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class XemPhimActivity extends AppCompatActivity {
    private ActivityXemPhimBinding binding; // Khai báo View Binding
    private ExoPlayer exoPlayer; // ExoPlayer để phát video
    private boolean isFullScreen = false;
    private TapPhimAdapter tapPhimAdapter;
    private List<ChiTietPhim.TapPhim.DuLieuServer> serverDataList = new ArrayList<>();
    private String movieLink;
    private ApiService apiService;
    private String movieSlug;
    private DatabaseReference favoritesRef;
    private String idUser;
    private String nameUser;
    private String emailUser;
    private int idLoaiND;
    private DatabaseReference usersRef;
    private String currentUserId;
    private LichSuPhim lichSuPhim;
    private DSPhimYeuThich dsPhimYeuThich;
    private DanhGiaPhim danhGiaPhim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityXemPhimBinding.inflate(getLayoutInflater()); // Khởi tạo View Binding
        setContentView(binding.getRoot()); // Đặt layout cho Activity
        apiService = ApiClient.getClient().create(ApiService.class);
        setControl();
        setEvent();
    }

    public void setControl() {
        binding.rcvTapPhim.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.HORIZONTAL, false)); // Thiết lập RecyclerView
        // Khởi tạo Firebase Database
        favoritesRef = FirebaseDatabase.getInstance().getReference("favorites"); // Thay "favorites" bằng tên bảng của bạn
    }

    public void setEvent() {
        KhoiTaoPhim();
        // Thiết lập sự kiện cho nút toàn màn hình
        movieSlug = getIntent().getStringExtra("slug");

        binding.playerView.findViewById(R.id.btnFullScreen).setOnClickListener(v -> phongToPhim());
        apiService = ApiClient.getClient().create(ApiService.class);
        hienThiChiTietPhim();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        laythongtinUser();
        // Giả sử bạn đã đăng nhập và lấy ID người dùng từ Firebase Auth
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid(); // Lấy ID người dùng
        }
        lichSuPhim = new LichSuPhim(this);
        dsPhimYeuThich = new DSPhimYeuThich(this, binding, movieSlug);
        // Kiểm tra và cập nhật màu nút trái tim
        dsPhimYeuThich.kiemTraYeuThich();
        // Thêm sự kiện nhấn cho nút thêm vào danh sách yêu thích
        binding.btnAddToFavorites.setOnClickListener(v -> dsPhimYeuThich.themYeuThich());
        String movieSlug = this.movieSlug;
        // Xử lý khi người dùng đánh giá phim
        binding.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                danhGiaPhim.luuDanhGia(movieSlug, idUser, rating);  // Lưu đánh giá
            }
        });
        // Khởi tạo đối tượng DanhGiaPhim
        danhGiaPhim = new DanhGiaPhim(this,binding,movieSlug);
        // Tính và hiển thị trung bình sao và số lượt đánh giá
        danhGiaPhim.tinhTrungBinhDanhGia();
        // Kiểm tra xem người dùng đã đánh giá phim hay chưa
        danhGiaPhim.kiemTraDanhGia();
    }


    private void KhoiTaoPhim() {
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
    // Hàm phát video
    private void phatPhim(String episodeLink) {
        if (exoPlayer != null) {
            MediaItem mediaItem = MediaItem.fromUri(episodeLink);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.play();
        }
    }
    // Hàm phong to
    private void phongToPhim() {
        if (isFullScreen) {
            // Quay về chế độ portrait
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            // Hiện thanh trạng thái và thanh điều hướng
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            View decorView = getWindow().getDecorView(); // Lấy View của cửa sổ
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE); // Hiển thị thanh trạng thái và thanh điều hướng

            // Thiết lập chiều cao của PlayerView về 250dp
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.playerView.getLayoutParams();// Lấy LayoutParams của PlayerView
            params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());// Đặt chiều cao mới
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


    private void hienThiChiTietPhim() {
        Call<ChiTietPhim> call = apiService.getChiTietPhim(movieSlug);
        call.enqueue(new Callback<ChiTietPhim>() {
            @Override
            public void onResponse(Call<ChiTietPhim> call, Response<ChiTietPhim> response) {
                // Xử lý kết quả trả về từ API
                if (response.isSuccessful() && response.body() != null) {
                    // Lấy thông tin từ phản hồi
                    ChiTietPhim chiTietPhim = response.body();
                    List<ChiTietPhim.TapPhim> tapPhim = chiTietPhim.getEpisodes();
                    String tenPhim = chiTietPhim.getMovie().getName();
                    String tenTapPhim;

                    // Lấy tên tập phim từ Intent
                    if (getIntent().getBooleanExtra("lichsu", false)) {
                        tenTapPhim = getIntent().getStringExtra("episode");
                    } else {
                        tenTapPhim = getIntent().getStringExtra("episodeCurrent");
                    }

                    // Đặt tiêu đề phim và tên tập phim
                    binding.tvMovieTitle.setText(tenPhim + " - " + tenTapPhim);

                    // Thiết lập RecyclerView cho các tập phim
                    if (tapPhim != null && !tapPhim.isEmpty()) {
                        serverDataList.clear();

                        // Lấy danh sách các tập phim
                        for (ChiTietPhim.TapPhim episode : tapPhim) {
                            // Lấy danh sách các server
                            List<ChiTietPhim.TapPhim.DuLieuServer> data = episode.getServerData();
                            if (data != null) {
                                serverDataList.addAll(data);
                            }
                        }

                        tapPhimAdapter = new TapPhimAdapter(XemPhimActivity.this, serverDataList);

                        // Thiết lập click listener cho các tập phim
                        tapPhimAdapter.setRecyclerViewItemClickListener(new TapPhimAdapter.OnRecyclerViewItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                ChiTietPhim.TapPhim.DuLieuServer tapPhimDaChon = serverDataList.get(position);
                                String linkPhimMoi = tapPhimDaChon.getLinkM3u8();

                                // Hiển thị tên tập phim đang xem
                                binding.tvMovieTitle.setText(tenPhim + " - " + tapPhimDaChon.getName());
                                lichSuPhim.luuLichSuXem(movieSlug, tapPhimDaChon.getName(), serverDataList);
                                phatPhim(linkPhimMoi);
                            }
                        });

                        binding.rcvTapPhim.setAdapter(tapPhimAdapter);

                        // Tự động phát tập phim đầu tiên
                        movieLink = serverDataList.get(0).getLinkM3u8();
                        KhoiTaoPhim(); // Khởi tạo trình phát với tập đầu tiên
                    } else {
                        Toast.makeText(XemPhimActivity.this, "Không có tập phim nào", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ChiTietPhim> call, Throwable t) {
                Toast.makeText(XemPhimActivity.this, "Không thể tải chi tiết phim", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void laythongtinUser() {
        // Lấy thông tin người dùng từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        idUser = sharedPreferences.getString("id_user", null);
        nameUser = sharedPreferences.getString("name", null);
        emailUser = sharedPreferences.getString("email", null);
        idLoaiND = sharedPreferences.getInt("id_loaiND", 0);

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
