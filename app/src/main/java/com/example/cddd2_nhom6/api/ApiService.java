package com.example.cddd2_nhom6.api;
import com.example.cddd2_nhom6.model.ChiTietPhim;
import com.example.cddd2_nhom6.response.DSPhimResponse;
import com.example.cddd2_nhom6.response.PhimResponse;


import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.GET;
import retrofit2.Call;

public interface ApiService {

    @GET("danh-sach/phim-moi-cap-nhat")
    Call<PhimResponse> getMovies(@Query("page") int page);
    @GET("v1/api/danh-sach/phim-bo")
    Call<DSPhimResponse> getSeries();
    @GET("v1/api/danh-sach/tv-shows")
    Call<DSPhimResponse> getTVShow();

    @GET("v1/api/danh-sach/phim-le")
    Call<DSPhimResponse> getPhimLe();

    @GET("v1/api/danh-sach/hoat-hinh")
    Call<DSPhimResponse> getHoatHinh();

    @GET("v1/api/the-loai/tinh-cam")
    Call<DSPhimResponse> getTheLoai();

    @GET("phim/{slug}")
    Call<ChiTietPhim> getChiTietPhim(@Path("slug") String slug);
}
