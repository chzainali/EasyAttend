package com.example.easyattend.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.easyattend.R;
import com.example.easyattend.adapters.AdapterTeachers;
import com.example.easyattend.authentication.LoginActivity;
import com.example.easyattend.databinding.ActivityAdminDashboardBinding;
import com.example.easyattend.databinding.ActivityLoginBinding;
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

public class AdminDashboardActivity extends AppCompatActivity {

    ActivityAdminDashboardBinding adminDashboardBinding;
    List<UserModel> userList = new ArrayList<>();
    AdapterTeachers adapterTeachers;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminDashboardBinding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(adminDashboardBinding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main));

        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://easyattend-73d60-default-rtdb.firebaseio.com").getReference("AllUsers");

        adminDashboardBinding.addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, AddTeacherActivity.class));
            }
        });

        adminDashboardBinding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(AdminDashboardActivity.this, LoginActivity.class));
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        progressDialog.setTitle(getResources().getString(R.string.app_name));
        progressDialog.setMessage("Please wait... ");
        progressDialog.setCancelable(false);
        progressDialog.show();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userList.clear();
                    progressDialog.dismiss();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            UserModel model = ds.getValue(UserModel.class);
                            if (model.getUserType().contentEquals("Lecturer")){
                                userList.add(model);
                            }
                        }catch (DatabaseException e){
                            e.printStackTrace();
                        }
                    }

                    setAdapter();

                    if (userList.isEmpty()){
                        adminDashboardBinding.noTeacherFoundTV.setVisibility(View.VISIBLE);
                        adminDashboardBinding.teachersRV.setVisibility(View.GONE);
                    }else{
                        adminDashboardBinding.noTeacherFoundTV.setVisibility(View.GONE);
                        adminDashboardBinding.teachersRV.setVisibility(View.VISIBLE);
                    }
                }else{
                    progressDialog.dismiss();
                    adminDashboardBinding.noTeacherFoundTV.setVisibility(View.VISIBLE);
                    adminDashboardBinding.teachersRV.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(AdminDashboardActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setAdapter(){
        adapterTeachers = new AdapterTeachers(this, userList);
        adminDashboardBinding.teachersRV.setLayoutManager(new LinearLayoutManager(this));
        adminDashboardBinding.teachersRV.setAdapter(adapterTeachers);
    }

}