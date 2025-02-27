package com.example.enduranceacademyapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.enduranceacademyapp.data.RegistroEntrenamiento;
import com.example.enduranceacademyapp.data.Rutina;

import java.util.List;

@Dao
public interface RegistroEntrenamientoDAO {
    @Insert
    void insert(RegistroEntrenamiento registro);

    @Query("select * from RegistroEntrenamientos")
    List<RegistroEntrenamiento> getAllRegistroEntrenamientos();

    @Query("SELECT * FROM RegistroEntrenamientos WHERE usuario = :usuario")
    List<RegistroEntrenamiento> getRegistroEntrenamientosByUser(String usuario);
}
