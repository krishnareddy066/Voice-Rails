package com.krishnareddy.voicerails;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class liveMainActivityText extends AppCompatActivity {

    private static final String TAG = "liveMainActivityText";

    private TextView tvLiveAnnouncement;
    private ListView listViewAnnouncements;
    private FirebaseFirestore db;
    private Button btnText;
    private Button btnAudio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_text);

        tvLiveAnnouncement = findViewById(R.id.tvLiveAnnouncement);
        listViewAnnouncements = findViewById(R.id.listViewAnnouncements);
        btnText = findViewById(R.id.btnText);
        btnAudio = findViewById(R.id.btnAudio);

        db = FirebaseFirestore.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listenToAnnouncements();

        // Set click listeners for buttons
        btnText.setOnClickListener(v -> {
            if (!isTextActivity()) { // Check if already in Text activity
                Intent intent = new Intent(liveMainActivityText.this, liveMainActivityText.class);
                startActivity(intent);
                finish(); // Close the current activity to prevent stacking
            }
        });

        btnAudio.setOnClickListener(v -> {
            if (!isAudioActivity()) { // Check if already in Audio activity
                Intent intent = new Intent(liveMainActivityText.this, live_audio.class);
                startActivity(intent);
                finish(); // Close the current activity to prevent stacking
            }
        });
    }

    // Helper methods to check current activity type
    private boolean isTextActivity() {
        return getClass().getSimpleName().equals("liveMainActivityText");
    }

    private boolean isAudioActivity() {
        return getClass().getSimpleName().equals("live_audio");
    }

    private void listenToAnnouncements() {
        db.collection("announcements")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(6) // Fetch latest 6 announcements to show 1 latest and 5 past
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error fetching announcements", e);
                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        List<DocumentSnapshot> documents = snapshots.getDocuments();
                        List<String> announcements = new ArrayList<>();

                        // Find the latest announcement based on timestamp
                        DocumentSnapshot latestAnnouncementDoc = null;
                        long currentTime = System.currentTimeMillis();
                        long closestTimeDiff = Long.MAX_VALUE;

                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());

                        for (DocumentSnapshot document : documents) {
                            String announcement = document.getString("announcement");

                            try {
                                Timestamp timestamp = document.getTimestamp("timestamp");
                                if (timestamp != null) {
                                    Date date = timestamp.toDate();
                                    String formattedDate = sdf.format(date);
                                    announcement = formattedDate + " - " + announcement;

                                    long announcementTime = date.getTime();
                                    long timeDiff = Math.abs(currentTime - announcementTime);

                                    if (timeDiff < closestTimeDiff) {
                                        closestTimeDiff = timeDiff;
                                        latestAnnouncementDoc = document;
                                    }
                                }
                            } catch (Exception ex) {
                                Log.e(TAG, "Error parsing timestamp", ex);
                            }

                            announcements.add(announcement);
                        }

                        // Display the latest announcement
                        if (latestAnnouncementDoc != null) {
                            String liveAnnouncement = latestAnnouncementDoc.getString("announcement");
                            Timestamp latestTimestamp = latestAnnouncementDoc.getTimestamp("timestamp");
                            if (latestTimestamp != null) {
                                Date date = latestTimestamp.toDate();
                                String formattedDate = sdf.format(date);
                                liveAnnouncement = formattedDate + " - " + liveAnnouncement;
                            }
                            tvLiveAnnouncement.setText(liveAnnouncement);
                            tvLiveAnnouncement.setSelected(true);

                            // Remove the latest announcement from the list and show the past five announcements
                            announcements.remove(liveAnnouncement);
                            if (announcements.size() > 5) {
                                announcements = announcements.subList(0, 5);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                    R.layout.list_item_announcement, announcements);
                            listViewAnnouncements.setAdapter(adapter);

                        }
                    } else {
                        Log.d(TAG, "No announcements found");
                    }
                });
    }
}
