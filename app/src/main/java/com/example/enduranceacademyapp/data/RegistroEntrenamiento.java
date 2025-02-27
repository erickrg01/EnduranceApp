package com.example.enduranceacademyapp.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "RegistroEntrenamientos")
public class RegistroEntrenamiento {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "usuario")
    private String usuario;

    @ColumnInfo(name = "fecha")
    private String fecha;

    @ColumnInfo(name = "repeticiones")
    private int repeticiones;

    @ColumnInfo(name = "series")
    private int series;

    @ColumnInfo(name = "rutina")
    private String rutina;

    @ColumnInfo(name = "anotaciones")
    private String anotaciones;

    // Constructor, getters y setters
    public RegistroEntrenamiento(String usuario, String fecha, int repeticiones, int series, String rutina,String anotaciones) {
        this.usuario = usuario;
        this.fecha = fecha;
        this.repeticiones = repeticiones;
        this.series = series;
        this.rutina = rutina;
        this.anotaciones = anotaciones;
    }
    public RegistroEntrenamiento() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
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

    public String getRutina() {
        return rutina;
    }

    public void setRutina(String rutina) {
        this.rutina = rutina;
    }

    public String getAnotaciones() {
        return anotaciones;
    }

    public void setAnotaciones(String anotaciones) {
        this.anotaciones = anotaciones;
    }
}
