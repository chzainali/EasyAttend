package com.example.easyattend.student.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import com.example.easyattend.adapters.AdapterAttendance;
import com.example.easyattend.adapters.AdapterSubjects;
import com.example.easyattend.databinding.FragmentAttendanceBinding;
import com.example.easyattend.databinding.FragmentAttendanceDetailsBinding;
import com.example.easyattend.models.AttendanceModel;
import com.example.easyattend.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AttendanceDetailsFragment extends Fragment {
    FragmentAttendanceDetailsBinding binding;
    List<AttendanceModel> attendanceList = new ArrayList<>();
    List<AttendanceModel> presentList = new ArrayList<>();
    List<AttendanceModel> absentList = new ArrayList<>();
    List<AttendanceModel> excuseList = new ArrayList<>();
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    AdapterAttendance adapterAttendance;
    UserModel previousModel;
    public AttendanceDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            previousModel = (UserModel) getArguments().getSerializable("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAttendanceDetailsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tvLabel.setText(previousModel.getTeacherSubject());
        progressDialog = new ProgressDialog(requireContext());
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://easyattend-73d60-default-rtdb.firebaseio.com").getReference("Attendance");

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog.setTitle(getResources().getString(R.string.app_name));
        progressDialog.setMessage("Please wait... ");
        progressDialog.setCancelable(false);
        progressDialog.show();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    attendanceList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            AttendanceModel model = ds.getValue(AttendanceModel.class);
                            if (model.getUserId().contentEquals(auth.getCurrentUser().getUid()) && model.getSubject().contentEquals(previousModel.getTeacherSubject())) {
                                attendanceList.add(model);
                                if (model.getStatus().contentEquals("Present")){
                                    presentList.add(model)
;                                }else if (model.getStatus().contentEquals("Absent")){
                                    absentList.add(model);
                                }else if (model.getStatus().contentEquals("Excuse")){
                                    excuseList.add(model);
                                }
                            }
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }
                    setAdapter();
                    if (attendanceList.isEmpty()) {
                        binding.tvNoRecordFound.setVisibility(View.VISIBLE);
                        binding.llTop.setVisibility(View.GONE);
                        binding.rvItems.setVisibility(View.GONE);
                    } else {
                        binding.tvNoRecordFound.setVisibility(View.GONE);
                        binding.llTop.setVisibility(View.VISIBLE);
                        binding.rvItems.setVisibility(View.VISIBLE);
                    }
                    progressDialog.dismiss();

                } else {
                    progressDialog.dismiss();
                    binding.tvNoRecordFound.setVisibility(View.VISIBLE);
                    binding.rvItems.setVisibility(View.GONE);
                    binding.llTop.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(requireActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void setAdapter() {
        adapterAttendance = new AdapterAttendance(requireContext(), attendanceList, "Student");
        binding.rvItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvItems.setAdapter(adapterAttendance);
        binding.tvPresent.setText(presentList.size()+" Present");
        binding.tvAbsent.setText(absentList.size()+" Absent");
        binding.tvExcuse.setText(excuseList.size()+" Excuse");
    }

}