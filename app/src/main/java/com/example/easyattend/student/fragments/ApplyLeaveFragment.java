package com.example.easyattend.student.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.easyattend.R;
import com.example.easyattend.databinding.FragmentApplyLeaveBinding;
import com.example.easyattend.databinding.FragmentApprovalBinding;
import com.example.easyattend.models.AttendanceModel;
import com.example.easyattend.models.LeaveModel;
import com.example.easyattend.models.NotificationModel;
import com.example.easyattend.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ApplyLeaveFragment extends Fragment {
    FragmentApplyLeaveBinding binding;
    Uri imageUri;
    int PICK_IMAGE = 123;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceNotifications;
    StorageReference storageReference;
    UserModel userModel;
    String type, date, reason;
    public ApplyLeaveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userModel = (UserModel) getArguments().getSerializable("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentApplyLeaveBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getCurrentDate();
        progressDialog = new ProgressDialog(requireContext());
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://easyattend-73d60-default-rtdb.firebaseio.com").getReference("Leave");
        databaseReferenceNotifications = FirebaseDatabase.getInstance("https://easyattend-73d60-default-rtdb.firebaseio.com").getReference("Notifications");
        storageReference = FirebaseStorage.getInstance().getReference("LeaveImages");

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        binding.ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE);
            }
        });

        binding.btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidation()){
                    progressDialog.setTitle(getResources().getString(R.string.app_name));
                    progressDialog.setMessage("Please wait... ");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    uploadLeaveImage(imageUri);
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == AppCompatActivity.RESULT_OK) {
            try {
                imageUri = data.getData();
                binding.ivImage.setImageURI(imageUri);
                binding.tvPickImage.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    Boolean checkValidation() {
        type = binding.leaveTypeEt.getText().toString();
        reason = binding.leaveReasonEt.getText().toString();

        if (imageUri == null) {
            showToast("Please pick image");
            return false;
        }
        if (type.isEmpty()) {
            showToast("Please enter leave type");
            return false;
        }
        if (reason.isEmpty()) {
            showToast("Please enter leave reason");
            return false;
        }

        return true;
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        date = sdf.format(new Date());
        binding.leaveDateEt.setText(date);
    }

    private void uploadLeaveImage(Uri imageUri){
        progressDialog.show();
        StorageReference imageReference1 = storageReference.child(imageUri.getLastPathSegment());
        imageReference1.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String uploadedImgURL = uri.toString();
                        uploadLeaveData(uploadedImgURL);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(requireContext(), ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(requireContext(), ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadLeaveData(String imageUri){
        String pushID = databaseReference.push().getKey();
        LeaveModel leaveModel = new LeaveModel(pushID, auth.getCurrentUser().getUid(),
                userModel.getUserId(), userModel.getTeacherSubject(), imageUri, type, date, reason, "Pending");
        databaseReference.child(pushID).setValue(leaveModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                String notificationPushId = databaseReferenceNotifications.push().getKey();
                NotificationModel notificationModel = new NotificationModel(notificationPushId, pushID, auth.getCurrentUser().getUid(),
                        userModel.getUserId(), userModel.getTeacherSubject(), imageUri, type, date, reason, "Student", "Pending");
                databaseReferenceNotifications.child(notificationPushId).setValue(notificationModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        showToast("Requested Successfully");
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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                showToast(e.getLocalizedMessage());
            }
        });
    }


}