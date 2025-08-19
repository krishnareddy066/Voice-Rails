package com.krishnareddy.voicerails;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BookFragment extends Fragment {

    private EditText etFromStation;
    private EditText etToStation;
    private EditText etPassengerName;
    private EditText etDate;
    private Button btnBookTicket;

    private ProgressDialog progressDialog;
    private Handler handler;

    private String upiId = "sampathsaipolamarisetti@oksbi";
    private static final int UPI_PAYMENT_REQUEST_CODE = 101;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);

        etFromStation = view.findViewById(R.id.etFromStation);
        etToStation = view.findViewById(R.id.etToStation);
        etPassengerName = view.findViewById(R.id.etPassengerName);
        etDate = view.findViewById(R.id.etDate);
        btnBookTicket = view.findViewById(R.id.btnBookTicket);

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Booking in progress...");
        progressDialog.setCancelable(false);

        handler = new Handler();

        btnBookTicket.setOnClickListener(v -> {
            String fromStation = etFromStation.getText().toString().trim();
            String toStation = etToStation.getText().toString().trim();
            String passengerName = etPassengerName.getText().toString().trim();
            String date = etDate.getText().toString().trim();

            if (fromStation.isEmpty() || toStation.isEmpty() || passengerName.isEmpty() || date.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Show progress dialog
                progressDialog.show();

                // Simulate booking process with a delay
                handler.postDelayed(() -> {
                    // Hide progress dialog
                    progressDialog.dismiss();

                    // Initiate UPI payment
                    initiateUpiPayment();
                }, 10000); // 10 seconds delay (10000 milliseconds)
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPI_PAYMENT_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                // Payment successful, refresh the fragment or perform necessary actions
                Toast.makeText(getContext(), "Payment successful", Toast.LENGTH_SHORT).show();
                refreshFragment();
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // Payment cancelled by user, refresh the fragment or perform necessary actions
                Toast.makeText(getContext(), "Payment cancelled", Toast.LENGTH_SHORT).show();
                refreshFragment();
            } else {
                // Payment failed, refresh the fragment or perform necessary actions
                Toast.makeText(getContext(), "Payment failed", Toast.LENGTH_SHORT).show();
                refreshFragment();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up handler to avoid memory leaks
        handler.removeCallbacksAndMessages(null);
    }

    private void initiateUpiPayment() {
        // Construct the UPI URI
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId) // UPI ID to pay
                .appendQueryParameter("pn", "Sampath Sai Polamarisetti") // Name of the payee
                .appendQueryParameter("tn", "Train Ticket Booking") // Transaction note
                .appendQueryParameter("am", "100.00") // Amount to pay (example: ₹100.00)
                .appendQueryParameter("cu", "INR") // Currency
                .build();

        // Create intent for UPI payment
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);

        // Verify if there's an app available to handle this intent
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            // Start the intent for UPI payment and wait for result
            startActivityForResult(intent, UPI_PAYMENT_REQUEST_CODE);
        } else {
            // No UPI app available, handle this scenario
            Toast.makeText(getContext(), "No UPI app found on your device", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshFragment() {
        // Reload or refresh your fragment content here
        // For example, you might want to reload the booking form
        etFromStation.setText("");
        etToStation.setText("");
        etPassengerName.setText("");
        etDate.setText("");
    }
}
