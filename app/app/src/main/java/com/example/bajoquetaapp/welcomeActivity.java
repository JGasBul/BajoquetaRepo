package com.example.bajoquetaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class welcomeActivity extends AppCompatActivity {
private static final int SPLASH_TIME_OUT = 1500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        new Handler().postDelayed(() -> {
            Intent i = new Intent(welcomeActivity.this, authActivity.class);
            startActivity(i);
            finish();
        },SPLASH_TIME_OUT);
    }
}