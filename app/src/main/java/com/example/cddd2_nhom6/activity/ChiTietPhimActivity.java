package com.example.cddd2_nhom6.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.api.ApiClient;
import com.example.cddd2_nhom6.api.ApiService;
import com.example.cddd2_nhom6.databinding.ActivityChiTietPhimBinding;
import com.example.cddd2_nhom6.model.ChiTietPhim;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChiTietPhimActivity extends AppCompatActivity {
    private String movieSlug;
    private List<ChiTietPhim.Episode.ServerData> serverDataList = new ArrayList<>();
    private ActivityChiTietPhimBinding binding;
    private ApiService apiService;
    private String movieLink;
    private ChiTietPhim.MovieItem movieDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChiTietPhimBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setEvent();
    }
    private void setEvent() {
        // Lấy slug từ Intent
        movieSlug = getIntent().getStringExtra("slug");
        // Lấy chi tiết phim
        //fetchMovieDetail();
        apiService = ApiClient.getClient().create(ApiService.class);
        loadMovieDetails(movieSlug);

        binding.btnXemPhim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChiTietPhimActivity.this, XemPhimActivity.class);
                startActivity(intent);
            }
        });
    }
    private void loadMovieDetails(String slug) {
        binding.progressBar.setVisibility(View.VISIBLE);
        Call<ChiTietPhim> call = apiService.getChiTietPhim(slug);
        call.enqueue(new Callback<ChiTietPhim>() {
            @Override
            public void onResponse(Call<ChiTietPhim> call, Response<ChiTietPhim> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Get movie details from the response
                    ChiTietPhim.MovieItem movie = response.body().getMovie();  // Assuming the response returns a Movie object

                    // Hiển thị thông tin phim using ViewBinding
                    binding.textViewTitle.setText(movie.getName());
                    binding.textViewDescription.setText(movie.getContent());
                    binding.textViewYear.setText(String.valueOf(movie.getYear()));
                    binding.textViewActors.setText(TextUtils.join(", ", movie.getActor()));
                    binding.textViewDirector.setText(TextUtils.join(", ", movie.getDirector()));
                    List<String> directores = movie.getDirector();
                    String directory = "";

                    // Kiểm tra nếu danh sách không null và không rỗng
                    if (directores != null && !directores.isEmpty()) {
                        // Sử dụng TextUtils để nối các chuỗi
                        directory = TextUtils.join(", ", directores);
                    }

                    // Gán giá trị vào TextView
                    binding.tvCountry.setText(directory);

                    List<ChiTietPhim.MovieItem.Country> countries = movie.getCountry();
                    if (countries != null && !countries.isEmpty()) {
                        // Duyệt qua danh sách thể loại và ghép tên của chúng thành chuỗi
                        List<String> countryNames = new ArrayList<>();
                        for (ChiTietPhim.MovieItem.Country country : countries) {
                            countryNames.add(country.getName());
                        }
                        // Chuyển danh sách tên thể loại thành chuỗi, ngăn cách bởi dấu phẩy
                        String countryText = TextUtils.join(", ", countryNames);
                        // Hiển thị chuỗi thể loại lên TextView
                        binding.tvCountry.setText(countryText);
                    }

                    List<ChiTietPhim.MovieItem.Category> categories = movie.getCategory();
                    if (categories != null && !categories.isEmpty()) {
                        // Duyệt qua danh sách thể loại và ghép tên của chúng thành chuỗi
                        List<String> categoryNames = new ArrayList<>();
                        for (ChiTietPhim.MovieItem.Category category : categories) {
                            categoryNames.add(category.getName());
                        }
                        // Chuyển danh sách tên thể loại thành chuỗi, ngăn cách bởi dấu phẩy
                        String categoryText = TextUtils.join(", ", categoryNames);
                        // Hiển thị chuỗi thể loại lên TextView
                        binding.categoryName.setText(categoryText);
                    }


                    // Tải poster bằng Glide (poster image and thumbnail)
                    Glide.with(ChiTietPhimActivity.this)
                            .load(movie.getThumbUrl())
                            .into(binding.imageViewthumburl);  // Use correct binding ID

                    Glide.with(ChiTietPhimActivity.this)
                            .load(movie.getPosterUrl())
                            .into(binding.imageViewPoster);  // Use correct binding ID

                    // Lấy danh sách các tập phim
                    List<ChiTietPhim.Episode> tapPhim = response.body().getEpisodes();
                    if (tapPhim != null && !tapPhim.isEmpty()) {
                        // Lưu danh sách các tập phim
                        serverDataList.clear(); // Xóa danh sách cũ
                        for (ChiTietPhim.Episode episode : tapPhim) {
                            List<ChiTietPhim.Episode.ServerData> data = episode.getServerData();
                            if (data != null) {
                                serverDataList.addAll(data); // Thêm tất cả các tập phim vào danh sách
                            }
                        }
                        binding.progressBar.setVisibility(View.GONE);
                        binding.scvChitiet.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(ChiTietPhimActivity.this, "Không có tập phim nào", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(ChiTietPhimActivity.this, "Failed to load movie details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChiTietPhim> call, Throwable t) {
                Toast.makeText(ChiTietPhimActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
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