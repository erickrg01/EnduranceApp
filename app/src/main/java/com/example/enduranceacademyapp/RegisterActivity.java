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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.enduranceacademyapp.data.AppDatabase;
import com.example.enduranceacademyapp.data.SharedPrefManager;
import com.example.enduranceacademyapp.data.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    EditText etUser;
    EditText etPassword;
    EditText etPasswordConfirm;
    ImageButton btListo;

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private Button btGoogle;

    AppDatabase appDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUser = findViewById(R.id.etUser);
        etPassword = findViewById(R.id.etPassword);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        btListo = findViewById(R.id.btListo);


        FirebaseApp.initializeApp(this);


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


        appDB = Room.databaseBuilder(getApplicationContext(),AppDatabase.class,
                "AppDB").addCallback(myCallBack).build();

        btListo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = etUser.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String passwordConfirm = etPasswordConfirm.getText().toString().trim();

                if (usuario.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.matches(".*[A-Z].*")) { // 🔹 Verifica que tenga al menos una mayúscula
                    Toast.makeText(RegisterActivity.this, "La contraseña debe incluir al menos una letra mayúscula", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(passwordConfirm)) {
                    Toast.makeText(RegisterActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    return;
                }

                User u1 = new User(usuario, password);
                addUserInBackground(u1);
                registerUserWithEmail(usuario, password);
            }
        });

        mAuth = FirebaseAuth.getInstance();

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configurar Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // Asegúrate de poner el ID de cliente web aquí
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btGoogle = findViewById(R.id.btGoogle);
        btGoogle.setOnClickListener(v -> signInWithGoogle());

    }
    public void addUserInBackground(User user){

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                appDB.getUserDAO().addUser(user);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegisterActivity.this,"Añadido a la base de datos",Toast.LENGTH_SHORT).show();

                    }

                });
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.e("Google Sign-In", "Error al iniciar sesión: " + e.getStatusCode());
                Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(RegisterActivity.this, "Inicio de sesión exitoso: " + user.getDisplayName(), Toast.LENGTH_SHORT).show();

                        // Redirigir al usuario a otra actividad después del inicio de sesión
                        startActivity(new Intent(RegisterActivity.this, LobbyActivity.class));
                        finish();
                    } else {
                        Log.e("Firebase Auth", "Error al autenticar con Firebase", task.getException());
                        Toast.makeText(RegisterActivity.this, "Error al autenticar con Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerUserWithEmail(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        SharedPrefManager.saveUsername(this, user.getEmail());
                        Toast.makeText(RegisterActivity.this, "Registro exitoso. Bienvenido " + user.getEmail(), Toast.LENGTH_SHORT).show();

                        // Redirigir a la pantalla principal
                        startActivity(new Intent(RegisterActivity.this, LobbyActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error en el registro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
