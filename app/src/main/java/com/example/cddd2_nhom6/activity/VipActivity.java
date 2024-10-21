package com.example.cddd2_nhom6.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.cddd2_nhom6.databinding.ActivityVipBinding;
import com.example.cddd2_nhom6.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class VipActivity extends AppCompatActivity {
    private ActivityVipBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVipBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
// Đặt item mặc định được chọn là màn hình Home
        binding.bottomNavigation.setSelectedItemId(R.id.nav_vip);


        // Xử lý sự kiện chọn item của Bottom Navigation
        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                if (item.getItemId() == R.id.nav_home) {
                    intent = new Intent(VipActivity.this, MainActivity.class);
                } else if (item.getItemId() == R.id.nav_vip) {
                    return true;
                }else if(item.getItemId() == R.id.nav_profile) {
                    intent = new Intent(VipActivity.this, ProfileActivity.class);
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
}