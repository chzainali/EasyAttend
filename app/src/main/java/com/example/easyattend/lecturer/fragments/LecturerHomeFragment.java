package com.example.easyattend.lecturer.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.easyattend.R;
import com.example.easyattend.databinding.FragmentLecturerHomeBinding;
import com.example.easyattend.models.AttendanceModel;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LecturerHomeFragment extends Fragment {
    FragmentLecturerHomeBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    String checkStatus = "";
    private DatabaseReference databaseReferenceProfile;
    List<AttendanceModel> attendanceList = new ArrayList<>();
    List<AttendanceModel> presentList = new ArrayList<>();
    List<AttendanceModel> yesterdayAttendanceList = new ArrayList<>();
    List<AttendanceModel> absentList = new ArrayList<>();
    List<AttendanceModel> excuseList = new ArrayList<>();
    List<UserModel> userList = new ArrayList<>();
    DatabaseReference databaseReferenceAttendance;
    String currentDateAndTime;
    UserModel teacherModel;


    public LecturerHomeFragment() {
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
        binding = FragmentLecturerHomeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getCurrentDate();
        progressDialog = new ProgressDialog(requireContext());
        auth = FirebaseAuth.getInstance();
        databaseReferenceProfile = FirebaseDatabase.getInstance("https://easyattend-73d60-default-rtdb.firebaseio.com").getReference("AllUsers");
        databaseReferenceAttendance = FirebaseDatabase.getInstance("https://easyattend-73d60-default-rtdb.firebaseio.com").getReference("Attendance");


        binding.rlTakeAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkStatus.contentEquals("true")) {
                    binding.ivCheck.setImageResource(R.drawable.ic_unchecked);
                    checkStatus = "false";
                    updateActive(checkStatus);
                } else {
                    binding.ivCheck.setImageResource(R.drawable.baseline_check_circle_24);
                    checkStatus = "true";
                    updateActive(checkStatus);
                }
            }
        });

        progressDialog.setTitle(getResources().getString(R.string.app_name));
        progressDialog.setMessage("Please wait... ");
        progressDialog.setCancelable(false);
        progressDialog.show();
        databaseReferenceProfile.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    teacherModel = snapshot.getValue(UserModel.class);
                    checkStatus = teacherModel.getActive();
                    binding.tvName.setText("Welcome Back,\n" + teacherModel.getUserName());
                    if (teacherModel.getActive().contentEquals("true")) {
                        binding.ivCheck.setImageResource(R.drawable.baseline_check_circle_24);
                    } else {
                        binding.ivCheck.setImageResource(R.drawable.ic_unchecked);
                    }
                    getAttendance();
                } else {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(requireActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateActive(String status) {
        Map<String, Object> update = new HashMap<String, Object>();
        update.put("active", status);
        databaseReferenceProfile.child(auth.getCurrentUser().getUid()).updateChildren(update).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                Toast.makeText(requireContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                            if (model.getTeacherId().contentEquals(auth.getCurrentUser().getUid()) && model.getDate().contentEquals(getPreviousDate())){
                                yesterdayAttendanceList.add(model);
                            }
                            if (model.getTeacherId().contentEquals(auth.getCurrentUser().getUid()) && model.getDate().contentEquals(currentDateAndTime)) {
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
                    getPercentage();
                    getAllUsers();
//                    progressDialog.dismiss();

                } else {
                    getPercentage();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(requireActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllUsers() {

        databaseReferenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            UserModel model = ds.getValue(UserModel.class);
                            if (model.getUserType().contentEquals("Student")) {
                                userList.add(model);
                            }
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }

                    checkAttendance();

                } else {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(requireActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkAttendance() {

        for (int i = 0; i < userList.size(); i++) {
            boolean isPresent = false;
            for (int j = 0; j < yesterdayAttendanceList.size(); j++) {
                if (userList.get(i).getUserType().contentEquals("Student") &&
                        yesterdayAttendanceList.get(j).getTeacherId().contentEquals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        && userList.get(i).getUserId().contentEquals(yesterdayAttendanceList.get(j).getUserId())) {
                    if (yesterdayAttendanceList.get(j).getDate().contentEquals(getPreviousDate())) {
                        isPresent = true;
                    }
                }
            }
            if (!isPresent && userList.get(i).getUserType().contentEquals("Student")){
                String pushID = databaseReferenceAttendance.push().getKey();
                AttendanceModel attendanceModel = new AttendanceModel(pushID, userList.get(i).getUserId(),
                        teacherModel.getUserId(), teacherModel.getTeacherSubject(), getPreviousDate(), "Absent");
                databaseReferenceAttendance.child(pushID).setValue(attendanceModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(requireActivity(), "yes", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireActivity(), "no", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
            progressDialog.dismiss();
        }
    }

    @SuppressLint("SetTextI18n")
    private void getPercentage() {
        int totalSize = presentList.size() + absentList.size() + excuseList.size();
        if (totalSize > 0) {
            int presentListPercentage = (presentList.size() * 100) / totalSize;
            int absentListPercentage = (absentList.size() * 100) / totalSize;
            int excuseListPercentage = (excuseList.size() * 100) / totalSize;

            binding.tvPresent.setText(presentListPercentage + "%");
            binding.tvAbsent.setText(absentListPercentage + "%");
            binding.tvExcused.setText(excuseListPercentage + "%");
        }

    }

    private void getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        currentDateAndTime = sdf.format(new Date());
    }


    public String getPreviousDate() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = new Date();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date previousDate = calendar.getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String formattedDate = dateFormat.format(previousDate);

        return formattedDate;
    }

}