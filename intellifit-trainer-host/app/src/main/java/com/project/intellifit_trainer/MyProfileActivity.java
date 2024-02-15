package com.project.intellifit_trainer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MyProfileActivity extends AppCompatActivity {

    EditText fullName, username, phoneNumber, dob, height, weight;
    Spinner gender;
    Button update;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());


        fullName = findViewById(R.id.myprofile_et_fullname);
        username = findViewById(R.id.myprofile_et_username);
        phoneNumber = findViewById(R.id.myprofile_et_phone);
        dob = findViewById(R.id.myprofile_et_dob);
        height = findViewById(R.id.myprofile_et_height);
        weight = findViewById(R.id.myprofile_et_weight);
        gender = findViewById(R.id.myprofile_spinner_gender);
        update = findViewById(R.id.myprofile_bt_update);

        dob.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MyProfileActivity.this,
                    (view1, year1, monthOfYear, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                        dob.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        ArrayAdapter<CharSequence> spinner_gender = ArrayAdapter.createFromResource(this,
                R.array.genders, R.layout.custom_spinner_item);
        spinner_gender.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        gender.setAdapter(spinner_gender);

        loadUserProfile();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserProfile();
            }
        });
    }

    private void loadUserProfile() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String str_fullName = dataSnapshot.child("fullName").getValue(String.class);
                    String str_username = dataSnapshot.child("username").getValue(String.class);
                    String str_phoneNumber = dataSnapshot.child("phoneNumber").getValue(String.class);
                    String str_dob = dataSnapshot.child("dob").getValue(String.class);
                    String str_height = dataSnapshot.child("height").getValue(String.class);
                    String str_weight = dataSnapshot.child("weight").getValue(String.class);
                    String str_gender = dataSnapshot.child("gender").getValue(String.class);

                    fullName.setText(str_fullName);
                    username.setText(str_username);
                    phoneNumber.setText(str_phoneNumber);
                    dob.setText(str_dob);
                    height.setText(str_height);
                    weight.setText(str_weight);

                    ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) gender.getAdapter();
                    int spinnerPosition = adapter.getPosition(str_gender);
                    gender.setSelection(spinnerPosition);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MyProfileActivity.this, "Failed to load user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserProfile() {
        String newFullName = String.valueOf(fullName.getText());
        String newUsername = String.valueOf(username.getText());
        String newPhoneNumber = String.valueOf(phoneNumber.getText());
        String newDob = String.valueOf(dob.getText());
        String newHeight = String.valueOf(height.getText());
        String newWeight = String.valueOf(weight.getText());
        String newGender = String.valueOf(gender.getSelectedItem());

        if (TextUtils.isEmpty(newFullName) || newFullName.length() < 3) {
            fullName.setError("The full name cannot be shorter than 3 characters.");
            fullName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(newUsername) || newUsername.length() < 3) {
            username.setError("The username cannot be shorter than 3 characters.");
            username.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(newPhoneNumber) || newPhoneNumber.length() != 10) {
            phoneNumber.setError("Please enter a valid phone number.");
            phoneNumber.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(newDob)) {
            dob.setError("Please select your date of birth.");
            dob.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(newGender) || newGender.equals("Select Gender")) {
            ((TextView)gender.getSelectedView()).setError("Please select your gender.");
            Toast.makeText(getApplicationContext(), "Please select your gender.", Toast.LENGTH_LONG).show();
            gender.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(newHeight) || !newHeight.matches("^[0-9]+(\\.[0-9]+)?$")) {
            height.setError("Please enter a valid height.");
            height.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(newWeight) || !newWeight.matches("^[0-9]+(\\.[0-9]+)?$")) {
            weight.setError("Please enter a valid weight.");
            weight.requestFocus();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName", newFullName);
        updates.put("username", newUsername);
        updates.put("phoneNumber", newPhoneNumber);
        updates.put("dob", newDob);
        updates.put("height", newHeight);
        updates.put("weight", newWeight);
        updates.put("gender", newGender);


        databaseReference.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MyProfileActivity.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyProfileActivity.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}