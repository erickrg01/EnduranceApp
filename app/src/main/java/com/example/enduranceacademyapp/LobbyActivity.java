package com.example.enduranceacademyapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.enduranceacademyapp.fragments.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class LobbyActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        // Inicializar componentes
        viewPager = findViewById(R.id.view_pager);
        bottomNav = findViewById(R.id.bottom_navigation);

        // Configurar ViewPager2 con Adapter
        setupViewPager();

        // Configurar BottomNavigationView
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(navListener);
        viewPager.setCurrentItem(0, false);

        // Sincronizar ViewPager2 con BottomNavigationView
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNav.setSelectedItemId(R.id.nav_home);
                        break;
                    case 1:
                        bottomNav.setSelectedItemId(R.id.nav_user);
                        break;
                }
            }
        });
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
    }

    private NavigationBarView.OnItemSelectedListener navListener = item -> {
        int itemId = item.getItemId();

        if(itemId == R.id.nav_home){
            viewPager.setCurrentItem(0, true);
        }else if (itemId == R.id.nav_user){
            viewPager.setCurrentItem(1, true);
        }
        return true;
    };
}
