package com.example.easyattend.authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.easyattend.R;
import com.example.easyattend.databinding.ActivityLoginBinding;
import com.example.easyattend.databinding.ActivityStartBinding;

public class StartActivity extends AppCompatActivity {

    ActivityStartBinding startBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startBinding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(startBinding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main));


        startBinding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));
            }
        });

        startBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
            }
        });

    }
}