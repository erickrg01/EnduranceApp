package com.example.enduranceacademyapp.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Rutinas")
public class Rutina {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "r_id")
    public int id;

    @ColumnInfo(name = "nombre_rutina")
    public String nombreRutina;

    @ColumnInfo(name = "grupo_muscular")
    public String grupoMuscular;

    @ColumnInfo(name = "ejercicio")
    public String ejercicio;

    @ColumnInfo(name = "repeticiones")
    public int repeticiones;

    @ColumnInfo(name = "series")
    public int series;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreRutina() {
        return nombreRutina;
    }

    public void setNombreRutina(String nombreRutina) {
        this.nombreRutina = nombreRutina;
    }

    public String getGrupoMuscular() {
        return grupoMuscular;
    }

    public void setGrupoMuscular(String grupoMuscular) {
        this.grupoMuscular = grupoMuscular;
    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(int repeticiones) {
        this.repeticiones = repeticiones;
    }

    public String getEjercicio() {
        return ejercicio;
    }

    public void setEjercicio(String ejercicio) {
        this.ejercicio = ejercicio;
    }
}
