package com.krishnareddy.voicerails;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class home extends AppCompatActivity {

    private View draggableButton;
    private FrameLayout sliderContainer;
    private boolean isDragging = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.homecont), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        draggableButton = findViewById(R.id.draggableButton);
        sliderContainer = findViewById(R.id.sliderContainer);

        // Set up a ViewTreeObserver to ensure views are fully laid out before setting up touch listener
        sliderContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                sliderContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                draggableButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                isDragging = true;
                                return true;
                            case MotionEvent.ACTION_MOVE:
                                if (isDragging) {
                                    float x = event.getRawX() - v.getWidth() / 2;
                                    float containerWidth = sliderContainer.getWidth();
                                    float buttonWidth = draggableButton.getWidth();

                                    // Calculate new X position
                                    float newX = x - sliderContainer.getLeft();

                                    // Bound new X position to container
                                    if (newX < 0) {
                                        newX = 0;
                                    } else if (newX + buttonWidth > containerWidth) {
                                        newX = containerWidth - buttonWidth;
                                    }

                                    draggableButton.setX(newX);
                                }
                                return true;
                            case MotionEvent.ACTION_UP:
                            case MotionEvent.ACTION_CANCEL:
                                isDragging = false;
                                // Handle snap to closest end if needed
                                float middle = sliderContainer.getWidth() / 2;
                                if (draggableButton.getX() < middle - draggableButton.getWidth() / 2) {
                                    draggableButton.setX(0);
                                } else {
                                    draggableButton.setX(sliderContainer.getWidth() - draggableButton.getWidth());
                                }
                                return true;
                        }
                        return false;
                    }
                });
            }
        });
    }
}