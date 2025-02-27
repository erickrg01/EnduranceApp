package com.example.enduranceacademyapp.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "DatosNutricion", indices = {@Index(value = {"usuario"}, unique = true)})
public class DatoNutricion {

    @ColumnInfo(name = "dato_nutricion_id")
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "usuario")
    String usuario;

    @ColumnInfo(name = "gramos_proteina")
    double gramosProteina;

    @ColumnInfo(name = "gramos_grasas")
    double gramosGrasas;

    @ColumnInfo(name = "gramos_carbohidratos")
    double gramosCarbohidratos;

    @ColumnInfo(name = "calorias_totales")
    double caloriasTotales;

    @ColumnInfo(name = "calorias_proteina")
    double caloriasProteina;

    @ColumnInfo(name = "calorias_grasas")
    double caloriasGrasas;

    @ColumnInfo(name = "calorias_carbohidratos")
    double caloriasCarbohidratos;

    @Ignore
    public DatoNutricion() {
    }

    public DatoNutricion(String usuario, double caloriasTotales, double gramosProteina, double caloriasProteina,
                         double gramosGrasas, double caloriasGrasas, double gramosCarbohidratos, double caloriasCarbohidratos) {

        this.usuario = usuario;
        this.caloriasTotales = caloriasTotales;
        this.gramosProteina = gramosProteina;
        this.caloriasProteina = caloriasProteina;
        this.gramosGrasas = gramosGrasas;
        this.caloriasGrasas = caloriasGrasas;
        this.gramosCarbohidratos = gramosCarbohidratos;
        this.caloriasCarbohidratos = caloriasCarbohidratos;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public double getCaloriasTotales() {
        return caloriasTotales;
    }

    public void setCaloriasTotales(double caloriasTotales) {
        this.caloriasTotales = caloriasTotales;
    }

    public double getGramosProteina() {
        return gramosProteina;
    }

    public void setGramosProteina(double gramosProteina) {
        this.gramosProteina = gramosProteina;
    }

    public double getCaloriasProteina() {
        return caloriasProteina;
    }

    public void setCaloriasProteina(double caloriasProteina) {
        this.caloriasProteina = caloriasProteina;
    }

    public double getGramosGrasas() {
        return gramosGrasas;
    }

    public void setGramosGrasas(double gramosGrasas) {
        this.gramosGrasas = gramosGrasas;
    }

    public double getCaloriasGrasas() {
        return caloriasGrasas;
    }

    public void setCaloriasGrasas(double caloriasGrasas) {
        this.caloriasGrasas = caloriasGrasas;
    }

    public double getGramosCarbohidratos() {
        return gramosCarbohidratos;
    }

    public void setGramosCarbohidratos(double gramosCarbohidratos) {
        this.gramosCarbohidratos = gramosCarbohidratos;
    }

    public double getCaloriasCarbohidratos() {
        return caloriasCarbohidratos;
    }

    public void setCaloriasCarbohidratos(double caloriasCarbohidratos) {
        this.caloriasCarbohidratos = caloriasCarbohidratos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
