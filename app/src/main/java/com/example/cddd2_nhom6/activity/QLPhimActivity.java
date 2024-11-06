package com.example.cddd2_nhom6.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.adapter.QLPhimAdapter;
import com.example.cddd2_nhom6.databinding.ActivityQlphimBinding;
import com.example.cddd2_nhom6.model.Goi;
import com.example.cddd2_nhom6.model.KieuPhim;
import com.example.cddd2_nhom6.model.Phim;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class QLPhimActivity extends AppCompatActivity {

    private QLPhimAdapter adapter;
    private List<Phim> phimList = new ArrayList<>(); // Danh sách phim
    private List<KieuPhim> kieuPhimList = new ArrayList<>();
    private List<Goi> goiList = new ArrayList<>();
    private ActivityQlphimBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQlphimBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnAddPhim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(QLPhimActivity.this  ,ThemPhimActivity.class);
                startActivity(a);

            }
        });
        // Cài đặt RecyclerView
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QLPhimAdapter(this, phimList, kieuPhimList, goiList, selectedCount -> {
            if (selectedCount > 0) {
                binding.deleteIcon.setVisibility(View.VISIBLE); // Hiển thị icon xóa nếu có phim được chọn
            } else {
                binding.deleteIcon.setVisibility(View.GONE); // Ẩn icon xóa nếu không có phim nào được chọn
            }
        });

        // Thêm listener cho item click
        adapter.setRecyclerViewItemClickListener((view, position) -> {
            Phim selectedPhim = phimList.get(position);
            Intent intent = new Intent(QLPhimActivity.this, ThemPhimActivity.class);
            intent.putExtra("id_movie", selectedPhim.getId_movie()); // Truyền id_movie
            startActivity(intent);
        });

        binding.recyclerView.setAdapter(adapter);

        // Xử lý sự kiện cho các nút
        binding.btnngaytao.setOnClickListener(v -> showDatePickerDialog());
        xulySpiner();

        // Tải dữ liệu từ Firebase

        fetchGoiFromFirebase();
        fetchMoviesFromFirebase("All");

        // Xử lý sự kiện xóa phim khi nhấn vào icon xóa
        binding.deleteIcon.setOnClickListener(v -> {
            List<Phim> selectedMovies = adapter.getSelectedMovies();
            if (!selectedMovies.isEmpty()) {
                deleteSelectedMovies(selectedMovies);
            } else {
                Toast.makeText(this, "Không có phim nào được chọn", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void xulySpiner() {
        List<String> goiOptions = Arrays.asList("All", "Thường", "Vip");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, goiOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTimGoi.setAdapter(adapter);

        binding.spinnerTimGoi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGoi = parent.getItemAtPosition(position).toString();
                // Gọi lại fetchMoviesFromFirebase() để tải lại dữ liệu phim từ Firebase
                fetchMoviesFromFirebase(selectedGoi); // Truyền loại gói phim vào đây
            }



            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không cần xử lý gì ở đây
            }
        });
    }

    private void filterMoviesByGoi(String goiType) {
        List<Phim> filteredList = new ArrayList<>();

        for (Phim phim : phimList) {
            // Kiểm tra giá trị của goiType và so sánh với giá trị số trong Firebase
            if (goiType.equals("All") ||
                    (goiType.equals("Thường") && "0".equals(phim.getGoi())) ||
                    (goiType.equals("Vip") && "1".equals(phim.getGoi()))) {
                filteredList.add(phim);
            }
        }

        adapter.updateMovieList(filteredList); // Cập nhật danh sách phim của adapter
    }


    private void deleteSelectedMovies(List<Phim> selectedMovies) {
        for (Phim phim : selectedMovies) {
            // Xóa phim từ Firebase
            FirebaseDatabase.getInstance().getReference("Movies")
                    .child(phim.getId_movie())
                    .removeValue();
        }
        // Cập nhật lại giao diện
        adapter.getSelectedMovies().clear(); // Xóa danh sách phim đã chọn
        adapter.notifyDataSetChanged(); // Cập nhật RecyclerView
        binding.deleteIcon.setVisibility(View.GONE); // Ẩn icon xóa sau khi xóa
        Toast.makeText(this, "Đã xóa " + selectedMovies.size() + " phim", Toast.LENGTH_SHORT).show();
    }

    private void fetchMoviesFromFirebase(String goiType) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Movies");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                phimList.clear(); // Xóa dữ liệu cũ (nếu có)
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Phim movie = snapshot.getValue(Phim.class);
                    if (movie != null) {
                        phimList.add(movie);
                    }
                }
                // Sau khi tải xong dữ liệu từ Firebase, gọi lại phương thức lọc phim
                filterMoviesByGoi(goiType);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("QLPhimActivity", "Failed to fetch data", databaseError.toException());
            }
        });
    }




    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                QLPhimActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    binding.btnngaytao.setText(selectedDate);
                },
                year, month, day);

        datePickerDialog.show();
    }


    private void fetchGoiFromFirebase() {
        DatabaseReference goiRef = FirebaseDatabase.getInstance().getReference("Goi");
        goiRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                goiList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Goi goi = snapshot.getValue(Goi.class);
                    if (goi != null) {
                        goiList.add(goi);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("QLPhimActivity", "Failed to fetch goi", databaseError.toException());
            }
        });
    }

}