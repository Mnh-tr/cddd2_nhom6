package com.example.cddd2_nhom6.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cddd2_nhom6.adapter.DSPhimAdapter;
import com.example.cddd2_nhom6.adapter.PhimAdapter;
import com.example.cddd2_nhom6.api.ApiClient;
import com.example.cddd2_nhom6.api.ApiService;
import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.databinding.ActivityMainBinding;
import com.example.cddd2_nhom6.model.DSPhim;
import com.example.cddd2_nhom6.model.Phim;
import com.example.cddd2_nhom6.response.DSPhimResponse;
import com.example.cddd2_nhom6.response.PhimResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private PhimAdapter movieAdapter;
    private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        apiService = ApiClient.getClient().create(ApiService.class);
        setupRecyclerViews();


        loadMovies();
        loadSeries();
        loadTVShow();
        loadPhimLe();
        loadPhimHoatHinh();

    }

    private void setupRecyclerViews() {
        binding.recyclerViewMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerViewSeries.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerViewtvShow.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerViewphimle.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerViewphimhoathinh.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }
    private void loadMovies() {
        apiService.getMovies(1).enqueue(new Callback<PhimResponse>() {
            @Override
            public void onResponse(Call<PhimResponse> call, Response<PhimResponse> response) {

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
                Toast.makeText(MainActivity.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSeries() {
        apiService.getSeries().enqueue(new Callback<DSPhimResponse>() {
            @Override
            public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DSPhim> dsPhims = response.body().getData().getItems();
                    binding.recyclerViewSeries.setAdapter(new DSPhimAdapter(MainActivity.this, dsPhims));
                }
            }

            @Override
            public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void loadPhimLe() {
        apiService.getPhimLe().enqueue(new Callback<DSPhimResponse>() {
            @Override
            public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    List<DSPhim> series = response.body().getData().getItems();
                    binding.recyclerViewphimle.setAdapter(new DSPhimAdapter(MainActivity.this, series));
                }
            }

            @Override
            public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTVShow() {
        apiService.getTVShow().enqueue(new Callback<DSPhimResponse>() {
            @Override
            public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DSPhim> series = response.body().getData().getItems();
                    binding.recyclerViewtvShow.setAdapter(new DSPhimAdapter(MainActivity.this, series));
                }
            }

            @Override
            public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPhimHoatHinh() {
        apiService.getHoatHinh().enqueue(new Callback<DSPhimResponse>() {
            @Override
            public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DSPhim> series = response.body().getData().getItems();
                    binding.recyclerViewphimhoathinh.setAdapter(new DSPhimAdapter(MainActivity.this, series));
                }
            }

            @Override
            public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
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