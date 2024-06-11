package com.example.easyattend.lecturer.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.easyattend.R;
import com.example.easyattend.databinding.FragmentLecturerNotificationBinding;
import com.example.easyattend.databinding.FragmentLecturerSubjectBinding;
import com.example.easyattend.databinding.FragmentSettingBinding;

public class LecturerSubjectFragment extends Fragment {
    FragmentLecturerSubjectBinding binding;
    public LecturerSubjectFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLecturerSubjectBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tvViewAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_lecturerSubjectFragment_to_viewAttendanceFragment);
            }
        });

        binding.tvViewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_lecturerSubjectFragment_to_viewReportFragment);
            }
        });

    }
}