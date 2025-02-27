package com.example.enduranceacademyapp.data;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.enduranceacademyapp.dao.DatoNutricionDAO;
import com.example.enduranceacademyapp.dao.RegistroEntrenamientoDAO;
import com.example.enduranceacademyapp.dao.RutinaDAO;
import com.example.enduranceacademyapp.dao.UserDAO;
import com.example.enduranceacademyapp.dao.User_RutinaDAO;

@Database(entities = {User.class, DatoNutricion.class,Rutina.class,User_Rutina.class, RegistroEntrenamiento.class}, version = 9)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract UserDAO getUserDAO();
    public abstract DatoNutricionDAO getDatoNutricionDAO();
    public abstract RutinaDAO getRutinaDAO();
    public abstract User_RutinaDAO getUser_RutinaDAO();
    public abstract RegistroEntrenamientoDAO getRegistroEntrenamientoDAO();


    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "AppDB")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}


