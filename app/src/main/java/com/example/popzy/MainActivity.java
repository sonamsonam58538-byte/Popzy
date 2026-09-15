package com.example.popzy;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView scoreText;
    TextView timerText;
    Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Main screen
        setContentView(R.layout.activity_main);

        // Find views
        scoreText = findViewById(R.id.gameScoreText);
        timerText = findViewById(R.id.gameTimerText);
        startButton = findViewById(R.id.startButton);

        // Initial values
        scoreText.setText("0");
        timerText.setText("30");

        // Start button
        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        });
    }
}