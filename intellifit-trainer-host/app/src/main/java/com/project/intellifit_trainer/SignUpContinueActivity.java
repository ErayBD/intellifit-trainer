package com.project.intellifit_trainer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.app.DatePickerDialog;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;


import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignUpContinueActivity extends AppCompatActivity {

    EditText phoneNumber, height, weight, dob;
    String str_phoneNumber, str_height, str_weight, str_dob, str_genders;
    Spinner genders;
    Button signUp;
    FirebaseAuth mAuth;
    Intent intent;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Toast.makeText(SignUpContinueActivity.this, "Already signed in. Forwarding.", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_continue);

        mAuth = FirebaseAuth.getInstance();
        intent = getIntent();

        String signup_fullname = intent.getStringExtra("FULLNAME");
        String signup_email = intent.getStringExtra("EMAIL");
        String signup_username = intent.getStringExtra("USERNAME");
        String signup_password = intent.getStringExtra("PASSWORD");

        phoneNumber = findViewById(R.id.signupcon_et_phone);
        height = findViewById(R.id.signupcon_et_height);
        weight = findViewById(R.id.signupcon_et_weight);
        dob = findViewById(R.id.signupcon_et_dob);
        genders = findViewById(R.id.signupcon_spinner_gender);
        signUp = findViewById(R.id.signupcon_bt_signup);

//        str_phoneNumber = String.valueOf(phoneNumber.getText());
//        str_height = String.valueOf(height.getText());
//        str_weight = String.valueOf(weight.getText());
//        str_dob = String.valueOf(dob.getText());
//        str_genders = String.valueOf(genders.getSelectedItem());

        dob.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    SignUpContinueActivity.this,
                    (view1, year1, monthOfYear, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                        dob.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        ArrayAdapter<CharSequence> spinner_gender = ArrayAdapter.createFromResource(this,
                R.array.genders, R.layout.custom_spinner_item);
        spinner_gender.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        genders.setAdapter(spinner_gender);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_phoneNumber = String.valueOf(phoneNumber.getText());
                str_height = String.valueOf(height.getText());
                str_weight = String.valueOf(weight.getText());
                str_dob = String.valueOf(dob.getText());
                str_genders = String.valueOf(genders.getSelectedItem());

                if (TextUtils.isEmpty(str_phoneNumber) || str_phoneNumber.length() != 10) {
                    phoneNumber.setError("Please enter a valid phone number.");
                    phoneNumber.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(str_height) || !str_height.matches("^[0-9]+(\\.[0-9]+)?$")) {
                    height.setError("Please enter a valid height.");
                    height.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(str_weight) || !str_weight.matches("^[0-9]+(\\.[0-9]+)?$")) {
                    weight.setError("Please enter a valid weight.");
                    weight.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(str_dob)) {
                    dob.setError("Please select your date of birth.");
                    dob.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(str_genders) || str_genders.equals("Select Gender")) {
                    ((TextView)genders.getSelectedView()).setError("Please select your gender.");
                    Toast.makeText(getApplicationContext(), "Please select your gender.", Toast.LENGTH_LONG).show();
                    genders.requestFocus();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(signup_email, signup_password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    sendVerificationEmail(user);

                                    saveAdditionalUserInfo(user.getUid(), signup_fullname, signup_email, signup_username, signup_password,
                                            str_phoneNumber, str_dob, str_genders, str_height, str_weight);

                                    Toast.makeText(SignUpContinueActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();

                                    intent = new Intent(SignUpContinueActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(SignUpContinueActivity.this, "Account creation failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
    }

    private void saveAdditionalUserInfo(String userId, String fullName, String email, String username, String password,
                                        String phoneNumber, String dob, String gender, String height, String weight) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");


        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("fullName", fullName);
        userInfo.put("email", email);
        userInfo.put("username", username);
        // userInfo.put("password", password);
        userInfo.put("phoneNumber", phoneNumber);
        userInfo.put("dob", dob);
        userInfo.put("gender", gender);
        userInfo.put("height", height);
        userInfo.put("weight", weight);
        userInfo.put("is_verified", Boolean.FALSE);

        myRef.child(userId).setValue(userInfo);

        myRef.child(userId).setValue(userInfo)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Data successfully saved!"))
                .addOnFailureListener(e -> Log.d("Firebase", "Failed to save data: " + e.toString()));
    }

    private void sendVerificationEmail(FirebaseUser user) {
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpContinueActivity.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignUpContinueActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
