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

    public static void fetchAllApiSourcesFromFirebase(final OnAllApiSourcesFetchListener listener, Context context) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("api_sources").child("selected_source");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ApiModel> apiSources = new ArrayList<>();

                // Duyệt qua tất cả các mục con
                for (DataSnapshot apiSnapshot : snapshot.getChildren()) {
                    String id = apiSnapshot.getKey();
                    String name = apiSnapshot.child("name").getValue(String.class);
                    String url = apiSnapshot.child("url").getValue(String.class);
                    Boolean isChecked = apiSnapshot.child("isChecked").getValue(Boolean.class);

                    // Kiểm tra nếu dữ liệu hợp lệ
                    if (name != null && url != null && !url.isEmpty()) {
                        ApiModel apiModel = new ApiModel( id,name, url, isChecked != null ? isChecked : false); // Bao gồm cả trạng thái
                        apiSources.add(apiModel);
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

