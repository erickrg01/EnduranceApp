package com.example.enduranceacademyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NutricionTrainerActivity extends AppCompatActivity {

    AppDatabase appDB;
    Spinner spinnerUsuarios;
    TextView txt_kcal, kcal_grasa, kcal_prote, kcal_carbo, txt_grasa, txt_proteina, txt_carbos;
    AnyChartView anyChartView;
    Pie pieChart;  // Guardar referencia al gráfico
    String[] macros = {"Kcal Grasas", "Kcal Proteinas", "Kcal Carbohidratos"};
    double[] kcals = {10, 15, 30};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutricion_trainer);

        // Inicializar vistas
        txt_kcal = findViewById(R.id.txt_kcal);
        kcal_grasa = findViewById(R.id.kcal_grasa);
        kcal_prote = findViewById(R.id.kcal_prote);
        kcal_carbo = findViewById(R.id.kcal_carbo);
        txt_grasa = findViewById(R.id.txt_grasa);
        txt_proteina = findViewById(R.id.txt_proteina);
        txt_carbos = findViewById(R.id.txt_carbos);
        spinnerUsuarios = findViewById(R.id.spinnerUsuarios);
        anyChartView = findViewById(R.id.anyChartView);

        appDB = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "AppDB")
                .fallbackToDestructiveMigration()
                .build();

        setupChartView();  // Inicializar el gráfico
        loadSpinnerData();
    }

    private void setupChartView() {
        pieChart = AnyChart.pie();
        updateChartData();  // Cargar datos iniciales

        pieChart.background().fill("#000000");
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
                    txt_kcal.setText(txt_kcal.getText() + " " + datoNutricion.getCaloriasTotales());
                    kcal_grasa.setText(kcal_grasa.getText() + " " + datoNutricion.getCaloriasGrasas());
                    kcal_prote.setText(kcal_prote.getText() + " " + datoNutricion.getCaloriasProteina());
                    kcal_carbo.setText(kcal_carbo.getText() + " " + datoNutricion.getCaloriasCarbohidratos());
                    txt_grasa.setText(txt_grasa.getText() + " " + datoNutricion.getGramosGrasas());
                    txt_proteina.setText(txt_proteina.getText() + " " + datoNutricion.getGramosProteina());
                    txt_carbos.setText(txt_carbos.getText() + " " + datoNutricion.getGramosCarbohidratos());

                    // Actualizar valores en la gráfica
                    kcals[0] += datoNutricion.getCaloriasGrasas();
                    kcals[1] += datoNutricion.getCaloriasProteina();
                    kcals[2] += datoNutricion.getCaloriasCarbohidratos();
                } else {
                    // Si no hay datos previos, inicializar normalmente
                    txt_kcal.setText("0");
                    kcal_grasa.setText("0");
                    kcal_prote.setText("0");
                    kcal_carbo.setText("0");
                    txt_grasa.setText("0");
                    txt_proteina.setText("0");
                    txt_carbos.setText("0");

                    kcals[0] = 0;
                    kcals[1] = 0;
                    kcals[2] = 0;
                }

                updateChartData();  // Actualizar la gráfica con los nuevos valores
            });
        });
    }


    public void calc_kcal(View vista) {
        Intent intent = new Intent(NutricionTrainerActivity.this, CalcularKcalActivity.class);
        startActivity(intent);
        finish();
    }
}
