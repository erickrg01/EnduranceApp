package com.example.enduranceacademyapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.enduranceacademyapp.data.AppDatabase;
import com.example.enduranceacademyapp.data.Rutina;
import com.example.enduranceacademyapp.data.User;
import com.example.enduranceacademyapp.data.User_Rutina;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsignarRutinaActivity extends AppCompatActivity {

    Spinner spinnerUsuarios, spinnerRutinas;
    AppDatabase appDB;
    Button btnAsignar;
    TextView txtNombre, txtGrupo, txtEjercicio, txtSeries, txtRepes;
    CheckBox ckMonday, ckTuesday, ckWednesday, ckThursday, ckFriday, ckSaturday, ckSunday;
    RadioGroup tipoEntrenamiento;
    Rutina r;
    String originalNombre, originalGrupo, originalEjercicio, originalSeries, originalRepes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asignar_rutina);

        txtNombre = findViewById(R.id.txtNombreRutina);
        txtGrupo = findViewById(R.id.txtGrupoMuscular);
        txtEjercicio = findViewById(R.id.txtEjercicio);
        txtSeries = findViewById(R.id.numSeries);
        txtRepes = findViewById(R.id.numReps);

        btnAsignar = findViewById(R.id.btnAsignar);

        spinnerUsuarios = findViewById(R.id.spinnerUsuarios);
        spinnerRutinas = findViewById(R.id.spinnerRutinas);

        tipoEntrenamiento = findViewById(R.id.tipoEntrenamiento);

        ckMonday = findViewById(R.id.ckMonday);
        ckTuesday = findViewById(R.id.ckTuesday);
        ckWednesday = findViewById(R.id.ckWednesday);
        ckThursday = findViewById(R.id.ckThursday);
        ckFriday = findViewById(R.id.ckFriday);
        ckSaturday = findViewById(R.id.ckSaturday);
        ckSunday = findViewById(R.id.ckSunday);

        originalNombre = txtNombre.getText().toString();
        originalGrupo = txtGrupo.getText().toString();
        originalEjercicio = txtEjercicio.getText().toString();
        originalSeries = txtSeries.getText().toString();
        originalRepes = txtRepes.getText().toString();

        RoomDatabase.Callback myCallBack = new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
            }

            @Override
            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                super.onOpen(db);
            }
        };

        appDB = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "AppDB")
                .addCallback(myCallBack)
                .fallbackToDestructiveMigration()
                .build();

        loadSpinnerData();

        spinnerRutinas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String rutinaSeleccionada = spinnerRutinas.getSelectedItem().toString();
                actualizarInformacionRutina(rutinaSeleccionada);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada si no se selecciona una rutina.
            }
        });

        btnAsignar.setOnClickListener(v -> asignarRutina());
    }

    private void asignarRutina() {
        String usuario = spinnerUsuarios.getSelectedItem().toString();
        String rutina = spinnerRutinas.getSelectedItem().toString();

        if (!validarSeleccion()) {
            return;
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            int idU = appDB.getUserDAO().getUserIdByUserName(usuario);
            int idR = appDB.getRutinaDAO().getRutinaIdByNameRutina(rutina);

            User_Rutina nUserRutina = new User_Rutina();
            nUserRutina.setIdUsuario(idU);
            nUserRutina.setIdRutina(idR);
            nUserRutina.setLunes(ckMonday.isChecked());
            nUserRutina.setMartes(ckTuesday.isChecked());
            nUserRutina.setMiercoles(ckWednesday.isChecked());
            nUserRutina.setJueves(ckThursday.isChecked());
            nUserRutina.setViernes(ckFriday.isChecked());
            nUserRutina.setSabado(ckSaturday.isChecked());
            nUserRutina.setDomingo(ckSunday.isChecked());
            nUserRutina.setTipoEntrenamiento(obtenerTipoEntrenamiento());

            saveDataInBackground(nUserRutina);
        });
    }

    private boolean validarSeleccion() {
        if (!(ckMonday.isChecked() || ckTuesday.isChecked() || ckWednesday.isChecked() ||
                ckThursday.isChecked() || ckFriday.isChecked() || ckSaturday.isChecked() || ckSunday.isChecked())) {
            Toast.makeText(this, "Debes seleccionar al menos un día de entrenamiento", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (tipoEntrenamiento.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Debes seleccionar un tipo de entrenamiento", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private String obtenerTipoEntrenamiento() {
        int selectedId = tipoEntrenamiento.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        return radioButton.getText().toString();
    }

    private void loadSpinnerData() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            // Obtener datos de la base de datos
            List<User> usuarios = appDB.getUserDAO().getAllUsers();
            List<Rutina> rutinas = appDB.getRutinaDAO().getAllRutinas();

            // Convertir a listas de strings para los adaptadores
            List<String> nombresUsuarios = new ArrayList<>();
            for (User user : usuarios) {
                nombresUsuarios.add(user.getUsuario());
            }

            List<String> nombresRutinas = new ArrayList<>();
            for (Rutina rutina : rutinas) {
                nombresRutinas.add(rutina.getNombreRutina());
            }

            // Actualizar la interfaz de usuario en el hilo principal
            runOnUiThread(() -> {
                ArrayAdapter<String> userAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresUsuarios);
                userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerUsuarios.setAdapter(userAdapter);

                ArrayAdapter<String> rutinaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresRutinas);
                rutinaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerRutinas.setAdapter(rutinaAdapter);
            });
        });
    }

    public void actualizarInformacionRutina(String nombreRutina) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            r = appDB.getRutinaDAO().getRutinaByNameRutina(nombreRutina);

            if (r != null) {
                runOnUiThread(() -> {
                    // Restaurar los valores originales antes de actualizar con la nueva rutina
                    txtNombre.setText(originalNombre + " " + r.getNombreRutina());
                    txtGrupo.setText(originalGrupo + " " + r.getGrupoMuscular());
                    txtEjercicio.setText(originalEjercicio + " " + r.getEjercicio());
                    txtSeries.setText(originalSeries + " " + r.getSeries());
                    txtRepes.setText(originalRepes + " " + r.getRepeticiones());
                });
            } else {
                runOnUiThread(() -> Toast.makeText(AsignarRutinaActivity.this, "No se encontró la rutina", Toast.LENGTH_SHORT).show());
            }
        });
    }


    public void saveDataInBackground(User_Rutina nUserRutina) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            appDB.getUser_RutinaDAO().insert(nUserRutina);
            runOnUiThread(() -> Toast.makeText(AsignarRutinaActivity.this, "Rutina asignada correctamente", Toast.LENGTH_SHORT).show());
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
