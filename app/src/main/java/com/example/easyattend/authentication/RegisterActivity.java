package com.example.easyattend.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.easyattend.R;
import com.example.easyattend.databinding.ActivityRegisterBinding;
import com.example.easyattend.databinding.ActivityStartBinding;
import com.example.easyattend.models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding registerBinding;
    String userName, userEmail, userPhone, userPassword;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(registerBinding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main));

        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://easyattend-73d60-default-rtdb.firebaseio.com").getReference("AllUsers");

        registerBinding.llBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

            registerBinding.btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (checkValidation()){
                        progressDialog.setTitle(getResources().getString(R.string.app_name));
                        progressDialog.setMessage("Please wait... ");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        auth.createUserWithEmailAndPassword(userEmail, userPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                UserModel model = new UserModel(auth.getCurrentUser().getUid(), "Student",  userName, userEmail, userPhone, userPassword, "", "", "");
                                databaseReference.child(auth.getCurrentUser().getUid()).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        showToast("Successfully Registered");
                                        finish();
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
            });

        }

        Boolean checkValidation() {
            userName = registerBinding.userNameEt.getText().toString();
            userEmail = registerBinding.emailEt.getText().toString();
            userPhone = registerBinding.phoneEt.getText().toString();
            userPassword = registerBinding.passET.getText().toString();

            if (userName.isEmpty()) {
                showToast("Please enter name");
                return false;
            }
            if (userEmail.isEmpty()) {
                showToast("Please enter email");
                return false;
            }
            if (userPhone.isEmpty()) {
                showToast("Please enter phone");
                return false;
            }
            if (userPassword.isEmpty()) {
                showToast("Please enter password");
                return false;
            }

            return true;
        }

        private void showToast(String message) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

}