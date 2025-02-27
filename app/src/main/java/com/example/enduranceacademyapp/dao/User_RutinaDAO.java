package com.example.enduranceacademyapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.enduranceacademyapp.data.User_Rutina;

import java.util.List;

@Dao
public interface User_RutinaDAO {

    @Insert
    void insert(User_Rutina user_Rutina);

    @Delete
    void delete(User_Rutina userRutinas);

    @Query("SELECT * FROM user_rutinas WHERE id_usuario = :userId")
    List<User_Rutina> getRutinasByUserId(int userId);
}
