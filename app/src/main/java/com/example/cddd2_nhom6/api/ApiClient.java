package com.example.cddd2_nhom6.api;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.cddd2_nhom6.activity.MainActivity;
import com.example.cddd2_nhom6.model.ApiModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static String baseUrl = "";

    private static Retrofit retrofit;

    public static ApiService createApiService(String baseUrl) {
        if (retrofit == null || !retrofit.baseUrl().toString().equals(baseUrl)) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }

    // Phương thức lấy URL từ Firebase
    public static void fetchBaseUrlFromFirebase(final OnBaseUrlFetchListener listener, Context context) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("api_sources").child("selected_source");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Duyệt qua tất cả các mục con trong selected_source
                for (DataSnapshot apiSnapshot : snapshot.getChildren()) {
                    String newUrl = apiSnapshot.child("url").getValue(String.class);
                    String source = apiSnapshot.child("name").getValue(String.class);

                    // Kiểm tra và sử dụng URL và name
                    if (newUrl != null && !newUrl.isEmpty() && source != null) {
                        setBaseUrl(newUrl);
                        listener.onBaseUrlFetched(source, newUrl); // Gọi callback với name và URL
                    } else {
                        listener.onError("URL hoặc name không tồn tại");
                    }
                }

//                String newUrl = snapshot.child("url").getValue(String.class);
//                String source = snapshot.child("name").getValue(String.class);
//
//                // Kiểm tra và sử dụng URL và name
//                if (newUrl != null && !newUrl.isEmpty() && source != null) {
//                    setBaseUrl(newUrl);
//                    listener.onBaseUrlFetched(source, newUrl); // Gọi callback với name và URL
//                } else {
//                    listener.onError("URL hoặc name không tồn tại");
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onError(error.getMessage());
            }
        });
    }
    public static Retrofit getClient() {
        // Kiểm tra xem baseUrl có giá trị hợp lệ không
        if (baseUrl == null || (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://"))) {
            throw new IllegalArgumentException("Base URL không hợp lệ: " + baseUrl);
        }
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public interface OnBaseUrlFetchListener {
        void onBaseUrlFetched(String name, String url); // Thêm name vào callback

        void onError(String errorMessage);
    }

    // Phương thức thay đổi baseUrl
    public static void setBaseUrl(String newBaseUrl) {
        baseUrl = newBaseUrl;
        retrofit = null; // Đặt lại retrofit để sử dụng baseUrl mới
    }

    public static void fetchAllApiSourcesFromFirebase(final OnAllApiSourcesFetchListener listener, Context context) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("api_sources").child("selected_source");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ApiModel> apiSources = new ArrayList<>();

                // Duyệt qua tất cả các mục con
                for (DataSnapshot apiSnapshot : snapshot.getChildren()) {
                    String name = apiSnapshot.child("name").getValue(String.class);
                    String url = apiSnapshot.child("url").getValue(String.class);

                    // Kiểm tra nếu cả name và url tồn tại
                    if (name != null && url != null && !url.isEmpty()) {
                        apiSources.add(new ApiModel(name, url)); // Thêm vào danh sách
                    }
                }

                // Trả danh sách về qua callback
                if (!apiSources.isEmpty()) {
                    listener.onAllApiSourcesFetched(apiSources);
                } else {
                    listener.onError("Không có nguồn API nào hợp lệ.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onError(error.getMessage());
            }
        });
    }
    public interface OnAllApiSourcesFetchListener {
        void onAllApiSourcesFetched(List<ApiModel> apiSources);
        void onError(String errorMessage);
    }



}

