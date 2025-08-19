package com.krishnareddy.voicerails;

import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class ProfileActivity extends AppCompatActivity implements ProfileInputFragment.OnSubmitListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (savedInstanceState == null) {
            ProfileInputFragment inputFragment = new ProfileInputFragment();
            inputFragment.setOnSubmitListener(this);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, inputFragment)
                    .commit();
        }
    }

    @Override
    public void onSubmit(String name, String phoneNumber, String email, String dateOfBirth, String gender, Uri imageUri) {
        ProfileDisplayFragment displayFragment = new ProfileDisplayFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, displayFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        getSupportFragmentManager().executePendingTransactions();

        displayFragment.updateProfile(name, phoneNumber, email, dateOfBirth, gender, imageUri);
    }
}
