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
import com.example.easyattend.adapters.AdapterAttendance;
import com.example.easyattend.databinding.FragmentAttendanceDetailsBinding;
import com.example.easyattend.databinding.FragmentLecturerHomeBinding;
import com.example.easyattend.databinding.FragmentViewAttendanceBinding;
import com.example.easyattend.models.AttendanceModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ViewAttendanceFragment extends Fragment {
    FragmentViewAttendanceBinding binding;
    List<AttendanceModel> attendanceList = new ArrayList<>();
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    AdapterAttendance adapterAttendance;
    private Calendar calendar;
    String dateString;

    public ViewAttendanceFragment() {
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
        binding = FragmentViewAttendanceBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendar = Calendar.getInstance();
        progressDialog = new ProgressDialog(requireContext());
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://easyattend-73d60-default-rtdb.firebaseio.com").getReference("Attendance");
        updateDateInView();

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        binding.ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                updateDateInView();
            }
        });

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                updateDateInView();
            }
        });

    }

    private void getAttendance(String date) {
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
                            if (model.getTeacherId().contentEquals(auth.getCurrentUser().getUid()) && model.getDate().contentEquals(date)) {
                                attendanceList.add(model);
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
        adapterAttendance = new AdapterAttendance(requireContext(), attendanceList, "Lecturer");
        binding.rvItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvItems.setAdapter(adapterAttendance);
    }

    private void updateDateInView() {
        Date currentDate = calendar.getTime();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        dateString = dateFormat.format(currentDate);
        binding.tvDate.setText(dateString);
        getAttendance(dateString);
    }

}