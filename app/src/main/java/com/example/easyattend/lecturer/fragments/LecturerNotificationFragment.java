package com.example.easyattend.lecturer.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.easyattend.R;
import com.example.easyattend.adapters.AdapterNotifications;
import com.example.easyattend.databinding.FragmentLecturerNotificationBinding;
import com.example.easyattend.databinding.FragmentNotificationBinding;
import com.example.easyattend.models.NotificationModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LecturerNotificationFragment extends Fragment {
    FragmentLecturerNotificationBinding binding;
    List<NotificationModel> notificationList = new ArrayList<>();
    AdapterNotifications adapterNotifications;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    public LecturerNotificationFragment() {
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
        binding = FragmentLecturerNotificationBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(requireContext());
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://easyattend-73d60-default-rtdb.firebaseio.com").getReference("Notifications");

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
                    notificationList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            NotificationModel notificationModel = ds.getValue(NotificationModel.class);
                            if (notificationModel.getCheckUser().contentEquals("Student")) {
                                notificationList.add(notificationModel);
                            }
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }

                    setAdapter();
                    if (notificationList.isEmpty()) {
                        binding.tvNoNotificationFound.setVisibility(View.VISIBLE);
                        binding.rvNotifications.setVisibility(View.GONE);
                    } else {
                        binding.tvNoNotificationFound.setVisibility(View.GONE);
                        binding.rvNotifications.setVisibility(View.VISIBLE);
                    }
                    progressDialog.dismiss();

                } else {
                    progressDialog.dismiss();
                    binding.tvNoNotificationFound.setVisibility(View.VISIBLE);
                    binding.rvNotifications.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(requireActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void setAdapter() {
        adapterNotifications = new AdapterNotifications(requireContext(), notificationList, pos -> {
            NotificationModel notificationModel = notificationList.get(pos);
            databaseReference.child(notificationModel.getNotificationId()).removeValue();
            notificationList.remove(pos);
            adapterNotifications.notifyDataSetChanged();
            Toast.makeText(requireContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
        });
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvNotifications.setAdapter(adapterNotifications);
    }

}