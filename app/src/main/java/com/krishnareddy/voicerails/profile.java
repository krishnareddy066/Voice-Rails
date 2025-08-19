package com.krishnareddy.voicerails;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class profile extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView profileImage;
    private EditText nameInput, phoneNumberInput, emailInput;
    private TextView dateOfBirthInput;
    private Spinner genderInput;
    private TextView nameDisplay, phoneNumberDisplay, emailDisplay, dateOfBirthDisplay, genderDisplay;
    private LinearLayout displayLayout;
    private LinearLayout inputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage = findViewById(R.id.profile_image);
        nameInput = findViewById(R.id.name_input);
        phoneNumberInput = findViewById(R.id.phone_number_input);
        emailInput = findViewById(R.id.email_input);
        dateOfBirthInput = findViewById(R.id.date_of_birth_input);
        genderInput = findViewById(R.id.gender_input);

        nameDisplay = findViewById(R.id.display_name);
        phoneNumberDisplay = findViewById(R.id.display_phone_number);
        emailDisplay = findViewById(R.id.display_email);
        dateOfBirthDisplay = findViewById(R.id.display_date_of_birth);
        genderDisplay = findViewById(R.id.display_gender);

        displayLayout = findViewById(R.id.display_layout);
        inputLayout = findViewById(R.id.input_layout);
    }

    public void onEditProfileImageClicked(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        }
    }

    public void onDateOfBirthClicked(View view) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateOfBirthInput.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    public void onSubmitClicked(View view) {
        String userName = nameInput.getText().toString();
        String userPhoneNumber = phoneNumberInput.getText().toString();
        String userEmail = emailInput.getText().toString();
        String userDateOfBirth = dateOfBirthInput.getText().toString();
        String userGender = genderInput.getSelectedItem().toString();

        nameDisplay.setText("Name: " + userName);
        phoneNumberDisplay.setText("Phone number: " + userPhoneNumber);
        emailDisplay.setText("Email: " + userEmail);
        dateOfBirthDisplay.setText("Date of birth: " + userDateOfBirth);
        genderDisplay.setText("Gender: " + userGender);

        displayLayout.setVisibility(View.VISIBLE);
        inputLayout.setVisibility(View.GONE);
    }


}