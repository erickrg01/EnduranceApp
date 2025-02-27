package com.example.enduranceacademyapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.enduranceacademyapp.data.AppDatabase;
import com.example.enduranceacademyapp.data.SharedPrefManager;
import com.example.enduranceacademyapp.data.User;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SignInClient oneTapClient;
    private EditText etUser, etPassword;
    private ImageButton btListo;
    private Button btGoogle;

    AppDatabase appDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUser = findViewById(R.id.etUser);
        etPassword = findViewById(R.id.etPassword);
        btListo = findViewById(R.id.btListo);
        btGoogle = findViewById(R.id.btGoogle);

        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();

        // Inicializar Google Sign-In
        oneTapClient = Identity.getSignInClient(this);

        RoomDatabase.Callback myCallBack = new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);  // Llamada al método de la clase base, que es `public`
            }

            @Override
            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                super.onOpen(db);  // Llamada al método de la clase base, que es `public`
            }
        };


        appDB = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "AppDB")
                .addCallback(myCallBack)
                .fallbackToDestructiveMigration()  // Habilita la migración destructiva
                .build();


        btGoogle.setOnClickListener(v -> signInWithGoogle());

        btListo.setOnClickListener(v -> signInWithEmail());
    }
    private void signInWithEmail() {
        String email = etUser.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        SharedPrefManager.saveUsername(this, user.getEmail());

                        Toast.makeText(this, "Bienvenido " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, LobbyActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Error al iniciar sesión: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void signInWithGoogle() {
        BeginSignInRequest signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.default_web_client_id)) // Usa tu Client ID de Google
                        .setFilterByAuthorizedAccounts(false) // Si es true, solo muestra cuentas ya usadas en la app
                        .build())
                .build();

        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();
                    googleSignInLauncher.launch(intentSenderRequest);
                })
                .addOnFailureListener(this, e -> {
                    Log.e("Google Sign-In", "Error al iniciar sesión con Google: " + e.getMessage());
                    Toast.makeText(LoginActivity.this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show();
                });
    }

    private final ActivityResultLauncher<IntentSenderRequest> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    try {
                        SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                        String idToken = credential.getGoogleIdToken();
                        if (idToken != null) {
                            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                            mAuth.signInWithCredential(firebaseCredential)
                                    .addOnCompleteListener(this, task -> {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            Toast.makeText(this, "Bienvenido " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(this, LobbyActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Error al autenticar con Google", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } catch (ApiException e) {
                        Log.e("Google Sign-In", "Error al obtener credenciales de Google: " + e.getStatusCode());
                    }
                }
            });



    public void volver(View vista) {
        finish();
    }
}
