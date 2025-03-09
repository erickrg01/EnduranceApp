package com.example.enduranceacademyapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.enduranceacademyapp.dao.NutricionDataCallback;
import com.example.enduranceacademyapp.data.AppDatabase;
import com.example.enduranceacademyapp.data.DatoNutricion;
import com.example.enduranceacademyapp.data.Rutina;
import com.example.enduranceacademyapp.data.SharedPrefManager;
import com.example.enduranceacademyapp.data.User;
import com.example.enduranceacademyapp.fragments.CircularProgressView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NutricionTrainerActivity extends AppCompatActivity {

    AppDatabase appDB;
    Spinner spinnerUsuarios;
    double caloriasTotales, caloriasGrasas, caloriasProteina, caloriasCarbohidratos;
    double gramosGrasas, gramosProteina, gramosCarbohidratos;
    AnyChartView anyChartView;
    Pie pieChart;  // Guardar referencia al gráfico
    String[] macros = {"Kcal Grasas", "Kcal Proteinas", "Kcal Carbohidratos"};
    double[] kcals = {10, 15, 30};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutricion_trainer);


        spinnerUsuarios = findViewById(R.id.spinnerUsuarios);
        anyChartView = findViewById(R.id.anyChartView);

        Typeface typeface = ResourcesCompat.getFont(this, R.font.exo2semibold);


        CircularProgressView progressCarbs = findViewById(R.id.progress_carbs);
        CircularProgressView progressFat = findViewById(R.id.progress_fat);
        CircularProgressView progressProtein = findViewById(R.id.progress_protein);

        CircularProgressView progressCarbsK = findViewById(R.id.progress_carbsK);
        CircularProgressView progressFatK = findViewById(R.id.progress_fatK);
        CircularProgressView progressProteinK = findViewById(R.id.progress_proteinK);

        progressCarbs.setTypeface(typeface);
        progressFat.setTypeface(typeface);
        progressProtein.setTypeface(typeface);

        progressCarbsK.setTypeface(typeface);
        progressFatK.setTypeface(typeface);
        progressProteinK.setTypeface(typeface);

        appDB = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "AppDB")
                .fallbackToDestructiveMigration()
                .build();

        setupChartView();  // Inicializar el gráfico
        loadSpinnerData();
    }

    private void setupChartView() {
        pieChart = AnyChart.pie();
        updateChartData();  // Cargar datos iniciales

        pieChart.background().fill("transparent");
        pieChart.labels().fontColor("#FFFFFF");
        pieChart.title("Kcal");
        pieChart.animation(true);

        anyChartView.setChart(pieChart);
    }

    private void updateChartData() {
        List<DataEntry> dataEntries = new ArrayList<>();
        for (int i = 0; i < macros.length; i++) {
            dataEntries.add(new ValueDataEntry(macros[i], kcals[i]));
        }
        pieChart.data(dataEntries);  // Actualizar los datos del gráfico
    }

    private void loadSpinnerData() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            List<User> usuarios = appDB.getUserDAO().getAllUsers();
            List<String> nombresUsuarios = new ArrayList<>();

            for (User user : usuarios) {
                nombresUsuarios.add(user.getUsuario());
            }

            runOnUiThread(() -> {
                ArrayAdapter<String> userAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresUsuarios);
                userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerUsuarios.setAdapter(userAdapter);

                // Agregar listener para detectar cambios en el usuario seleccionado
                spinnerUsuarios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedUser = nombresUsuarios.get(position);
                        cargarDatosNutricion(selectedUser);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            });
        });
    }

    private void cargarDatosNutricion(String selectedUser) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            DatoNutricion datoNutricion = appDB.getDatoNutricionDAO().getDatosNutricion(selectedUser);

            runOnUiThread(() -> {
                if (datoNutricion != null) {
                    // Guardar valores previos y concatenar nueva información
                    caloriasGrasas = datoNutricion.getCaloriasGrasas();
                    caloriasProteina = datoNutricion.getCaloriasProteina();
                    caloriasCarbohidratos = datoNutricion.getCaloriasCarbohidratos();
                    gramosGrasas = datoNutricion.getGramosGrasas();
                    gramosProteina = datoNutricion.getGramosProteina();
                    gramosCarbohidratos = datoNutricion.getGramosCarbohidratos();

                    // Actualizar valores en la gráfica
                    kcals[0] += caloriasGrasas;
                    kcals[1] += caloriasProteina;
                    kcals[2] += caloriasCarbohidratos;

                } else {
                    // Si no hay datos previos, inicializar normalmente
                    kcals[0] = 0;
                    kcals[1] = 0;
                    kcals[2] = 0;
                    caloriasGrasas = 0;
                    caloriasProteina = 0;
                    caloriasCarbohidratos = 0;
                    gramosGrasas = 0;
                    gramosProteina = 0;
                    gramosCarbohidratos = 0;
                }

                // Sumar todos los gramos para definir el máximo dinámico
                float totalGramos = (float) (gramosGrasas + gramosProteina + gramosCarbohidratos);
                float totalKcal = (float) (caloriasGrasas + caloriasProteina + caloriasCarbohidratos);

                // Actualizar los CircularProgressView
                CircularProgressView progressCarbs = findViewById(R.id.progress_carbs);
                CircularProgressView progressFat = findViewById(R.id.progress_fat);
                CircularProgressView progressProtein = findViewById(R.id.progress_protein);

                CircularProgressView progressCarbsK = findViewById(R.id.progress_carbsK);
                CircularProgressView progressFatK = findViewById(R.id.progress_fatK);
                CircularProgressView progressProteinK = findViewById(R.id.progress_proteinK);

                progressCarbs.setProgress((float) (gramosCarbohidratos / totalGramos), String.format("%.2f", gramosCarbohidratos), 0xFF4285F4);
                progressFat.setProgress((float) (gramosGrasas / totalGramos), String.format("%.2f", gramosGrasas), 0xFFFBBC05);
                progressProtein.setProgress((float) (gramosProteina / totalGramos), String.format("%.2f", gramosProteina), 0xFF34A853);

                progressCarbsK.setProgress((float) (caloriasCarbohidratos / totalKcal), String.format("%.2f", caloriasCarbohidratos), 0xFF4285F4);
                progressFatK.setProgress((float) (caloriasGrasas / totalKcal), String.format("%.2f", caloriasGrasas), 0xFFFBBC05);
                progressProteinK.setProgress((float) (caloriasProteina / totalKcal), String.format("%.2f", caloriasProteina), 0xFF34A853);

                // Actualizar la gráfica con los nuevos valores
                updateChartData();
            });
        });
    }



    public void calc_kcal(View vista) {
        Intent intent = new Intent(NutricionTrainerActivity.this, CalcularKcalActivity.class);
        startActivity(intent);
        finish();
    }
}
