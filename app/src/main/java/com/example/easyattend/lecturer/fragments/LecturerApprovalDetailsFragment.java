package com.example.easyattend.lecturer.fragments;

import android.annotation.SuppressLint;
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

import com.bumptech.glide.Glide;
import com.example.easyattend.R;
import com.example.easyattend.databinding.FragmentLecturerApprovalBinding;
import com.example.easyattend.databinding.FragmentLecturueApprovalDetailsBinding;
import com.example.easyattend.models.AttendanceModel;
import com.example.easyattend.models.LeaveModel;
import com.example.easyattend.models.NotificationModel;
import com.example.easyattend.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LecturerApprovalDetailsFragment extends Fragment {
    FragmentLecturueApprovalDetailsBinding binding;
    LeaveModel leaveModel;
    UserModel userModel;
    AttendanceModel attendanceModel;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference databaseReferenceUser;
    DatabaseReference databaseReferenceAttendance;
    DatabaseReference databaseReferenceLeave;
    DatabaseReference databaseReferenceNotifications;
    String currentDateAndTime;
    String isAlreadyAttempted = "false";

    public LecturerApprovalDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            leaveModel = (LeaveModel) getArguments().getSerializable("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLecturueApprovalDetailsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(requireContext());
        auth = FirebaseAuth.getInstance();
        databaseReferenceAttendance = FirebaseDatabase.getInstance("https://easyattend-73d60-default-rtdb.firebaseio.com").getReference("Attendance");
        databaseReferenceUser = FirebaseDatabase.getInstance("https://easyattend-73d60-default-rtdb.firebaseio.com").getReference("AllUsers");
        databaseReferenceLeave = FirebaseDatabase.getInstance("https://easyattend-73d60-default-rtdb.firebaseio.com").getReference("Leave");
        databaseReferenceNotifications = FirebaseDatabase.getInstance("https://easyattend-73d60-default-rtdb.firebaseio.com").getReference("Notifications");

        progressDialog.setTitle(getResources().getString(R.string.app_name));
        progressDialog.setMessage("Please wait... ");
        progressDialog.setCancelable(false);

        getCurrentDate();

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        if (leaveModel != null){
            getUserName();
            Glide.with(binding.ivImage)
                    .load(leaveModel.getImage())
                    .centerCrop()
                    .into(binding.ivImage);
            binding.tvSubjectName.setText("Subject Name: "+leaveModel.getSubject());
            binding.tvLeaveType.setText("Leave Type: "+leaveModel.getType());
            binding.tvLeaveDate.setText("Leave Date: "+leaveModel.getDate());
            binding.tvStatus.setText("Leave Status: "+leaveModel.getStatus());
            binding.tvLeaveReason.setText("Leave Reason: "+leaveModel.getReason());

            binding.btnApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeLeaveStatus("Approved");
                }
            });

            binding.btnReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeLeaveStatus("Rejected");
                }
            });

        }

    }

    private void getUserName(){
        progressDialog.show();
        databaseReferenceUser.child(leaveModel.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userModel = snapshot.getValue(UserModel.class);
                    binding.tvStudentName.setText("Student Name: "+userModel.getUserName());
                    getAttendance(userModel);
                }else{
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(requireActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeLeaveStatus(String status){
        progressDialog.show();
        Map<String, Object> update = new HashMap<String, Object>();
        update.put("status", status);
        databaseReferenceLeave.child(leaveModel.getLeaveId()).updateChildren(update).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (status.contentEquals("Approved")){
                    if (isAlreadyAttempted.contentEquals("false")){
                        String pushID = databaseReferenceAttendance.push().getKey();
                        if (userModel != null && leaveModel != null){
                            AttendanceModel attendanceModel = new AttendanceModel(pushID, userModel.getUserId(),
                                    leaveModel.getTeacherId(), leaveModel.getSubject(), currentDateAndTime, "Excuse");
                            databaseReferenceAttendance.child(pushID).setValue(attendanceModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    setNotification("Approved Successfully", "Approved");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            progressDialog.dismiss();
                        }
                    }else{
                        Map<String, Object> update = new HashMap<String, Object>();
                        update.put("status", "Excuse");
                        databaseReferenceAttendance.child(attendanceModel.getAttendanceId()).updateChildren(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                setNotification("Approved Successfully", "Approved");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }else{
                    setNotification("Rejected Successfully", "Rejected");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAttendance(UserModel userModel){
        databaseReferenceAttendance.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            AttendanceModel model = ds.getValue(AttendanceModel.class);
                            if (userModel != null){
                                if (model.getUserId().contentEquals(userModel.getUserId()) && model.getDate().contentEquals(currentDateAndTime) && model.getSubject().contentEquals(leaveModel.getSubject())) {
                                    isAlreadyAttempted = "true";
                                    attendanceModel = model;
                                }
                            }
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
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
    }

    private void getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        currentDateAndTime = sdf.format(new Date());
    }

    private void setNotification(String message, String status){
        String notificationPushId = databaseReferenceNotifications.push().getKey();
        NotificationModel notificationModel = new NotificationModel(notificationPushId, leaveModel.getLeaveId(), leaveModel.getUserId(),
                leaveModel.getTeacherId(), leaveModel.getSubject(), leaveModel.getImage(), leaveModel.getType(), currentDateAndTime, leaveModel.getReason(), "Lecturer", status);
        databaseReferenceNotifications.child(notificationPushId).setValue(notificationModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(binding.getRoot()).navigateUp();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}