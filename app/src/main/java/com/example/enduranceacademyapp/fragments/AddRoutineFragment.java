package com.example.enduranceacademyapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.enduranceacademyapp.AsignarRutinaActivity;
import com.example.enduranceacademyapp.CalcularKcalActivity;
import com.example.enduranceacademyapp.CrearRutinaActivity;
import com.example.enduranceacademyapp.NutricionTrainerActivity;
import com.example.enduranceacademyapp.R;

public class AddRoutineFragment extends Fragment {

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstance){
        super.onCreate(savedInstance);

        view = inflater.inflate(R.layout.activity_add_routine,container,false);
        view.findViewById(R.id.new_routine).setOnClickListener(v -> addNewRoutine(v));
        view.findViewById(R.id.asignar_rutina).setOnClickListener(v -> asignarRutina(v));
        view.findViewById(R.id.nutricion).setOnClickListener(v -> nutricionUsers(v));

        return view;
    }

    public void addNewRoutine(View view) {
        Intent intent = new Intent(getActivity(), CrearRutinaActivity.class);
        startActivity(intent);
    }

    public void asignarRutina(View view) {
        Intent intent = new Intent(getActivity(), AsignarRutinaActivity.class);
        startActivity(intent);
    }
    public void nutricionUsers(View view) {
        Intent intent = new Intent(getActivity(), NutricionTrainerActivity.class);
        startActivity(intent);
    }


}