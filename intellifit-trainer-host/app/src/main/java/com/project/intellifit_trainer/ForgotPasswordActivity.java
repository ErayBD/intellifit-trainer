package com.project.intellifit_trainer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText email, username;
    String str_email, str_username;
    Button resetpw;
    FirebaseAuth mAuth;
    Intent intent;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        email = findViewById(R.id.forgotpassword_et_email);
        username = findViewById(R.id.forgotpassword_et_username);
        resetpw = findViewById(R.id.forgotpassword_bt_resetpw);

//        str_email = String.valueOf(email.getText());
//        str_username = String.valueOf(username.getText());

        resetpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_email = String.valueOf(email.getText());
                str_username = String.valueOf(username.getText());

                if (TextUtils.isEmpty(str_email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(str_email).matches()) {
                    email.setError("The email address is invalid.");
                    email.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(str_username) || str_username.length() < 3) {
                    username.setError("The username cannot be shorter than 3 characters.");
                    username.requestFocus();
                    return;
                }
                verifyUserInformation();
            }
        });
    }

    private void verifyUserInformation() {
        databaseReference.orderByChild("email").equalTo(str_email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String databaseUsername = userSnapshot.child("username").getValue(String.class);
                        Boolean databaseIsVerified = userSnapshot.child("is_verified").getValue(Boolean.class);
                        if (databaseUsername != null) {
                            if (databaseUsername.equals(str_username)) {
                                if (databaseIsVerified.equals(Boolean.TRUE)) {
                                    sendPasswordResetEmail(str_email);
                                } else {
                                    Toast.makeText(ForgotPasswordActivity.this, "Please verify your email address.", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }
                        }
                    }
                }
                Toast.makeText(ForgotPasswordActivity.this, "No matching user found.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ForgotPasswordActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this, "Check your email to reset your password.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset password email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updatePassword(FirebaseUser user) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        userRef.child("password").setValue(true)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "User verified status updated to true"))
                .addOnFailureListener(e -> Log.d("Firebase", "Failed to update user verified status: " + e.toString()));

    }
}