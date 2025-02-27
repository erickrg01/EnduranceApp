package com.example.enduranceacademyapp.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Users")
public class User {

    @ColumnInfo(name = "user_id")
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "usuario")
    String usuario;

    @ColumnInfo(name = "password")
    String password;

    @Ignore
    public User() {
    }

    public User(String usuario, String password) {
        this.usuario = usuario;
        this.password = password;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
