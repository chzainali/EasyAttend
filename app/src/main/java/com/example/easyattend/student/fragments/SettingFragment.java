package com.example.easyattend.student.fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.easyattend.R;
import com.example.easyattend.authentication.LoginActivity;
import com.example.easyattend.databinding.FragmentSettingBinding;
import com.example.easyattend.databinding.FragmentStudentHomeBinding;
import com.example.easyattend.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingFragment extends Fragment {
    FragmentSettingBinding binding;
    Uri imageUri;
    int PICK_IMAGE = 123;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    private DatabaseReference databaseReference;
    StorageReference storageReference;
    public SettingFragment() {
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
        binding = FragmentSettingBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(requireContext());
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://easyattend-73d60-default-rtdb.firebaseio.com").getReference("AllUsers");
        storageReference = FirebaseStorage.getInstance().getReference("UserImages");

        binding.tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showDialog();
            }
        });

        binding.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE);
            }
        });

        progressDialog.setTitle(getResources().getString(R.string.app_name));
        progressDialog.setMessage("Please wait... ");
        progressDialog.setCancelable(false);
        progressDialog.show();
        databaseReference.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    progressDialog.dismiss();
                    UserModel model = snapshot.getValue(UserModel.class);
                    if (!model.getUserImage().isEmpty()){
                        Glide.with(binding.ivProfile)
                                .load(model.getUserImage())
                                .centerCrop()
                                .into(binding.ivProfile);
                    }
                    binding.tvName.setText(model.getUserName());
                    binding.tvPhone.setText(model.getUserPhone());
                    binding.tvEmail.setText(model.getUserEmail());
                    binding.tvPassword.setText(model.getUserPassword());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null) {
            imageUri = data.getData();
            uploadProfileImage(imageUri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                binding.ivProfile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void uploadProfileImage(Uri imageUri){
        progressDialog.show();
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String uploadedImgURL = uri.toString();
                        Map<String, Object> update = new HashMap<String, Object>();
                        update.put("userImage", uploadedImgURL);
                        databaseReference.child(auth.getCurrentUser().getUid()).updateChildren(update).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    public void showDialog(){
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.logout_dialog);

        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        Button btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(requireContext(), LoginActivity.class));
                requireActivity().finish();
            }
        });

        dialog.show();

    }

}