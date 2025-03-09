package com.example.enduranceacademyapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Configurar la imagen
        ImageView splashImage = findViewById(R.id.splash_image);
        splashImage.setImageResource(R.drawable.enduranceacademyblanco); // Imagen personalizada

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Retraso de 3 segundos antes de comprobar el estado de la sesión
        new Handler().postDelayed(() -> checkUserSession(), 3000);
    }

    private void checkUserSession() {
        FirebaseUser user = mAuth.getCurrentUser();
        Intent intent;

        if (user != null) {
            // Si el usuario ya ha iniciado sesión, ir a LobbyActivity
            intent = new Intent(SplashActivity.this, LobbyActivity.class);
        } else {
            // Si no hay sesión iniciada, ir a BaseActivity (Login o Registro)
            intent = new Intent(SplashActivity.this, BaseActivity.class);
        }

        startActivity(intent);
        finish(); // Finalizar SplashActivity para evitar volver atrás
    }
}
