package com.example.easyattend.student.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.easyattend.R;
import com.example.easyattend.adapters.AdapterSubjects;
import com.example.easyattend.adapters.AdapterTeachers;
import com.example.easyattend.admin.AddTeacherActivity;
import com.example.easyattend.admin.AdminDashboardActivity;
import com.example.easyattend.databinding.FragmentStudentHomeBinding;
import com.example.easyattend.models.AttendanceModel;
import com.example.easyattend.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentHomeFragment extends Fragment {
    FragmentStudentHomeBinding binding;
    String currentDateAndTime;
    List<UserModel> subjectsList = new ArrayList<>();
    AdapterSubjects adapterTeachers;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceAttendance;
    String isAlreadyAttempted = "false";
    UserModel userModel;

    public StudentHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStudentHomeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getCurrentDate();
        progressDialog = new ProgressDialog(requireContext());
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://easyattend-73d60-default-rtdb.firebaseio.com").getReference("AllUsers");
        databaseReferenceAttendance = FirebaseDatabase.getInstance("https://easyattend-73d60-default-rtdb.firebaseio.com").getReference("Attendance");


    }

    private void getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        currentDateAndTime = sdf.format(new Date());
        binding.tvDate.setText(currentDateAndTime);
    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog.setTitle(getResources().getString(R.string.app_name));
        progressDialog.setMessage("Please wait... ");
        progressDialog.setCancelable(false);
        progressDialog.show();
        isAlreadyAttempted = "false";
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    subjectsList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            UserModel model = ds.getValue(UserModel.class);
                            if (model.getUserType().contentEquals("Lecturer")) {
                                subjectsList.add(model);
                            }
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }

                    setAdapter();
                    if (subjectsList.isEmpty()) {
                        binding.tvNoSubjectFound.setVisibility(View.VISIBLE);
                        binding.rvSubjects.setVisibility(View.GONE);
                    } else {
                        binding.tvNoSubjectFound.setVisibility(View.GONE);
                        binding.rvSubjects.setVisibility(View.VISIBLE);
                    }
                    progressDialog.dismiss();

                } else {
                    progressDialog.dismiss();
                    binding.tvNoSubjectFound.setVisibility(View.VISIBLE);
                    binding.rvSubjects.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(requireActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setAdapter() {
        adapterTeachers = new AdapterSubjects(requireContext(), subjectsList, pos -> {
            userModel = subjectsList.get(pos);
            databaseReferenceAttendance.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            try {
                                AttendanceModel model = ds.getValue(AttendanceModel.class);
                                if (userModel != null){
                                    if (model.getUserId().contentEquals(auth.getCurrentUser().getUid()) && model.getDate().contentEquals(currentDateAndTime) && model.getSubject().contentEquals(userModel.getTeacherSubject())) {
                                        isAlreadyAttempted = "true";
                                    }
                                }
                            } catch (DatabaseException e) {
                                e.printStackTrace();
                            }
                        }
                        if (userModel.getActive().contentEquals("true")) {
                            if (isAlreadyAttempted.contains("false")){
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("data", userModel);
                                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_studentHomeFragment_to_tapFragment, bundle);
                            }else{
                                Toast.makeText(requireContext(), "For today attendance, you have already completed", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(requireContext(), "Not Active for now", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }else{
                        progressDialog.dismiss();
                        isAlreadyAttempted = "false";
                        if (userModel.getActive().contentEquals("true")) {
                            if (isAlreadyAttempted.contains("false")){
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("data", userModel);
                                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_studentHomeFragment_to_tapFragment, bundle);
                            }else{
                                Toast.makeText(requireContext(), "For today attendance, you have already completed", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(requireContext(), "Not Active for now", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(requireActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
//            checkAttendance();
//            if (userModel.getActive().contentEquals("true")) {
//                if (isAlreadyAttempted.contains("false")){
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("data", userModel);
//                    Navigation.findNavController(binding.getRoot()).navigate(R.id.action_studentHomeFragment_to_tapFragment, bundle);
//                }else{
//                    Toast.makeText(requireContext(), "For today attendance, you have already completed", Toast.LENGTH_SHORT).show();
//                }
//            }else{
//                Toast.makeText(requireContext(), "Not Active for now", Toast.LENGTH_SHORT).show();
//            }
        });
        binding.rvSubjects.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSubjects.setAdapter(adapterTeachers);
    }

}