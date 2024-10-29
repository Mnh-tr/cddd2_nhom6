package com.example.cddd2_nhom6.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cddd2_nhom6.adapter.DSPhimAdapter;
import com.example.cddd2_nhom6.adapter.PhimAdapter;
import com.example.cddd2_nhom6.api.ApiClient;
import com.example.cddd2_nhom6.api.ApiService;
import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.databinding.ActivityMainBinding;
import com.example.cddd2_nhom6.model.DSPhim;
import com.example.cddd2_nhom6.model.Phim;
import com.example.cddd2_nhom6.model.ThongBaoKhiUngDungTat;
import com.example.cddd2_nhom6.model.ThongBaoTrenManHinh;
import com.example.cddd2_nhom6.model.TruyCap;
import com.example.cddd2_nhom6.response.DSPhimResponse;
import com.example.cddd2_nhom6.response.PhimResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private PhimAdapter movieAdapter;
    private ApiService apiService;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String idUser;
    private  String nameUser;
    private String emailUser;
    private int idLoaiND;
    // bien de kiểm tra người dùng có đang ỏ trong ứng dụng hay không
    public static Boolean truycap = false;
    private List<DSPhim> seriesKkphimPhimLe, seriesKkphimPhimBo, seriesKkphimPhimHoatHinh, seriesKkphimTvShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        laythongtinUser();
        Toast.makeText(MainActivity.this, "Xin chào " + nameUser, Toast.LENGTH_SHORT).show();
        updateUser();
        // Kiểm tra và thêm thông tin truy cập
        kiemTraTruyCap(idUser);

        // Thiết lập ActionBar và DrawerLayout
        setSupportActionBar(binding.toolbar);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerlayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );

        binding.drawerlayout.addDrawerListener(toggle);
        toggle.syncState();

        // Đặt biểu tượng trở về
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiện biểu tượng trở về
        }
        // Ẩn tiêu đề
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        swipeRefreshLayout = binding.swipeRefreshLayout; // Khởi tạo SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadMovies(); // Tải lại danh sách phim
            loadSeries(); // Tải lại danh sach phim bo
            loadTVShow();// Tải lại danh sách tvshow
            loadPhimLe();
            loadPhimHoatHinh();
        });

        apiService = ApiClient.getClient().create(ApiService.class);
        seriesKkphimPhimBo = new ArrayList<>();

        setupRecyclerViews();
        loadMovies();
        loadSeries();
        loadTVShow();
        loadPhimLe();
        loadPhimHoatHinh();
        navigationBottom();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            // Khởi tạo ThongBaoTrenManHinh
            ThongBaoTrenManHinh thongBao = new ThongBaoTrenManHinh(getApplicationContext());
            // Bắt đầu dịch vụ để lắng nghe thông báo
            thongBao.batDichVuThongBao();
            // Lấy ID người dùng và bắt đầu lắng nghe
            thongBao.layIdNguoiDungHienTai();
            // Khởi động dịch vụ khi nhận được thông báo
            Intent serviceIntent = new Intent(MainActivity.this, ThongBaoKhiUngDungTat.class);
            MainActivity.this.startForegroundService(serviceIntent);
        } else {
            Toast.makeText(this, "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }
        binding.xemThemPhimBo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, XemThemPhim.class);
            intent.putExtra("type", "series"); // truyền loại phim
            startActivity(intent);
        });
        binding.xemThemPhimLe.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, XemThemPhim.class);
            intent.putExtra("type", "phimle"); // truyền loại phim
            startActivity(intent);
        });
        binding.xemThemTVshow.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, XemThemPhim.class);
            intent.putExtra("type", "tvshow"); // truyền loại phim
            startActivity(intent);
        });
        binding.xemThemHoatHinh.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, XemThemPhim.class);
            intent.putExtra("type", "hoathinh"); // truyền loại phim
            startActivity(intent);
        });
        binding.xemThemPhimMoiNhat.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, XemThemPhim.class);
            intent.putExtra("type", "moinhat"); // truyền loại phim
            startActivity(intent);
        });

    }
    public static void kiemTraTruyCap(String idUser) {
        // Kiểm tra xem id_user có null hay không và xem ngày truy cập đã tồn tại hay chưa
        DatabaseReference truyCapRef = FirebaseDatabase.getInstance().getReference("TruyCap");
        long currentTime = System.currentTimeMillis();

        // Lấy ngày hiện tại (không bao gồm giờ, phút, giây)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date(currentTime));

        // Tìm kiếm bản ghi theo id_user và ngày truy cập
        truyCapRef.orderByChild("id_user").equalTo(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // neu truycap == false thì sẽ thêm vào TruyCap trên firebase
                if (truycap == false){
                    themTruyCap(idUser);
                    truycap = true;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TruyCap", "Lỗi khi kiểm tra truy cập: " + databaseError.getMessage());
            }
        });
    }


    public static void themTruyCap(String idUser) {
        DatabaseReference truyCapRef = FirebaseDatabase.getInstance().getReference("TruyCap");
        // Định dạng ngày và giờ thanh toán theo dd-MM-yyyy HH:mm:ss
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        String formattedDate = dateFormat.format(new Date()); // Lấy ngày và giờ hiện tại và định dạng

        // Tạo một ID mới cho bản ghi truy cập
        String truyCapId = truyCapRef.push().getKey();
        TruyCap truyCap = new TruyCap(idUser, formattedDate);

        // Thêm thông tin truy cập vào Firebase
        truyCapRef.child(truyCapId).setValue(truyCap)
                .addOnSuccessListener(aVoid -> {
                    // Xử lý thành công
                    Log.d("TruyCap", "Thêm truy cập thành công cho người dùng: " + idUser);
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi
                    Log.e("TruyCap", "Lỗi khi thêm truy cập: " + e.getMessage());
                });
    }
    private void laythongtinUser(){
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        idUser = sharedPreferences.getString("id_user", null);
        nameUser = sharedPreferences.getString("name", null);
        emailUser  = sharedPreferences.getString("email", null);
        idLoaiND = sharedPreferences.getInt("id_loaiND", 0);

    }
    private void updateUser(){
        // Tham chiếu đến NavigationView
        NavigationView navigationView = findViewById(R.id.navigationView);  // Giả sử NavigationView có id là nav_view

        // Lấy header view từ NavigationView
        View headerView = navigationView.getHeaderView(0);

        // Tham chiếu đến TextView trong header view
        TextView textView = headerView.findViewById(R.id.tvTenNguoiDung); // Thay bằng id của TextView trong layout_header

        if(nameUser != null){
            // Thay đổi nội dung TextView
            textView.setText(nameUser);
        }else{
            textView.setText("Khách");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Nạp menu
        getMenuInflater().inflate(R.menu.menu_timkiem, menu);
        // Tìm kiếm item trong menu
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        // Thiết lập listener cho sự kiện tìm kiếm
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Khi người dùng nhấn vào nút tìm kiếm trên bàn phím
                // Hiển thị nội dung tìm kiếm qua Toast
                Toast.makeText(MainActivity.this, "Tìm kiếm: " + query, Toast.LENGTH_SHORT).show();

                // Gọi API tìm kiếm với từ khóa và giới hạn 10 kết quả
                apiService.searchMovies(query, 10).enqueue(new Callback<DSPhimResponse>() {
                    @Override
                    public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<DSPhim> series = response.body().getData().getItems();
                            binding.recyclerViewMovies.setAdapter(new DSPhimAdapter(MainActivity.this, series));

                            // Đóng SearchView sau khi tìm kiếm
                            searchView.clearFocus();
                        } else {
                            Toast.makeText(MainActivity.this, "Không tìm thấy phim", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Lỗi khi tìm kiếm", Toast.LENGTH_SHORT).show();
                    }
                });

                //searchView.clearFocus();
                searchItem.collapseActionView();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Xử lý khi nội dung tìm kiếm thay đổi (nếu cần)
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            // Xử lý sự kiện khi nhấn vào tìm kiếm
            Toast.makeText(this, "Bạn muốn tìm kiếm gì", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.nav_thongbao) {
            Intent intent = new Intent(MainActivity.this,ThongBaoActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerViews() {
        binding.recyclerViewMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerViewSeries.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerViewtvShow.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerViewphimle.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerViewphimhoathinh.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }
    private void loadMovies() {
        // Hiển thị ProgressBar và ẩn nội dung chính
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.mainContent.setVisibility(View.GONE);

        apiService.getMovies(1).enqueue(new Callback<PhimResponse>() {
            @Override
            public void onResponse(Call<PhimResponse> call, Response<PhimResponse> response) {

                // Ẩn ProgressBar và hiển thị nội dung chính
                binding.progressBar.setVisibility(View.GONE);
                binding.mainContent.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false); // Ngừng loading

                if (response.isSuccessful() && response.body() != null) {
                    List<Phim> movies = response.body().getItems();
                    // Khởi tạo MovieAdapter
                    movieAdapter = new PhimAdapter(MainActivity.this, movies);
                    // Thiết lập sự kiện click cho từng item
                    movieAdapter.setRecyclerViewItemClickListener(new PhimAdapter.OnRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            //Lay thong tin chi tiet phim tu slug truyen den man hinh chi tiet phim
                            Intent intent = new Intent(view.getContext(), ChiTietPhimActivity.class);
                            Phim movie = movies.get(position);
                            intent.putExtra("slug", movie.getSlug());
                            view.getContext().startActivity(intent);
                        }
                    });
                    binding.recyclerViewMovies.setAdapter(movieAdapter);
                }
            }

            @Override
            public void onFailure(Call<PhimResponse> call, Throwable t) {
                // Ẩn ProgressBar và thông báo lỗi
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                binding.mainContent.setVisibility(View.VISIBLE); // Hiển thị lại nội dung chính
                swipeRefreshLayout.setRefreshing(false); // Ngừng loading
            }
        });
    }

    private void loadSeries() {
        apiService.getSeries(1).enqueue(new Callback<DSPhimResponse>() {
            @Override
            public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                // Ẩn ProgressBar và hiển thị nội dung chính
                binding.progressBar.setVisibility(View.GONE);
                binding.mainContent.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false); // Ngừng loading

                if (response.isSuccessful() && response.body() != null) {
                    List<DSPhim> dsPhims = response.body().getData().getItems();
                    binding.recyclerViewSeries.setAdapter(new DSPhimAdapter(MainActivity.this, dsPhims));
                }
            }

            @Override
            public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                // Ẩn ProgressBar và hiển thị nội dung chính
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                binding.mainContent.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false); // Ngừng loading

            }
        });
    }

    private void loadPhimLe() {

        apiService.getPhimLe(1).enqueue(new Callback<DSPhimResponse>() {
            @Override
            public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                // Ẩn ProgressBar và hiển thị nội dung chính
                binding.progressBar.setVisibility(View.GONE);
                binding.mainContent.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false); // Ngừng loading

                if (response.isSuccessful() && response.body() != null) {
                    List<DSPhim> series = response.body().getData().getItems();
                    binding.recyclerViewphimle.setAdapter(new DSPhimAdapter(MainActivity.this, series));
                }
            }

            @Override
            public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                // Ẩn ProgressBar và hiển thị nội dung chính
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                binding.mainContent.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false); // Ngừng loading
            }
        });
    }

    private void loadTVShow() {


        apiService.getTVShow(1).enqueue(new Callback<DSPhimResponse>() {
            @Override
            public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                // Ẩn ProgressBar và hiển thị nội dung chính
                binding.progressBar.setVisibility(View.GONE);
                binding.mainContent.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false); // Ngừng loading

                if (response.isSuccessful() && response.body() != null) {
                    List<DSPhim> series = response.body().getData().getItems();
                    binding.recyclerViewtvShow.setAdapter(new DSPhimAdapter(MainActivity.this, series));
                }
            }

            @Override
            public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                // Ẩn ProgressBar và hiển thị nội dung chính
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                binding.mainContent.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false); // Ngừng loading
            }
        });
    }

    private void loadPhimHoatHinh() {
        apiService.getHoatHinh(1).enqueue(new Callback<DSPhimResponse>() {
            @Override
            public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                // Ẩn ProgressBar và hiển thị nội dung chính
                binding.progressBar.setVisibility(View.GONE);
                binding.mainContent.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false); // Ngừng loading

                if (response.isSuccessful() && response.body() != null) {
                    List<DSPhim> series = response.body().getData().getItems();
                    binding.recyclerViewphimhoathinh.setAdapter(new DSPhimAdapter(MainActivity.this, series));
                }
            }

            @Override
            public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                // Ẩn ProgressBar và hiển thị nội dung chính
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                binding.mainContent.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false); // Ngừng loading
            }
        });
    }

    private void navigationBottom() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Đặt item mặc định được chọn là màn hình Home
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        // Xử lý sự kiện chọn item của Bottom Navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                if (item.getItemId() == R.id.nav_home) {
                    return true;
                } else if (item.getItemId() == R.id.nav_vip) {
                    intent = new Intent(MainActivity.this, VipActivity.class);
                }else if(item.getItemId() == R.id.nav_profile) {
                    intent = new Intent(MainActivity.this, CaNhanActivity.class);
                }
                // Pass the selected item to the new Activity
                if (intent != null) {
                    intent.putExtra("selected_item_id", item.getItemId());
                    startActivity(intent);
                    overridePendingTransition(0, 0);  // No animation for smooth transition
                    return true;
                }
                return false;

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Giữ màn hình sáng khi ứng dụng hoạt động
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Xóa cờ giữ màn hình sáng khi ứng dụng không còn hoạt động
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}