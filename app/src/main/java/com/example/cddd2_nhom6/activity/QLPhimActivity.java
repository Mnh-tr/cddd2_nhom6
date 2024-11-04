package com.example.cddd2_nhom6.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cddd2_nhom6.R;
import com.example.cddd2_nhom6.databinding.ActivityQlphimBinding;

public class QLPhimActivity extends AppCompatActivity {

    private ActivityQlphimBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQlphimBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}