package com.example.cddd2_nhom6.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.adapter.DSPhimAdapter;
import com.example.cddd2_nhom6.adapter.PhimAdapter;
import com.example.cddd2_nhom6.api.ApiClient;
import com.example.cddd2_nhom6.api.ApiService;
import com.example.cddd2_nhom6.databinding.ActivityXemThemPhimBinding;
import com.example.cddd2_nhom6.model.DSPhim;
import com.example.cddd2_nhom6.model.Phim;
import com.example.cddd2_nhom6.response.DSPhimResponse;
import com.example.cddd2_nhom6.response.PhimResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class XemThemPhim extends AppCompatActivity {
    private ActivityXemThemPhimBinding binding;
    private int currentPage = 1; // Trang hiện tại
    private String type;
    private List<DSPhim> DSPhimXemThem;
    private List<Phim> PhimXemThem;
    private DSPhimAdapter dsPhimAdapter;
    private PhimAdapter phimAdapter;
    private boolean isLoading = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityXemThemPhimBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Nhận dữ liệu từ Intent và lưu vào một biến duy nhất
        type = getIntent().getStringExtra("type");
        Log.d("XemThemPhim", "Received type: " + type);

        DSPhimXemThem = new ArrayList<>();
        PhimXemThem = new ArrayList<>();
        if ("moinhat".equals(type)) {
            phimAdapter = new PhimAdapter(this, PhimXemThem); // Adapter cho phim mới nhất
            binding.recyclerViewMovies.setAdapter(phimAdapter); // Sử dụng phimAdapter
        } else {
            dsPhimAdapter = new DSPhimAdapter(this, DSPhimXemThem); // Adapter cho các loại phim khác
            binding.recyclerViewMovies.setAdapter(dsPhimAdapter); // Sử dụng dsPhimAdapter
        }
        binding.recyclerViewMovies.setLayoutManager(new GridLayoutManager(this, 3));

        // Tải dữ liệu ban đầu
        loadXemThemPhim(currentPage, type);

        // Thêm listener cuộn để tải thêm dữ liệu khi đến cuối danh sách
        binding.recyclerViewMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();

                if (layoutManager != null && !isLoading) {
                    // Số lượng mục hiện tại đang hiển thị trên màn hình.
                    int visibleItemCount = layoutManager.getChildCount();
                    //Tổng số lượng mục trong danh sách.
                    int totalItemCount = layoutManager.getItemCount();
                    //Vị trí của mục đầu tiên đang được hiển thị.
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        currentPage++;
                        loadXemThemPhim(currentPage, type);
                    }
                }
            }
        });
    }

    private void loadXemThemPhim(int page, String type) {
        isLoading = true;
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        if ("moinhat".equals(type)) {
            Call<PhimResponse> call = apiService.getMovies(page);
            call.enqueue(new Callback<PhimResponse>() {
                @Override
                public void onResponse(Call<PhimResponse> call, Response<PhimResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Phim> newMovies = response.body().getItems();
                        if (newMovies != null) {
                            PhimXemThem.addAll(newMovies);
                            phimAdapter.notifyDataSetChanged();
                        }
                    }
                    isLoading = false;
                }

                @Override
                public void onFailure(Call<PhimResponse> call, Throwable t) {
                    isLoading = false;
                }
            });
        } else {
            Call<DSPhimResponse> call;
            switch (type) {
                case "phimle":
                    call = apiService.getPhimLe(page);
                    break;
                case "series":
                    call = apiService.getSeries(page);
                    break;
                case "hoathinh":
                    call = apiService.getHoatHinh(page);
                    break;
                case "tvshow":
                    call = apiService.getTVShow(page);
                    break;
                default:
                    isLoading = false;
                    return;
            }

            call.enqueue(new Callback<DSPhimResponse>() {
                @Override
                public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<DSPhim> newMovies = response.body().getData().getItems();
                        if (newMovies != null) {
                            DSPhimXemThem.addAll(newMovies);
                            dsPhimAdapter.notifyDataSetChanged();
                        }
                    }
                    isLoading = false;
                }

                @Override
                public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                    isLoading = false;
                }
            });
        }
    }
}
