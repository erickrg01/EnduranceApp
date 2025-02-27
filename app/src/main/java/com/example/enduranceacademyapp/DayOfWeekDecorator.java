package com.example.enduranceacademyapp;

import android.graphics.Color;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.example.enduranceacademyapp.data.AppDatabase;
import com.example.enduranceacademyapp.data.User_Rutina;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DayOfWeekDecorator implements DayViewDecorator {
    private final List<Integer> daysToHighlight;
    private final int color;

    public DayOfWeekDecorator(List<Integer> targetDays, int color) {
        this.color = color;
        this.daysToHighlight = new ArrayList<>(targetDays);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(day.getYear(), day.getMonth(), day.getDay()); // ✅ Mes corregido

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return daysToHighlight.contains(dayOfWeek);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.WHITE)); // Color del texto
        view.addSpan(new BackgroundColorSpan(color)); // Fondo del día
    }

    public static void applyDecorators(MaterialCalendarView calendarView, AppDatabase appDB, String userName) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Integer userId = appDB.getUserDAO().getUserIdByUserName(userName);
                if (userId == null) {
                    Log.e("CalendarDecorator", "❌ Usuario no encontrado en la base de datos.");
                    return;
                }

                List<User_Rutina> rutinas = appDB.getUser_RutinaDAO().getRutinasByUserId(userId);
                List<DayOfWeekDecorator> decorators = new ArrayList<>();

                Log.d("CalendarDecorator", "📌 Usuario " + userName + " tiene " + rutinas.size() + " rutinas.");

                for (User_Rutina rutina : rutinas) {
                    List<Integer> days = new ArrayList<>();
                    if (rutina.isLunes()) days.add(Calendar.MONDAY);
                    if (rutina.isMartes()) days.add(Calendar.TUESDAY);
                    if (rutina.isMiercoles()) days.add(Calendar.WEDNESDAY);
                    if (rutina.isJueves()) days.add(Calendar.THURSDAY);
                    if (rutina.isViernes()) days.add(Calendar.FRIDAY);
                    if (rutina.isSabado()) days.add(Calendar.SATURDAY);
                    if (rutina.isDomingo()) days.add(Calendar.SUNDAY);

                    Log.d("CalendarDecorator", "➡️ Días de entrenamiento: " + days.toString());

                    if (!days.isEmpty()) {
                        int color = getColorForTrainingType(rutina.getTipoEntrenamiento());
                        decorators.add(new DayOfWeekDecorator(days, color));
                    }
                }

                calendarView.post(() -> {
                    calendarView.removeDecorators(); // 🛑 Limpiar decoradores viejos
                    for (DayOfWeekDecorator decorator : decorators) {
                        calendarView.addDecorator(decorator);
                    }
                    calendarView.invalidateDecorators(); // 🔄 Refrescar
                    Log.d("CalendarDecorator", "✅ Decoradores aplicados.");
                });

            } catch (Exception e) {
                Log.e("CalendarDecorator", "❌ Error al aplicar decoradores", e);
            }
        });
    }

    private static int getColorForTrainingType(String tipoEntrenamiento) {
        switch (tipoEntrenamiento.toLowerCase()) {
            case "fuerza":
                return Color.RED;
            case "resistencia":
                return Color.BLUE;
            case "hipertrofia":
                return Color.GREEN;
            default:
                return Color.GRAY;
        }
    }
}
