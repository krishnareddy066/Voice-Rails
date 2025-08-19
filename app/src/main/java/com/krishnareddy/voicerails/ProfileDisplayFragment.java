package com.krishnareddy.voicerails;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileDisplayFragment extends Fragment {

    private ImageView profileImage;
    private TextView nameDisplay, phoneNumberDisplay, emailDisplay, dateOfBirthDisplay, genderDisplay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_display, container, false);

        profileImage = view.findViewById(R.id.profile_image);
        nameDisplay = view.findViewById(R.id.display_name);
        phoneNumberDisplay = view.findViewById(R.id.display_phone_number);
        emailDisplay = view.findViewById(R.id.display_email);
        dateOfBirthDisplay = view.findViewById(R.id.display_date_of_birth);
        genderDisplay = view.findViewById(R.id.display_gender);

        return view;
    }

    public void updateProfile(String name, String phoneNumber, String email, String dateOfBirth, String gender, Uri imageUri) {
        nameDisplay.setText("Name: " + name);
        phoneNumberDisplay.setText("Phone number: " + phoneNumber);
        emailDisplay.setText("Email: " + email);
        dateOfBirthDisplay.setText("Date of birth: " + dateOfBirth);
        genderDisplay.setText("Gender: " + gender);
        profileImage.setImageURI(imageUri);
    }
}
