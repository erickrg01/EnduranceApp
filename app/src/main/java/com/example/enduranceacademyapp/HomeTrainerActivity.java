package com.example.enduranceacademyapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.enduranceacademyapp.fragments.ViewPagerAdapter2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeTrainerActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_trainer);

        // Inicializar componentes
        viewPager = findViewById(R.id.view_pagert);
        bottomNav = findViewById(R.id.bottom_navigationt);

        // Configurar ViewPager2 con Adapter
        setupViewPager();

        // Configurar BottomNavigationView
        bottomNav.setSelectedItemId(R.id.nav_editroutine);
        bottomNav.setOnItemSelectedListener(navListener);
        viewPager.setCurrentItem(0, false);

        // Sincronizar ViewPager2 con BottomNavigationView
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNav.setSelectedItemId(R.id.nav_editroutine);
                        break;
                }
            }
        });
    }
    private void setupViewPager() {
        ViewPagerAdapter2 adapter = new ViewPagerAdapter2(this);
        viewPager.setAdapter(adapter);
    }

    private NavigationBarView.OnItemSelectedListener navListener = item -> {
        int itemId = item.getItemId();

        if(itemId == R.id.nav_editroutine){
            viewPager.setCurrentItem(0, true);
        }
        return true;
    };
}