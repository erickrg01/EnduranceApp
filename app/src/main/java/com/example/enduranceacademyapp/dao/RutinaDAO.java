package com.example.enduranceacademyapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.enduranceacademyapp.data.Rutina;
import com.example.enduranceacademyapp.data.User;

import java.util.List;

@Dao
public interface RutinaDAO {

    @Insert
    public void addRutina(Rutina rutina);

    @Query("select * from Rutinas")
    List<Rutina> getAllRutinas();

    @Query("SELECT * FROM Rutinas WHERE nombre_rutina = :nombre_rutina LIMIT 1")
    Rutina getRutinaByNameRutina(String nombre_rutina);

    @Query("SELECT r_id FROM Rutinas WHERE nombre_rutina = :nombre_rutina LIMIT 1")
    int getRutinaIdByNameRutina(String nombre_rutina);

    @Query("SELECT * FROM Rutinas WHERE r_id = :idRutina LIMIT 1")
    Rutina getRutinaById(int idRutina);
}
