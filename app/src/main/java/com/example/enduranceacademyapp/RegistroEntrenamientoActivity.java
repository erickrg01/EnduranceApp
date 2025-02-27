package com.example.enduranceacademyapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.enduranceacademyapp.data.AppDatabase;
import com.example.enduranceacademyapp.data.RegistroEntrenamiento;
import com.example.enduranceacademyapp.data.SharedPrefManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegistroEntrenamientoActivity extends AppCompatActivity {

    AppDatabase appDB;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_entrenamiento);

        currentUser = SharedPrefManager.getUsername(getApplicationContext());

        TableLayout tableLayout;
        tableLayout = findViewById(R.id.tableLayout);

        appDB = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "AppDB")
                .fallbackToDestructiveMigration()
                .build();


        cargarRegistros(tableLayout);

    }

    private void cargarRegistros(TableLayout tableLayout) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {

            List<RegistroEntrenamiento> registros = appDB.getRegistroEntrenamientoDAO().getRegistroEntrenamientosByUser(currentUser);

            runOnUiThread(() -> {

                TextView infoTextView = findViewById(R.id.infoTextView);

                for (RegistroEntrenamiento registro : registros) {
                    TableRow row = new TableRow(this);
                    row.setPadding(4, 4, 4, 4);

                    TextView fecha = new TextView(this);
                    fecha.setTextColor(Color.BLACK);
                    fecha.setText(registro.getFecha());
                    fecha.setGravity(Gravity.CENTER);
                    fecha.setPadding(8, 8, 8, 8);
                    fecha.setBackgroundColor(Color.WHITE);

                    TextView rutina = new TextView(this);
                    rutina.setTextColor(Color.BLACK);
                    rutina.setText(registro.getRutina());
                    rutina.setGravity(Gravity.CENTER);
                    rutina.setPadding(8, 8, 8, 8);
                    rutina.setBackgroundColor(Color.WHITE);

                    TextView repes = new TextView(this);
                    repes.setTextColor(Color.BLACK);
                    repes.setText(String.valueOf(registro.getRepeticiones()));
                    repes.setGravity(Gravity.CENTER);
                    repes.setPadding(8, 8, 8, 8);
                    repes.setBackgroundColor(Color.WHITE);

                    TextView series = new TextView(this);
                    series.setTextColor(Color.BLACK);
                    series.setText(String.valueOf(registro.getSeries()));
                    series.setGravity(Gravity.CENTER);
                    series.setPadding(8, 8, 8, 8);
                    series.setBackgroundColor(Color.WHITE);

                    row.addView(fecha);
                    row.addView(rutina);
                    row.addView(repes);
                    row.addView(series);

                    row.setOnClickListener(v -> {
                        String info = "Anotaciones: "+registro.getAnotaciones();

                        infoTextView.setText(info);
                        infoTextView.setVisibility(View.VISIBLE);
                    });

                    tableLayout.addView(row);
                }
            });
        });
    }
}
