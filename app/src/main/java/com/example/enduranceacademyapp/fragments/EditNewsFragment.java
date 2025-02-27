package com.example.enduranceacademyapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.enduranceacademyapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EditNewsFragment extends Fragment {

    View view;
    FloatingActionButton fab,fabOption1,fabOption2;
    private boolean isFabOpen = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstance){
        super.onCreate(savedInstance);

        view = inflater.inflate(R.layout.activity_edit_news,container,false);
        fab = view.findViewById(R.id.fab);
        fabOption1 = view.findViewById(R.id.fab_option1);
        fabOption2 = view.findViewById(R.id.fab_option2);
        fab.setOnClickListener(v -> {
            if (isFabOpen) {
                closeFABMenu();
            } else {
                openFABMenu();
            }
        });

        fabOption1.setOnClickListener(v -> {
            // Acción para la opción 1
        });

        fabOption2.setOnClickListener(v -> {
            // Acción para la opción 2
        });

        return view;
    }

    private void openFABMenu() {
        isFabOpen = true;

        fabOption1.setVisibility(View.VISIBLE);
        fabOption2.setVisibility(View.VISIBLE);

        fabOption1.animate().translationY(-180).alpha(1).setDuration(300).start();
        fabOption2.animate().translationY(-360).alpha(1).setDuration(300).start();
    }

    private void closeFABMenu() {
        isFabOpen = false;

        fabOption1.animate()
                .translationY(0)
                .alpha(0)
                .setDuration(300)
                .withEndAction(() -> fabOption1.setVisibility(View.GONE))
                .start();

        fabOption2.animate()
                .translationY(0)
                .alpha(0)
                .setDuration(300)
                .withEndAction(() -> fabOption2.setVisibility(View.GONE))
                .start();
    }
}