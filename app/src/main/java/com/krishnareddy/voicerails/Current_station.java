package com.krishnareddy.voicerails;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Current_station extends AppCompatActivity {
    private static final String TAG = "Current_station";

    // Firestore instance
    private FirebaseFirestore db;
    private TextView stationNameTextView;
    private AutoCompleteTextView stationIdInput;
    private Button searchButton;
    private List<String> stationNames;
    private Map<String, Object> stationData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_station);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        stationNameTextView = findViewById(R.id.station_text);
        stationIdInput = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        TextView loginText = findViewById(R.id.login_text);

        // Retrieve data from Firestore
        retrieveStationList();

        // Set up search button click listener
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = stationIdInput.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchStation(query);
                } else {
                    stationNameTextView.setText("Please enter a station ID or name");
                }
            }
        });

        // Set up item click listener for AutoCompleteTextView
        stationIdInput.setOnItemClickListener((parent, view, position, id) -> {
            String selectedStation = (String) parent.getItemAtPosition(position);
            redirectToNextPage(selectedStation);
        });

        // Set up login text click listener
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Current_station.this, Login_activity.class);
                startActivity(intent);
            }
        });
    }

    private void retrieveStationList() {
        // Reference to the document
        DocumentReference docRef = db.collection("stations").document("stationList");

        // Fetch data from the document
        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Handle the retrieved data
                        stationData = documentSnapshot.getData();
                        if (stationData != null) {
                            stationNames = new ArrayList<>();
                            for (Map.Entry<String, Object> entry : stationData.entrySet()) {
                                stationNames.add(entry.getValue().toString());
                            }
                            setUpAutoCompleteTextView();
                        } else {
                            stationNameTextView.setText("No data found");
                        }
                        Log.d(TAG, "Data retrieved successfully");
                    } else {
                        Log.d(TAG, "No such document");
                        stationNameTextView.setText("Document does not exist");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Error retrieving document", e);
                    stationNameTextView.setText("Error retrieving data");
                });
    }

    private void setUpAutoCompleteTextView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, stationNames);
        stationIdInput.setAdapter(adapter);
    }

    private void searchStation(String query) {
        if (stationData != null) {
            StringBuilder result = new StringBuilder();
            boolean found = false;
            for (Map.Entry<String, Object> entry : stationData.entrySet()) {
                String stationId = entry.getKey();
                String stationName = entry.getValue().toString();

                if (stationId.equalsIgnoreCase(query) || stationName.equalsIgnoreCase(query)) {
                    result.append(stationId).append(": ").append(stationName).append("\n");
                    found = true;
                }
            }
            if (found) {
                stationNameTextView.setText(result.toString());
            } else {
                stationNameTextView.setText("No matching station found");
            }
        } else {
            stationNameTextView.setText("No data available");
        }
    }

    private void redirectToNextPage(String selectedStation) {
        // Example: Replace NextActivity.class with your actual next activity
        Intent intent = new Intent(Current_station.this, liveMainActivityText.class);
        intent.putExtra("selectedStation", selectedStation);
        startActivity(intent);

        // Save selected station to Firestore
        saveSelectedStationToFirestore(selectedStation);
    }

    private void saveSelectedStationToFirestore(String selectedStation) {
        // Example: Assuming you have a "selected_stations" collection in Firestore
        db.collection("selected_stations")
                .add(selectedStation)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Selected station saved to Firestore");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding selected station to Firestore", e);
                    }
                });
    }
}
