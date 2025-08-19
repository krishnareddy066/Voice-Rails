package com.krishnareddy.voicerails;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sign_up extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextPhone;
    private EditText editTextEmail;
    private Button buttonSendOtp;
    private FirebaseAuth auth;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonSendOtp = findViewById(R.id.buttonSendOtp);

        auth = FirebaseAuth.getInstance();

        // Check if all views are initialized properly
        if (editTextName == null || editTextPhone == null || editTextEmail == null || buttonSendOtp == null) {
            Toast.makeText(this, "Some views are not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set default country code for phone number
        editTextPhone.setText("+91");

        buttonSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOtp();
            }
        });
    }

    private void sendOtp() {
        String name = editTextName.getText().toString();
        String phone = editTextPhone.getText().toString();
        String email = editTextEmail.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPhoneNumber(phone)) {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        signInWithPhoneAuthCredential(credential, name, phone, email);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(Sign_up.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        Sign_up.this.verificationId = verificationId;
                        Toast.makeText(Sign_up.this, "OTP sent", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Sign_up.this, otp.class);
                        intent.putExtra("verificationId", verificationId);
                        intent.putExtra("name", name);
                        intent.putExtra("phone", phone);
                        intent.putExtra("email", email);
                        startActivity(intent);
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential, String name, String phone, String email) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Sign_up.this, "Verification successful", Toast.LENGTH_SHORT).show();
                        saveUserToDatabase(name, phone, email);
                    } else {
                        Toast.makeText(Sign_up.this, "Verification failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToDatabase(String name, String phone, String email) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        String userId = auth.getCurrentUser().getUid();
        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("phone", phone);
        userMap.put("email", email);
        databaseReference.child(userId).setValue(userMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Sign_up.this, "User data saved", Toast.LENGTH_SHORT).show();
                // Proceed to next activity
                startActivity(new Intent(Sign_up.this, otp.class));
                finish();
            } else {
                Toast.makeText(Sign_up.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("\\+\\d{1,3}\\d{10}");
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.+[a-zA-Z]+";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
