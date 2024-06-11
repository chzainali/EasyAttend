package com.example.easyattend.lecturer.fragments;

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
import com.example.easyattend.adapters.AdapterSubjects;
import com.example.easyattend.adapters.AdapterUsers;
import com.example.easyattend.databinding.FragmentStudentHomeBinding;
import com.example.easyattend.databinding.FragmentViewReportBinding;
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

public class ViewReportFragment extends Fragment {
    FragmentViewReportBinding binding;
    List<UserModel> userList = new ArrayList<>();
    List<AttendanceModel> attendanceList = new ArrayList<>();
    List<AttendanceModel> presentList = new ArrayList<>();
    List<AttendanceModel> absentList = new ArrayList<>();
    List<AttendanceModel> excuseList = new ArrayList<>();
    AdapterUsers adapterUsers;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceAttendance;
    String currentDateAndTime;
    public ViewReportFragment() {
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
        binding = FragmentViewReportBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        progressDialog = new ProgressDialog(requireContext());
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://easyattend-73d60-default-rtdb.firebaseio.com").getReference("AllUsers");
        databaseReferenceAttendance = FirebaseDatabase.getInstance("https://easyattend-73d60-default-rtdb.firebaseio.com").getReference("Attendance");

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
                    userList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            UserModel model = ds.getValue(UserModel.class);
                            if (model.getUserType().contentEquals("Student")){
                                userList.add(model);
                            }
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }

                    setAdapter();
                    getAttendance();
                    if (userList.isEmpty()) {
                        binding.tvNoRecordFound.setVisibility(View.VISIBLE);
                        binding.rvUsers.setVisibility(View.GONE);
                    } else {
                        binding.tvNoRecordFound.setVisibility(View.GONE);
                        binding.rvUsers.setVisibility(View.VISIBLE);
                    }

                } else {
                    progressDialog.dismiss();
                    binding.tvNoRecordFound.setVisibility(View.VISIBLE);
                    binding.rvUsers.setVisibility(View.GONE);
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
        adapterUsers = new AdapterUsers(requireContext(), userList, pos -> {
          UserModel userModel = userList.get(pos);
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", userModel);
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_viewReportFragment_to_eachStudentAttendanceFragment, bundle);

        });
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvUsers.setAdapter(adapterUsers);
    }

    private void getAttendance() {
        databaseReferenceAttendance.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    attendanceList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            AttendanceModel model = ds.getValue(AttendanceModel.class);
                            if (model.getTeacherId().contentEquals(auth.getCurrentUser().getUid())) {
                                attendanceList.add(model);
                                if (model.getStatus().contentEquals("Present")) {
                                    presentList.add(model);
                                } else if (model.getStatus().contentEquals("Absent")) {
                                    absentList.add(model);
                                } else if (model.getStatus().contentEquals("Excuse")) {
                                    excuseList.add(model);
                                }
                            }
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }

                }
                getPercentage();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(requireActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getPercentage(){
        int totalSize = presentList.size() + absentList.size() + excuseList.size();
        if (totalSize > 0){
            int presentListPercentage = (presentList.size() * 100) / totalSize;
            int absentListPercentage = (absentList.size() * 100) / totalSize;
            int excuseListPercentage = (excuseList.size() * 100) / totalSize;

            binding.tvPresent.setText(presentListPercentage+"%");
            binding.tvAbsent.setText(absentListPercentage+"%");
            binding.tvExcused.setText(excuseListPercentage+"%");
        }

    }

}