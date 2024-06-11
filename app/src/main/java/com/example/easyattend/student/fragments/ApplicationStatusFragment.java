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
import com.example.easyattend.adapters.AdapterLeaves;
import com.example.easyattend.adapters.AdapterSubjects;
import com.example.easyattend.databinding.FragmentApplicationStatusBinding;
import com.example.easyattend.databinding.FragmentApplyLeaveBinding;
import com.example.easyattend.databinding.FragmentApprovalBinding;
import com.example.easyattend.models.AttendanceModel;
import com.example.easyattend.models.LeaveModel;
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

public class ApplicationStatusFragment extends Fragment {
    FragmentApplicationStatusBinding binding;
    List<LeaveModel> leaveList = new ArrayList<>();
    AdapterLeaves adapterLeaves;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceLeave;

    public ApplicationStatusFragment() {
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
        binding = FragmentApplicationStatusBinding.inflate(getLayoutInflater());
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
        databaseReferenceLeave = FirebaseDatabase.getInstance("https://easyattend-73d60-default-rtdb.firebaseio.com").getReference("Leave");

    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog.setTitle(getResources().getString(R.string.app_name));
        progressDialog.setMessage("Please wait... ");
        progressDialog.setCancelable(false);
        progressDialog.show();
        databaseReferenceLeave.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    leaveList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            LeaveModel model = ds.getValue(LeaveModel.class);
                            if (model != null && model.getUserId() != null && model.getUserId().contentEquals(auth.getCurrentUser().getUid())) {
                                leaveList.add(model);
                            }
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }

                    setAdapter();
                    if (leaveList.isEmpty()) {
                        binding.tvNoSubjectFound.setVisibility(View.VISIBLE);
                        binding.rvRequest.setVisibility(View.GONE);
                    } else {
                        binding.tvNoSubjectFound.setVisibility(View.GONE);
                        binding.rvRequest.setVisibility(View.VISIBLE);
                    }
                    progressDialog.dismiss();

                } else {
                    progressDialog.dismiss();
                    binding.tvNoSubjectFound.setVisibility(View.VISIBLE);
                    binding.rvRequest.setVisibility(View.GONE);
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
        adapterLeaves = new AdapterLeaves(requireContext(), leaveList, databaseReference, pos -> {
//            LeaveModel leaveModel = leaveList.get(pos);
        });
        binding.rvRequest.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRequest.setAdapter(adapterLeaves);
    }

}