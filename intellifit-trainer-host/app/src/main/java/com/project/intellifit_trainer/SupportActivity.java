package com.project.intellifit_trainer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SupportActivity extends AppCompatActivity {

    RadioButton suggestion, complaint, thankYou;
    RadioGroup radioGroup;
    EditText subject, yourMessage;
    Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        suggestion = findViewById(R.id.support_radiobutton_suggest);
        complaint = findViewById(R.id.support_radiobutton_complaint);
        thankYou = findViewById(R.id.support_radiobutton_thankyou);
        radioGroup = findViewById(R.id.support_radiogroup);
        subject = findViewById(R.id.support_et_subject);
        yourMessage = findViewById(R.id.support_et_message);
        send = findViewById(R.id.support_bt_send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedOption = "";
                int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                if (selectedRadioButtonId == -1) {
                    Toast.makeText(SupportActivity.this, "Please select an option.", Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                selectedOption = selectedRadioButton.getText().toString();


                String str_subject = String.valueOf(subject.getText());
                String str_message = String.valueOf(yourMessage.getText());

                if (TextUtils.isEmpty(str_subject)) {
                    subject.setError("This field cannot be empty.");
                    subject.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(str_message)) {
                    yourMessage.setError("This field cannot be empty.");
                    yourMessage.requestFocus();
                    return;
                } else {
                    if (str_message.length() < 10) {
                        yourMessage.setError("Your message cannot be shorter than 10 characters.");
                        yourMessage.requestFocus();
                        return;
                    }
                }

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"intellifit.trainer@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, str_subject);
                emailIntent.putExtra(Intent.EXTRA_TEXT, "About: " + selectedOption + "\n\n" + str_message);

                try {
                    startActivity(Intent.createChooser(emailIntent, "Send email using..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(SupportActivity.this, "No email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}