package com.example.petshopp.Admin.Order;

import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.petshopp.Admin.Order.Fragment.ChuaXacNhanFragment;
import com.example.petshopp.Admin.Order.Fragment.DaGiaoFragment;
import com.example.petshopp.Admin.Order.Fragment.DaXacNhanFragment;
import com.example.petshopp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class OrderActivity extends AppCompatActivity {
    ChuaXacNhanFragment chuaXacNhanFragment = new ChuaXacNhanFragment();
    DaXacNhanFragment daXacNhanFragment = new DaXacNhanFragment();
    DaGiaoFragment daGiaoFragment =new DaGiaoFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order2);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_order);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame,chuaXacNhanFragment).commit();

        Menu menu = bottomNavigationView.getMenu();

        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);

            SpannableString spannable = new SpannableString(menuItem.getTitle());
            spannable.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            menuItem.setTitle(spannable);
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.chua:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame,chuaXacNhanFragment).commit();
                        return true;
                    case R.id.daxac:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame,daXacNhanFragment).commit();
                        return true;
                    case R.id.dagiao:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame,daGiaoFragment).commit();
                        return true;

                }
                return false;
            }
        });
        boolean navigateToUserFragment = getIntent().getBooleanExtra("navigateToUserFragment", false);
        if (navigateToUserFragment) {
            if (!(getSupportFragmentManager().findFragmentById(R.id.frame) instanceof DaXacNhanFragment)) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, daXacNhanFragment).commit();
                bottomNavigationView.getMenu().findItem(R.id.daxac).setChecked(true);
            }
        }

        boolean navigateToDaXacFragment = getIntent().getBooleanExtra("navigateToDaXacFragment", false);
        if (navigateToDaXacFragment) {
            if (!(getSupportFragmentManager().findFragmentById(R.id.frame) instanceof DaGiaoFragment)) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, daGiaoFragment).commit();
                bottomNavigationView.getMenu().findItem(R.id.dagiao).setChecked(true);
            }
        }
    }
}