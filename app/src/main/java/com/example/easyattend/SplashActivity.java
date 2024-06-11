package com.example.easyattend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.easyattend.authentication.LoginActivity;
import com.example.easyattend.authentication.StartActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main));

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                    startActivity(new Intent(SplashActivity.this, StartActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        thread.start();


    }
}