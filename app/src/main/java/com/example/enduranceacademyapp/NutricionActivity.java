package com.example.enduranceacademyapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
import com.example.enduranceacademyapp.data.SharedPrefManager;
import com.example.enduranceacademyapp.fragments.CircularProgressView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NutricionActivity extends AppCompatActivity {

    AppDatabase appDB;
    double caloriasTotales, caloriasGrasas, caloriasProteina, caloriasCarbohidratos;
    double gramosGrasas, gramosProteina, gramosCarbohidratos;
    AnyChartView anyChartView;
    String [] macros ={"Kcal Grasas","Kcal Proteinas","Kcal Carbohidratos"};
    double [] kcals = {10,15,30};
    DatoNutricion datoNutricion = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutricion);

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


        appDB = Room.databaseBuilder(getApplicationContext(),AppDatabase.class,
                "AppDB").addCallback(myCallBack).build();
                anyChartView = findViewById(R.id.anyChartView);

        loadNutritionData(new NutricionDataCallback() {
            @Override
            public void onNutritionDataLoaded() {
                float carbs = (float) gramosCarbohidratos;
                float fat = (float) gramosGrasas;
                float protein = (float) gramosProteina;

                float carbsK = (float) caloriasCarbohidratos;
                float fatK = (float) caloriasGrasas;
                float proteinK = (float) caloriasProteina;
                float kcal = (float) caloriasTotales;

                // Sumar todos los gramos para definir el máximo dinámico
                float totalGramos = carbs + fat + protein;

                // Usar la suma como valor máximo para cada ProgressView
                progressCarbs.setProgress(carbs / totalGramos, String.format("%.2f", gramosCarbohidratos), 0xFF4285F4);
                progressFat.setProgress(fat / totalGramos, String.format("%.2f", gramosGrasas), 0xFFFBBC05);
                progressProtein.setProgress(protein / totalGramos, String.format("%.2f", gramosProteina), 0xFF34A853);

                progressCarbsK.setProgress(carbsK / kcal, String.format("%.2f", caloriasCarbohidratos), 0xFF4285F4);
                progressFatK.setProgress(fatK / kcal, String.format("%.2f", caloriasGrasas), 0xFFFBBC05);
                progressProteinK.setProgress(proteinK / kcal, String.format("%.2f", caloriasProteina), 0xFF34A853);

                // Configurar el gráfico después de cargar los datos
                setupChartView();
            }
        });

    }

    private void setupChartView(){
        Pie pie = AnyChart.pie();
        List<DataEntry> dataEntries = new ArrayList<>();

        for(int i = 0; i< macros.length;i++){
            dataEntries.add(new ValueDataEntry(macros[i],kcals[i]));
        }

        pie.background().fill("transparent");

        // Opcional: Cambiar el color del texto para que sea visible sobre fondo negro
        pie.labels().fontColor("#FFFFFF");
        pie.data(dataEntries);
        pie.title("Calorías");
        pie.animation(true);


        anyChartView.setChart(pie);
    }

    private void loadNutritionData(NutricionDataCallback callback) {
        new Thread(() -> {
            String currentUser = SharedPrefManager.getUsername(getApplicationContext());
            if (currentUser != null) {
                datoNutricion = appDB.getDatoNutricionDAO().getDatosNutricion(currentUser);
            }

            runOnUiThread(() -> {
                if (datoNutricion != null) {
                    caloriasTotales = datoNutricion.getCaloriasTotales();
                    caloriasGrasas = datoNutricion.getCaloriasGrasas();
                    caloriasProteina = datoNutricion.getCaloriasProteina();
                    caloriasCarbohidratos = datoNutricion.getCaloriasCarbohidratos();
                    gramosGrasas = datoNutricion.getGramosGrasas();
                    gramosProteina = datoNutricion.getGramosProteina();
                    gramosCarbohidratos = datoNutricion.getGramosCarbohidratos();
                    kcals[0] = caloriasGrasas;
                    kcals[1] = caloriasProteina;
                    kcals[2] = caloriasCarbohidratos;
                }
                if (callback != null) {
                    callback.onNutritionDataLoaded();
                }
            });
        }).start();
    }

    public void calc_kcal(View vista) {
        Intent intent = new Intent(NutricionActivity.this, CalcularKcalActivity.class);
        startActivity(intent);
        finish();
    }

    private void generarArchivoTXT() {
        String fileName = "nutricion_" + SharedPrefManager.getUsername(this) + ".txt";
        String contenido = "Reporte Nutricional\n\n"
                + "Calorías Totales: " + caloriasTotales + " kcal\n"
                + "Calorías Grasas: " + caloriasGrasas + " kcal\n"
                + "Calorías Proteínas: " + caloriasProteina + " kcal\n"
                + "Calorías Carbohidratos: " + caloriasCarbohidratos + " kcal\n"
                + "Gramos Grasas: " + gramosGrasas + " g\n"
                + "Gramos Proteína: " + gramosProteina + " g\n"
                + "Gramos Carbohidratos: " + gramosCarbohidratos + " g\n";

        try {
            File file = new File(getExternalFilesDir(null), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(contenido.getBytes());
            fos.close();
            Toast.makeText(this, "Archivo TXT generado: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al generar el archivo TXT", Toast.LENGTH_SHORT).show();
        }
    }

    private void generarArchivoPDF() {
        String fileName = "nutricion_" + SharedPrefManager.getUsername(this) + ".pdf";
        File file = new File(getExternalFilesDir(null), fileName);

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(600, 800, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        paint.setTextSize(20);
        canvas.drawText("Reporte Nutricional", 200, 50, paint);

        paint.setTextSize(16);
        int y = 100;
        canvas.drawText("Calorías Totales: " + caloriasTotales + " kcal", 50, y, paint);
        canvas.drawText("Calorías Grasas: " + caloriasGrasas + " kcal", 50, y + 30, paint);
        canvas.drawText("Calorías Proteínas: " + caloriasProteina + " kcal", 50, y + 60, paint);
        canvas.drawText("Calorías Carbohidratos: " + caloriasCarbohidratos + " kcal", 50, y + 90, paint);
        canvas.drawText("Gramos Grasas: " + gramosGrasas + " g", 50, y + 120, paint);
        canvas.drawText("Gramos Proteína: " + gramosProteina + " g", 50, y + 150, paint);
        canvas.drawText("Gramos Carbohidratos: " + gramosCarbohidratos + " g", 50, y + 180, paint);

        pdfDocument.finishPage(page);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            pdfDocument.writeTo(fos);
            pdfDocument.close();
            fos.close();
            Toast.makeText(this, "Archivo PDF generado: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al generar el PDF", Toast.LENGTH_SHORT).show();
        }

    }

    public void exportarTXT(View view) {

        generarArchivoTXT();
        verTXT(view);
    }

    public void exportarPDF(View view) {

        generarArchivoPDF();
        verPDF(view);
    }

    private void abrirArchivo(File file, String mimeType) {
        Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mimeType);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No hay una app compatible para abrir este archivo", Toast.LENGTH_SHORT).show();
        }
    }

    public void verTXT(View view) {
        File file = new File(getExternalFilesDir(null), "nutricion_" + SharedPrefManager.getUsername(this) + ".txt");
        abrirArchivo(file, "text/plain");
    }

    public void verPDF(View view) {
        File file = new File(getExternalFilesDir(null), "nutricion_" + SharedPrefManager.getUsername(this) + ".pdf");
        abrirArchivo(file, "application/pdf");
    }
}