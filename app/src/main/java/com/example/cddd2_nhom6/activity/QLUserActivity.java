package com.example.cddd2_nhom6.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.databinding.ActivityQluserBinding;

public class QLUserActivity extends AppCompatActivity {
    private ActivityQluserBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQluserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}