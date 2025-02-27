package com.example.enduranceacademyapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.enduranceacademyapp.data.DatoNutricion;


@Dao
public interface DatoNutricionDAO {
    @Insert
    public void addDatoNutricion(DatoNutricion datoNutricion);

    @Query("select * from DatosNutricion where usuario = :usuario")
    DatoNutricion getDatosNutricion(String usuario);

    @Update
    void updateDatoNutricion(DatoNutricion datoNutricion);

    // Método para verificar si existe un usuario antes de actualizar
    @Query("SELECT COUNT(*) FROM DatosNutricion WHERE usuario = :usuario")
    int checkUsuarioExists(String usuario);

    @Query("SELECT dato_nutricion_id FROM DatosNutricion WHERE usuario = :usuario LIMIT 1")
    Integer getUserId(String usuario);
}
