package com.example.enduranceacademyapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.enduranceacademyapp.R;

public class NewsFragment extends Fragment {

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstance){
        super.onCreate(savedInstance);

        view = inflater.inflate(R.layout.activity_news,container,false);
        return view;
    }
}