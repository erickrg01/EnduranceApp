package com.example.enduranceacademyapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NutricionActivity extends AppCompatActivity {

    AppDatabase appDB;
    TextView txt_kcal,kcal_grasa,kcal_prote,kcal_carbo,txt_grasa,txt_proteina,txt_carbos;
    AnyChartView anyChartView;
    String [] macros ={"Kcal Grasas","Kcal Proteinas","Kcal Carbohidratos"};
    double [] kcals = {10,15,30};
    DatoNutricion datoNutricion = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutricion);

        txt_kcal = findViewById(R.id.txt_kcal);
        kcal_grasa = findViewById(R.id.kcal_grasa);
        kcal_prote = findViewById(R.id.kcal_prote);
        kcal_carbo = findViewById(R.id.kcal_carbo);
        txt_grasa = findViewById(R.id.txt_grasa);
        txt_proteina = findViewById(R.id.txt_proteina);
        txt_carbos = findViewById(R.id.txt_carbos);

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
                anyChartView = findViewById(R.id.anyChartView);

        loadNutritionData(new NutricionDataCallback() {
            @Override
            public void onNutritionDataLoaded() {
                // Cuando los datos estén cargados, configura el gráfico
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

        pie.background().fill("#000000");

        // Opcional: Cambiar el color del texto para que sea visible sobre fondo negro
        pie.labels().fontColor("#FFFFFF");
        pie.data(dataEntries);
        pie.title("Kcal");
        pie.animation(true);
        anyChartView.setChart(pie);
    }

    private void loadNutritionData(NutricionDataCallback callback) {
        // Ejecutar la consulta en un hilo de fondo para no bloquear el hilo principal
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Obtener los datos desde la base de datos (ejemplo: obtener el primer dato de nutrición)
                String currentUser = SharedPrefManager.getUsername(getApplicationContext());

                if (currentUser != null) {
                    // Si hay un usuario, puedes usarlo para filtrar los datos en la base de datos
                    datoNutricion = appDB.getDatoNutricionDAO().getDatosNutricion(currentUser);
                }

                // Actualizar los TextViews en el hilo principal
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (datoNutricion != null) {
                            // Aquí asignas los valores de la base de datos a los TextViews
                            txt_kcal.setText(txt_kcal.getText() + " " + String.valueOf(datoNutricion.getCaloriasTotales()));
                            kcal_grasa.setText(kcal_grasa.getText() + " " +String.valueOf(datoNutricion.getCaloriasGrasas()));
                            kcal_prote.setText(kcal_prote.getText() + " " +String.valueOf(datoNutricion.getCaloriasProteina()));
                            kcal_carbo.setText(kcal_carbo.getText() + " " +String.valueOf(datoNutricion.getCaloriasCarbohidratos()));
                            txt_grasa.setText(txt_grasa.getText() + " " +String.valueOf(datoNutricion.getGramosGrasas()));
                            txt_proteina.setText(txt_proteina.getText() + " " +String.valueOf(datoNutricion.getGramosProteina()));
                            txt_carbos.setText(txt_carbos.getText() + " " +String.valueOf(datoNutricion.getGramosCarbohidratos()));

                            kcals[0] = datoNutricion.getCaloriasGrasas();
                            kcals[1] = datoNutricion.getCaloriasProteina();
                            kcals[2] = datoNutricion.getCaloriasCarbohidratos();
                        }

                        // Llamamos al callback para indicar que los datos han sido cargados
                        if (callback != null) {
                            callback.onNutritionDataLoaded();
                        }
                    }
                });
            }
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
                + "Calorías Totales: " + txt_kcal.getText().toString() + " kcal\n"
                + "Calorías Grasas: " + kcal_grasa.getText().toString() + " kcal\n"
                + "Calorías Proteínas: " + kcal_prote.getText().toString() + " kcal\n"
                + "Calorías Carbohidratos: " + kcal_carbo.getText().toString() + " kcal\n"
                + "Gramos Grasas: " + txt_grasa.getText().toString() + " g\n"
                + "Gramos Proteína: " + txt_proteina.getText().toString() + " g\n"
                + "Gramos Carbohidratos: " + txt_carbos.getText().toString() + " g\n";

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
        canvas.drawText("Calorías Totales: " + txt_kcal.getText().toString() + " kcal", 50, y, paint);
        canvas.drawText("Calorías Grasas: " + kcal_grasa.getText().toString() + " kcal", 50, y + 30, paint);
        canvas.drawText("Calorías Proteínas: " + kcal_prote.getText().toString() + " kcal", 50, y + 60, paint);
        canvas.drawText("Calorías Carbohidratos: " + kcal_carbo.getText().toString() + " kcal", 50, y + 90, paint);
        canvas.drawText("Gramos Grasas: " + txt_grasa.getText().toString() + " g", 50, y + 120, paint);
        canvas.drawText("Gramos Proteína: " + txt_proteina.getText().toString() + " g", 50, y + 150, paint);
        canvas.drawText("Gramos Carbohidratos: " + txt_carbos.getText().toString() + " g", 50, y + 180, paint);

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
    }

    public void exportarPDF(View view) {
        generarArchivoPDF();
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