package com.krishnareddy.voicerails;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class live_audio extends AppCompatActivity {

    private static final String TAG = "live_audio";

    private Button btnText;
    private Button btnAudio;
    private Button translateButton;
    private Button speakButton;
    private Button buttonGoBack;
    private TextView outputText;
    private Spinner languageSpinner;
    private Translator translator;
    private TextToSpeech tts;

    private Map<String, String> languageMap;
    private Map<String, Locale> localeMap;
    private String selectedLanguage;
    private FirebaseFirestore db;

    private String latestAnnouncement;

    private Handler handler;
    private Runnable fetchAnnouncementsRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_audio);

        btnText = findViewById(R.id.btnText);
        btnAudio = findViewById(R.id.btnAudio);
        translateButton = findViewById(R.id.translateButton);
        speakButton = findViewById(R.id.speakButton);
        buttonGoBack = findViewById(R.id.btback); // Find the buttonGoBack by its ID
        outputText = findViewById(R.id.outputText);
        languageSpinner = findViewById(R.id.languageSpinner);

        db = FirebaseFirestore.getInstance();

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize language maps
        initializeLanguageMaps();

        // Set up spinner
        setUpSpinner();

        // Initialize TextToSpeech
        initializeTextToSpeech();

        // Set click listeners
        setUpClickListeners();

        // Set up go back button listener
        buttonGoBack.setOnClickListener(v -> finish());

        // Initialize Handler and Runnable for periodic fetching
        handler = new Handler();
        fetchAnnouncementsRunnable = new Runnable() {
            @Override
            public void run() {
                fetchLatestAnnouncement();
                handler.postDelayed(this, 2000); // 2 seconds
            }
        };

        // Start periodic fetching
        handler.post(fetchAnnouncementsRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop the periodic fetching
        handler.removeCallbacks(fetchAnnouncementsRunnable);
        // Shutdown TTS to release resources
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    private void initializeLanguageMaps() {
        languageMap = new HashMap<>();
        languageMap.put("Hindi", TranslateLanguage.HINDI);
        languageMap.put("Telugu", TranslateLanguage.TELUGU);
        languageMap.put("Bengali", TranslateLanguage.BENGALI);
        languageMap.put("Marathi", TranslateLanguage.MARATHI);
        languageMap.put("Tamil", TranslateLanguage.TAMIL);
        languageMap.put("Gujarati", TranslateLanguage.GUJARATI);
        languageMap.put("Kannada", TranslateLanguage.KANNADA);
        languageMap.put("Urdu", TranslateLanguage.URDU);
        languageMap.put("Spanish", TranslateLanguage.SPANISH);
        languageMap.put("French", TranslateLanguage.FRENCH);
        languageMap.put("German", TranslateLanguage.GERMAN);
        languageMap.put("Italian", TranslateLanguage.ITALIAN);
        languageMap.put("Portuguese", TranslateLanguage.PORTUGUESE);
        languageMap.put("Russian", TranslateLanguage.RUSSIAN);
        languageMap.put("Chinese (Simplified)", TranslateLanguage.CHINESE);
        languageMap.put("Japanese", TranslateLanguage.JAPANESE);
        languageMap.put("Korean", TranslateLanguage.KOREAN);
        languageMap.put("Arabic", TranslateLanguage.ARABIC);

        localeMap = new HashMap<>();
        localeMap.put("Hindi", new Locale("hi"));
        localeMap.put("Telugu", new Locale("te"));
        localeMap.put("Bengali", new Locale("bn"));
        localeMap.put("Marathi", new Locale("mr"));
        localeMap.put("Tamil", new Locale("ta"));
        localeMap.put("Gujarati", new Locale("gu"));
        localeMap.put("Kannada", new Locale("kn"));
        localeMap.put("Urdu", new Locale("ur"));
        localeMap.put("Spanish", new Locale("es"));
        localeMap.put("French", new Locale("fr"));
        localeMap.put("German", new Locale("de"));
        localeMap.put("Italian", new Locale("it"));
        localeMap.put("Portuguese", new Locale("pt"));
        localeMap.put("Russian", new Locale("ru"));
        localeMap.put("Chinese (Simplified)", Locale.SIMPLIFIED_CHINESE);
        localeMap.put("Japanese", new Locale("ja"));
        localeMap.put("Korean", new Locale("ko"));
        localeMap.put("Arabic", new Locale("ar"));
    }

    private void setUpSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, new ArrayList<>(languageMap.keySet()));
        adapter.setDropDownViewResource(R.layout.spinner_item);
        languageSpinner.setAdapter(adapter);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLanguage = parent.getItemAtPosition(position).toString();
                setTranslator(selectedLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void initializeTextToSpeech() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            }
        });
    }

    private void setUpClickListeners() {
        btnText.setOnClickListener(v -> {
            if (!isTextActivity()) {
                Intent intent = new Intent(live_audio.this, liveMainActivityText.class);
                startActivity(intent);
                finish();
            }
        });

        btnAudio.setOnClickListener(v -> {
            // Do nothing because we are already in the audio activity
        });

        translateButton.setOnClickListener(v -> {
            if (latestAnnouncement != null) {
                translateText(latestAnnouncement);
            }
        });

        speakButton.setOnClickListener(v -> speakText());
    }

    private void fetchLatestAnnouncement() {
        db.collection("announcements")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot latestAnnouncementDoc = queryDocumentSnapshots.getDocuments().get(0);
                        latestAnnouncement = latestAnnouncementDoc.getString("announcement");
                        translateText(latestAnnouncement);
                    } else {
                        Log.d(TAG, "No announcements found");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching latest announcement", e));
    }

    private void setTranslator(String targetLanguage) {
        if (translator != null) {
            translator.close();
        }

        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(languageMap.get(targetLanguage))
                .build();
        translator = Translation.getClient(options);

        // Disable the translate button until the model is downloaded
        translateButton.setEnabled(false);

        // Download the model
        downloadModel();
    }

    private void downloadModel() {
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(unused -> {
                    // Enable the translate button once the model is downloaded
                    translateButton.setEnabled(true);
                })
                .addOnFailureListener(e -> {
                    // Handle the failure of model download
                    outputText.setText("Model download failed: " + e.getMessage());
                });
    }

    private void translateText(String text) {
        if (text != null && !text.isEmpty()) {
            translator.translate(text)
                    .addOnSuccessListener(translatedText -> outputText.setText(translatedText))
                    .addOnFailureListener(e -> outputText.setText("Translation failed: " + e.getMessage()));
        }
    }

    private void speakText() {
        String text = outputText.getText().toString();
        if (!text.isEmpty()) {
            Locale locale = localeMap.get(selectedLanguage);
            if (locale != null) {
                tts.setLanguage(locale);
            }
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    // Helper methods to check current activity type
    private boolean isTextActivity() {
        return getClass().getSimpleName().equals("liveMainActivityText");
    }
}
