package com.krishnareddy.voicerails;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class create_password extends AppCompatActivity {

    private EditText editTextPassword, editTextRePassword;
    private Button buttonProceed;
    private TextView tvPasswordRequirements, tvPasswordRequirements1, tvPasswordRequirements2;
    private FirebaseAuth auth;
    private String name, phone, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Get user data from intent
        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra("name");
            phone = intent.getStringExtra("phone");
            email = intent.getStringExtra("email");
        }

        editTextPassword = findViewById(R.id.etPassword);
        editTextRePassword = findViewById(R.id.etRePassword);
        buttonProceed = findViewById(R.id.btnProceed);
        tvPasswordRequirements = findViewById(R.id.tvPasswordRequirements);
        tvPasswordRequirements1 = findViewById(R.id.tvPasswordRequirements1);
        tvPasswordRequirements2 = findViewById(R.id.tvPasswordRequirements2);

        buttonProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSavePassword();
            }
        });
    }

    private void validateAndSavePassword() {
        String password = editTextPassword.getText().toString();
        String rePassword = editTextRePassword.getText().toString();

        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(rePassword)) {
            Toast.makeText(this, "Please enter both passwords", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if passwords match
        if (!password.equals(rePassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if password meets the criteria
        if (!isPasswordValid(password)) {
            Toast.makeText(this, "Password must contain at least one capital letter, one number, and one special character (#, @, _, -)", Toast.LENGTH_LONG).show();
            return;
        }

        // If all validations pass, save password to Firestore
        savePasswordToFirestore(password);
    }

    private boolean isPasswordValid(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=_-])(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private void savePasswordToFirestore(String password) {
        // Access Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new user object with password
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("phone", phone);
        user.put("email", email);
        user.put("password", password);

        // Add a new document with a generated ID
        db.collection("users")
                .document(auth.getCurrentUser().getUid()) // Use UID as the document ID
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    // DocumentSnapshot added successfully
                    Toast.makeText(create_password.this, "Password set successfully!", Toast.LENGTH_SHORT).show();
                    // Proceed to the next activity
                    startActivity(new Intent(create_password.this, Login_activity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Error handling
                    Toast.makeText(create_password.this, "Failed to set password. Please try again later.", Toast.LENGTH_SHORT).show();
                });
    }
}
