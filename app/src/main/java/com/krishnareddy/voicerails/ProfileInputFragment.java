package com.krishnareddy.voicerails;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.Calendar;

public class ProfileInputFragment extends Fragment {

    private static final int PICK_IMAGE = 1;
    private ImageView profileImage;
    private EditText nameInput, phoneNumberInput, emailInput;
    private TextView dateOfBirthInput;
    private Spinner genderInput;

    private OnSubmitListener onSubmitListener;

    public interface OnSubmitListener {
        void onSubmit(String name, String phoneNumber, String email, String dateOfBirth, String gender, Uri imageUri);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_input, container, false);

        profileImage = view.findViewById(R.id.profile_image);
        nameInput = view.findViewById(R.id.name_input);
        phoneNumberInput = view.findViewById(R.id.phone_number_input);
        emailInput = view.findViewById(R.id.email_input);
        dateOfBirthInput = view.findViewById(R.id.date_of_birth_input);
        genderInput = view.findViewById(R.id.gender_input);

        profileImage.setOnClickListener(v -> onEditProfileImageClicked());
        dateOfBirthInput.setOnClickListener(this::onDateOfBirthClicked);
        view.findViewById(R.id.submit_button).setOnClickListener(this::onSubmitClicked);

        return view;
    }

    public void setOnSubmitListener(OnSubmitListener onSubmitListener) {
        this.onSubmitListener = onSubmitListener;
    }

    private void onEditProfileImageClicked() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == getActivity().RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        }
    }

    private void onDateOfBirthClicked(View view) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateOfBirthInput.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void onSubmitClicked(View view) {
        String userName = nameInput.getText().toString();
        String userPhoneNumber = phoneNumberInput.getText().toString();
        String userEmail = emailInput.getText().toString();
        String userDateOfBirth = dateOfBirthInput.getText().toString();
        String userGender = genderInput.getSelectedItem().toString();
        Uri imageUri = (Uri) profileImage.getTag();  // Assuming you save the imageUri as a tag

        if (onSubmitListener != null) {
            onSubmitListener.onSubmit(userName, userPhoneNumber, userEmail, userDateOfBirth, userGender, imageUri);
        }
    }
}
