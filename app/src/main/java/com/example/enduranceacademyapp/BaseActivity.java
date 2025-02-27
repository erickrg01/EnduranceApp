package com.example.enduranceacademyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    public void login(View vista) {
        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
        startActivity(intent);

    }
    public void register(View vista){
        Intent intent = new Intent(BaseActivity.this, RegisterActivity.class);
        startActivity(intent);

    }
    public void entrenador(View vista) {
        Intent intent = new Intent(BaseActivity.this, HomeTrainerActivity.class);
        startActivity(intent);

    }
}
