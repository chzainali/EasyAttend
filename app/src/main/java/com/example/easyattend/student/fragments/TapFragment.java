package com.example.easyattend.student.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.easyattend.R;
import com.example.easyattend.databinding.FragmentStudentHomeBinding;
import com.example.easyattend.databinding.FragmentTapBinding;
import com.example.easyattend.models.AttendanceModel;
import com.example.easyattend.models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TapFragment extends Fragment {
    FragmentTapBinding binding;
    UserModel teacherModel;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    String currentDateAndTime;

    public TapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            teacherModel = (UserModel) getArguments().getSerializable("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTapBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getCurrentDate();
        binding.tvSubject.setText(teacherModel.getTeacherSubject());
        progressDialog = new ProgressDialog(requireContext());
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://easyattend-73d60-default-rtdb.firebaseio.com").getReference("Attendance");

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        binding.rlTap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setTitle(getResources().getString(R.string.app_name));
                progressDialog.setMessage("Please wait... ");
                progressDialog.setCancelable(false);
                progressDialog.show();
                String pushID = databaseReference.push().getKey();
                AttendanceModel attendanceModel = new AttendanceModel(pushID, auth.getCurrentUser().getUid(),
                        teacherModel.getUserId(), teacherModel.getTeacherSubject(), currentDateAndTime, "Present");
                databaseReference.child(pushID).setValue(attendanceModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        showToast("Completed Successfully");
                        Navigation.findNavController(binding.getRoot()).navigateUp();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        showToast(e.getLocalizedMessage());
                    }
                });

            }
        });

    }

    private void getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        currentDateAndTime = sdf.format(new Date());
        binding.tvDate.setText(currentDateAndTime);
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

}