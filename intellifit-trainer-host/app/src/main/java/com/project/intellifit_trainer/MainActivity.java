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

public class MainActivity extends AppCompatActivity {

    EditText username, password;
    TextView forgotpw;
    Button login, signup;
    Intent intent;
    String str_username, str_password;
    FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clearSelectedWorkout();

        // FirebaseAuth.getInstance().signOut(); // uygulama her calistiginda log-out

        mAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.main_et_username);
        password = findViewById(R.id.main_et_pw);
        login = findViewById(R.id.main_bt_login);
        signup = findViewById(R.id.main_bt_signup);
        forgotpw = findViewById(R.id.main_tv_forgotpw);

        forgotpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
                // finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_username = String.valueOf(username.getText());
                str_password = String.valueOf(password.getText());

                if (TextUtils.isEmpty(str_username)) {
                    Toast.makeText(MainActivity.this, "Please enter your username.", Toast.LENGTH_SHORT).show();
                    username.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(str_password)) {
                    Toast.makeText(MainActivity.this, "Please enter your password.", Toast.LENGTH_SHORT).show();
                    password.requestFocus();
                    return;
                }

                mAuth.signInWithEmailAndPassword(str_username, str_password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateVerificationStatus(user);

                                    if (user != null) {
                                        if (user.isEmailVerified()) {
                                            Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                                            intent = new Intent(MainActivity.this, LoggedInActivity.class);
                                            startActivity(intent);
                                            finish();

                                        } else {
                                            Toast.makeText(MainActivity.this, "Please verify your email address first.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "Username or password incorrect.",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
            ref.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    if (username != null) {
                        if (currentUser.isEmailVerified()) {
                            Toast.makeText(MainActivity.this, "Welcome back " + username + "!", Toast.LENGTH_SHORT).show();

                            intent = new Intent(MainActivity.this, LoggedInActivity.class);
                            intent.putExtra("USERNAME", username);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(MainActivity.this, "Welcome back " + username + "!\nPlease verify your email.\n", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("updateUI", "Database error", databaseError.toException());
                }
            });
        }
    }

    private void getUserInfo(String userId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String fullName = dataSnapshot.child("fullName").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);
                String username = dataSnapshot.child("username").getValue(String.class);
                String phoneNumber = dataSnapshot.child("phoneNumber").getValue(String.class);
                String dob = dataSnapshot.child("dob").getValue(String.class);
                String gender = dataSnapshot.child("gender").getValue(String.class);
                String height = dataSnapshot.child("height").getValue(String.class);
                String weight = dataSnapshot.child("weight").getValue(String.class);

                String userInfo = "Full Name: " + fullName + "\n" +
                                "Email: " + email + "\n" +
                                "Username: " + username + "\n" +
                                "Phone Number: " + phoneNumber + "\n" +
                                "Date of Birth: " + dob + "\n" +
                                "Gender: " + gender + "\n" +
                                "Height: " + height + "\n" +
                                "Weight: " + weight;

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("User Information")
                        .setMessage(userInfo)
                        .setPositiveButton("OK", null)
                        .show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Data load failed: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateVerificationStatus(FirebaseUser user) {
        if (user.isEmailVerified()) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            userRef.child("is_verified").setValue(true)
                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "User verified status updated to true"))
                    .addOnFailureListener(e -> Log.d("Firebase", "Failed to update user verified status: " + e.toString()));
        }
    }

    private void clearSelectedWorkout() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userID = user.getUid();
            DatabaseReference selectedWorkoutRef = FirebaseDatabase.getInstance().getReference("users").child(userID).child("selected_workout");

            selectedWorkoutRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("ClearSelectedWorkout", "Selected workout cleared successfully.");
                } else {
                    Log.w("ClearSelectedWorkout", "Failed to clear selected workout.", task.getException());
                }
            });
        }
    }

}