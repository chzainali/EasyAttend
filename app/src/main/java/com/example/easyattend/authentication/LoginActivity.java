package com.example.easyattend.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.easyattend.MainActivity;
import com.example.easyattend.R;
import com.example.easyattend.admin.AdminDashboardActivity;
import com.example.easyattend.databinding.ActivityLoginBinding;
import com.example.easyattend.lecturer.LecturerDashboardActivity;
import com.example.easyattend.models.UserModel;
import com.example.easyattend.student.StudentDashboardActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding loginBinding;
    String checkType = "";
    String userEmail, userPassword;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main));

        databaseReference = FirebaseDatabase.getInstance("https://easyattend-73d60-default-rtdb.firebaseio.com").getReference("AllUsers");

        loginBinding.rgChhose.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = findViewById(checkedId);
            String selectedText = selectedRadioButton.getText().toString();
            checkType = selectedText;
        });

        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();

        loginBinding.llBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        loginBinding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidation()){
                    progressDialog.setTitle(getResources().getString(R.string.app_name));
                    progressDialog.setMessage("Please wait... ");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (userEmail.equals("admin987@gmail.com") && userPassword.equals("admin987")) {
                                    if (checkType.equals("Admin")) {
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "Successfully Login", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        progressDialog.dismiss();
                                        auth.signOut();
                                        Toast.makeText(LoginActivity.this, "Not exist as Admin", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Calling the database method to get the user's data from the database
                                    databaseReference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            progressDialog.dismiss();
                                            if (snapshot.exists()) {
                                                UserModel model = snapshot.getValue(UserModel.class);
                                                if (checkType.equals("Lecturer")) {
                                                    if (model != null && model.getUserType().equals(checkType)) {
                                                        startActivity(new Intent(LoginActivity.this, LecturerDashboardActivity.class));
                                                        Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    } else {
                                                        progressDialog.dismiss();
                                                        auth.signOut();
                                                        Toast.makeText(LoginActivity.this, "Not exist as Lecturer", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else if (checkType.equals("Student")) {
                                                    progressDialog.dismiss();
                                                    if (model != null && model.getUserType().equals(checkType)) {
                                                        startActivity(new Intent(LoginActivity.this, StudentDashboardActivity.class));
                                                        Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    } else {
                                                        progressDialog.dismiss();
                                                        auth.signOut();
                                                        Toast.makeText(LoginActivity.this, "Not exist as Student", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            } else {
                                                // Showing the message that no email exists
                                                Toast.makeText(LoginActivity.this, "No Email Exists", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {
                                            // Dismissing the progress dialog
                                            progressDialog.dismiss();
                                            // Showing the error message if an error occurs
                                            Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                // Dismissing the progress dialog
                                progressDialog.dismiss();
                                // Showing the error message if the user login is not successful
                                Toast.makeText(LoginActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

    }

    Boolean checkValidation() {
        userEmail = loginBinding.emailEt.getText().toString();
        userPassword = loginBinding.passET.getText().toString();

        if (checkType.isEmpty()) {
            showToast("Please select type");
            return false;
        }

        if (userEmail.isEmpty()) {
            showToast("Please enter email");
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