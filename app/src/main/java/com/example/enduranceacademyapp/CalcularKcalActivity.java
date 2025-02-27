package com.example.enduranceacademyapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.enduranceacademyapp.data.AppDatabase;
import com.example.enduranceacademyapp.data.DatoNutricion;
import com.example.enduranceacademyapp.data.SharedPrefManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalcularKcalActivity extends AppCompatActivity {

    EditText etEdad, etPeso, etAltura, etVolumen;
    RadioGroup radioGroupGenero;
    Button btnCalcular;
    Spinner spinnerEjercicio; // Usaremos Spinner en lugar de AutoCompleteTextView
    int selectedPosition = -1; // -1 indica que no se ha seleccionado nada todavía
    String currentUser;

    AppDatabase appDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc_kcal);

        currentUser = SharedPrefManager.getUsername(getApplicationContext());

        // Inicializamos el spinner
        spinnerEjercicio = findViewById(R.id.spinnerEjercicio);

        String[] opciones = {"Poco o ningún ejercicio", "Ejercicio ligero (1 - 3 días por semana)",
                "Ejercicio moderado (3 - 5 días por semana)", "Ejercicio fuerte (6 - 7 días por semana)",
                "Ejercicio muy fuerte (dos veces al día, entrenamientos muy duros)"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item, opciones);
        spinnerEjercicio.setAdapter(adapter);

        // Configuración del evento de selección en el spinner
        spinnerEjercicio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position; // Guarda la posición seleccionada
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPosition = -1; // Si no se selecciona nada, mantén la posición en -1
            }
        });

        etEdad = findViewById(R.id.editTextEdad);
        etPeso = findViewById(R.id.editTextPeso);
        etAltura = findViewById(R.id.editTextAltura);
        etVolumen = findViewById(R.id.editTextVolumen);
        radioGroupGenero = findViewById(R.id.radioGroupGenero);
        btnCalcular = findViewById(R.id.btnCalcular);

        // Inicialización de la base de datos
        appDB = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "AppDB").build();

        btnCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition == -1) {
                    Toast.makeText(CalcularKcalActivity.this, "Selecciona una opción", Toast.LENGTH_SHORT).show();
                    return;
                }

                double edad = Double.parseDouble(etEdad.getText().toString());
                double peso = Double.parseDouble(etPeso.getText().toString());
                double altura = Double.parseDouble(etAltura.getText().toString());
                double volumen = Double.parseDouble(etVolumen.getText().toString());

                int selectedGenderId = radioGroupGenero.getCheckedRadioButtonId();
                RadioButton selectedGenderButton = findViewById(selectedGenderId);
                String genero = selectedGenderButton.getText().toString();

                double caloriasTotales = 10 * peso + 6.25 * altura - 5 * edad;
                if (genero.equalsIgnoreCase("Mujer")) {
                    caloriasTotales += -161; // Ajuste para mujeres
                } else if (genero.equalsIgnoreCase("Hombre")) {
                    caloriasTotales += 5; // Ajuste para hombres
                }
                caloriasTotales += volumen; // Agregar el volumen

                // Usamos if en lugar de switch
                if (selectedPosition == 0) { // Poco o ningún ejercicio
                    caloriasTotales *= 1.2;
                } else if (selectedPosition == 1) { // Ejercicio ligero
                    caloriasTotales *= 1.375;
                } else if (selectedPosition == 2) { // Ejercicio moderado
                    caloriasTotales *= 1.55;
                } else if (selectedPosition == 3) { // Ejercicio fuerte
                    caloriasTotales *= 1.725;
                } else if (selectedPosition == 4) { // Ejercicio muy fuerte
                    caloriasTotales *= 1.9;
                }

                double gramosProteina = (genero.equalsIgnoreCase("Hombre")) ? 2.5 * peso : 2.25 * peso;
                double caloriasProteina = gramosProteina * 4;

                double gramosGrasas = (genero.equalsIgnoreCase("Hombre")) ? 0.8 * peso : peso;
                double caloriasGrasas = gramosGrasas * 9;

                double caloriasCarbohidratos = caloriasTotales - (caloriasProteina + caloriasGrasas);
                double gramosCarbohidratos = caloriasCarbohidratos / 4;

                DatoNutricion datoNutricion = new DatoNutricion(
                        currentUser,
                        caloriasTotales,
                        gramosProteina,
                        caloriasProteina,
                        gramosGrasas,
                        caloriasGrasas,
                        gramosCarbohidratos,
                        caloriasCarbohidratos
                );

                saveDataInBackground(datoNutricion);
                finish();
            }
        });
    }

    public void saveDataInBackground(DatoNutricion datoNutricion) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                int count = appDB.getDatoNutricionDAO().checkUsuarioExists(currentUser);

                if (count > 0) {
                    DatoNutricion existingData = appDB.getDatoNutricionDAO().getDatosNutricion(currentUser);
                    existingData.setCaloriasTotales(datoNutricion.getCaloriasTotales());
                    existingData.setGramosProteina(datoNutricion.getGramosProteina());
                    existingData.setCaloriasProteina(datoNutricion.getCaloriasProteina());
                    existingData.setGramosGrasas(datoNutricion.getGramosGrasas());
                    existingData.setCaloriasGrasas(datoNutricion.getCaloriasGrasas());
                    existingData.setGramosCarbohidratos(datoNutricion.getGramosCarbohidratos());
                    existingData.setCaloriasCarbohidratos(datoNutricion.getCaloriasCarbohidratos());

                    appDB.getDatoNutricionDAO().updateDatoNutricion(existingData);
                } else {
                    appDB.getDatoNutricionDAO().addDatoNutricion(datoNutricion);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CalcularKcalActivity.this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
