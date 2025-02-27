package com.example.enduranceacademyapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.enduranceacademyapp.data.User;

import java.util.List;


@Dao
public interface UserDAO {

    @Insert
    void addUser(User user);

    @Query("select * from Users")
    List<User> getAllUsers();

    @Query("SELECT * FROM Users WHERE usuario = :usuario AND password = :password LIMIT 1")
    User getUserByCredentials(String usuario, String password);

    @Query("SELECT user_id FROM Users WHERE usuario = :nombreU LIMIT 1")
    int getUserIdByUserName(String nombreU);

}
