package com.krishnareddy.voicerails;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class otp extends AppCompatActivity {

    private EditText editTextOtp1, editTextOtp2, editTextOtp3, editTextOtp4, editTextOtp5, editTextOtp6;
    private Button buttonVerifyOtp;
    private FirebaseAuth auth;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Find all EditText fields for OTP
        editTextOtp1 = findViewById(R.id.editTextOtp1);
        editTextOtp2 = findViewById(R.id.editTextOtp2);
        editTextOtp3 = findViewById(R.id.editTextOtp3);
        editTextOtp4 = findViewById(R.id.editTextOtp4);
        editTextOtp5 = findViewById(R.id.editTextOtp5);
        editTextOtp6 = findViewById(R.id.editTextOtp6);
        buttonVerifyOtp = findViewById(R.id.buttonVerifyOtp);

        // Get the verification ID from the Intent
        verificationId = getIntent().getStringExtra("verificationId");

        // Set up TextWatcher for each OTP EditText
        setupOtpEditText(editTextOtp1, editTextOtp2);
        setupOtpEditText(editTextOtp2, editTextOtp3);
        setupOtpEditText(editTextOtp3, editTextOtp4);
        setupOtpEditText(editTextOtp4, editTextOtp5);
        setupOtpEditText(editTextOtp5, editTextOtp6);

        buttonVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOtp();
            }
        });
    }

    private void setupOtpEditText(final EditText currentEditText, final EditText nextEditText) {
        currentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1 && nextEditText != null) {
                    nextEditText.requestFocus();
                }
            }
        });
    }

    private void verifyOtp() {
        String otp = editTextOtp1.getText().toString() +
                editTextOtp2.getText().toString() +
                editTextOtp3.getText().toString() +
                editTextOtp4.getText().toString() +
                editTextOtp5.getText().toString() +
                editTextOtp6.getText().toString();

        if (TextUtils.isEmpty(otp) || otp.length() != 6) {
            Toast.makeText(this, "Please enter a valid OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(otp.this, "Verification successful", Toast.LENGTH_SHORT).show();
                        // Proceed to the create_password activity
                        Intent intent = new Intent(otp.this, create_password.class);
                        intent.putExtra("name", getIntent().getStringExtra("name"));
                        intent.putExtra("phone", getIntent().getStringExtra("phone"));
                        intent.putExtra("email", getIntent().getStringExtra("email"));
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(otp.this, "Verification failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
