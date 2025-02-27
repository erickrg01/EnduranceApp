package com.example.enduranceacademyapp.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "user_prefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FORMATO_CALENDARIO = "formato_calendario"; // Nueva clave para el formato
    private static final String KEY_USER_IMAGE = "user_image"; // Nueva clave para la imagen de usuario

    // Guardar el nombre de usuario en SharedPreferences
    public static void saveUsername(Context context, String username) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.apply();  // Guarda los cambios de manera asincrónica
    }

    // Obtener el nombre de usuario de SharedPreferences
    public static String getUsername(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null);  // Retorna null si no se encuentra
    }

    // Guardar el formato de calendario seleccionado ("Semanal" o "Mensual")
    public static void saveFormatoCalendario(Context context, String formato) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_FORMATO_CALENDARIO, formato);
        editor.apply();
    }

    // Obtener el formato de calendario guardado (por defecto "Semanal")
    public static String getFormatoCalendario(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_FORMATO_CALENDARIO, "Semanal");
    }

    // Guardar la imagen de usuario en SharedPreferences
    public static void saveUserImage(Context context, String encodedImage) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_IMAGE, encodedImage);
        editor.apply();
    }

    // Obtener la imagen de usuario de SharedPreferences
    public static String getUserImage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_IMAGE, null);
    }
}
