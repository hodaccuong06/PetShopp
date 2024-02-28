package com.example.petshopp.Activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.petshopp.Domain.fragment.ChatFragment;
import com.example.petshopp.Domain.fragment.HomeFragment;
import com.example.petshopp.Domain.fragment.NotificationFragment;
import com.example.petshopp.Domain.fragment.UserFragment;
import com.example.petshopp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {


    private BottomNavigationView mBottomNAV;
    HomeFragment homeFragment = new HomeFragment();
    ChatFragment communityFragment = new ChatFragment();
    NotificationFragment notificationFragment = new NotificationFragment();
    UserFragment userFragment =new UserFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomNAV = findViewById(R.id.bottom_nav);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();

        mBottomNAV.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();
                        return true;
                    case R.id.group:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,communityFragment).commit();
                        return true;
                    case R.id.user:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,userFragment).commit();
                        return true;

                }
                return false;
            }
        });
        boolean navigateToUserFragment = getIntent().getBooleanExtra("navigateToUserFragment", false);
        if (navigateToUserFragment) {
            if (!(getSupportFragmentManager().findFragmentById(R.id.container) instanceof UserFragment)) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                mBottomNAV.getMenu().findItem(R.id.home).setChecked(true);
            }
        }
    }
}