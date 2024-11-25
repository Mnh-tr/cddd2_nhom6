package com.example.cddd2_nhom6.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.adapter.DSPhimAdapter;
import com.example.cddd2_nhom6.adapter.DSPhimAdapterOphim;
import com.example.cddd2_nhom6.adapter.PhimAdapter;
import com.example.cddd2_nhom6.adapter.PhimLeAdapter;
import com.example.cddd2_nhom6.api.ApiClient;
import com.example.cddd2_nhom6.api.ApiService;
import com.example.cddd2_nhom6.databinding.ActivityXemThemPhimBinding;
import com.example.cddd2_nhom6.model.ApiModel;
import com.example.cddd2_nhom6.model.DSPhim;
import com.example.cddd2_nhom6.model.DSPhimAPiOphim;
import com.example.cddd2_nhom6.model.Phim;
import com.example.cddd2_nhom6.response.DSPhimResponse;
import com.example.cddd2_nhom6.response.DSResponseOphim;
import com.example.cddd2_nhom6.response.PhimResponse;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class XemThemPhim extends AppCompatActivity {
    private ActivityXemThemPhimBinding binding;
    private int currentPage = 1; // Trang hiện tại
    private List<DSPhim> kkphimList = new ArrayList<>();
    private List<DSPhim> ophimList = new ArrayList<>();
    private DSPhimAdapter phimLeAdapter;
    private boolean isLoading = false;
    private boolean doubleBackToExitPressedOnce = false;
    private AtomicInteger completedApis = new AtomicInteger(0); // Đếm số API đã hoàn thành
    private int totalApis;  // Tổng số API cần gọi
    private ArrayList<ApiModel> apiSources;
    private List<DSPhim> combinedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityXemThemPhimBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Goi chuc nang nhan 2 lan de thoat
        getOnBackPressedDispatcher().addCallback(this, callback);
        // Thiết lập ActionBar và DrawerLayout
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Danh sách Phim");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Nhận dữ liệu từ Intent
        apiSources = getIntent().getParcelableArrayListExtra("apiSources");
        String type = getIntent().getStringExtra("type");
        totalApis = apiSources.size();


        // Gọi API để tải dữ liệu phim ban đầu
        switch (type) {
            case "phimle":
                // Gọi hàm xử lý phim lẻ
                loadPhimLe(currentPage);
                break;
            case "phimbo":
                // Gọi hàm xử lý phim bộ
                loadPhimBo(currentPage);
                break;
            case "phimhoathinh":
                // Gọi hàm xử lý phim hoạt hình
                loadPhimHoatHinh(currentPage);
                break;
            case "tvshow":
                // Gọi hàm xử lý TV show
                loadTVShow(currentPage); // Hàm này bạn cần định nghĩa để xử lý TV show
                break;
        }

        binding.recyclerViewMovies.setLayoutManager(new GridLayoutManager(this, 3));
        binding.recyclerViewMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();

                if (layoutManager != null && !isLoading) {
                    // Số lượng mục hiện tại đang hiển thị trên màn hình.
                    int visibleItemCount = layoutManager.getChildCount();
                    // Tổng số lượng mục trong danh sách.
                    int totalItemCount = layoutManager.getItemCount();
                    // Vị trí của mục đầu tiên đang được hiển thị.
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    // Kiểm tra xem người dùng đã cuộn tới cuối chưa
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        // Đảm bảo không tải lại phim khi đã đang tải
                        if (!isLoading) {
                            currentPage++;
                            switch (type) {
                                case "phimle":
                                    // Gọi hàm xử lý phim lẻ
                                    loadPhimLe(currentPage);
                                    break;
                                case "phimbo":
                                    // Gọi hàm xử lý phim bộ
                                    loadPhimBo(currentPage);
                                    break;
                                case "phimhoathinh":
                                    // Gọi hàm xử lý phim hoạt hình
                                    loadPhimHoatHinh(currentPage);
                                    break;
                                case "tvshow":
                                    // Gọi hàm xử lý TV show
                                    loadTVShow(currentPage); // Hàm này bạn cần định nghĩa để xử lý TV show
                                    break;
                            }
                        }
                    }
                }
            }
        });




    }
    // Cập nhật lại loadPhimLe để thêm phim mới vào danh sách
    private void loadPhimLe(int page) {
        if (isLoading) return;  // Nếu đang tải, không gọi API nữa

        isLoading = true;  // Đánh dấu bắt đầu quá trình tải phim mới

        ApiClient.fetchAllApiSourcesFromFirebase(new ApiClient.OnAllApiSourcesFetchListener() {
            @Override
            public void onAllApiSourcesFetched(List<ApiModel> apiSources) {
                for (ApiModel api : apiSources) {
                    ApiService currentApiService = ApiClient.createApiService(api.getUrl());

                    if ("Kkphim".equals(api.getName())) {
                        // Gọi API của Kkphim
                        currentApiService.getPhimLe(page).enqueue(new Callback<DSPhimResponse>() {
                            @Override
                            public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<DSPhim> kkphim = response.body().getData().getItems();
                                    if (kkphim != null) {
                                        kkphim = kkphim.subList(0, Math.min(kkphim.size(), 6));  // Lấy 6 phim mỗi lần
                                        for (DSPhim phim : kkphim) {
                                            phim.setSource("Kkphim");
                                        }
                                        kkphimList.addAll(kkphim);  // Thêm phim mới vào danh sách
                                    }
                                }
                                checkAndUpdateRecyclerView();
                            }

                            @Override
                            public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                                checkAndUpdateRecyclerView();
                            }
                        });
                    } else if ("Ophim".equals(api.getName())) {
                        // Gọi API của Ophim
                        currentApiService.getPhimLeOphim(page).enqueue(new Callback<DSPhimResponse>() {
                            @Override
                            public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<DSPhim> ophim = response.body().getData().getItems();
                                    if (ophim != null) {
                                        ophim = ophim.subList(0, Math.min(ophim.size(), 6));  // Lấy 6 phim mỗi lần
                                        for (DSPhim phim : ophim) {
                                            phim.setSource("Ophim");
                                        }
                                        ophimList.addAll(ophim);  // Thêm phim mới vào danh sách
                                    }
                                }
                                checkAndUpdateRecyclerView();
                            }

                            @Override
                            public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                                checkAndUpdateRecyclerView();
                            }
                        });
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(XemThemPhim.this, "Lỗi khi lấy danh sách API: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        }, XemThemPhim.this);
    }
    private void loadPhimBo(int page) {
        if (isLoading) return;  // Nếu đang tải, không gọi API nữa

        isLoading = true;  // Đánh dấu bắt đầu quá trình tải phim mới

        ApiClient.fetchAllApiSourcesFromFirebase(new ApiClient.OnAllApiSourcesFetchListener() {
            @Override
            public void onAllApiSourcesFetched(List<ApiModel> apiSources) {
                for (ApiModel api : apiSources) {
                    ApiService currentApiService = ApiClient.createApiService(api.getUrl());

                    if ("Kkphim".equals(api.getName())) {
                        // Gọi API của Kkphim
                        currentApiService.getSeries(page).enqueue(new Callback<DSPhimResponse>() {
                            @Override
                            public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<DSPhim> kkphim = response.body().getData().getItems();
                                    if (kkphim != null) {
                                        kkphim = kkphim.subList(0, Math.min(kkphim.size(), 6));  // Lấy 6 phim mỗi lần
                                        for (DSPhim phim : kkphim) {
                                            phim.setSource("Kkphim");
                                        }
                                        kkphimList.addAll(kkphim);  // Thêm phim mới vào danh sách
                                    }
                                }
                                checkAndUpdateRecyclerView();
                            }

                            @Override
                            public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                                checkAndUpdateRecyclerView();
                            }
                        });
                    } else if ("Ophim".equals(api.getName())) {
                        // Gọi API của Ophim
                        currentApiService.getSeriesOphim(page).enqueue(new Callback<DSPhimResponse>() {
                            @Override
                            public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<DSPhim> ophim = response.body().getData().getItems();
                                    if (ophim != null) {
                                        ophim = ophim.subList(0, Math.min(ophim.size(), 6));  // Lấy 6 phim mỗi lần
                                        for (DSPhim phim : ophim) {
                                            phim.setSource("Ophim");
                                        }
                                        ophimList.addAll(ophim);  // Thêm phim mới vào danh sách
                                    }
                                }
                                checkAndUpdateRecyclerView();
                            }

                            @Override
                            public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                                checkAndUpdateRecyclerView();
                            }
                        });
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(XemThemPhim.this, "Lỗi khi lấy danh sách API: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        }, XemThemPhim.this);
    }

    private void loadTVShow(int page) {
        if (isLoading) return;  // Nếu đang tải, không gọi API nữa

        isLoading = true;  // Đánh dấu bắt đầu quá trình tải phim mới

        ApiClient.fetchAllApiSourcesFromFirebase(new ApiClient.OnAllApiSourcesFetchListener() {
            @Override
            public void onAllApiSourcesFetched(List<ApiModel> apiSources) {
                for (ApiModel api : apiSources) {
                    ApiService currentApiService = ApiClient.createApiService(api.getUrl());

                    if ("Kkphim".equals(api.getName())) {
                        // Gọi API của Kkphim
                        currentApiService.getTVShow(page).enqueue(new Callback<DSPhimResponse>() {
                            @Override
                            public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<DSPhim> kkphim = response.body().getData().getItems();
                                    if (kkphim != null) {
                                        kkphim = kkphim.subList(0, Math.min(kkphim.size(), 6));  // Lấy 6 phim mỗi lần
                                        for (DSPhim phim : kkphim) {
                                            phim.setSource("Kkphim");
                                        }
                                        kkphimList.addAll(kkphim);  // Thêm phim mới vào danh sách
                                    }
                                }
                                checkAndUpdateRecyclerView();
                            }

                            @Override
                            public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                                checkAndUpdateRecyclerView();
                            }
                        });
                    } else if ("Ophim".equals(api.getName())) {
                        // Gọi API của Ophim
                        currentApiService.getTVShowOphim(page).enqueue(new Callback<DSPhimResponse>() {
                            @Override
                            public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<DSPhim> ophim = response.body().getData().getItems();
                                    if (ophim != null) {
                                        ophim = ophim.subList(0, Math.min(ophim.size(), 6));  // Lấy 6 phim mỗi lần
                                        for (DSPhim phim : ophim) {
                                            phim.setSource("Ophim");
                                        }
                                        ophimList.addAll(ophim);  // Thêm phim mới vào danh sách
                                    }
                                }
                                checkAndUpdateRecyclerView();
                            }

                            @Override
                            public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                                checkAndUpdateRecyclerView();
                            }
                        });
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(XemThemPhim.this, "Lỗi khi lấy danh sách API: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        }, XemThemPhim.this);
    }
    private void loadPhimHoatHinh(int page) {
        if (isLoading) return;  // Nếu đang tải, không gọi API nữa

        isLoading = true;  // Đánh dấu bắt đầu quá trình tải phim mới

        ApiClient.fetchAllApiSourcesFromFirebase(new ApiClient.OnAllApiSourcesFetchListener() {
            @Override
            public void onAllApiSourcesFetched(List<ApiModel> apiSources) {
                for (ApiModel api : apiSources) {
                    ApiService currentApiService = ApiClient.createApiService(api.getUrl());

                    if ("Kkphim".equals(api.getName())) {
                        // Gọi API của Kkphim
                        currentApiService.getHoatHinh(page).enqueue(new Callback<DSPhimResponse>() {
                            @Override
                            public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<DSPhim> kkphim = response.body().getData().getItems();
                                    if (kkphim != null) {
                                        kkphim = kkphim.subList(0, Math.min(kkphim.size(), 6));  // Lấy 6 phim mỗi lần
                                        for (DSPhim phim : kkphim) {
                                            phim.setSource("Kkphim");
                                        }
                                        kkphimList.addAll(kkphim);  // Thêm phim mới vào danh sách
                                    }
                                }
                                checkAndUpdateRecyclerView();
                            }

                            @Override
                            public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                                checkAndUpdateRecyclerView();
                            }
                        });
                    } else if ("Ophim".equals(api.getName())) {
                        // Gọi API của Ophim
                        currentApiService.getHoatHinhOphim(page).enqueue(new Callback<DSPhimResponse>() {
                            @Override
                            public void onResponse(Call<DSPhimResponse> call, Response<DSPhimResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<DSPhim> ophim = response.body().getData().getItems();
                                    if (ophim != null) {
                                        ophim = ophim.subList(0, Math.min(ophim.size(), 6));  // Lấy 6 phim mỗi lần
                                        for (DSPhim phim : ophim) {
                                            phim.setSource("Ophim");
                                        }
                                        ophimList.addAll(ophim);  // Thêm phim mới vào danh sách
                                    }
                                }
                                checkAndUpdateRecyclerView();
                            }

                            @Override
                            public void onFailure(Call<DSPhimResponse> call, Throwable t) {
                                checkAndUpdateRecyclerView();
                            }
                        });
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(XemThemPhim.this, "Lỗi khi lấy danh sách API: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        }, XemThemPhim.this);
    }




    // Kiểm tra và cập nhật RecyclerView sau khi tất cả các API đã hoàn thành
    private void checkAndUpdateRecyclerView() {
        // Kết hợp danh sách phim từ cả 2 API theo kiểu xen kẽ
        List<DSPhim> combinedList = new ArrayList<>();
        int maxSize = Math.max(kkphimList.size(), ophimList.size());

        for (int i = 0; i < maxSize; i++) {
            if (i < kkphimList.size()) {
                combinedList.add(kkphimList.get(i));  // Thêm phim từ Kkphim
            }
            if (i < ophimList.size()) {
                combinedList.add(ophimList.get(i));  // Thêm phim từ Ophim
            }
        }

        // Cập nhật RecyclerView với danh sách phim đã kết hợp
        updateRecyclerView(combinedList);
    }


    // Cập nhật RecyclerView với danh sách phim mới
    private void updateRecyclerView(List<DSPhim> movies) {
        if (phimLeAdapter == null) {
            phimLeAdapter = new DSPhimAdapter(this, movies);
            binding.recyclerViewMovies.setAdapter(phimLeAdapter);
        } else {
            phimLeAdapter.updateFilms(movies); // Cập nhật dữ liệu vào adapter thay vì tạo mới
            phimLeAdapter.notifyDataSetChanged(); // Thông báo adapter về việc cập nhật
        }

        isLoading = false; // Kết thúc quá trình tải phim
    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    };
}


