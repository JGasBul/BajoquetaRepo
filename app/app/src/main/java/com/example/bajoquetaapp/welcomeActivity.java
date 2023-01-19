package com.example.bajoquetaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class welcomeActivity extends AppCompatActivity {
    private static final int SPLASH_TIME_OUT = 1500;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        new Handler().postDelayed(() -> {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            Intent i;
            if (currentUser == null) {
                i = new Intent(welcomeActivity.this, authActivity.class);
            } else {
                i = new Intent(welcomeActivity.this, MainActivity.class);
                i.putExtra("user", currentUser);
            }
            startActivity(i);
            finish();
        }, SPLASH_TIME_OUT);
    }
}