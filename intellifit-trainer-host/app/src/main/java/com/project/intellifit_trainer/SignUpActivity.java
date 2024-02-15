package com.project.intellifit_trainer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    EditText fullname, email, username, password;
    Button continuee;
    Intent intent;
    String str_fullname, str_email, str_username, str_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        fullname = findViewById(R.id.signup_et_fullname);
        email = findViewById(R.id.signup_et_email);
        username = findViewById(R.id.signup_et_username);
        password = findViewById(R.id.signup_et_pw);
        continuee = findViewById(R.id.signup_bt_signup);

        continuee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_fullname = String.valueOf(fullname.getText());
                str_email = String.valueOf(email.getText());
                str_username = String.valueOf(username.getText());
                str_password = String.valueOf(password.getText());

                if (TextUtils.isEmpty(str_fullname) || str_fullname.length() < 3) {
                    fullname.setError("The full name cannot be shorter than 3 characters.");
                    fullname.requestFocus();
                    return;
                }
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
                if (TextUtils.isEmpty(str_password) || str_password.length() < 6) {
                    password.setError("The password cannot be shorter than 6 characters.");
                    password.requestFocus();
                    return;
                }

                intent = new Intent(SignUpActivity.this, SignUpContinueActivity.class);
                intent.putExtra("FULLNAME", str_fullname);
                intent.putExtra("EMAIL", str_email);
                intent.putExtra("USERNAME", str_username);
                intent.putExtra("PASSWORD", str_password);
                startActivity(intent);
                // finish();

            }
        });


    }
}