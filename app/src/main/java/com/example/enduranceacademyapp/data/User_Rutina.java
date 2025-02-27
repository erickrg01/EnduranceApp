package com.example.enduranceacademyapp.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_rutinas")
public class User_Rutina {

    @PrimaryKey(autoGenerate = true)
    private int id_user_rutina;

    @ColumnInfo(name = "id_usuario")
    public int idUsuario; // Relaciona con la clave primaria de User

    @ColumnInfo(name = "id_rutina")
    public int idRutina; // Relaciona con la clave primaria de Rutina

    @ColumnInfo(name="lunes")
    public boolean lunes;

    @ColumnInfo(name="martes")
    public boolean martes;

    @ColumnInfo(name="miercoles")
    public boolean miercoles;

    @ColumnInfo(name="jueves")
    public boolean jueves;

    @ColumnInfo(name="viernes")
    public boolean viernes;

    @ColumnInfo(name="sabado")
    public boolean sabado;

    @ColumnInfo(name="domingo")
    public boolean domingo;

    @ColumnInfo(name="tipoEntrenamiento")
    public String tipoEntrenamiento;


    // Constructor vacío (requerido por Room)
    public User_Rutina() {}

    // Constructor para inicializar los valores
    public User_Rutina(int idUsuario, int idRutina) {
        this.idUsuario = idUsuario;
        this.idRutina = idRutina;
    }

    public User_Rutina(int idUsuario, int idRutina, boolean lunes,
                       boolean martes, boolean miercoles, boolean jueves,
                       boolean viernes, boolean sabado, boolean domingo, String tipoEntrenamiento) {
        this.idUsuario = idUsuario;
        this.idRutina = idRutina;
        this.lunes = lunes;
        this.martes = martes;
        this.miercoles = miercoles;
        this.jueves = jueves;
        this.viernes = viernes;
        this.sabado = sabado;
        this.domingo = domingo;
        this.tipoEntrenamiento = tipoEntrenamiento;
    }

    // Getters y setters
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdRutina() {
        return idRutina;
    }

    public void setIdRutina(int idRutina) {
        this.idRutina = idRutina;
    }

    public boolean isLunes() {
        return lunes;
    }

    public void setLunes(boolean lunes) {
        this.lunes = lunes;
    }

    public boolean isMartes() {
        return martes;
    }

    public void setMartes(boolean martes) {
        this.martes = martes;
    }

    public boolean isMiercoles() {
        return miercoles;
    }

    public void setMiercoles(boolean miercoles) {
        this.miercoles = miercoles;
    }

    public boolean isJueves() {
        return jueves;
    }

    public void setJueves(boolean jueves) {
        this.jueves = jueves;
    }

    public boolean isViernes() {
        return viernes;
    }

    public void setViernes(boolean viernes) {
        this.viernes = viernes;
    }

    public boolean isSabado() {
        return sabado;
    }

    public void setSabado(boolean sabado) {
        this.sabado = sabado;
    }

    public String getTipoEntrenamiento() {
        return tipoEntrenamiento;
    }

    public void setTipoEntrenamiento(String tipoEntrenamiento) {
        this.tipoEntrenamiento = tipoEntrenamiento;
    }

    public boolean isDomingo() {
        return domingo;
    }

    public void setDomingo(boolean domingo) {
        this.domingo = domingo;
    }

    public int getId_user_rutina() {
        return id_user_rutina;
    }

    public void setId_user_rutina(int id_user_rutina) {
        this.id_user_rutina = id_user_rutina;
    }
}
