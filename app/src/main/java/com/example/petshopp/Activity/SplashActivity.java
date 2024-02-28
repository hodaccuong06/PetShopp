package com.example.petshopp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petshopp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                nextActivity(user);
                Intent intent = new Intent(SplashActivity.this, IntroActivity.class);
                startActivity(intent);
            }
        }, 3000); // 3000 milliseconds = 3 seconds
    }

//    private void nextActivity(FirebaseUser user) {
//        if (user != null) {
//            // Người dùng đã đăng nhập
//            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        } else {
//            // Người dùng chưa đăng nhập
//            Intent intent = new Intent(SplashActivity.this, IntroActivity.class);
//            startActivity(intent);
//            finish();
//        }
//    }
}
