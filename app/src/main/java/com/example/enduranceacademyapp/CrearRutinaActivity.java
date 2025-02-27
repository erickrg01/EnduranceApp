package com.example.enduranceacademyapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.enduranceacademyapp.data.AppDatabase;
import com.example.enduranceacademyapp.data.Rutina;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CrearRutinaActivity extends AppCompatActivity {

    Spinner spinnerGrupoMuscular, spinnerEjercicios;
    AppDatabase appDB;
    Button btnCrearRutina;
    EditText txtNombre, txtRepes, txtSeries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_rutina);

        // Inicializar campos de texto y botones
        txtNombre = findViewById(R.id.txt_nombre);
        txtRepes = findViewById(R.id.txt_repes);
        txtSeries = findViewById(R.id.txt_series);
        btnCrearRutina = findViewById(R.id.guardarRutina);

        // Inicializar spinner de grupo muscular
        spinnerGrupoMuscular = findViewById(R.id.spinner_grupo_muscular);
        String[] opcionesGrupoMuscular = {"Pectorales", "Dorsales", "Cuádriceps", "Bíceps"};
        ArrayAdapter<String> adapterGrupoMuscular = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesGrupoMuscular);
        adapterGrupoMuscular.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGrupoMuscular.setAdapter(adapterGrupoMuscular);

        // Inicializar spinner de ejercicios (vacío inicialmente)
        spinnerEjercicios = findViewById(R.id.spinner_ejercicio);

        // Configurar el spinner de ejercicios según el grupo muscular seleccionado
        spinnerGrupoMuscular.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] opcionesEjercicios;

                // Verificamos que position sea un valor entero
                if (position == 0) { // Pectorales
                    opcionesEjercicios = new String[]{"Press de banca con barra", "Aperturas con mancuernas"};
                } else if (position == 1) { // Dorsales
                    opcionesEjercicios = new String[]{"Dominadas", "Remo con barra"};
                } else if (position == 2) { // Cuádriceps
                    opcionesEjercicios = new String[]{"Sentadilla con barra", "Prensa de piernas"};
                } else if (position == 3) { // Bíceps
                    opcionesEjercicios = new String[]{"Curl con barra", "Curl de martillo con mancuernas"};
                } else { // Si no es ninguno de los anteriores
                    opcionesEjercicios = new String[]{};
                }

                // Actualizamos el spinner de ejercicios
                ArrayAdapter<String> adapterEjercicios = new ArrayAdapter<>(CrearRutinaActivity.this, android.R.layout.simple_spinner_item, opcionesEjercicios);
                adapterEjercicios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerEjercicios.setAdapter(adapterEjercicios);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Puedes dejar este método vacío o manejar el caso donde no se selecciona nada
            }
        });

        // Configurar la base de datos
        appDB = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "AppDB")
                .fallbackToDestructiveMigration()
                .build();

        // Guardar la rutina
        btnCrearRutina.setOnClickListener(v -> {
            String nombre = txtNombre.getText().toString();
            String grupoMuscular = spinnerGrupoMuscular.getSelectedItem().toString();
            String ejercicio = spinnerEjercicios.getSelectedItem().toString();
            String seriesText = txtSeries.getText().toString();
            String repesText = txtRepes.getText().toString();

            // Validar los campos
            if (nombre.isEmpty() || grupoMuscular.isEmpty() || ejercicio.isEmpty() || seriesText.isEmpty() || repesText.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            int series, repes;
            try {
                series = Integer.parseInt(seriesText);
                repes = Integer.parseInt(repesText);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Series y repeticiones deben ser números válidos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear un objeto Rutina
            Rutina rutinaNueva = new Rutina();
            rutinaNueva.setNombreRutina(nombre);
            rutinaNueva.setGrupoMuscular(grupoMuscular);
            rutinaNueva.setEjercicio(ejercicio);
            rutinaNueva.setSeries(series);
            rutinaNueva.setRepeticiones(repes);

            // Guardar en la base de datos en segundo plano
            saveDataInBackground(rutinaNueva);
        });
    }

    // Guardar en la base de datos en segundo plano
    public void saveDataInBackground(Rutina rutina) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            appDB.getRutinaDAO().addRutina(rutina);
            runOnUiThread(() -> Toast.makeText(CrearRutinaActivity.this, "Rutina guardada correctamente", Toast.LENGTH_SHORT).show());
        });
    }
}
