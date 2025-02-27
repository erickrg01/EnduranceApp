package com.example.enduranceacademyapp;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

public class MyApp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this); // Habilitar MultiDex
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
