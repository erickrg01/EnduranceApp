package com.example.enduranceacademyapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.enduranceacademyapp.DayOfWeekDecorator;
import com.example.enduranceacademyapp.MiRutinaActivity;
import com.example.enduranceacademyapp.NutricionActivity;
import com.example.enduranceacademyapp.R;
import com.example.enduranceacademyapp.RegistroEntrenamientoActivity;
import com.example.enduranceacademyapp.data.AppDatabase;
import com.example.enduranceacademyapp.data.SharedPrefManager;
import com.example.enduranceacademyapp.data.User_Rutina;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private View view;
    private AppDatabase appDB;
    private MaterialCalendarView calendarView;
    private String currentUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_home, container, false);
        calendarView = view.findViewById(R.id.calendarView);

        SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", 0);
        String formatoCalendario = prefs.getString("formato_calendario", "Semanal");

        requireActivity().runOnUiThread(() -> {
            if (formatoCalendario.equals("Semanal")) {
                calendarView.state().edit()
                        .setFirstDayOfWeek(Calendar.MONDAY)
                        .setCalendarDisplayMode(CalendarMode.WEEKS)
                        .commit();
            } else {
                calendarView.state().edit()
                        .setFirstDayOfWeek(Calendar.MONDAY)
                        .setCalendarDisplayMode(CalendarMode.MONTHS)
                        .commit();
            }
        });


        // Obtener usuario actual
        currentUser = SharedPrefManager.getUsername(requireContext());

        if (currentUser == null || currentUser.isEmpty()) {
            Log.e("HomeFragment", "El usuario actual es nulo o vacío.");
        } else {
            Log.d("HomeFragment", "Usuario actual: " + currentUser);
        }

        // Inicializar base de datos en un hilo separado
        loadDatabase();

        // Configurar eventos de botones
        view.findViewById(R.id.rutina).setOnClickListener(this::miRutina);
        view.findViewById(R.id.nutricion).setOnClickListener(this::btnNutri);
        view.findViewById(R.id.regs_entre).setOnClickListener(this::btnRegEntrenamiento);

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            if (!isAdded() || getActivity() == null) return;

            Calendar calendar = Calendar.getInstance();
            calendar.set(date.getYear(), date.getMonth(), date.getDay());

            Locale locale = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ?
                    getResources().getConfiguration().getLocales().get(0) :
                    getResources().getConfiguration().locale;

            // Obtener el nombre del día
            SimpleDateFormat sdfDay = new SimpleDateFormat("EEEE", locale);
            String nombreDia = sdfDay.format(calendar.getTime());

            mostrarInfoDia(date);

            // Obtener la fecha en formato dd/MM/yyyy
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy", locale);
            String fechaSeleccionada = sdfDate.format(calendar.getTime());

            Intent intent = new Intent(getActivity(), MiRutinaActivity.class);
            intent.putExtra("nombre_dia", nombreDia);
            intent.putExtra("fecha_seleccionada", fechaSeleccionada); // Se pasa la fecha como String
            startActivity(intent);
        });


        return view;
    }

    private void mostrarInfoDia(CalendarDay date) {
        new Thread(() -> {
            try {
                if (appDB == null) {
                    Log.e("HomeFragment", "Intento de acceder a appDB cuando es nulo.");
                    return;
                }

                int userId = appDB.getUserDAO().getUserIdByUserName(currentUser);
                if (userId == 0) return;

                List<User_Rutina> rutinas = appDB.getUser_RutinaDAO().getRutinasByUserId(userId);
                String info = "No hay rutina asignada.";

                for (User_Rutina rutina : rutinas) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(date.getYear(), date.getMonth(), date.getDay());
                    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                    if ((dayOfWeek == Calendar.MONDAY && rutina.isLunes()) ||
                            (dayOfWeek == Calendar.TUESDAY && rutina.isMartes()) ||
                            (dayOfWeek == Calendar.WEDNESDAY && rutina.isMiercoles()) ||
                            (dayOfWeek == Calendar.THURSDAY && rutina.isJueves()) ||
                            (dayOfWeek == Calendar.FRIDAY && rutina.isViernes()) ||
                            (dayOfWeek == Calendar.SATURDAY && rutina.isSabado()) ||
                            (dayOfWeek == Calendar.SUNDAY && rutina.isDomingo())) {

                        info = "Tipo de entrenamiento: " + rutina.getTipoEntrenamiento();
                        break;
                    }
                }

                String finalInfo = info;
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (isAdded()) {
                        Toast.makeText(requireActivity(), finalInfo, Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.e("HomeFragment", "Error obteniendo información del día", e);
            }
        }).start();
    }

    private void miRutina(View vista) {
        if (!isAdded() || getActivity() == null) return;
        startActivity(new Intent(getActivity(), MiRutinaActivity.class));
    }

    private void btnNutri(View vista) {
        if (!isAdded() || getActivity() == null) return;
        startActivity(new Intent(getActivity(), NutricionActivity.class));
    }

    private void btnRegEntrenamiento(View vista) {
        if (!isAdded() || getActivity() == null) return;
        startActivity(new Intent(getActivity(), RegistroEntrenamientoActivity.class));
    }

    private void loadDatabase() {
        new Thread(() -> {
            appDB = Room.databaseBuilder(requireContext(), AppDatabase.class, "AppDB")
                    .fallbackToDestructiveMigration()
                    .build();

            new Handler(Looper.getMainLooper()).post(() -> {
                if (isAdded() && currentUser != null && !currentUser.isEmpty()) {
                    DayOfWeekDecorator.applyDecorators(calendarView, appDB, currentUser);
                    calendarView.invalidateDecorators();
                } else {
                    Log.e("HomeFragment", "Usuario actual no encontrado o fragmento no añadido.");
                }
            });
        }).start();
    }
    @Override
    public void onResume() {
        super.onResume();
        actualizarCalendario();
    }

    private void actualizarCalendario() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", 0);
        String formatoCalendario = prefs.getString("formato_calendario", "Semanal");

        if (calendarView != null) {
            if (formatoCalendario.equals("Semanal")) {
                calendarView.state().edit()
                        .setFirstDayOfWeek(Calendar.MONDAY)
                        .setCalendarDisplayMode(CalendarMode.WEEKS)
                        .commit();
            } else {
                calendarView.state().edit()
                        .setFirstDayOfWeek(Calendar.MONDAY)
                        .setCalendarDisplayMode(CalendarMode.MONTHS)
                        .commit();
            }
        }
    }

}