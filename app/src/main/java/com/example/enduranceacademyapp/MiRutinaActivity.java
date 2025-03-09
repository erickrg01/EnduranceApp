package com.example.enduranceacademyapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.enduranceacademyapp.data.AppDatabase;
import com.example.enduranceacademyapp.data.RegistroEntrenamiento;
import com.example.enduranceacademyapp.data.Rutina;
import com.example.enduranceacademyapp.data.SharedPrefManager;
import com.example.enduranceacademyapp.data.User_Rutina;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MiRutinaActivity extends AppCompatActivity {

    private AppDatabase appDB;
    private TextView txtNombre, txtGrupoMuscular, txtEjercicio, txtRepes, txtSeries;
    private String currentUser;
    private Button btnRegistrarEntrenamiento;
    private EditText etxAnotaciones,regRepes,regSeries;

    String fechaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_rutina);

        // Obtener el día seleccionado de la intent
        String nombreDia = getIntent().getStringExtra("nombre_dia");
        fechaSeleccionada = getIntent().getStringExtra("fecha_seleccionada");


        // Inicializar vistas
        txtNombre = findViewById(R.id.txtNombreRutina);
        txtGrupoMuscular = findViewById(R.id.txtGrupoMuscular);
        txtEjercicio = findViewById(R.id.txtEjercicio);
        txtRepes = findViewById(R.id.numRepeticiones);
        txtSeries = findViewById(R.id.numSeries);
        etxAnotaciones = findViewById(R.id.etxAnotaciones);
        regRepes = findViewById(R.id.regRepes);
        regSeries = findViewById(R.id.regSeries);
        btnRegistrarEntrenamiento = findViewById(R.id.registrarEntrenamientoBtn);

        // Obtener el usuario actual
        currentUser = SharedPrefManager.getUsername(getApplicationContext());
        if (currentUser == null || currentUser.isEmpty()) {
            Toast.makeText(this, "No se encontró el usuario actual", Toast.LENGTH_SHORT).show();
            Log.e("MiRutinaActivity", "El usuario actual es nulo o vacío.");
            return;
        }

        // Inicializar la base de datos
        appDB = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "AppDB")
                .fallbackToDestructiveMigration()
                .build();

        // Cargar la rutina basada en el usuario y el día seleccionado
        if (nombreDia != null && !nombreDia.isEmpty()) {
            cargarRutina(nombreDia);
        } else {
            Toast.makeText(this, "No se recibió el nombre del día", Toast.LENGTH_SHORT).show();
        }


    }

    private void cargarRutina(String nombreDia) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                int userId = appDB.getUserDAO().getUserIdByUserName(currentUser);
                Log.d("MiRutinaActivity", "Usuario actual: " + currentUser);
                Log.d("MiRutinaActivity", "ID de usuario obtenido: " + userId);
                if (userId == 0) {
                    Log.e("MiRutinaActivity", "El usuario no tiene un ID válido.");
                    return;
                }

                List<User_Rutina> rutinas = appDB.getUser_RutinaDAO().getRutinasByUserId(userId);
                Rutina rutinaSeleccionada = null;

                for (User_Rutina userRutina : rutinas) {
                    if (esDiaValido(userRutina, nombreDia)) {
                        rutinaSeleccionada = appDB.getRutinaDAO().getRutinaById(userRutina.getIdRutina());
                        break;
                    }
                }

                Rutina finalRutina = rutinaSeleccionada;
                runOnUiThread(() -> {
                    if (finalRutina != null) {
                        txtNombre.setText(txtNombre.getText()+" "+finalRutina.getNombreRutina());
                        txtGrupoMuscular.setText(txtGrupoMuscular.getText()+" "+finalRutina.getGrupoMuscular());
                        txtEjercicio.setText(txtEjercicio.getText()+" "+finalRutina.getEjercicio());
                        txtSeries.setText(txtSeries.getText()+" "+String.valueOf(finalRutina.getSeries()));
                        txtRepes.setText(txtRepes.getText()+" "+String.valueOf(finalRutina.getRepeticiones()));
                    } else {
                        Toast.makeText(MiRutinaActivity.this, "No hay rutina para " + nombreDia, Toast.LENGTH_SHORT).show();
                        Log.e("MiRutinaActivity", "No se encontró una rutina para el día " + nombreDia);
                    }
                });

            } catch (Exception e) {
                Log.e("MiRutinaActivity", "Error al cargar la rutina", e);
                runOnUiThread(() -> Toast.makeText(MiRutinaActivity.this, "Error al cargar la rutina", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private boolean esDiaValido(User_Rutina rutina, String nombreDia) {
        switch (nombreDia.toLowerCase()) {
            case "monday": case "lunes":
                return rutina.isLunes();
            case "tuesday": case "martes":
                return rutina.isMartes();
            case "wednesday": case "miércoles": case "miercoles":
                return rutina.isMiercoles();
            case "thursday": case "jueves":
                return rutina.isJueves();
            case "friday": case "viernes":
                return rutina.isViernes();
            case "saturday": case "sábado": case "sabado":
                return rutina.isSabado();
            case "sunday": case "domingo":
                return rutina.isDomingo();
            default:
                return false;
        }
    }

    public void regEntrenamiento(View vista) {
        String fecha = fechaSeleccionada;
        String usuario = currentUser;
        String repeticiones = regRepes.getText().toString();
        String series = regSeries.getText().toString();
        String nombreRutina = txtNombre.getText().toString();
        String anotaciones = etxAnotaciones.getText().toString();

        if (usuario.isEmpty() || repeticiones.isEmpty() || series.isEmpty() || nombreRutina.isEmpty()) {
            Toast.makeText(this, "Faltan datos por completar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear el objeto de entrenamiento
        RegistroEntrenamiento nuevoRegistro = new RegistroEntrenamiento(usuario, fecha, Integer.parseInt(repeticiones), Integer.parseInt(series), nombreRutina, anotaciones);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            appDB.getRegistroEntrenamientoDAO().insert(nuevoRegistro);

            runOnUiThread(() -> {
                Toast.makeText(MiRutinaActivity.this, "Entrenamiento registrado con éxito", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void generarImagenes(int numSeries) {
        GridLayout imageContainer = findViewById(R.id.imageContainer);
        imageContainer.removeAllViews(); // Limpiar imágenes previas

        int numColumns = 3; // Número de columnas en la cuadrícula
        int imageSize = getResources().getDisplayMetrics().widthPixels / numColumns - 40; // Ajustar tamaño de imagen

        for (int i = 0; i < numSeries; i++) {
            ImageView imageView = new ImageView(this);

            // Crear LayoutParams con tamaño dinámico y margen
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.width = imageSize;
            layoutParams.height = imageSize;
            layoutParams.setMargins(10, 10, 10, 10); // Márgenes entre imágenes
            imageView.setBackgroundResource(R.drawable.rounded_background);
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(R.drawable.bp2); // Reemplazar con imagen real
            imageContainer.addView(imageView);
        }
    }



    public void toggleView(View view) {
        LinearLayout textContainer = findViewById(R.id.textViewContainer);
        GridLayout imageContainer = findViewById(R.id.imageContainer);
        Button toggleButton = findViewById(R.id.btnToggleView);

        if (textContainer.getVisibility() == View.VISIBLE) {
            textContainer.setVisibility(View.GONE);
            imageContainer.setVisibility(View.VISIBLE);
            toggleButton.setText("Modo Rutina");

            // Obtener el número de series y generar imágenes
            int numSeries = Integer.parseInt(txtSeries.getText().toString().replaceAll("[^0-9]", ""));
            generarImagenes(numSeries);
        } else {
            textContainer.setVisibility(View.VISIBLE);
            imageContainer.setVisibility(View.GONE);
            toggleButton.setText("Modo Imagen");
        }
    }


}